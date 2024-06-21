package com.yms.mind.data

class TodoItemsRepository {
    private val todoItems = HashMap<String, TodoItem>()
    private val longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    private var uniqueId = 7
    init {
        todoItems["1"] = TodoItem("1", "Buy groceries", Priority.NORMAL, "2022-09-30", false, "2022-09-27", null)
        todoItems["2"] = TodoItem("2", "Finish report blah blah  blah blah  blah blah  blah blah", Priority.HIGH, "2022-10-05", false, "2022-09-27", null)
        todoItems["3"] = TodoItem("3", "Call mom", Priority.LOW, "null", true, "2022-09-28", null)
        todoItems["4"] = TodoItem("4", "Go for a run", Priority.NORMAL, "2022-09-29", false, "2022-09-27", null)
        todoItems["5"] = TodoItem("5", "Read a book", Priority.LOW, null, false, "2022-09-28", null)
        todoItems["6"] = TodoItem("6", longText, Priority.LOW, null, false, "2022-09-28", null)
    }

    fun getTodoItems(): List<TodoItem> {
        return todoItems.values.toList()
    }

    fun checkItem(id: String, status: Boolean) {
        val newItem = todoItems[id]?.copy(status = status)
        if (newItem != null) todoItems[id] = newItem
    }

    fun addTodoItem(todoItem: TodoItem) {
        todoItems[todoItem.id] = todoItem
    }

    fun generateId() : String {
        while (todoItems[uniqueId.toString()] != null) {
            uniqueId++
        }
        return uniqueId.toString()
    }
}
