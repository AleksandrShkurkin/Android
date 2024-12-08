package com.example.indwork

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject

class ListSelection : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var logoutButton: Button
    private lateinit var createButton: Button
    private lateinit var scrollViewLayout: LinearLayout
    private lateinit var uploadButton: ImageButton
    private lateinit var db: FirebaseFirestore
    private var userEmail: String? = null

    private val getContentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null)
            {
                processFile(uri)
            }
            else
            {
                Toast.makeText(this, "No file was chosen", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_selection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        userEmail = auth.currentUser?.email
        logoutButton = findViewById(R.id.logoutButton)
        createButton = findViewById(R.id.createNewListButton)
        uploadButton = findViewById(R.id.uploadListButton)
        scrollViewLayout = findViewById(R.id.shoppingListContainer)
        db = Firebase.firestore

        logoutButton.setOnClickListener {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            auth.signOut()
            finish()
        }

        createButton.setOnClickListener {
            val intent = Intent(this, CreateForm::class.java)
            startActivity(intent)
        }

        uploadButton.setOnClickListener {
            getContentLauncher.launch("application/json")
        }
    }

    public override fun onResume() {
        super.onResume()
        userEmail = auth.currentUser!!.email
        updateList(userEmail!!)
    }

    private fun updateList(email: String)
    {
        scrollViewLayout.removeAllViews()

        var userDocRef = db.collection("users").document(email)

        userDocRef.get().addOnSuccessListener { resultDoc ->
            if (resultDoc.exists())
            {
                Toast.makeText(this, "Db found", Toast.LENGTH_SHORT).show()
                val list = resultDoc.get("shopping_lists") as? List<String> ?: emptyList()
                list.forEach { listName ->
                    val listRef = userDocRef.collection(listName)
                    listRef.get().addOnSuccessListener { resultListDoc ->
                        val entryCount = resultListDoc.size()
                        if (entryCount > 0)
                        {
                            addShoppingListCard(listName, entryCount)
                        }
                    }
                }
            }
            else
            {
                userDocRef.set(hashMapOf("shopping_list" to emptyList<String>())).addOnSuccessListener {
                    Toast.makeText(this, "Db initialized!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addShoppingListCard(listName: String, count: Int)
    {
        val card = layoutInflater.inflate(R.layout.shopping_list_item, scrollViewLayout, false)

        val listNameText = card.findViewById<TextView>(R.id.listNameText)
        val countText = card.findViewById<TextView>(R.id.entryCountText)
        val deleteButton = card.findViewById<Button>(R.id.deleteListButton)

        listNameText.text = listName
        val countReal = count - 1
        countText.text = "$countReal Entries"

        deleteButton.setOnClickListener {
            it.isPressed = true
            deleteList(listName)
            return@setOnClickListener
        }

        card.setOnClickListener {
            val intent = Intent(this, ListShow::class.java)
            intent.putExtra("list_name", listName)
            startActivity(intent)
        }

        scrollViewLayout.addView(card)
    }

    private fun deleteList(listName: String)
    {
        val userDocRef = db.collection("users").document(userEmail!!)
        userDocRef.collection(listName).get().addOnSuccessListener { listRef ->
            for (document in listRef.documents)
            {
                userDocRef.collection(listName).document(document.id).delete()
            }

            userDocRef.get().addOnSuccessListener { arrayRef ->
                val currentList = arrayRef.get("shopping_lists") as? List<String> ?: emptyList()

                val mutableList = currentList.toMutableList()
                val indexToRemove = mutableList.indexOf(listName)
                if (indexToRemove != -1)
                {
                    mutableList.removeAt(indexToRemove)
                }

                userDocRef.update("shopping_lists", mutableList)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Deleted $listName", Toast.LENGTH_SHORT).show()
                        updateList(userEmail!!)
                    }
            }
        }
    }

    private fun processFile(uri: Uri)
    {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val jsonContent = inputStream?.bufferedReader().use { it?.readText() }

            if (jsonContent != null)
            {
                try {
                    val listObject = JSONObject(jsonContent)
                    val listName = listObject.getString("list_name")
                    val productArray = listObject.getJSONArray("products")
                    var userDocRef = db.collection("users").document(userEmail!!)

                    userDocRef.get().addOnSuccessListener { resultDoc ->
                        val currentList =
                            resultDoc.get("shopping_lists") as? List<String> ?: emptyList()

                        var mutableList = currentList.toMutableList()
                        val tasks = mutableListOf<Task<Void>>()

                        for (i in 0 until productArray.length()) {
                            val product = productArray.getJSONObject(i)
                            val productName = product.getString("name")
                            val productDesc = product.getString("description")

                            val indexName = mutableList.indexOf(listName)
                            if (indexName == -1) {
                                userDocRef.update("shopping_lists", FieldValue.arrayUnion(listName))
                                mutableList.add(listName)
                            }

                            val task = userDocRef.collection(listName).document(productName).set(
                                hashMapOf(
                                    "description" to productDesc,
                                    "isChecked" to false
                                )
                            )
                            tasks.add(task)
                        }
                        Tasks.whenAllComplete(tasks).addOnSuccessListener {
                            updateList(userEmail!!)
                        }
                    }
                } catch (e: JSONException)
                {
                    e.printStackTrace()
                    Toast.makeText(this, "Wrong file format", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Could`t read the file", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception)
        {
            e.printStackTrace()
            Toast.makeText(this, "Error in getting the file", Toast.LENGTH_SHORT).show()
        }
    }
}