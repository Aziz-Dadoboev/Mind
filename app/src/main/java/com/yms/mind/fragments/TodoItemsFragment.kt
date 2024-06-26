package com.yms.mind.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yms.mind.R
import com.yms.mind.adapters.OnCheckBoxListener
import com.yms.mind.adapters.OnItemClickListener
import com.yms.mind.adapters.TaskAdapter
import com.yms.mind.data.TodoItem
import com.yms.mind.viewmodels.TodoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TodoItemsFragment : Fragment(), OnCheckBoxListener, OnItemClickListener {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var subtitle: TextView
    private lateinit var recyclerView: RecyclerView
    private var isVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todoViewModel = ViewModelProvider(requireActivity())[TodoViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todo_items, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        subtitle = view.findViewById<TextView>(R.id.tasks_done)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TaskAdapter(this, this)
        recyclerView.adapter = adapter

        lifecycleScope.launch(Dispatchers.Main) {
            todoViewModel.todoItems.collect { items ->
                adapter.data = items
                subtitle.text = getString(R.string.subtitle, items.count { it.status })
            }
        }
        fab.setOnClickListener {
            val nextFrag = EditTodoItemFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, nextFrag, "findTodoItemsFragment")
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_menu, menu)
        val menuVisibilityItem = menu.findItem(R.id.visibility)
        setupMenu(menuVisibilityItem)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupMenu(menuVisibilityItem: MenuItem) {
        menuVisibilityItem.setOnMenuItemClickListener { it ->
            isVisible = if (isVisible) {
                it.setIcon(R.drawable.ic_visibility_off)
                false
            } else {
                it.setIcon(R.drawable.ic_visibility)
                true
            }
            todoViewModel.setVisible(isVisible)
            adapter.data = todoViewModel.todoItems.value
            true
        }
    }

    override fun onCheckBoxClicked(position: Int, isChecked: Boolean) {
        val item = adapter.data[position]
        todoViewModel.checkItem(item.id, !item.status, isVisible)
    }

    override fun onItemClick(todoItem: TodoItem) {
        val editFragment = EditTodoItemFragment()

        val bundle = Bundle()
        bundle.putString("todoItem", todoItem.id)
        editFragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, editFragment, "EditTodoItemFragment")
            .addToBackStack(null)
            .commit()
    }
}