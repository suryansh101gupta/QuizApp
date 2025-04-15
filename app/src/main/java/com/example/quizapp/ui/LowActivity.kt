package com.example.quizapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizapp.MainActivity
import com.example.quizapp.R
import com.example.quizapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LowActivity : AppCompatActivity() {

    private lateinit var textViewScore: TextView
    private lateinit var textViewName: TextView
    private lateinit var homeButton: Button

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_low)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textViewScore = findViewById(R.id.tv_score)
        textViewName = findViewById(R.id.tv_name)
        homeButton = findViewById(R.id.btn_home)

        val scoreTextView = findViewById<TextView>(R.id.tv_score)
        val resultsTextView = findViewById<TextView>(R.id.tv_prev_score)


        val totalQuestions = intent.getIntExtra(Constants.TOTAL_QUESTIONS, 0)
        val score = intent.getIntExtra(Constants.SCORE,0)
        val name = intent.getStringExtra(Constants.USER_NAME)

        textViewScore.text = "Your Score is $score out of $totalQuestions"
        textViewName.text = name

        homeButton.setOnClickListener{
            Intent(this, MainActivity::class.java).also{
                startActivity(it)
            }
        }

        if (userId != null) {
            val scoreData = hashMapOf(
                "score" to score,
                "timestamp" to FieldValue.serverTimestamp()
            )

            db.collection("users")
                .document(userId)
                .collection("attempts")
                .add(scoreData) // .add() generates a unique document ID
                .addOnSuccessListener {
                    Toast.makeText(this, "Score saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save score", Toast.LENGTH_SHORT).show()
                }

            db.collection("users")
                .document(userId)
                .collection("attempts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    val scores = StringBuilder()
                    val totalAttempts = documents.size()
                    for ((index, doc) in documents.withIndex()) {
                        val score = doc.getLong("score") ?: 0
                        val time = doc.getTimestamp("timestamp")?.toDate()
                        val attemptNumber = totalAttempts - index
                        scores.append("Attempt $attemptNumber: $score (${time})\n")
                    }
                    resultsTextView.text = scores.toString()
                }
                .addOnFailureListener {
                    resultsTextView.text = "Error loading scores."
                }

//            val uid = FirebaseAuth.getInstance().currentUser?.uid
//            val db = FirebaseFirestore.getInstance()
//
//            uid?.let {
//                db.collection("users").document(it).get()
//                    .addOnSuccessListener { document ->
//                        if (document != null && document.exists()) {
//                            val name = document.getString("name")
//                            val nameTextView = findViewById<TextView?>(R.id.tv_name)
//                            if (nameTextView != null) {
//                                nameTextView.text = "Hello $name! Congratulations!"
//                            } else {
//                                Log.e("ResultActivity", "tv_name TextView is null!")
//                            }
//                        }
//                    }
//                    .addOnFailureListener { e ->
//                        Log.e("Firestore", "Failed to fetch name", e)
//                    }
//            }
        }
    }
}