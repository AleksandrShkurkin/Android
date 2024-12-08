package com.example.indwork

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateForm : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var listName: EditText
    private lateinit var backButton: ImageButton
    private lateinit var createButton: Button
    private lateinit var auth: FirebaseAuth
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        userEmail = auth.currentUser?.email
        db = Firebase.firestore
        listName = findViewById(R.id.listNameInput)
        backButton = findViewById(R.id.backButton)
        createButton = findViewById(R.id.createButton)

        backButton.setOnClickListener {
            finish()
        }

        createButton.setOnClickListener {
            var newListName = listName.text.toString().trim()

            if (newListName.isEmpty())
            {
                Toast.makeText(this, "Enter a name of new list!", Toast.LENGTH_SHORT).show()
            }
            else
            {
                var userDocRef = db.collection("users").document(userEmail!!)

                userDocRef.get().addOnSuccessListener { resultDoc ->
                    val currentList = resultDoc.get("shopping_lists") as? List<String> ?: emptyList()

                    val indexName = currentList.indexOf(newListName)
                    if (indexName != -1)
                    {
                        Toast.makeText(this, "This list already exists!", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        userDocRef.update("shopping_lists", FieldValue.arrayUnion(newListName))
                        userDocRef.collection(newListName).document("Empty").set(hashMapOf("description" to "", "isChecked" to false))
                            .addOnSuccessListener {
                                Toast.makeText(this, "New list $newListName created!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                    }
                }
            }
        }
    }
}