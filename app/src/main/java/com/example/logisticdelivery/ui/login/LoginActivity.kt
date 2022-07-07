package com.example.logisticdelivery.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.logisticdelivery.R

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        if (supportFragmentManager.fragments.isEmpty())
            addLoginFragment()
    }
    private fun addLoginFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, LoginFragment())
            .commit()
    }
}