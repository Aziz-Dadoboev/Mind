package com.yms.mind.activities

/**
 * MainActivity - Главное активити приложения
 * Отображает фрагменты, обеспечивает связь между ними и доступ к общим частям приложения
 */

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yms.mind.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}