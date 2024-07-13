package com.yms.mind.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yms.mind.activities.MainActivity
import com.yms.mind.ui.screens.EditItemScreen
import com.yms.mind.ui.theme.AppTheme
import com.yms.mind.viewmodels.EditViewModel
import com.yms.mind.viewmodels.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject


class EditTodoItemFragment : Fragment() {

    private lateinit var composeView: ComposeView

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val todoViewModel: EditViewModel by viewModels { viewModelFactory }

    private var todoItemId: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        arguments?.getString("todoItemId")?.let {
            todoItemId = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireContext() as MainActivity).activityComponent.fragmentComponentFactory().create()
            .inject(this)

        if (todoItemId != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    todoViewModel.setTodoItem(todoItemId)
                    Log.d("TODO", "fetch item")
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
                    item = todoViewModel.todoItem,
                    onNavigationClick = { requireActivity().supportFragmentManager.popBackStack() }
                )
            }
        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                todoViewModel.errorMessages.collect { errorMessage ->
//                    errorMessage?.let {
//                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
//                        todoViewModel.clearErrorMessage()
//                    }
//                }
//            }
//        }
    }
}