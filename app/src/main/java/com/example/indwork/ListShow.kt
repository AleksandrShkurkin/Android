package com.example.indwork

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

class ListShow : AppCompatActivity() {
    private lateinit var listName: String
    private lateinit var returnButton: ImageButton
    private lateinit var scrollViewLayout: LinearLayout
    private lateinit var productName: EditText
    private lateinit var productDescription: EditText
    private lateinit var addProductButton: Button
    private lateinit var downloadButton: ImageButton
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_show)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        userEmail = auth.currentUser?.email
        db = Firebase.firestore
        listName = intent.getStringExtra("list_name")!!
        returnButton = findViewById(R.id.backButton)
        scrollViewLayout = findViewById(R.id.productListContainer)
        productName = findViewById(R.id.productNameInput)
        productDescription = findViewById(R.id.productDescriptionInput)
        addProductButton = findViewById(R.id.addProductButton)
        downloadButton = findViewById(R.id.downloadButton)

        returnButton.setOnClickListener {
            finish()
        }

        addProductButton.setOnClickListener {
            var name = productName.text.toString().trim()
            var description = productDescription.text.toString().trim()

            if (name.isEmpty())
            {
                Toast.makeText(this, "Enter a product to add", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val userDocRef = db.collection("users").document(userEmail!!).collection(listName).document(name)
                userDocRef.get().addOnSuccessListener { documentRef ->
                    if (documentRef.exists())
                    {
                        db.collection("users").document(userEmail!!).collection(listName)
                            .document(name).update("description", description, "isChecked", false)
                        productName.text.clear()
                        productDescription.text.clear()
                    }
                    else if (description.isEmpty())
                    {
                        db.collection("users").document(userEmail!!).collection(listName)
                            .document(name)
                            .set(hashMapOf("description" to "No description", "isChecked" to false))
                        productName.text.clear()
                        productDescription.text.clear()
                    }
                    else
                    {
                        db.collection("users").document(userEmail!!).collection(listName)
                            .document(name)
                            .set(hashMapOf("description" to description, "isChecked" to false))
                        productName.text.clear()
                        productDescription.text.clear()
                    }
                    updateList(userEmail!!)
                }
            }
        }

        downloadButton.setOnClickListener {
            val listJson = JSONObject()

            db.collection("users").document(userEmail!!).collection(listName).get()
                .addOnSuccessListener { products ->
                    val productJSONArray = JSONArray()
                    for (product in products)
                    {
                        val productJSON = JSONObject().apply {
                            put("name", product.id)
                            put("description", product.getString("description"))
                        }
                        productJSONArray.put(productJSON)
                    }
                    listJson.put("list_name", listName)
                    listJson.put("products", productJSONArray)

                    val userList = TextView(this)
                    userList.text = listJson.toString()
                    userList.textSize = 16f
                    scrollViewLayout.addView(userList)

                    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
                    {
                        saveToDownloads("$listName.json", listJson.toString())
                    }
                    else
                    {
                        Toast.makeText(this, "External storage not available", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    public override fun onResume() {
        super.onResume()
        userEmail = auth.currentUser!!.email
        updateList(userEmail!!)
    }

    private fun saveToDownloads(fileName: String, content: String)
    {
        try {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadDir, fileName)
            file.writeText(content)
            Toast.makeText(this, "Shopping list $listName saved!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException)
        {
            e.printStackTrace()
            Toast.makeText(this, "Error in saving the file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateList(email: String)
    {
        scrollViewLayout.removeAllViews()

        var userDocRef = db.collection("users").document(email).collection(listName)

        userDocRef.get().addOnSuccessListener { resultDoc ->
            var index = 1
            for (document in resultDoc.documents)
            {
                if (document.id != "Empty") {
                    addProductCard(
                        index,
                        document.id,
                        document.getString("description")!!,
                        document.getBoolean("isChecked")!!
                    )
                    index++
                }
            }
        }
    }

    private fun addProductCard(index: Int, productNameSet: String, productDescriptionSet: String, state: Boolean)
    {
        val card = layoutInflater.inflate(R.layout.product_card, scrollViewLayout, false)

        if (state)
        {
            card.background = ColorDrawable(Color.parseColor("#AAAAAA"))
        }

        val indexShow = card.findViewById<TextView>(R.id.productIndex)
        val productNameText = card.findViewById<TextView>(R.id.productName)
        val productDescText = card.findViewById<TextView>(R.id.productDescription)
        val deleteButton = card.findViewById<ImageButton>(R.id.deleteButton)
        val updateButton = card.findViewById<ImageButton>(R.id.editButton)
        val checkBox = card.findViewById<CheckBox>(R.id.productCheckBox)

        checkBox.isChecked = state

        indexShow.text = index.toString()
        productNameText.text = productNameSet
        productDescText.text = productDescriptionSet

        deleteButton.setOnClickListener {
            db.collection("users").document(userEmail!!).collection(listName).document(productNameSet).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "$productNameSet deleted!", Toast.LENGTH_SHORT).show()
                    updateList(userEmail!!)
                }
        }

        updateButton.setOnClickListener {
            productName.setText(productNameSet)
            productDescription.setText(productDescriptionSet)
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                card.background = ColorDrawable(Color.parseColor("#AAAAAA"))
            }
            else
            {
                card.background = ColorDrawable(Color.WHITE)
            }

            db.collection("users").document(userEmail!!).collection(listName).document(productNameSet).update("isChecked", isChecked)
        }

        scrollViewLayout.addView(card)
    }
}