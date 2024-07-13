package com.yms.mind.activities

/**
 * MainActivity - Главное активити приложения
 * Отображает фрагменты, обеспечивает связь между ними и доступ к общим частям приложения
 */

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yms.mind.MindApp
import com.yms.mind.databinding.ActivityMainBinding
import com.yms.mind.di.ActivityComponent


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MindApp).appComponent.activityComponentFactory().create(this).also {
            activityComponent = it
            it.inject(this)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}