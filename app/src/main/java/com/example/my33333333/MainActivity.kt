@file:Suppress("PLUGIN_WARNING")

package com.example.easycalc

import android.content.Context
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.graphics.Color.parseColor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.view.postOnAnimationDelayed
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.Math.sqrt
import java.text.DateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.atan
import kotlin.math.tan
import java.time.LocalTime as LocalTime1

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_main)
        //supportActionBar?.hide()    //removed title bar
    }

    val calculatorStateOff: String = "SWITCHED OFF"
    val calculatorStateOn: String = "WORKING"
    val helpPopupTitle: String = "HELP"
    val helpPopupText: String = "CLS - CLEAR" + "\n" + "DEL - DELETE" + "\n" + "F - SECOND GRADE" + "\n" + "HST - OPERATING HISTORY" + "\n" + "STK - CURRENT STACK" + "\n" + "CM - CLEANUP MEMORY" + "\n" + "RND - RANDOM NUMBER"
    val showStackActiveRegisterX: String = "ACTIVE REGISTER: X"
    val showStackActiveRegisterY: String = "ACTIVE REGISTER: Y"
    val showStackInputModeOperation: String = "MODE: OPERATION"
    val showStackInputModeDigit: String = "MODE: NUMBER"
    val showStackMemoryLabel: String = "MEMORY: "
    val showStackLastOperation: String = "LAST OPERATION: "
    val limitReached: String = "LIMIT REACHED"
    val registerXStamp: String = "REGISTER Х "
    val registerYStamp: String = "REGISTER Y "



    var RegX: Float = 0f
    var RegY: Float = 0f
    var RegP: Float = 0f
    var currentOperation: String = ""
    var isRegX: Boolean = true
    var isOperationOn: Boolean = false
    var isSecondGradeOn: Boolean = false
    var isEqualPressed: Boolean = false
    var HistoryStack: Array<String> = Array(6, {""})

    fun clearStack (view: View){
        RegX = 0f
        RegY = 0f
        currentOperation = ""
        isRegX = true
        isOperationOn = false
        isSecondGradeOn = false
        isEqualPressed = false
        idFunctionalButtonLabel.setTextColor(parseColor("#37474f"))
        button_functional.setBackgroundColor(parseColor("#C6AE1F"))
    }

    fun generateVibration (view: View, vibrationLength: Long) {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val isVibratorOn: Boolean = vibrator.hasVibrator()
        if (isVibratorOn) vibrator.vibrate(VibrationEffect.createOneShot(vibrationLength, VibrationEffect.DEFAULT_AMPLITUDE)) else vibrator.vibrate(vibrationLength)
    }

    private fun checkCurrentState (view: View) : Boolean{       //return true if device is ON
        if (IndTextView.text.toString() == "") {
            val myToast = Toast.makeText(this, calculatorStateOff, Toast.LENGTH_SHORT)
            myToast.show()
            return false
        }
        else return true
    }

    fun turnOnOff(view: View) {
        val toastValue :String
        clearStack(view)
        generateVibration (view, 500)
        HistoryStack = Array(6, {""})
        if (IndTextView.text.toString() == "") {
            IndTextView.text = "0"
            toastValue = calculatorStateOn
        }
        else{
            IndTextView.text = ""
            HistoryTextView.text = ""
            toastValue = calculatorStateOff
        }
        val myToast = Toast.makeText(this, toastValue, Toast.LENGTH_SHORT)
        myToast.show()
    }

    fun AppendHistory (view: View, RegXi: String, RegYi: String, iOperation: String) {
        var timestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        timestamp = timestamp.subSequence(12, 19).toString()
        HistoryStack[5] = HistoryStack[4]
        HistoryStack[4] = HistoryStack[3]
        HistoryStack[3] = HistoryStack[2]
        HistoryStack[2] = HistoryStack[1]
        HistoryStack[1] = HistoryStack[0]
        when (iOperation){
            "²"->HistoryStack[0] = timestamp + "     " + RegXi + RegYi + " " + iOperation
            "√"->HistoryStack[0] = timestamp + "     " + iOperation + " " + RegXi + RegYi
            "π"->HistoryStack[0] = timestamp + "     " + iOperation + " "
            "!"->HistoryStack[0] = timestamp + "     " + RegXi + RegYi + " " + iOperation
            "1/"->HistoryStack[0] = timestamp + "     " + iOperation + RegXi + RegYi
            "lg"->HistoryStack[0] = timestamp + "     " + iOperation + " " + RegXi + RegYi
            "ln"->HistoryStack[0] = timestamp + "     " + iOperation + " " + RegXi + RegYi
            "sin"->HistoryStack[0] = timestamp + "     " + iOperation + " " + RegXi + RegYi
            //"sin"->HistoryStack[0] = timestamp + "     " + iOperation + " " + RegXi + RegYi
            "cos"->HistoryStack[0] = timestamp + "     " + iOperation + " " + RegXi + RegYi
            "tg"->HistoryStack[0] = timestamp + "     " + iOperation + " " + RegXi + RegYi
            "arcsin"->HistoryStack[0] = timestamp + "     " + iOperation + " " + RegXi + RegYi
            "arccos"->HistoryStack[0] = timestamp + "     " + iOperation + " " + RegXi + RegYi
            "arctg"->HistoryStack[0] = timestamp + "     " + iOperation + " " + RegXi + RegYi
            else->HistoryStack[0] = timestamp + "     " + RegXi + " " + iOperation + " " + RegYi
        }
    }

    private fun displayCurrentValue (view:View, currentValue: String):String {
        var finalValue:String = currentValue.toString()
        if (finalValue == "Infinity") finalValue = "∞"
        if (finalValue == "-Infinity") finalValue = "-∞"
        if (finalValue == "NaN") finalValue = "0÷0"
        if (currentValue.subSequence(currentValue.lastIndex - 1, currentValue.lastIndex + 1) == ".0") finalValue = currentValue.dropLast(2)
        if (currentValue.length > 9) {if (currentValue.contains("E")) finalValue = currentValue.subSequence(0, 3).toString() + "..." + currentValue.subSequence(currentValue.lastIndex-3, currentValue.lastIndex + 1).toString() else finalValue = currentValue.dropLast(currentValue.length - 9) }
        return finalValue
    }

    fun PutToCurrentRegister (view: View, calculatedValueX: Float, calculatedValueY: Float, usedOperation: String){
        generateVibration (view, 100)
        if(isRegX){
            AppendHistory(view, RegX.toString(), "", usedOperation)
            RegX = calculatedValueX
            HistoryStack[0] = HistoryStack[0] + " = " + RegX.toString()
            IndTextView.text = displayCurrentValue(view, RegX.toString())
        } else {
            AppendHistory(view, "", RegY.toString(), usedOperation)
            RegY = calculatedValueY
            HistoryStack[0] = HistoryStack[0] + " = " + RegY.toString()
            IndTextView.text = displayCurrentValue(view, RegY.toString())
        }
    }

    fun calculateFactorial(view: View, ValueOfCurrentRegister: Float): Float {
        var factorialValue: Float = 1f
        if (ValueOfCurrentRegister < 35f){
            if (ValueOfCurrentRegister != 0f) for (factorialStep in 1..ValueOfCurrentRegister.toInt()) factorialValue *= factorialStep
        }
        else factorialValue = Float.POSITIVE_INFINITY
        return factorialValue
    }

    fun AnimateIndicator (view: View) {
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 100
        anim.repeatCount = 1
        anim.repeatMode = Animation.REVERSE
        IndTextView.startAnimation (anim)
    }

    fun pushButtonClear (view: View){         //for button Clear
        if (checkCurrentState(view)) {
            generateVibration (view, 200)
            IndTextView.text = "0"
            clearStack(view)
        }
    }

    fun pushButtonDelete (view: View) {   //for button Delete
        if (checkCurrentState(view)) {
            generateVibration (view, 100)
            if (IndTextView.text.toString() == "∞") {
                IndTextView.text = "0"
                if (isRegX) RegX = 0F else RegY = 0F
            }
            if (IndTextView.text.toString() == "0÷0") {
                IndTextView.text = "0"
                if (isRegX) RegX = 0F else RegY = 0F
            }
            if (IndTextView.text.toString() == "0") {
                val myToast = Toast.makeText(this, limitReached, Toast.LENGTH_SHORT)
                myToast.show()
                if (isRegX) RegX = 0F else RegY = 0F
            }
            else {
                if (IndTextView.text.length > 1){
                    if (IndTextView.text.toString().contains("E")) {
                        val myToast = Toast.makeText(this, limitReached, Toast.LENGTH_SHORT)
                        myToast.show()
                    }
                    if (IndTextView.text.toString().contains("...")) {
                        val myToast = Toast.makeText(this, limitReached, Toast.LENGTH_SHORT)
                        myToast.show()
                    }
                    else {
                        if (IndTextView.text.toString().contains("-")) {
                                if (IndTextView.text.length == 2) {
                                    IndTextView.text = "0"
                                    if (isRegX) RegX = 0F else RegY = 0F
                                }
                                else {
                                    IndTextView.text = IndTextView.text.dropLast(1)
                                    if (isRegX) RegX = IndTextView.text.toString().toFloat() else RegY = IndTextView.text.toString().toFloat()
                                }
                            }
                        else {
                            IndTextView.text = IndTextView.text.dropLast(1)
                            if (isRegX) RegX = IndTextView.text.toString().toFloat() else RegY = IndTextView.text.toString().toFloat()
                        }
                    }
                } else {
                    IndTextView.text = "0"
                    if (isRegX) RegX = 0F else RegY = 0F
            }
        }
    }
    }

    fun pushButtonF (view: View) {
        if (checkCurrentState(view)) {
            generateVibration (view, 100)
            if (isSecondGradeOn) {
                isSecondGradeOn = false
                idFunctionalButtonLabel.setTextColor(parseColor("#37474f"))
                view.button_functional.setBackgroundColor(parseColor("#C6AE1F"))
            } else {
                isSecondGradeOn = true
                idFunctionalButtonLabel.setTextColor(parseColor("#e53935"))
                view.button_functional.setBackgroundColor(parseColor("#e53935"))
            }
        }
    }

    fun pushButtonEqual (view: View){
        if (checkCurrentState(view)){
            //generateVibration (view, 100)
            AnimateIndicator(view)
            if (!isRegX) {
                AppendHistory(view, RegX.toString(), RegY.toString(), currentOperation)
                RegY = IndTextView.text.toString().toFloat()
                when (currentOperation){
                    "+" -> RegX += RegY
                    "-" -> RegX -= RegY
                    "*" -> RegX *= RegY
                    "/" -> RegX /= RegY
                }
                HistoryStack[0] = HistoryStack[0] + " = " + RegX.toString()
                IndTextView.text = displayCurrentValue(view, RegX.toString())
                if (isEqualPressed) {
                    isOperationOn = true
                    isRegX = true
                    RegY = 0f
                } else {
                    isOperationOn = true
                    isRegX = false
                }
            }
        }
    }

    private fun pushButtonX (view: View, X: String) {   //for numeric buttons
        if (checkCurrentState(view)) {
            generateVibration (view, 100)
            if (IndTextView.text.toString() == "∞") IndTextView.text = "0"
            if (IndTextView.text.toString() == "0÷0") IndTextView.text = "0"

              if (IndTextView.text.toString() == "0"){
                if (X == ".") {
                    if (isOperationOn){
                        IndTextView.text = "0."
                        isOperationOn = false
                    }
                    else {
                        IndTextView.text = IndTextView.text.toString() + X
                    }
                } else {
                    IndTextView.text = X
                    isOperationOn = false
                }
                if (isRegX) RegX = IndTextView.text.toString().toFloat() else RegY = IndTextView.text.toString().toFloat()
            }
              else {
                if (IndTextView.text.length < 8) {
                    if (!isOperationOn){
                        IndTextView.text = IndTextView.text.toString() + X
                    } else {
                        IndTextView.text = X
                        isOperationOn = false
                    }
                } else {
                    if (!isOperationOn) {
                        val myToast = Toast.makeText(this, limitReached, Toast.LENGTH_SHORT)
                        myToast.show()
                    }
                    else {
                        IndTextView.text = X
                        isOperationOn = false
                    }
                }
            }
            if (isRegX) RegX = IndTextView.text.toString().toFloat() else RegY = IndTextView.text.toString().toFloat()
        }
    }

    private fun pushButtonY (view: View, Y: String){
            if (checkCurrentState(view)) {
                AnimateIndicator(view)
//                generateVibration (view, 100)
                if (!isOperationOn) {
                if (isRegX) {
                    when (IndTextView.text.toString()){
                        "0÷0"->RegX = Float.NaN
                        "∞"->RegX = Float.POSITIVE_INFINITY
                        //else->RegX = IndTextView.text.toString().toFloat()
                    }
                    isRegX = false
                    currentOperation = Y
                    isOperationOn = true
                } else {
                        pushButtonEqual(view)
                        currentOperation = Y
                }
        }
            else {
                if (isRegX) isRegX = false
                currentOperation = Y
            }
    }
    }

    fun pushButtonOne(view: View) {pushButtonX(view, "1")}
    fun pushButtonTwo(view: View) {pushButtonX(view, "2")}
    fun pushButtonThree(view: View) {pushButtonX(view, "3")}
    fun pushButtonFour(view: View) {pushButtonX(view, "4")}
    fun pushButtonFive(view: View) {pushButtonX(view, "5")}
    fun pushButtonSix(view: View) {pushButtonX(view, "6")}
    fun pushButtonSeven(view: View) {pushButtonX(view, "7")}
    fun pushButtonEight(view: View) {pushButtonX(view, "8")}
    fun pushButtonNine(view: View) {pushButtonX(view, "9")}
    fun pushButtonZero(view: View) {pushButtonX(view, "0")}
    fun pushButtonDot(view: View) {if (!(IndTextView.text.toString().contains("."))) pushButtonX(view, ".")}

    fun pushButtonPlus(view: View) {pushButtonY(view, "+")}
    fun pushButtonMinus(view: View) {pushButtonY(view, "-")}
    fun pushButtonMultiple(view: View) {pushButtonY(view, "*")}
    fun pushButtonDivision(view: View) {pushButtonY(view, "/")}

    fun pushButtonPhysicalEqual(view: View) {
        isEqualPressed = true
        pushButtonEqual(view)
    }

    fun pushButtonSign(view: View) {
        if (checkCurrentState(view)) {
            generateVibration (view, 100)
            if (!(IndTextView.text.toString() == "0")) {
                if (isRegX) {
                    RegX *= -1
                    IndTextView.text = displayCurrentValue(view, RegX.toString())
                } else {
                    if (!isOperationOn){
                        RegY *= -1
                        IndTextView.text = displayCurrentValue(view, RegY.toString())

                    }
                    else {
                        RegX *= -1
                        IndTextView.text = displayCurrentValue(view, RegX.toString())
                    }
                }
            }
        }
    }

    fun pushButtonXtoMemory(view: View){
        if (checkCurrentState(view)) {
            generateVibration (view, 100)
            if (isRegX) RegP = RegX else RegP = RegY
        }
        }

    fun pushButtonMemorytoX(view: View){
        if (checkCurrentState(view)) {
            generateVibration (view, 100)
            if (isRegX) RegX = RegP else RegY = RegP
            IndTextView.text = displayCurrentValue(view, RegP.toString())
        }
    }

    fun pushButtonPplus(view: View){
        if (checkCurrentState(view)) {
            generateVibration (view, 100)
            if (isRegX) RegP += RegX else RegP += RegY
        }
    }

    fun pushButtonCancelMemory(view: View){
        if (checkCurrentState(view)) {
            generateVibration (view, 100)
            if(!isSecondGradeOn){
                RegP = 0f
            } else{
                if (isRegX) {
                    val Buffer: String = Math.random().toFloat().toString()
                    RegX = Buffer.dropLast(3).toFloat()
                    IndTextView.text = displayCurrentValue(view, RegX.toString())
                } else {
                    RegY = Math.random().toFloat()
                    IndTextView.text = displayCurrentValue(view, RegY.toString())
                }
            }
        }
    }

    fun pushButtonPercent(view: View){if (checkCurrentState(view)) if(!isSecondGradeOn) PutToCurrentRegister(view, RegX/100, RegY*100f/RegX, "%") else PutToCurrentRegister(view, Math.PI.toFloat(), Math.PI.toFloat(), "π")}
    fun pushButtonSqrt(view: View){if (checkCurrentState(view)) if(!isSecondGradeOn) PutToCurrentRegister(view, sqrt(RegX.toDouble()).toFloat(), sqrt(RegY.toDouble()).toFloat(), "√") else PutToCurrentRegister(view, RegX*RegX, RegY*RegY, "²")}

    fun pushButtonXY(view: View){
        if (checkCurrentState(view)) {
            generateVibration (view, 100)
            if(!isSecondGradeOn){
                if(isRegX){
                    RegX = RegY.also { RegY = RegX }
                    IndTextView.text = displayCurrentValue(view, RegX.toString())
                } else {
                    RegY = RegX.also { RegX = RegY }
                    IndTextView.text = displayCurrentValue(view, RegY.toString())
                }
            } else{
                if (isRegX) {
                    RegX = RegP.also { RegP = RegX }
                    IndTextView.text = displayCurrentValue(view, RegX.toString())
                } else {
                    RegY = RegP.also { RegP = RegY }
                    IndTextView.text = displayCurrentValue(view, RegY.toString())
                }
            }
        }
    }

    fun pushButton1X(view: View){if (checkCurrentState(view)) if(!isSecondGradeOn) PutToCurrentRegister(view, 1/RegX, 1/RegY, "1/") else PutToCurrentRegister(view, calculateFactorial(view, RegX), calculateFactorial(view, RegY), "!")}
    fun pushButtonLg(view: View){if (checkCurrentState(view)) if(!isSecondGradeOn) PutToCurrentRegister(view, Math.log10(RegX.toDouble()).toFloat(), Math.log10(RegY.toDouble()).toFloat(), "lg") else PutToCurrentRegister(view, Math.log(RegX.toDouble()).toFloat(), Math.log(RegY.toDouble()).toFloat(), "ln")}
    fun pushButtonSin(view: View){if (checkCurrentState(view)) if(!isSecondGradeOn) PutToCurrentRegister(view, Math.sin(RegX.toDouble()).toFloat(), Math.sin(RegY.toDouble()).toFloat(), "sin") else PutToCurrentRegister(view, Math.asin(RegX.toDouble()).toFloat(), Math.asin(RegY.toDouble()).toFloat(), "arcsin")}
    fun pushButtonCos(view: View){if (checkCurrentState(view)) if(!isSecondGradeOn) PutToCurrentRegister(view, Math.cos(RegX.toDouble()).toFloat(), Math.cos(RegY.toDouble()).toFloat(), "cos") else PutToCurrentRegister(view, Math.acos(RegX.toDouble()).toFloat(), Math.acos(RegY.toDouble()).toFloat(), "arccos")}
    fun pushButtonTan(view: View){if (checkCurrentState(view)) if(!isSecondGradeOn) PutToCurrentRegister(view, Math.tan(RegX.toDouble()).toFloat(), Math.tan(RegY.toDouble()).toFloat(), "tg") else PutToCurrentRegister(view, Math.atan(RegX.toDouble()).toFloat(), Math.atan(RegY.toDouble()).toFloat(), "arctg")}

    fun pushButtonHelp (view: View) {
        if (checkCurrentState(view)) {
            generateVibration (view, 100)
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(helpPopupTitle)
            builder.setMessage(helpPopupText)
            builder.show()
        }
    }

    fun showStack(view: View){
        if (checkCurrentState(view)) {
            generateVibration (view, 100)
            if (!isSecondGradeOn) {
                val activeRegister: String
                val inputMode: String
                if (isRegX) activeRegister = showStackActiveRegisterX else activeRegister = showStackActiveRegisterY
                if (isOperationOn) inputMode = showStackInputModeOperation else inputMode = showStackInputModeDigit
                HistoryTextView.text = registerXStamp + RegX.toString() + "\n" + registerYStamp + RegY.toString() + "\n" + showStackMemoryLabel + RegP.toString() + "\n" + showStackLastOperation + currentOperation + "\n" + activeRegister + "\n" + inputMode
            }
            else HistoryTextView.text = HistoryStack[0] + "\n" + HistoryStack[1] + "\n" + HistoryStack[2]+ "\n" + HistoryStack[3]+ "\n" + HistoryStack[4]+ "\n" + HistoryStack[5]
        }
    }
}