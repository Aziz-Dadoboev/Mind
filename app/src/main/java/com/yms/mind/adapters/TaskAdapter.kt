package com.yms.mind.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yms.mind.data.TodoItem
import com.yms.mind.databinding.TodoItemBinding

class TaskAdapter(
    private val checkBoxClickListener: OnCheckBoxListener,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    var data: List<TodoItem> = mutableListOf()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(TodoListDiffUtilCallback(field, value))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    class TaskViewHolder(
        private val binding: TodoItemBinding,
        private val checkBoxClickListener: OnCheckBoxListener,
        private val itemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: TodoItem) {
            with(binding) {
                checkText.text = task.text
                checkBox.isChecked = task.status
                checkBox.setOnCheckedChangeListener(null)
                checkBox.setOnClickListener {
                    checkBoxClickListener.onCheckBoxClicked(adapterPosition, checkBox.isChecked)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TodoItemBinding.inflate(inflater, parent, false)

        return TaskViewHolder(binding, checkBoxClickListener, itemClickListener)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = data[position]
        holder.bind(task)
    }
}