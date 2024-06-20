package com.yms.mind

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yms.mind.adapters.OnCheckBoxListener
import com.yms.mind.adapters.TaskAdapater
import com.yms.mind.data.TodoItemsRepository


class TodoItemsFragment : Fragment(), OnCheckBoxListener {

    private lateinit var repository: TodoItemsRepository
    private lateinit var adapter: TaskAdapater
    private lateinit var subtitle: TextView
    private lateinit var recyclerView: RecyclerView
    private var isVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = TodoItemsRepository()
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

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TaskAdapater(this)
        setupData(isVisible)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        val menuVisibilityItem = menu.findItem(R.id.visibility)
        Log.d("VISIBLE", "$menuVisibilityItem Here")
        setupMenu(menuVisibilityItem)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupMenu(menuVisibilityItem: MenuItem) {
        menuVisibilityItem.setOnMenuItemClickListener {
            Log.d("VISIBLE", "$isVisible")
            isVisible = if (isVisible) {
                it.setIcon(R.drawable.ic_visibility_off)
                setupData(false)
                false
            } else {
                it.setIcon(R.drawable.ic_visibility)
                setupData(true)
                true
            }
            true
        }
    }

    private fun setupData(all: Boolean) {
        adapter.data =
            if (all) repository.getTodoItems()
            else repository.getUndoneTasks()
        recyclerView.adapter = adapter
        subtitle.text = getString(R.string.subtitle, repository.getDoneCount())
    }
    override fun onCheckBoxClicked(position: Int, isChecked: Boolean) {
        val item = adapter.data[position]
        repository.checkItem(item.id, !item.status)
        subtitle.text = getString(R.string.subtitle, repository.getDoneCount())
        setupData(isVisible)
    }
}
