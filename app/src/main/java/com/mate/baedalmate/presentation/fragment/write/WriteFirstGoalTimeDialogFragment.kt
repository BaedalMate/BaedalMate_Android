package com.mate.baedalmate.presentation.fragment.write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.RoundDialogFragment
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentWriteFirstGoalTimeDialogBinding
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WriteFirstGoalTimeDialogFragment : RoundDialogFragment() {
    private var binding by autoCleared<FragmentWriteFirstGoalTimeDialogBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogSizeRatio = 0.8f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFirstGoalTimeDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.isCancelable = false
        setTimePicker()
        setActionClickListener()
    }

    private fun setTimePicker() {
        binding.timepickerWriteFirstGoalTimeDialog.setIs24HourView(true)

    }

    private fun setActionClickListener() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        binding.tvWriteFirstGoalTimeDialogCancel.setOnDebounceClickListener {
            this.dismiss()
        }
        binding.tvWriteFirstGoalTimeDialogConfirm.setOnDebounceClickListener {
            val currentDateTime = LocalDateTime.now()
            val currentTimePickerHour = binding.timepickerWriteFirstGoalTimeDialog.hour
            val currentTimePickerMinute = binding.timepickerWriteFirstGoalTimeDialog.minute
            if( currentDateTime.hour > currentTimePickerHour || (currentDateTime.hour == currentTimePickerHour && currentDateTime.minute >= currentTimePickerMinute)) {
                val tomorrowDateTime = currentDateTime.plusDays(1L)
                writeViewModel.deadLineTime?.postValue(
                    LocalDateTime.of(tomorrowDateTime.year, tomorrowDateTime.month, tomorrowDateTime.dayOfMonth, currentTimePickerHour, currentTimePickerMinute)
                        .format(formatter)
                )
            } else {
                writeViewModel.deadLineTime?.postValue(
                    LocalDateTime.of(currentDateTime.year, currentDateTime.month, currentDateTime.dayOfMonth, currentTimePickerHour, currentTimePickerMinute)
                        .format(formatter)
                )
            }
            this.dismiss()
        }
    }
}