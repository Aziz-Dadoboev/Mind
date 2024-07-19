package com.yms.mind.activities

/**
 * MainActivity - Главное активити приложения
 * Отображает фрагменты, обеспечивает связь между ними и доступ к общим частям приложения
 */

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.yms.mind.MindApp
import com.yms.mind.databinding.ActivityMainBinding
import com.yms.mind.di.ActivityComponent


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val theme = sharedPreferences.getString("app_theme", "system")

        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        super.onCreate(savedInstanceState)
        (application as MindApp).appComponent.activityComponentFactory().create(this).also {
            activityComponent = it
            it.inject(this)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}