package com.example.instafire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private var TAG = "LOGIN ACTIVITY"
    private lateinit var loginButton : Button
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        //create updateUI() function to customize UI


        loginButton = findViewById(R.id.loginButton)
        email = findViewById(R.id.getEmail)
        password = findViewById(R.id.enterPassword)

        if (currentUser != null) {
            goPostActivity()
        }

        loginButton.setOnClickListener {
            loginButton.isEnabled = false
            val enteredEmail = email.text.toString()
            val enteredPassword =  password.text.toString()
            if (enteredEmail.isBlank() || enteredPassword.isBlank()) {
                Toast.makeText(this, "Email and password can't be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            // Firebase auth
            auth.signInWithEmailAndPassword(enteredEmail, enteredPassword).addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    loginButton.isEnabled = true
                    Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show()
                    goPostActivity()
                } else {
                    Log.e(TAG, "login with email failed", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun goPostActivity() {
        Log.i(TAG, "in post activity")
        val intent = Intent(this, PostsActivity:: class.java)
        startActivity(intent)
        finish()
    }
}