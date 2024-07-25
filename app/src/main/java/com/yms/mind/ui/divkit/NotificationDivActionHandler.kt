package com.yms.mind.ui.divkit

import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivViewFacade
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction

class NotificationDivActionHandler(
    private val fragment: Fragment
): DivActionHandler() {
    override fun handleAction(
        action: DivAction,
        view: DivViewFacade,
        resolver: ExpressionResolver
    ): Boolean {
        val url = action.url?.evaluate(resolver) ?: return super.handleAction(action, view, resolver)

        return if (url.scheme == SCHEME_SAMPLE && handleSampleAction(url, view.view.context)) {
            true
        } else {
            super.handleAction(action, view, resolver)
        }
    }

    private fun handleSampleAction(action: Uri, context: Context?): Boolean {
        return when(action.host) {
            "back" -> {
                fragment.requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> false
        }
    }

    companion object {
        const val SCHEME_SAMPLE = "sample-action"
    }

}
