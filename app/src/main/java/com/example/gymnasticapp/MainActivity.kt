package com.example.gymnasticapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "GymnasticsApp"
    }

    private val activityHistory = mutableListOf<String>()

    private var score = 0
    private var elementNumber = 1
    private var deductionTaken = false

    private lateinit var scoreText: TextView
    private lateinit var elementText: TextView
    private lateinit var performButton: Button
    private lateinit var deductionButton: Button
    private lateinit var resetButton: Button
    private lateinit var historyText: TextView

    private fun recordAction(message: String) {
        Log.d(TAG, message)

        activityHistory.add(0, message)

        if (activityHistory.size > 50) {
            activityHistory.removeAt(activityHistory.lastIndex)
        }

        historyText.text = activityHistory.joinToString("\n")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreText = findViewById(R.id.scoreText)
        elementText = findViewById(R.id.elementText)
        historyText = findViewById(R.id.historyText)
        performButton = findViewById(R.id.performButton)
        deductionButton = findViewById(R.id.deductionButton)
        resetButton = findViewById(R.id.resetButton)

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("score", 0)
            elementNumber = savedInstanceState.getInt("elementNumber", 1)
            deductionTaken = savedInstanceState.getBoolean("deductionTaken", false)
            savedInstanceState.getStringArrayList("activityHistory")?.let {
                activityHistory.addAll(it)
            }
        }

        historyText.text = if (activityHistory.isEmpty()) {
            getString(R.string.no_actions_yet)
        } else {
            activityHistory.joinToString("\n")
        }

        performButton.setOnClickListener {
            if (elementNumber <= 10 && !deductionTaken) {

                val points = when (elementNumber) {
                    in 1..3 -> 1
                    in 4..7 -> 2
                    in 8..10 -> 3
                    else -> 0
                }

                score = (score + points).coerceAtMost(20)

                recordAction(getString(R.string.history_element_performed, elementNumber, score))

                elementNumber++
                updateDisplay()
            }
        }

        deductionButton.setOnClickListener {
            if (elementNumber in 2..10 && !deductionTaken) {
                score = (score - 2).coerceAtLeast(0)
                deductionTaken = true

                recordAction(getString(R.string.history_deduction_applied, score))

                updateDisplay()
            }
        }

        resetButton.setOnClickListener {
            score = 0
            elementNumber = 1
            deductionTaken = false

            activityHistory.clear()
            recordAction(getString(R.string.history_routine_reset))
            updateDisplay()
        }

        updateDisplay()
    }

    private fun updateDisplay() {
        scoreText.text = score.toString()

        when {
            elementNumber > 10 -> {
                elementText.text = getString(R.string.routine_complete)
                performButton.isEnabled = false
                deductionButton.isEnabled = false
            }

            deductionTaken -> {
                elementText.text = getString(R.string.deduction_taken)
                performButton.isEnabled = false
                deductionButton.isEnabled = false
            }

            else -> {
                elementText.text = getString(R.string.element_number, elementNumber)
                performButton.isEnabled = true
                deductionButton.isEnabled = elementNumber > 1
            }
        }

        scoreText.setTextColor(
            when (elementNumber) {
                in 1..3 -> Color.BLUE
                in 4..7 -> Color.GREEN
                in 8..10 -> Color.rgb(255, 152, 0)
                else -> Color.GRAY
            }
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("score", score)
        outState.putInt("elementNumber", elementNumber)
        outState.putBoolean("deductionTaken", deductionTaken)
        outState.putStringArrayList(
            "activityHistory",
            ArrayList(activityHistory)
        )

        super.onSaveInstanceState(outState)
    }
}
