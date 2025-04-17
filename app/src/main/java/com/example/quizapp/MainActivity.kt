package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizapp.ui.QuestionsActivity
import com.example.quizapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.quizapp.ui.ResultActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var button: Button
    private lateinit var textView: TextView
    private lateinit var user: FirebaseUser
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        button = findViewById(R.id.btn_logout)
        textView = findViewById(R.id.user_details)
        profileImageView = findViewById(R.id.profileImageView)

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val imageUrl = document.getString("imageUrl")
                    if (!imageUrl.isNullOrEmpty()) {
                        // Load the image using Glide
                        Glide.with(this)
                            .load(imageUrl)
                            .into(profileImageView)
                    }
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }

//        user = auth.currentUser!!
//        if(user == null){
//            Intent(applicationContext, Login::class.java).also{
//                startActivity(it)
//                finish()
//            }
//        }else{
//            textView.text = user.email
//        }

        user = auth.currentUser ?: run {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        button.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
            Intent(applicationContext, Login::class.java).also{
                startActivity(it)
                finish()
            }
        }



        val startButton: Button = findViewById(R.id.button_start)
        val editTextName: EditText = findViewById(R.id.name)

//        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val registeredName = document.getString("name")?.trim()
                    if (!registeredName.isNullOrEmpty()) {
                        editTextName.setText(registeredName)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load name", Toast.LENGTH_SHORT).show()
                }
        }

        startButton.setOnClickListener {
            val enteredName = editTextName.text.toString().trim()
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        val registeredName = document.getString("name")?.trim()
                        if (enteredName == registeredName) {
                            // Names match, proceed
                            val intent = Intent(this@MainActivity, QuestionsActivity::class.java)
                            intent.putExtra(Constants.USER_NAME, enteredName)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Name does not match registered name", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to verify name", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}