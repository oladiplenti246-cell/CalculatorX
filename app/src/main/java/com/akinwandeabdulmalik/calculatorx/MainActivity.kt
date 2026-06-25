package com.akinwandeabdulmalik.calculatorx

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private lateinit var tvPrevious: TextView
    private lateinit var scientificGrid: android.widget.GridLayout

    private var currentInput = "0"
    private var previousInput = ""
    private var operation = ""
    private var shouldResetDisplay = false
    private var isScientificMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)
        tvPrevious = findViewById(R.id.tvPrevious)
        scientificGrid = findViewById(R.id.scientificGrid)

        setupButtons()
    }

    private fun setupButtons() {
        // Toggle
        findViewById<Button>(R.id.btnToggleScientific).setOnClickListener {
            toggleScientificMode()
        }

        // Scientific buttons
        findViewById<Button>(R.id.btnSin).setOnClickListener { onScientificClick("sin") }
        findViewById<Button>(R.id.btnCos).setOnClickListener { onScientificClick("cos") }
        findViewById<Button>(R.id.btnTan).setOnClickListener { onScientificClick("tan") }
        findViewById<Button>(R.id.btnLog).setOnClickListener { onScientificClick("log") }
        findViewById<Button>(R.id.btnSqrt).setOnClickListener { onScientificClick("sqrt") }
        findViewById<Button>(R.id.btnPow).setOnClickListener { onScientificClick("pow") }
        findViewById<Button>(R.id.btnPi).setOnClickListener { onPiClick() }
        findViewById<Button>(R.id.btnFactorial).setOnClickListener { onFactorialClick() }

        // Number buttons
        val numberButtons = mapOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2", R.id.btn3 to "3",
            R.id.btn4 to "4", R.id.btn5 to "5", R.id.btn6 to "6", R.id.btn7 to "7",
            R.id.btn8 to "8", R.id.btn9 to "9"
        )

        numberButtons.forEach { (id, digit) ->
            findViewById<Button>(id).setOnClickListener { onNumberClick(digit) }
        }

        // Operation buttons
        findViewById<Button>(R.id.btnPlus).setOnClickListener { onOperationClick("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { onOperationClick("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperationClick("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperationClick("/") }

        // Function buttons
        findViewById<Button>(R.id.btnDot).setOnClickListener { onDotClick() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { onClear() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { onBackspace() }
        findViewById<Button>(R.id.btnSign).setOnClickListener { onSign() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { onPercent() }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEquals() }
    }

    private fun toggleScientificMode() {
        isScientificMode = !isScientificMode
        scientificGrid.visibility = if (isScientificMode) android.view.View.VISIBLE else android.view.View.GONE
        val toggleBtn = findViewById<Button>(R.id.btnToggleScientific)
        toggleBtn.text = if (isScientificMode) "Simple Mode" else "Scientific Mode"
    }

    private fun onScientificClick(func: String) {
        if (currentInput == "Error") return
        try {
            val value = currentInput.toDouble()
            val result = when (func) {
                "sin" -> Math.sin(Math.toRadians(value))
                "cos" -> Math.cos(Math.toRadians(value))
                "tan" -> Math.tan(Math.toRadians(value))
                "log" -> Math.log10(value)
                "sqrt" -> Math.sqrt(value)
                "pow" -> value * value
                else -> value
            }
            currentInput = if (result.isNaN() || result.isInfinite()) {
                "Error"
            } else {
                BigDecimal(result).stripTrailingZeros().toPlainString()
            }
            shouldResetDisplay = true
            updateDisplay()
        } catch (e: Exception) {
            currentInput = "Error"
            updateDisplay()
        }
    }

    private fun onPiClick() {
        currentInput = Math.PI.toString()
        shouldResetDisplay = true
        updateDisplay()
    }

    private fun onFactorialClick() {
        if (currentInput == "Error") return
        try {
            val value = currentInput.toDouble().toInt()
            if (value < 0 || value > 20) { // Limit for simplicity and precision
                currentInput = "Error"
            } else {
                var fact: Long = 1
                for (i in 1..value) fact *= i
                currentInput = fact.toString()
            }
            shouldResetDisplay = true
            updateDisplay()
        } catch (e: Exception) {
            currentInput = "Error"
            updateDisplay()
        }
    }

    private fun onNumberClick(digit: String) {
        if (shouldResetDisplay || currentInput == "0" || currentInput == "Error") {
            currentInput = digit
            shouldResetDisplay = false
        } else {
            currentInput += digit
        }
        updateDisplay()
    }

    private fun onDotClick() {
        if (shouldResetDisplay || currentInput == "Error") {
            currentInput = "0."
            shouldResetDisplay = false
        } else if (!currentInput.contains(".")) {
            currentInput += "."
        }
        updateDisplay()
    }

    private fun onOperationClick(op: String) {
        if (currentInput == "Error") return

        if (operation.isNotEmpty() && !shouldResetDisplay) {
            onEquals()
            if (currentInput == "Error") return
        }
        
        previousInput = currentInput
        operation = op
        shouldResetDisplay = true
        updatePrevious()
        updateDisplay()
    }

    private fun onEquals() {
        if (previousInput.isEmpty() || operation.isEmpty() || currentInput == "Error") return

        val result = calculate(previousInput, currentInput, operation)
        currentInput = result
        previousInput = ""
        operation = ""
        shouldResetDisplay = true
        updateDisplay()
        updatePrevious()
    }

    private fun onClear() {
        currentInput = "0"
        previousInput = ""
        operation = ""
        shouldResetDisplay = false
        updateDisplay()
        updatePrevious()
    }

    private fun onBackspace() {
        if (currentInput == "Error" || shouldResetDisplay) {
            currentInput = "0"
            shouldResetDisplay = false
        } else if (currentInput.length > 1) {
            currentInput = currentInput.dropLast(1)
            if (currentInput == "-") currentInput = "0"
        } else {
            currentInput = "0"
        }
        updateDisplay()
    }

    private fun onSign() {
        if (currentInput == "Error" || currentInput == "0") return
        try {
            currentInput = if (currentInput.startsWith("-")) {
                currentInput.substring(1)
            } else {
                "-$currentInput"
            }
            updateDisplay()
        } catch (e: Exception) {
            // Invalid input
        }
    }

    private fun onPercent() {
        if (currentInput == "Error") return
        try {
            val value = BigDecimal(currentInput)
            // Use 8 decimal places for percent to avoid extreme precision issues but keep accuracy
            currentInput = value.divide(BigDecimal("100"), 8, java.math.RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString()
            updateDisplay()
        } catch (e: Exception) {
            currentInput = "Error"
            updateDisplay()
        }
    }

    private fun calculate(prev: String, current: String, op: String): String {
        return try {
            val prevValue = BigDecimal(prev)
            val currentValue = BigDecimal(current)

            val result = when (op) {
                "+" -> prevValue.add(currentValue)
                "-" -> prevValue.subtract(currentValue)
                "*" -> prevValue.multiply(currentValue)
                "/" -> {
                    if (currentValue.compareTo(BigDecimal.ZERO) == 0) {
                        return "Error"
                    }
                    prevValue.divide(currentValue, 10, java.math.RoundingMode.HALF_UP)
                }
                else -> currentValue
            }

            result.stripTrailingZeros().toPlainString()
        } catch (e: Exception) {
            "Error"
        }
    }

    private fun updateDisplay() {
        if (currentInput == "Error" || currentInput == "Cannot ÷ by 0") {
            tvDisplay.text = currentInput
        } else {
            tvDisplay.text = formatNumber(currentInput)
        }
    }

    private fun updatePrevious() {
        if (previousInput.isEmpty()) {
            tvPrevious.text = ""
        } else {
            val formattedPrev = formatNumber(previousInput)
            val displayOp = when (operation) {
                "*" -> "×"
                "/" -> "÷"
                else -> operation
            }
            tvPrevious.text = "$formattedPrev $displayOp"
        }
    }

    private fun formatNumber(value: String): String {
        if (value == "Error" || value.isEmpty()) return value
        return try {
            val parts = value.split(".")
            val integerPart = parts[0]
            val decimalPart = if (parts.size > 1) "." + parts[1] else ""

            if (integerPart == "-" || integerPart.isEmpty()) {
                integerPart + decimalPart
            } else {
                val number = BigDecimal(integerPart)
                val formatter = DecimalFormat("#,###")
                formatter.format(number) + decimalPart
            }
        } catch (e: Exception) {
            value
        }
    }
}