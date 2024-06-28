package com.yms.mind.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yms.mind.R
import com.yms.mind.data.Priority
import com.yms.mind.data.TodoItem
import com.yms.mind.databinding.TodoItemBinding

class TaskAdapter(
    private val checkBoxClickListener: OnCheckBoxListener,
    private val itemClickListener: OnItemClickListener
) : ListAdapter<TodoItem, TaskAdapter.TaskViewHolder>(TodoListDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TodoItemBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, checkBoxClickListener, itemClickListener)
    }

    class TaskViewHolder(
        private val binding: TodoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            task: TodoItem,
            checkBoxClickListener: OnCheckBoxListener,
            itemClickListener: OnItemClickListener
        ) {
            with(binding) {
                checkText.text = task.text
                if (task.status) {
                    checkText.paintFlags = checkText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }

                checkBox.isChecked = task.status

//                checkBox.setOnCheckedChangeListener(null)
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        checkText.paintFlags = checkText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        checkText.paintFlags = checkText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                }
                checkBox.setOnClickListener {
                    checkBoxClickListener.onCheckBoxClicked(task.id, checkBox.isChecked)
                }
                if (task.deadline != null && !task.status) {
                    dateText.text = task.deadline
                    dateText.visibility = View.VISIBLE
                } else {
                    dateText.visibility = View.GONE
                }

                checkBox.buttonIconDrawable = if (!task.status && task.priority == Priority.HIGH) {
                    ContextCompat.getDrawable(checkBox.context, R.drawable.ic_unchecked_red)
                } else if (!task.status) {
                    ContextCompat.getDrawable(checkBox.context, R.drawable.ic_unchecked)
                } else {
                    ContextCompat.getDrawable(checkBox.context, R.drawable.ic_checked)
                }

                root.setOnClickListener {
                    itemClickListener.onItemClick(task)
                }
            }
        }
    }
}