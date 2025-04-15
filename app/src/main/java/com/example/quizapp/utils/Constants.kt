package com.example.quizapp.utils

import com.example.quizapp.R
import com.example.quizapp.model.Question

object Constants {

    const val USER_NAME ="user_name"
    const val TOTAL_QUESTIONS ="total_questions"
    const val SCORE ="correct_answers"
    fun getQuestions(): MutableList<Question>{
        val questions = mutableListOf<Question>()

        val quest1 = Question(
            1,
            "What country does this flag belong to?",
            R.drawable.italy,
            "Italy",
            "Iran",
            "India",
            "Iceland",
            1
        )
        questions.add(quest1)

        val quest2 = Question(
            2,
            "What country does this flag belong to?",
            R.drawable.argentina,
            "Italy",
            "Iran",
            "India",
            "argentina",
            4
        )
        questions.add(quest2)

        val quest3 = Question(
            3,
            "What country does this flag belong to?",
            R.drawable.brazil,
            "Italy",
            "Iran",
            "brazil",
            "Iceland",
            3
        )
        questions.add(quest3)

        val quest4 = Question(
            4,
            "What country does this flag belong to?",
            R.drawable.finland,
            "Italy",
            "finland",
            "India",
            "Iceland",
            2
        )
        questions.add(quest4)

        val quest5 = Question(
            5,
            "What country does this flag belong to?",
            R.drawable.france,
            "Italy",
            "france",
            "India",
            "Iceland",
            2
        )
        questions.add(quest5)

        val quest6 = Question(
            6,
            "What country does this flag belong to?",
            R.drawable.germany,
            "Italy",
            "Iran",
            "India",
            "germany",
            4
        )
        questions.add(quest6)

        val quest7 = Question(
            7,
            "What country does this flag belong to?",
            R.drawable.india,
            "Italy",
            "Iran",
            "India",
            "Iceland",
            3
        )
        questions.add(quest7)

        val quest8 = Question(
            8,
            "What country does this flag belong to?",
            R.drawable.nigeria,
            "nigeria",
            "Iran",
            "India",
            "Iceland",
            1
        )
        questions.add(quest8)

        val quest9 = Question(
            9,
            "What country does this flag belong to?",
            R.drawable.romania,
            "Italy",
            "Iran",
            "romania",
            "Iceland",
            3
        )
        questions.add(quest9)

        val quest10 = Question(
            10,
            "What country does this flag belong to?",
            R.drawable.spain,
            "Italy",
            "spain",
            "India",
            "Iceland",
            2
        )
        questions.add(quest10)

        return questions
    }
}