package com.yms.mind

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yms.mind.data.TodoItemsRepository
import com.yms.mind.adapters.TaskAdapater

class TodoItemsFragment : Fragment() {

    private lateinit var repository: TodoItemsRepository
    private lateinit var adapter: TaskAdapater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = TodoItemsRepository()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todo_items, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val subtitle = view.findViewById<TextView>(R.id.tasks_done)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TaskAdapater()
        adapter.data = repository.getTodoItems()
        recyclerView.adapter = adapter
        subtitle.text = getString(R.string.subtitle, repository.getDoneCount())

        return view
    }
}