package edu.guigu.accountbook.ui.dialog

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import edu.guigu.accountbook.data.model.Record
import edu.guigu.accountbook.databinding.DialogAddEditRecordBinding
import edu.guigu.accountbook.util.DateUtils
import java.util.*

class AddEditRecordDialog : BottomSheetDialogFragment() {

    private var _binding: DialogAddEditRecordBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: Long = System.currentTimeMillis()
    private var selectedType: Int = Record.TYPE_EXPENSE
    private var editRecord: Record? = null
    private var isInitializing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            if (args.containsKey(ARG_RECORD_ID)) {
                editRecord = Record(
                    id = args.getLong(ARG_RECORD_ID),
                    type = args.getInt(ARG_RECORD_TYPE),
                    category = args.getString(ARG_RECORD_CATEGORY, "其他"),
                    amount = args.getDouble(ARG_RECORD_AMOUNT),
                    note = args.getString(ARG_RECORD_NOTE),
                    date = args.getLong(ARG_RECORD_DATE)
                )
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddEditRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMode()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupMode() {
        isInitializing = true
        if (editRecord != null) {
            binding.tvDialogTitle.text = "编辑记录"
            selectedType = editRecord!!.type
            binding.spinnerType.setSelection(selectedType)
            updateCategorySpinner(selectedType)
            val index = Record.getCategoriesByType(selectedType).indexOf(editRecord!!.category)
            if (index >= 0) binding.spinnerCategory.setSelection(index)
            binding.etAmount.setText(DateUtils.formatAmount(editRecord!!.amount))
            if (!editRecord!!.note.isNullOrBlank()) binding.etNote.setText(editRecord!!.note)
            selectedDate = editRecord!!.date
            binding.tvSelectedDate.text = DateUtils.formatDate(selectedDate)
        } else {
            binding.tvDialogTitle.text = "添加记录"
            binding.spinnerType.setSelection(0)
            updateCategorySpinner(Record.TYPE_EXPENSE)
            selectedDate = System.currentTimeMillis()
            binding.tvSelectedDate.text = DateUtils.formatDate(selectedDate)
        }
        isInitializing = false
    }

    private fun setupListeners() {
        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isInitializing) return
                selectedType = position
                updateCategorySpinner(selectedType)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.btnPickDate.setOnClickListener { showDatePicker() }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnSave.setOnClickListener { saveRecord() }
    }

    private fun updateCategorySpinner(type: Int) {
        val categories = Record.getCategoriesByType(type)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = selectedDate
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                cal.set(year, month, day, 12, 0, 0)
                cal.set(Calendar.MILLISECOND, 0)
                selectedDate = cal.timeInMillis
                binding.tvSelectedDate.text = DateUtils.formatDate(selectedDate)
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveRecord() {
        val amountStr = binding.etAmount.text?.toString()?.trim()
        if (amountStr.isNullOrBlank()) {
            binding.etAmount.error = "请输入金额"
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.etAmount.error = "请输入有效的金额"
            return
        }

        binding.btnSave.isEnabled = false

        val category = binding.spinnerCategory.selectedItem?.toString() ?: "其他"
        val note = binding.etNote.text?.toString()?.trim()?.takeIf { it.isNotBlank() }

        val record = Record(
            id = editRecord?.id ?: 0,
            type = selectedType,
            category = category,
            amount = amount,
            note = note,
            date = selectedDate
        )

        val result = Bundle().apply {
            putLong(RESULT_RECORD_ID, record.id)
            putInt(RESULT_RECORD_TYPE, record.type)
            putString(RESULT_RECORD_CATEGORY, record.category)
            putDouble(RESULT_RECORD_AMOUNT, record.amount)
            putString(RESULT_RECORD_NOTE, record.note)
            putLong(RESULT_RECORD_DATE, record.date)
            putBoolean(RESULT_IS_EDIT, editRecord != null)
        }
        setFragmentResult(REQUEST_KEY, result)
        dismiss()
    }

    companion object {
        const val REQUEST_KEY = "add_edit_record_request"

        // Arguments keys
        private const val ARG_RECORD_ID = "arg_record_id"
        private const val ARG_RECORD_TYPE = "arg_record_type"
        private const val ARG_RECORD_CATEGORY = "arg_record_category"
        private const val ARG_RECORD_AMOUNT = "arg_record_amount"
        private const val ARG_RECORD_NOTE = "arg_record_note"
        private const val ARG_RECORD_DATE = "arg_record_date"

        // Result keys
        const val RESULT_RECORD_ID = "result_record_id"
        const val RESULT_RECORD_TYPE = "result_record_type"
        const val RESULT_RECORD_CATEGORY = "result_record_category"
        const val RESULT_RECORD_AMOUNT = "result_record_amount"
        const val RESULT_RECORD_NOTE = "result_record_note"
        const val RESULT_RECORD_DATE = "result_record_date"
        const val RESULT_IS_EDIT = "result_is_edit"

        fun newInstance(editRecord: Record? = null): AddEditRecordDialog {
            return AddEditRecordDialog().apply {
                arguments = Bundle().apply {
                    if (editRecord != null) {
                        putLong(ARG_RECORD_ID, editRecord.id)
                        putInt(ARG_RECORD_TYPE, editRecord.type)
                        putString(ARG_RECORD_CATEGORY, editRecord.category)
                        putDouble(ARG_RECORD_AMOUNT, editRecord.amount)
                        putString(ARG_RECORD_NOTE, editRecord.note)
                        putLong(ARG_RECORD_DATE, editRecord.date)
                    }
                }
            }
        }
    }
}
