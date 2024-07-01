package com.yms.mind.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yms.mind.data.TodoItem
import com.yms.mind.ui.screens.EditItemScreen
import com.yms.mind.ui.theme.AppTheme
import com.yms.mind.viewmodels.TodoViewModel
import kotlinx.coroutines.launch


class EditTodoItemFragment : Fragment() {

    private lateinit var composeView: ComposeView
    private lateinit var todoViewModel: TodoViewModel
    private var todoItem: TodoItem? = null
    private var todoItemId: String? = null
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        todoViewModel = ViewModelProvider(requireActivity())[TodoViewModel::class.java]
        arguments?.getString("todoItemId")?.let {
            todoItemId = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (todoItemId != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    todoItem = todoViewModel.fetchItem(todoItemId!!)!!
                }
            }
        }
        return ComposeView(requireContext()).also { composeView = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            AppTheme {
                EditItemScreen(
                    viewModel = todoViewModel,
                    item = todoItem,
                    onNavigationClick = { requireActivity().supportFragmentManager.popBackStack() }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                todoViewModel.errorMessages.collect { errorMessage ->
                    errorMessage?.let {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        todoViewModel.clearErrorMessage()
                    }
                }
            }
        }
    }
}