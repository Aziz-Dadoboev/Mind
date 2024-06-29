package com.yms.mind.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        setupRecycler(binding.recyclerView)
        setupFab(binding.fab)
        subtitle = binding.tasksDone

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                todoViewModel.todoItems.collect { tasks ->
                    adapter.submitList(tasks)
                    updateSubtitle()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                todoViewModel.errorMessages.collect { errorMessage ->
                    errorMessage?.let {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        todoViewModel.clearErrorMessage() // Добавьте метод в ViewModel для очистки сообщения
                    }
                }
            }
        }

        return binding.root
    }

    private fun setupFab(fab: FloatingActionButton) {
        fab.setOnClickListener {
            val nextFrag = EditTodoItemFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, nextFrag, "findTodoItemsFragment")
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecycler(recyclerView: RecyclerView) {
        adapter = TaskAdapter(
            checkBoxClickListener = object : OnCheckBoxListener {
                override fun onCheckBoxClicked(id: String, isChecked: Boolean) {
                    todoViewModel.checkItem(id, isChecked)
                    updateSubtitle()
                }
            },
            itemClickListener = object : OnItemClickListener {
                override fun onItemClick(todoItem: TodoItem) {
                    val nextFrag = EditTodoItemFragment()
                    val bundle = Bundle()
                    bundle.putString("todoItemId", todoItem.id)
                    nextFrag.arguments = bundle
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, nextFrag, "findTodoItemsFragment")
                        .addToBackStack(null)
                        .commit()
                }
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
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
