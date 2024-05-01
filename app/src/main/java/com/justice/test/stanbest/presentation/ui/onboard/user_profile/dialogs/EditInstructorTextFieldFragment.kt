package com.justice.test.stanbest.presentation.ui.onboard.user_profile.dialogs

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import com.justice.test.stanbest.R
import com.justice.test.stanbest.databinding.FragmentEditInstructorTextFieldBinding
import com.justice.test.stanbest.utils.showAlerterError

class EditInstructorTextFieldFragment(val editValue: EditValue, val onSave: (String) -> Unit) :
    DialogFragment(R.layout.fragment_edit_instructor_text_field) {
    private val TAG = "EditInstructorTextField"
    private lateinit var binding: FragmentEditInstructorTextFieldBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditInstructorTextFieldBinding.bind(view)
        setOnClickListeners()
        setDefaults()

    }

    private fun setDefaults() {
        Log.d(TAG, "setDefaults: ")
        binding.tvHeader.text = "Enter ${editValue.title}"
        binding.edtValue.setText(editValue.value)

    }

    private fun setOnClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnSave.setOnClickListener {
            val result = binding.edtValue.text.toString()
            if (result.isBlank()) {
                showAlerterError(message = "Please input $editValue")
                return@setOnClickListener
            }

            onSave(result.trim())
            dismiss()
        }
    }

    data class EditValue(val title: String, val value: String?)
}