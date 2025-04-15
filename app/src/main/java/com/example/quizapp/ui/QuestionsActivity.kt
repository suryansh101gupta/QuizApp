package com.example.quizapp.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizapp.Login
import com.example.quizapp.R
import com.example.quizapp.model.Question
import com.example.quizapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class QuestionsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewProgress: TextView
    private lateinit var textViewQuestion: TextView
    private lateinit var image: ImageView

    private lateinit var textViewOptionOne: TextView
    private lateinit var textViewOptionTwo: TextView
    private lateinit var textViewOptionThree: TextView
    private lateinit var textViewOptionFour: TextView

    private lateinit var checkButton: Button

    private var questionsCounter = 1
    private lateinit var questionsList: MutableList<Question>
    private var selectedAnswer = 0
    private lateinit var currentQuestion: Question
    private var answered = false

    private var selectedOptionPosition = 0

    private lateinit var name:String
    private var score = 0

    private lateinit var auth: FirebaseAuth
    private lateinit var button: Button

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val db = FirebaseFirestore.getInstance()

    val data = hashMapOf(
        "score" to score,
        "timestamp" to FieldValue.serverTimestamp()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_question)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = findViewById(R.id.progress_bar)
        textViewProgress = findViewById(R.id.text_view_progress)
        textViewQuestion = findViewById(R.id.question_text_view)
        image = findViewById(R.id.image_flag)

        textViewOptionOne = findViewById(R.id.text_view_option_1)
        textViewOptionTwo = findViewById(R.id.text_view_option_2)
        textViewOptionThree = findViewById(R.id.text_view_option_3)
        textViewOptionFour = findViewById(R.id.text_view_option_4)

        textViewOptionOne.setOnClickListener(this)
        textViewOptionTwo.setOnClickListener(this)
        textViewOptionThree.setOnClickListener(this)
        textViewOptionFour.setOnClickListener(this)

        checkButton = findViewById(R.id.button_check)
        checkButton.setOnClickListener(this)

        auth = FirebaseAuth.getInstance()
        button = findViewById(R.id.btn_logout)

        button.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
            Intent(applicationContext, Login::class.java).also{
                startActivity(it)
                finish()
            }
        }

        questionsList = Constants.getQuestions()
        Log.d("QuestionSize", "${questionsList.size}")

        showNextQuestion()

        if(intent.hasExtra(Constants.USER_NAME)){
            name = intent.getStringExtra(Constants.USER_NAME)!!
        }

        if (userId != null) {
            db.collection("users").document(userId)
                .set(data, SetOptions.merge())  // merge if user doc already exists
                .addOnSuccessListener {
                    Log.d("Firestore", "Score saved successfully")
                }
                .addOnFailureListener {
                    Log.e("Firestore", "Failed to save score: ${it.message}")
                }
        }

    }


    private fun showNextQuestion(){



        if(questionsCounter < questionsList.size + 1){
            checkButton.text = "CHECK"
            currentQuestion = questionsList[questionsCounter-1]

            resetOptions()
            answered = false  // Reset answered flag
            selectedAnswer = 0

            val question = questionsList[questionsCounter - 1]
            image.setImageResource(question.image)
            progressBar.progress = questionsCounter
            textViewProgress.text = "$questionsCounter/${progressBar.max}"
            textViewQuestion.text = question.question
            textViewOptionOne.text = question.optionOne
            textViewOptionTwo.text = question.optionTwo
            textViewOptionThree.text = question.optionThree
            textViewOptionFour.text = question.optionFour
        }else if(questionsCounter >= questionsList.size ){
            checkButton.text = "SUBMIT"
            if(score>5) {
                Intent(this, ResultActivity::class.java).also {
                    it.putExtra(Constants.USER_NAME, name)
                    it.putExtra(Constants.SCORE, score)
                    it.putExtra(Constants.TOTAL_QUESTIONS, questionsList.size)

                    startActivity(it)
                }
            }else{
                Intent(this, LowActivity::class.java).also {
                    it.putExtra(Constants.USER_NAME, name)
                    it.putExtra(Constants.SCORE, score)
                    it.putExtra(Constants.TOTAL_QUESTIONS, questionsList.size)

                    startActivity(it)
                }
            }
        }

        questionsCounter++
    }

    private fun resetOptions(){
        val options = mutableListOf<TextView>()
        options.add(textViewOptionOne)
        options.add(textViewOptionTwo)
        options.add(textViewOptionThree)
        options.add(textViewOptionFour)

        for(option in options){
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this,
                R.drawable.default_option_border_bg
            )
        }
    }

    private fun selectedOption(textView: TextView, selectedOptionNumber: Int){
        resetOptions()

        selectedOptionPosition = selectedOptionNumber
        selectedAnswer = selectedOptionNumber
        textView.setTextColor(Color.parseColor("#363A43"))
        textView.setTypeface(textView.typeface, Typeface.BOLD)
        textView.background = ContextCompat.getDrawable(
            this,
            R.drawable.selected_option_border_bg
        )
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.text_view_option_1 -> {
                selectedOption(textViewOptionOne, 1)
            }
            R.id.text_view_option_2 -> {
                selectedOption(textViewOptionTwo, 2)
            }
            R.id.text_view_option_3 -> {
                selectedOption(textViewOptionThree, 3)
            }
            R.id.text_view_option_4 -> {
                selectedOption(textViewOptionFour, 4)
            }
            R.id.button_check -> {
                if(!answered){
                    checkAnswer()
                }else{
                    showNextQuestion()
                }
            }
        }
    }

    private fun checkAnswer() {
        answered = true
        if(selectedAnswer == currentQuestion.correctAnswer){
            score++
            when(selectedAnswer){
                1 -> {
                    textViewOptionOne.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.correct_option_border_bg
                    )
                }
                2 -> {
                    textViewOptionTwo.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.correct_option_border_bg
                    )
                }
                3 -> {
                    textViewOptionThree.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.correct_option_border_bg
                    )
                }
                4 -> {
                    textViewOptionFour.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.correct_option_border_bg
                    )
                }
            }
        }else{
            when(selectedAnswer){
                1 -> {
                    textViewOptionOne.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.wrong_option_border_bg
                    )
                }
                2 -> {
                    textViewOptionTwo.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.wrong_option_border_bg
                    )
                }
                3 -> {
                    textViewOptionThree.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.wrong_option_border_bg
                    )
                }
                4 -> {
                    textViewOptionFour.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.wrong_option_border_bg
                    )
                }
            }
        }
        if (questionsCounter >= questionsList.size) {
            checkButton.text = "SUBMIT"
        } else {
            checkButton.text = "NEXT"
        }
        showSolution()
    }

    private fun showSolution() {
        selectedAnswer = currentQuestion.correctAnswer

        when(selectedAnswer){
            1 -> {
                textViewOptionOne.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.correct_option_border_bg
                )
            }
            2 -> {
                textViewOptionTwo.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.correct_option_border_bg
                )
            }
            3 -> {
                textViewOptionThree.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.correct_option_border_bg
                )
            }
            4 -> {
                textViewOptionFour.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.correct_option_border_bg
                )
            }
        }
    }

//    private fun highlightAnswer(answer:Int){
//
//    }


}