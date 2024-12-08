package com.example.indwork

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passInput: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        emailInput = findViewById(R.id.emailInput)
        passInput = findViewById(R.id.passwordInput)
        registerButton = findViewById(R.id.btnRegister)
        loginButton = findViewById(R.id.btnLogin)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this, "Please enter email AND password", Toast.LENGTH_SHORT).show()
            }
            else
            {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                        emailInput.text.clear()
                        passInput.text.clear()
                        updateUI(auth.currentUser)
                    }
                    else
                    {
                        Toast.makeText(this, "This account already exists!", Toast.LENGTH_SHORT).show()
                        emailInput.text.clear()
                        passInput.text.clear()
                    }
                }
            }
        }

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this, "Please enter email AND password", Toast.LENGTH_SHORT).show()
            }
            else
            {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        emailInput.text.clear()
                        passInput.text.clear()
                        updateUI(auth.currentUser)
                    }
                    else
                    {
                        Toast.makeText(this, "Wrong email or password", Toast.LENGTH_SHORT).show()
                        emailInput.text.clear()
                        passInput.text.clear()
                    }
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        updateUI(auth.currentUser)
    }

    private fun updateUI(user: FirebaseUser?)
    {
        if (user != null)
        {
            Toast.makeText(this, "User detected", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ListSelection::class.java)
            startActivity(intent)
        }
        else
        {
            Toast.makeText(this, "No user detected", Toast.LENGTH_SHORT).show()
        }
    }
}