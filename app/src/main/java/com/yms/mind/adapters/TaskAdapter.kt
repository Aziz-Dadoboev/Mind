package com.yms.mind.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
                checkBox.isChecked = task.status
                checkBox.setOnCheckedChangeListener(null)
                checkBox.setOnClickListener {
                    checkBoxClickListener.onCheckBoxClicked(task.id, checkBox.isChecked)
                }
                if (task.deadline != null) {
                    dateText.text = task.deadline
                    dateText.visibility = View.VISIBLE
                } else {
                    dateText.visibility = View.GONE
                }

                root.setOnClickListener {
                    itemClickListener.onItemClick(task)
                }
            }
        }
    }
}