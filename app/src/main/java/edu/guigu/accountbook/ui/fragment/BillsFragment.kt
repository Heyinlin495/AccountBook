package edu.guigu.accountbook.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import edu.guigu.accountbook.data.database.AppDatabase
import edu.guigu.accountbook.data.model.Record
import edu.guigu.accountbook.databinding.FragmentBillsBinding
import edu.guigu.accountbook.ui.adapter.RecordAdapter
import edu.guigu.accountbook.ui.dialog.AddEditRecordDialog
import edu.guigu.accountbook.ui.viewmodel.RecordViewModel
import edu.guigu.accountbook.util.DateUtils
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class BillsFragment : Fragment() {

    private var _binding: FragmentBillsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecordViewModel
    private lateinit var adapter: RecordAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBillsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RecordViewModel::class.java]

        adapter = RecordAdapter(
            onItemClick = { record -> showEditDialog(record) },
            onItemLongClick = { record -> showDeleteConfirmDialog(record) }
        )
        binding.rvRecords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecords.adapter = adapter

        viewModel.allRecords.observe(viewLifecycleOwner) { records ->
            adapter.updateRecords(records)
        }

        binding.fabAdd.setOnClickListener { showAddDialog() }

        setupFilter()
        setupSearch()

        // 监听弹窗返回结果
        setFragmentResultListener(AddEditRecordDialog.REQUEST_KEY) { _, bundle ->
            val record = Record(
                id = bundle.getLong(AddEditRecordDialog.RESULT_RECORD_ID),
                type = bundle.getInt(AddEditRecordDialog.RESULT_RECORD_TYPE),
                category = bundle.getString(AddEditRecordDialog.RESULT_RECORD_CATEGORY, "其他"),
                amount = bundle.getDouble(AddEditRecordDialog.RESULT_RECORD_AMOUNT),
                note = bundle.getString(AddEditRecordDialog.RESULT_RECORD_NOTE),
                date = bundle.getLong(AddEditRecordDialog.RESULT_RECORD_DATE)
            )
            val isEdit = bundle.getBoolean(AddEditRecordDialog.RESULT_IS_EDIT)
            if (isEdit) {
                viewModel.update(record)
                Toast.makeText(requireContext(), "记录已更新", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insert(record)
                Toast.makeText(requireContext(), "记录已添加", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupFilter() {
        binding.chipFilter.setOnClickListener { showMonthPicker() }

        binding.chipFilter.setOnCloseIconClickListener {
            binding.chipFilter.isChecked = false
            viewModel.setFilterMonth(null, null)
        }
    }

    private fun showMonthPicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, _ ->
                binding.chipFilter.isChecked = true
                binding.chipFilter.text = "${year}年${month + 1}月"
                viewModel.setFilterMonth(year, month)
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1
        ).apply {
            datePicker.findViewById<View>(
                resources.getIdentifier("day", "id", "android")
            )?.visibility = View.GONE
        }.show()
    }

    private fun setupSearch() {
        binding.btnSearch.setOnClickListener {
            if (binding.searchView.visibility == View.GONE) {
                binding.searchView.visibility = View.VISIBLE
                binding.searchView.isIconified = false
            } else {
                binding.searchView.visibility = View.GONE
                binding.searchView.setQuery("", false)
                lifecycleScope.launch {
                    val results = AppDatabase.getInstance(requireContext()).recordDao().getAllRecords()
                    adapter.updateRecords(results)
                }
            }
        }

        binding.searchView.setOnCloseListener {
            binding.searchView.visibility = View.GONE
            lifecycleScope.launch {
                val results = AppDatabase.getInstance(requireContext()).recordDao().getAllRecords()
                adapter.updateRecords(results)
            }
            false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                // 每输入一个字就实时过滤
                val keyword = newText?.trim() ?: ""
                lifecycleScope.launch {
                    val results = if (keyword.isBlank()) {
                        AppDatabase.getInstance(requireContext()).recordDao().getAllRecords()
                    } else {
                        AppDatabase.getInstance(requireContext()).recordDao().searchRecords(keyword)
                    }
                    adapter.updateRecords(results)
                }
                return true
            }
        })
    }

    private fun showAddDialog() {
        AddEditRecordDialog.newInstance()
            .show(parentFragmentManager, "AddEditDialog")
    }

    private fun showEditDialog(record: Record) {
        AddEditRecordDialog.newInstance(record)
            .show(parentFragmentManager, "AddEditDialog")
    }

    private fun showDeleteConfirmDialog(record: Record) {
        AlertDialog.Builder(requireContext())
            .setTitle("删除确认")
            .setMessage("确定要删除「${record.category}」的这条记录吗？\n金额：¥${DateUtils.formatAmount(record.amount)}")
            .setPositiveButton("删除") { _, _ ->
                viewModel.delete(record)
                Toast.makeText(requireContext(), "记录已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
