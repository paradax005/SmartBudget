package com.example.smartbusget2.ui.util

import android.util.Patterns
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import android.view.View

object Helpers {
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= Constants.MIN_PASSWORD_LENGTH
    }

    fun showError(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    fun showSuccess(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
}

// Extension function for EditText (works with TextInputEditText too since it extends EditText)
fun EditText.getTextString(): String {
    return this.text.toString().trim()
}

