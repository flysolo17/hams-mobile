package com.bryll.hams.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DateEditText : AppCompatEditText {
    private var dateFormat: SimpleDateFormat? = null
    var date: Date? = null
        private set

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        addTextChangedListener(dateTextWatcher)
    }

    private val dateTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
        }

        override fun afterTextChanged(editable: Editable) {
            val inputText = editable.toString().trim { it <= ' ' }
            validateDateFormat(inputText)
        }
    }

    private fun validateDateFormat(inputText: String) {
        try {
            date = dateFormat!!.parse(inputText)
            val formattedDate = dateFormat!!.format(date)
            if (inputText != formattedDate) {
                setText(formattedDate)
                setSelection(formattedDate.length)
            }
        } catch (e: ParseException) {
            // Date format is incorrect, handle the error if needed
            error = "Invalid date"
        }
    }

    val formattedDate: String
        get() = dateFormat!!.format(date)
}