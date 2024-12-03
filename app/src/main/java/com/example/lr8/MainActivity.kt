package com.example.lr8

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var registerButton: Button
    private lateinit var themeButton: Button
    private lateinit var scrollViewText: LinearLayout
    private lateinit var db: FirebaseFirestore
    private lateinit var addButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var idTextInput: EditText
    private lateinit var textInput: EditText
    private var userEmail: String? = null

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
        userEmail = auth.currentUser?.email
        remoteConfig = Firebase.remoteConfig
        db = Firebase.firestore

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }

        remoteConfig.setConfigSettingsAsync(configSettings)

        setTheme()

        email = findViewById(R.id.et_email)
        pass = findViewById(R.id.et_password)
        registerButton = findViewById(R.id.btn_register)
        themeButton = findViewById(R.id.btn_change_theme)
        scrollViewText = findViewById(R.id.linear_layout_db_output)
        addButton = findViewById(R.id.btn_add_entry)
        updateButton = findViewById(R.id.btn_update_entry)
        deleteButton = findViewById(R.id.btn_delete_entry)
        idTextInput = findViewById(R.id.et_db_field1)
        textInput = findViewById(R.id.et_db_field2)

        registerButton.setOnClickListener {
            if (auth.currentUser == null) {
                val emailVal = email.text.toString().trim()
                val passVal = pass.text.toString().trim()

                if (emailVal.isEmpty() || passVal.isEmpty()) {
                    Toast.makeText(this, "Email and/or password is empty", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    auth.signInWithEmailAndPassword(emailVal, passVal)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                                email.text.clear()
                                pass.text.clear()
                                val user = auth.currentUser
                                updateUI(user)
                            } else {
                                auth.createUserWithEmailAndPassword(emailVal, passVal)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                            email.text.clear()
                                            pass.text.clear()
                                            val user = auth.currentUser
                                            updateUI(user)
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Something went wrong",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            updateUI(null)
                                            throw RuntimeException("Punishment for wrong password")
                                        }
                                    }
                            }
                        }
                }
            }
            else
            {
                auth.signOut()
                email.text.clear()
                pass.text.clear()
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }

        themeButton.setOnClickListener {
            Log.d("Click", "${remoteConfig.getBoolean("dark_theme_enabled")}")
            setTheme()
        }

        addButton.setOnClickListener {
            val text = textInput.text.toString()

            if (text.isNotEmpty() && userEmail != null)
            {
                val userDoc = db.collection("users").document(userEmail!!)

                userDoc.collection("entries").add(mapOf("text" to text))
                    .addOnSuccessListener { docRef ->
                        textInput.text.clear()
                        updateUI(auth.currentUser)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to add entry: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            else if (text.isEmpty())
            {
                Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this, "You are not signed in", Toast.LENGTH_SHORT).show()
            }
        }

        updateButton.setOnClickListener {
            val id = idTextInput.text.toString()
            val text = textInput.text.toString()

            if (id.isNotEmpty() && text.isNotEmpty() && userEmail != null)
            {
                val userDoc = db.collection("users").document(userEmail!!)

                userDoc.collection("entries").document(id).update("text", text)
                    .addOnSuccessListener {
                        idTextInput.text.clear()
                        textInput.text.clear()
                        updateUI(auth.currentUser)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update entry: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            else if (id.isEmpty() || text.isEmpty())
            {
                Toast.makeText(this, "Please enter ID and text", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this, "You are not signed in", Toast.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            val id = idTextInput.text.toString()

            if (id.isNotEmpty() && userEmail != null)
            {
                val userDoc = db.collection("users").document(userEmail!!)

                userDoc.collection("entries").document(id).delete()
                    .addOnSuccessListener {
                        idTextInput.text.clear()
                        updateUI(auth.currentUser)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to delete entry: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            else if (id.isEmpty())
            {
                Toast.makeText(this, "Please enter ID", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this, "You are not signed in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?)
    {
        scrollViewText.removeAllViews()

        if (user != null)
        {
            userEmail = user.email

            setWelcome()

            val userDoc = db.collection("users").document(userEmail!!)

            userDoc.collection("entries").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val userEntry = TextView(this)

                        userEntry.text = "${document.id}: ${document.getString("text")}"

                        userEntry.textSize = 16f
                        userEntry.setPadding(16, 16, 16, 16)
                        scrollViewText.addView(userEntry)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load entries: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            registerButton.text = "Logout"
        }
        else
        {
            userEmail = null

            registerButton.text = "Register"
        }
    }

    private fun setTheme()
    {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val theme = remoteConfig.getBoolean("dark_theme_enabled")
                if (!theme)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    setBackground()
                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                setDirection()
                setOrientation()
            }
            else
            {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setWelcome()
    {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val message = remoteConfig.getString("welcome_message")
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
            else
            {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setBackground()
    {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            var mainLayout = findViewById<ConstraintLayout>(R.id.main)
            if (task.isSuccessful)
            {
                val color = remoteConfig.getString("background_color")
                try {
                    val parsedColor = Color.parseColor(color)
                    mainLayout.setBackgroundColor(parsedColor)
                } catch (e: IllegalArgumentException)
                {
                    mainLayout.setBackgroundColor(Color.WHITE)
                }
            }
            else
            {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setDirection()
    {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            var mainLayout = findViewById<ConstraintLayout>(R.id.main)
            if (task.isSuccessful)
            {
                val layoutDirection = remoteConfig.getBoolean("normal_direction")
                if(layoutDirection)
                {
                    mainLayout.layoutDirection = View.LAYOUT_DIRECTION_LTR
                }
                else
                {
                    mainLayout.layoutDirection = View.LAYOUT_DIRECTION_RTL
                }
            }
            else
            {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setOrientation()
    {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            var linearLayout = findViewById<LinearLayout>(R.id.linear_layout_db_output)
            if (task.isSuccessful)
            {
                val layoutDirection = remoteConfig.getBoolean("orientation")
                if(layoutDirection)
                {
                    linearLayout.orientation = LinearLayout.HORIZONTAL
                }
                else
                {
                    linearLayout.orientation = LinearLayout.VERTICAL
                }
            }
            else
            {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}