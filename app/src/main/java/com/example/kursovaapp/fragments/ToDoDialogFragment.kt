package com.example.kursovaapp.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.kursovaapp.databinding.FragmentToDoDialogBinding
import com.example.kursovaapp.utils.model.ToDoData
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class ToDoDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentToDoDialogBinding
    private var listener: OnDialogNextBtnClickListener? = null
    private var toDoData: ToDoData? = null
    private var selectedReminderTime: Long? = null // Для збереження часу нагадування

    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String) =
            ToDoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)
                    putString("task", task)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToDoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            toDoData = ToDoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString()
            )
            binding.todoEt.setText(toDoData?.task)
        }

        binding.todoClose.setOnClickListener {
            dismiss()
        }

        binding.todoNextBtn.setOnClickListener {
            val todoTask = binding.todoEt.text.toString()
            if (todoTask.isNotEmpty()) {
                if (toDoData == null) {
                    listener?.saveTask(todoTask, selectedReminderTime, binding.todoEt)
                } else {
                    toDoData!!.task = todoTask
                    listener?.updateTask(toDoData!!, binding.todoEt)
                }
                dismiss()
            }
        }

        // Додано логіку для кнопки selectTimeBtn
        binding.selectTimeBtn.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun showTimePickerDialog() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                selectedReminderTime = selectedTime.timeInMillis // Збереження обраного часу
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    interface OnDialogNextBtnClickListener {
        fun saveTask(todoTask: String, reminderTime: Long?, todoEdit: TextInputEditText)
        fun updateTask(toDoData: ToDoData, todoEdit: TextInputEditText)
    }
}
