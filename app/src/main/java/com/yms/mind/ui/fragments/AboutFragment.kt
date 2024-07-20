package com.yms.mind.ui.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.picasso.PicassoDivImageLoader
import com.yandex.div2.DivData
import com.yms.mind.R
import com.yms.mind.databinding.FragmentAboutBinding
import com.yms.mind.ui.divkit.NotificationDivActionHandler
import org.json.JSONObject

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageLoader = PicassoDivImageLoader(requireContext())
        val configuration = DivConfiguration.Builder(imageLoader)
            .actionHandler(NotificationDivActionHandler(this))
            .build()

        val jsonString = requireContext().assets.open("about.json").bufferedReader().use { it.readText() }
        val divData = JSONObject(jsonString).asDiv2DataWithTemplates()

        val div2View = Div2View(Div2Context(
            baseContext = requireActivity(),
            configuration = configuration,
            lifecycleOwner = viewLifecycleOwner
        ))
        val darkTheme: Boolean
        val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val themePreference = sharedPreferences.getString("app_theme", "system")
        darkTheme = when (themePreference) {
            "dark" -> true
            "system" -> isSystemInDarkTheme()
            else -> false
        }

        val container = view.findViewById<FrameLayout>(R.id.content_frame)
        container.removeAllViews()
        container.addView(div2View)
        div2View.setData(divData, DivDataTag("about_app_screen"))
        div2View.setVariable("dark_theme", "$darkTheme")
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun JSONObject.asDiv2DataWithTemplates(): DivData {
        val templates = getJSONObject("templates")
        val card = getJSONObject("card")
        val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)
        environment.parseTemplates(templates)
        return DivData(environment, card)
    }

    private fun isSystemInDarkTheme(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}