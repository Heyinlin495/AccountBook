package edu.guigu.accountbook.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import edu.guigu.accountbook.data.dao.CategorySummary
import edu.guigu.accountbook.data.dao.MonthlyTrend
import edu.guigu.accountbook.data.model.Record
import edu.guigu.accountbook.databinding.FragmentStatisticsBinding
import edu.guigu.accountbook.ui.viewmodel.RecordViewModel
import edu.guigu.accountbook.util.DateUtils

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecordViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[RecordViewModel::class.java]
        setupPieChart()
        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 35f
            transparentCircleRadius = 40f
            setHoleColor(Color.WHITE)
            setDrawEntryLabels(false)
            setCenterText("支出分类")
            setCenterTextSize(14f)
            setCenterTextColor(Color.DKGRAY)
            setUsePercentValues(false)
            setExtraOffsets(0f, 0f, 0f, 0f)

            legend.apply {
                isEnabled = true
                textSize = 12f
                textColor = Color.DKGRAY
                form = Legend.LegendForm.CIRCLE
                formSize = 10f
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                orientation = Legend.LegendOrientation.HORIZONTAL
                isWordWrapEnabled = true
                xEntrySpace = 16f
            }

            setNoDataText("暂无支出数据")
        }
    }

    private fun observeData() {
        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvMonthIncome.text = "¥${DateUtils.formatAmount(income)}"
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.tvMonthExpense.text = "¥${DateUtils.formatAmount(expense)}"
            updateBalance()
        }

        viewModel.expenseCategorySummary.observe(viewLifecycleOwner) { summary ->
            updatePieChart(summary)
        }

        // 观察月度趋势 → 画折线图
        viewModel.monthlyTrend.observe(viewLifecycleOwner) { trend ->
            setupLineChart(trend)
        }
    }

    private fun updateBalance() {
        val income = viewModel.totalIncome.value ?: 0.0
        val expense = viewModel.totalExpense.value ?: 0.0
        val balance = income - expense
        binding.tvMonthBalance.text = "¥${DateUtils.formatAmount(balance)}"
    }

    private fun updatePieChart(summary: List<CategorySummary>) {
        val filtered = summary.filter { it.total > 0.001 }
        if (filtered.isEmpty()) {
            binding.pieChart.clear()
            binding.pieChart.centerText = "暂无数据"
            return
        }

        val entries = filtered.map { PieEntry(it.total.toFloat(), it.category) }

        val colors = filtered.map {
            val color = Record.getCategoryColor(it.category)
            Color.rgb(
                android.graphics.Color.red(color),
                android.graphics.Color.green(color),
                android.graphics.Color.blue(color)
            )
        }

        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            sliceSpace = 3f
            valueTextSize = 11f
            valueTextColor = Color.WHITE
            valueLinePart1Length = 0.4f
            valueLinePart2Length = 0.6f
            valueLineWidth = 1.5f
        }

        val pieData = PieData(dataSet).apply {
            setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "¥${DateUtils.formatAmount(value.toDouble())}"
                }
            })
        }

        binding.pieChart.apply {
            data = pieData
            centerText = "支出分类"
            animateY(1000)
            invalidate()
        }
    }

    /** 配置并渲染折线图 */
    private fun setupLineChart(trend: List<MonthlyTrend>) {
        if (trend.isEmpty()) return
        val incomeEntries = mutableListOf<Entry>()
        val expenseEntries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        trend.forEachIndexed { index, item ->
            incomeEntries.add(Entry(index.toFloat(), item.income.toFloat()))
            expenseEntries.add(Entry(index.toFloat(), item.expense.toFloat()))
            labels.add(item.month)
        }

        val incomeSet = LineDataSet(incomeEntries, "收入").apply {
            color = Color.parseColor("#2ECC71")
            setCircleColor(Color.parseColor("#2ECC71"))
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 10f
        }

        val expenseSet = LineDataSet(expenseEntries, "支出").apply {
            color = android.graphics.Color.parseColor("#E74C3C")
            setCircleColor(Color.parseColor("#E74C3C"))
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 10f
        }

        binding.lineChartMonthly.apply {
            data = LineData(incomeSet, expenseSet)
            description.isEnabled = false
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.granularity = 1f
            axisRight.isEnabled = false
            animateX(800)
            invalidate()
        }
    }
}
