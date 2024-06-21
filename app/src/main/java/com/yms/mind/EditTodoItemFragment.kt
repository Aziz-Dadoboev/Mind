package com.yms.mind

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.yms.mind.data.Priority
import com.yms.mind.data.TodoItem
import java.util.*


class EditTodoItemFragment : Fragment() {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var todoText: TextInputEditText
    private lateinit var importanceTextView: TextView
    private lateinit var deadlineSwitch: SwitchMaterial
    private lateinit var deadlineTextView: TextView
    private lateinit var deleteButton: Button
    private var todoItemId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todoViewModel = ViewModelProvider(requireActivity())[TodoViewModel::class.java]
        arguments?.getString("todoItem")?.let {
            todoItemId = it
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

        todoText = view.findViewById(R.id.todo_text)
        importanceTextView = view.findViewById(R.id.importance)
        deadlineSwitch = view.findViewById(R.id.switch_deadline)
        deadlineTextView = view.findViewById(R.id.deadline)
        deleteButton = view.findViewById(R.id.delete_button)

        importanceTextView.setOnClickListener{ showPopup(it) }
        deleteButton.setOnClickListener { deleteItem() }
        setUpSwitch()
        setUpDataIfGiven()

        return view
    }

    private fun setUpSwitch() {
        var isUserClicked = false
        deadlineSwitch.setOnClickListener {
            isUserClicked = true
        }
        deadlineSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && isUserClicked) {
                showDatePicker()
                isUserClicked = false
            } else {
                deadlineTextView.text = ""
            }
        }
    }

    private fun setUpDataIfGiven() {
        val todoItem: TodoItem? = todoItemId?.let { todoViewModel.getItem(it) }
        if (todoItem != null) {
            todoText.setText(todoItem.text)
            importanceTextView.text = getPriorityString(todoItem.priority.toString())
            deadlineSwitch.isChecked = todoItem.deadline != null
            deadlineTextView.text = todoItem.deadline
        }
    }

    private fun showDatePicker() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)

            val stringDate = "$selectedDay.$selectedMonth.$selectedYear"
            deadlineTextView.text = stringDate
        }, year, month, day)

        datePicker.show()
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
        if (title == getString(R.string.importance_low)) {
            return Priority.LOW
        } else if (title == getString(R.string.importance_no)) {
            return Priority.NORMAL
        }
        return Priority.HIGH
    }

    private fun getPriorityString(priority: String): String {
        if (priority == "NORMAL") {
            return "Нет"
        } else if (priority == "LOW") {
            return "Низкий"
        }
        return "Высокий"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                saveData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveData() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val id = todoViewModel.generateId()
        val text = todoText.text.toString()
        val priority = getPriorityId(importanceTextView.text.toString())
        val deadline = if (deadlineSwitch.isChecked) deadlineTextView.text.toString()
            else null
        val creationDate = "$day.$month.$year"

        val todoItem = TodoItem(id, text, priority, deadline, false, creationDate, creationDate)
        todoViewModel.addTodoItem(todoItem)

        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun deleteItem() {
        todoItemId?.let { todoViewModel.deleteItem(it) }
        requireActivity().supportFragmentManager.popBackStack()
    }
}