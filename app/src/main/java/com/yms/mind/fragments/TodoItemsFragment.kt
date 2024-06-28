package com.yms.mind.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yms.mind.R
import com.yms.mind.adapters.OnCheckBoxListener
import com.yms.mind.adapters.OnItemClickListener
import com.yms.mind.adapters.TaskAdapter
import com.yms.mind.data.TodoItem
import com.yms.mind.databinding.FragmentTodoItemsBinding
import com.yms.mind.viewmodels.TodoViewModel
import kotlinx.coroutines.launch


class TodoItemsFragment : Fragment() {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var subtitle: TextView
    private lateinit var fab: FloatingActionButton
    private var isVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todoViewModel = ViewModelProvider(requireActivity())[TodoViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTodoItemsBinding.inflate(inflater, container, false)
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val menuHost: MenuHost = requireActivity()
        setupMenu(menuHost)

        subtitle = binding.tasksDone
        fab = binding.fab

        adapter = TaskAdapter(
            checkBoxClickListener = object : OnCheckBoxListener {
                override fun onCheckBoxClicked(id: String, isChecked: Boolean) {
                    todoViewModel.checkItem(id, isChecked)
                    updateSubtitle()
                }
            },
            itemClickListener = object : OnItemClickListener {
                override fun onItemClick(todoItem: TodoItem) {
                    // TODO Перейти на экран редактирования
                }
            }
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                todoViewModel.todoItems.collect { tasks ->
                    adapter.submitList(tasks)
                    updateSubtitle()
                }
            }
        }

        fab.setOnClickListener {
            val nextFrag = EditTodoItemFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, nextFrag, "findTodoItemsFragment")
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun setupMenu(menuHost: MenuHost) {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.visibility -> {
                        toggleVisibility(menuItem)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                todoViewModel.todoItems.collect { tasks ->
                    adapter.submitList(tasks)
                }
            }
        }
    }

    private fun toggleVisibility(menuVisibilityItem: MenuItem) {
        isVisible = !isVisible
        if (isVisible) {
            menuVisibilityItem.setIcon(R.drawable.ic_visibility)
        } else {
            menuVisibilityItem.setIcon(R.drawable.ic_visibility_off)
        }
        todoViewModel.setItemsVisible(isVisible)
    }

    private fun updateSubtitle() {
        val completedTasksCount = todoViewModel.getCompletedTasksCount()
        subtitle.text = getString(R.string.subtitle, completedTasksCount)
    }
}
