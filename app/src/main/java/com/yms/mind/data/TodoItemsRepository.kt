package com.yms.mind.data

class TodoItemsRepository {
    private val todoItems = mutableListOf<TodoItem>()

    init {
        todoItems.add(TodoItem("1", "Buy groceries", Priority.NORMAL, "2022-09-30", false, "2022-09-27", null))
        todoItems.add(TodoItem("2", "Finish report", Priority.HIGH, "2022-10-05", false, "2022-09-27", null))
        todoItems.add(TodoItem("3", "Call mom", Priority.LOW, "null", false, "2022-09-28", null))
        todoItems.add(TodoItem("4", "Go for a run", Priority.NORMAL, "2022-09-29", false, "2022-09-27", null))
        todoItems.add(TodoItem("5", "Read a book", Priority.LOW, null, false, "2022-09-28", null))
    }

    fun getTodoItems(): List<TodoItem> {
        return todoItems
    }

    fun addTodoItem(todoItem: TodoItem) {
        todoItems.add(todoItem)
    }
}
