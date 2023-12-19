package com.hw.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    lateinit var email: TextInputLayout
    lateinit var password: TextInputLayout
    lateinit var login: Button
    lateinit var error: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.etEm)
        password = findViewById(R.id.etPas)
        login = findViewById(R.id.log_in)
        error = findViewById(R.id.error)

        login.setOnClickListener {

            val username: String = email.editText?.text.toString()
            val passwordText: String = password.editText?.text.toString()

            if (username.isEmpty() && passwordText.isEmpty()) {
                error.text = resources.getString(R.string.noLoginOrPassword)
            } else if (username.isEmpty()) {
                error.text = resources.getString(R.string.noLogin)
            } else if (passwordText.isEmpty()) {
                error.text = resources.getString(R.string.noPassword)
            } else {
                error.text = ""
            }
        }
    }
}