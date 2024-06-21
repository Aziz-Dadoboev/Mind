package com.yms.mind

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.yms.mind.data.OnTodoItemAddedListener
import com.yms.mind.data.Priority
import com.yms.mind.data.TodoItem
import com.yms.mind.data.TodoItemsRepository


class EditTodoItemFragment : Fragment() {
    private lateinit var repository: TodoItemsRepository
    private lateinit var todoText: TextInputEditText
    private lateinit var importanceTextView: TextView
    private lateinit var deadlineSwitch: SwitchMaterial
    private lateinit var deleteButton: Button
    private lateinit var callback: OnTodoItemAddedListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = TodoItemsRepository()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as OnTodoItemAddedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnTodoItemAddedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_todo_item, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.topAppBar)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        todoText = view.findViewById(R.id.todo_text)
        importanceTextView = view.findViewById(R.id.importance)
        deadlineSwitch = view.findViewById(R.id.switch_deadline)
        deleteButton = view.findViewById(R.id.delete_button)

        importanceTextView.setOnClickListener{ showPopup(it) }
    }


    private fun showPopup(v: View) {
        val popup = PopupMenu(context, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.importance_menu, popup.menu)
        popup.show()
        popup.setOnMenuItemClickListener {
            importanceTextView.text = it.title
            true
        }
    }

    private fun getPriorityId(title: String): Priority {
        if (title == getString(R.string.importance_no)) {
            return Priority.LOW
        } else if (title == getString(R.string.importance_low)) {
            return Priority.NORMAL
        }
        return Priority.HIGH
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_toolbar_menu, menu)
        val menuVisibilityItem = menu.findItem(R.id.save)
        setupMenu(menuVisibilityItem)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupMenu(menuVisibilityItem: MenuItem) {
        menuVisibilityItem.setOnMenuItemClickListener {
            saveData()
            true
        }
    }

    private fun saveData() {
        val id = repository.generateId()
        val text = todoText.text.toString()
        val priority = getPriorityId(importanceTextView.text.toString())
        val deadline = if (deadlineSwitch.isChecked) {
            "22.06.2024"
        } else {
            null
        }
        repository.addTodoItem(
            TodoItem(
            id,
            text,
            priority,
            deadline,
            false,
            "22.06.2024",
            null
            )
        )
        callback.onTodoItemAdded()
        requireActivity().supportFragmentManager.popBackStack()
    }
}