package com.yms.mind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = TodoItemsRepository()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_todo_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        subtitle = view.findViewById<TextView>(R.id.tasks_done)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TaskAdapater(this)
        setupData()
    }

    private fun setupData() {
        adapter.data = repository.getTodoItems()
        recyclerView.adapter = adapter
        subtitle.text = getString(R.string.subtitle, repository.getDoneCount())
    }
    override fun onCheckBoxClicked(position: Int, isChecked: Boolean) {
        val item = adapter.data[position]
        repository.checkItem(item.id, !item.status)
        subtitle.text = getString(R.string.subtitle, repository.getDoneCount())
        setupData()
    }
}
