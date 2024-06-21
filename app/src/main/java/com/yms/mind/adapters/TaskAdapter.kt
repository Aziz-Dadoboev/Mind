package com.yms.mind.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yms.mind.data.TodoItem
import com.yms.mind.databinding.TodoItemBinding

class TaskAdapter(
    private val checkBoxClickListener: OnCheckBoxListener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    var data: List<TodoItem> = mutableListOf()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(TodoListDiffUtilCallback(field, value))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    class TaskViewHolder(val binding: TodoItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TodoItemBinding.inflate(inflater, parent, false)

        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = data[position]

        with(holder.binding) {
            checkBox.text = task.text
            checkBox.isChecked = task.status
            checkBox.setOnCheckedChangeListener(null)
            checkBox.setOnClickListener {
                checkBoxClickListener.onCheckBoxClicked(holder.adapterPosition, checkBox.isChecked)
            }
        }
    }
}