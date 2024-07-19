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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yms.mind.R
import com.yms.mind.activities.MainActivity
import com.yms.mind.adapters.OnCheckBoxListener
import com.yms.mind.adapters.OnItemClickListener
import com.yms.mind.adapters.TaskAdapter
import com.yms.mind.data.model.TodoItem
import com.yms.mind.databinding.FragmentTodoItemsBinding
import com.yms.mind.viewmodels.TodoViewModel
import com.yms.mind.viewmodels.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject


class TodoItemsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val todoViewModel: TodoViewModel by viewModels { viewModelFactory }
    private lateinit var adapter: TaskAdapter
    private lateinit var subtitle: TextView
    private var isVisible = true
    private lateinit var binding: FragmentTodoItemsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireContext() as MainActivity).activityComponent.fragmentComponentFactory().create()
            .inject(this)

        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val menuHost: MenuHost = requireActivity()
        setupMenu(menuHost)
        setupRecycler(binding.recyclerView)
        setupFab(binding.fab)
        subtitle = binding.tasksDone

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                todoViewModel.currentTasks.collect { tasks ->
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
                        todoViewModel.clearErrorMessage()
                    }
                }
            }
        }

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
                    todoViewModel.changeTaskStatus(id)
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
                    R.id.settings -> {
                        openSettings()
                        true
                    }
                    R.id.about -> {
                        openAbout()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun toggleVisibility(menuVisibilityItem: MenuItem) {
        isVisible = !isVisible
        if (isVisible) {
            menuVisibilityItem.setIcon(R.drawable.ic_visibility)
        } else {
            menuVisibilityItem.setIcon(R.drawable.ic_visibility_off)
        }
        todoViewModel.toggleShowCompletedTasks()
    }

    private fun openSettings() {
        val nextFrag = SettingsFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, nextFrag, "findTodoItemsFragment")
            .addToBackStack(null)
            .commit()
    }

    private fun openAbout() {
        val nextFrag = EditTodoItemFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, nextFrag, "findTodoItemsFragment")
            .addToBackStack(null)
            .commit()
    }

    private fun updateSubtitle() {
        lifecycleScope.launch {
            todoViewModel.numberOfCompletedTasks.flowWithLifecycle(
                lifecycle, Lifecycle.State.STARTED
            ).collect { count ->
                subtitle.text = getString(R.string.subtitle, count)
            }
        }
    }
}
