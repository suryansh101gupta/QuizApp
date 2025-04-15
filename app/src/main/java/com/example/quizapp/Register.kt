package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizapp.ui.QuestionsActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.util.UUID


class Register : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonReg: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView

    private lateinit var profileImageView: ImageView
    private var selectedImageUri: Uri? = null


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Intent(applicationContext, MainActivity::class.java).also{
                startActivity(it)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            profileImageView.setImageURI(selectedImageUri)

            val imageUri = data.data

            Glide.with(this)
                .load(imageUri)
                .circleCrop()
                .into(profileImageView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        buttonReg = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.progress_bar)
        textView = findViewById(R.id.loginnow)

        profileImageView = findViewById(R.id.profile_image)

        profileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 101)
        }

        textView.setOnClickListener(){
            Intent(this, Login::class.java).also{
                startActivity(it)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }
        }

        buttonReg.setOnClickListener(){
            progressBar.visibility = View.GONE
            var email = editTextEmail.getText().toString()
            var password = editTextPassword.getText().toString()
            val anim = AnimationUtils.loadAnimation(this, R.anim.bounce)
            buttonReg.startAnimation(anim)

            val name = findViewById<TextInputEditText>(R.id.etname).text.toString()

            if (selectedImageUri == null) {
                Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(email)){
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(password)){
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(name)){
                Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            buttonReg.postDelayed({
//                startActivity(Intent(this, Register::class.java))
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }, 300)

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        // val user = auth.currentUser

                        val uid = auth.currentUser?.uid
                        val db = FirebaseFirestore.getInstance()

                        if (selectedImageUri != null && uid != null) {
                            val fileName = UUID.randomUUID().toString()
                            val ref = com.google.firebase.storage.FirebaseStorage.getInstance().getReference("/profileImages/$fileName")

                            ref.putFile(selectedImageUri!!)
                                .addOnSuccessListener {
                                    ref.downloadUrl.addOnSuccessListener { uri: Uri ->
                                        val userMap = hashMapOf(
                                            "name" to name,
                                            "imageUrl" to uri.toString()
                                        )
                                        db.collection("users").document(uid).set(userMap)
                                            .addOnSuccessListener {
                                                Log.d("Firestore", "Name and image URL saved")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("Firestore", "Firestore error", e)
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
                                }
                        } else if (uid != null) {
                            val userMap = hashMapOf("name" to name)
                            db.collection("users").document(uid).set(userMap)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Name saved without image")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error saving name", e)
                                }
                        }



                        Toast.makeText(
                            baseContext,
                            "Account Created.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        Intent(this, Login::class.java).also{
                            startActivity(it)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            finish()
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}