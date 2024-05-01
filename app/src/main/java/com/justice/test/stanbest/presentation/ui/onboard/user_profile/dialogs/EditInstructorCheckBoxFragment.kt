package com.justice.test.stanbest.presentation.ui.onboard.user_profile.dialogs


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import com.justice.test.stanbest.R
import com.justice.test.stanbest.databinding.FragmentEditInstructorCheckBoxBinding
import com.justice.test.stanbest.presentation.ui.onboard.login.Gender

class EditInstructorCheckBoxFragment(val currentGender: Gender?, val onSave: (Gender) -> Unit) :
    DialogFragment(R.layout.fragment_edit_instructor_check_box) {
    private val TAG = "EditInstructorCheckBoxF"
    private lateinit var binding: FragmentEditInstructorCheckBoxBinding
    private lateinit var choosenGender: Gender
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditInstructorCheckBoxBinding.bind(view)
        setOnClickListeners()
        setDefaults()

    }

    private fun setDefaults() {
        Log.d(TAG, "setDefaults: gender:$currentGender")
        when (currentGender) {
            Gender.FEMALE -> {
                binding.tvFemale.setBackgroundResource(R.drawable.bg_color_primary_stroke_green)
                choosenGender = Gender.FEMALE
            }
            else -> {
                choosenGender = Gender.MALE
                binding.tvMale.setBackgroundResource(R.drawable.bg_color_primary_stroke_green)

            }
        }

    }


    private fun setOnClickListeners() {

        setListenersForMaleFemaleBtns()







        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnSave.setOnClickListener {
            saveClicked()
        }
    }

    private fun setListenersForMaleFemaleBtns() {
        binding.tvMale.setOnClickListener {
            binding.tvMale.setBackgroundResource(R.drawable.bg_color_primary_stroke_green)
            binding.tvFemale.setBackgroundResource(R.drawable.bg_color_primary)
            choosenGender = Gender.MALE

        }
        binding.tvFemale.setOnClickListener {
            binding.tvFemale.setBackgroundResource(R.drawable.bg_color_primary_stroke_green)
            binding.tvMale.setBackgroundResource(R.drawable.bg_color_primary)

            choosenGender = Gender.FEMALE
        }
    }

    private fun saveClicked() {
        Log.d(TAG, "saveClicked: ")

        onSave(choosenGender)
        dismiss()


    }

    data class EditValue(val title: String, val value: String?)
}