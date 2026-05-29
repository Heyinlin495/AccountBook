package edu.guigu.accountbook.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.guigu.accountbook.data.dao.CategorySummary
import edu.guigu.accountbook.data.dao.DailySummary
import edu.guigu.accountbook.data.dao.MonthlySummary
import edu.guigu.accountbook.data.dao.MonthlyTrend
import edu.guigu.accountbook.data.database.AppDatabase
import edu.guigu.accountbook.data.model.Record
import edu.guigu.accountbook.data.repository.RecordRepository
import edu.guigu.accountbook.util.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecordRepository

    private val _allRecords = MutableLiveData<List<Record>>(emptyList())
    val allRecords: LiveData<List<Record>> get() = _allRecords

    private val _totalIncome = MutableLiveData(0.0)
    val totalIncome: LiveData<Double> get() = _totalIncome

    private val _totalExpense = MutableLiveData(0.0)
    val totalExpense: LiveData<Double> get() = _totalExpense

    private val _expenseCategorySummary = MutableLiveData<List<CategorySummary>>(emptyList())
    val expenseCategorySummary: LiveData<List<CategorySummary>> get() = _expenseCategorySummary

    // 当前选中的年月
    private val _currentYear = MutableLiveData(Calendar.getInstance().get(Calendar.YEAR))
    val currentYear: LiveData<Int> get() = _currentYear

    private val _currentMonth = MutableLiveData(Calendar.getInstance().get(Calendar.MONTH))
    val currentMonth: LiveData<Int> get() = _currentMonth

    // 每日汇总（柱状图）
    private val _dailySummary = MutableLiveData<List<DailySummary>>(emptyList())
    val dailySummary: LiveData<List<DailySummary>> get() = _dailySummary

    // 月度收支汇总（折线图）
    private val _monthlySummary = MutableLiveData<List<MonthlySummary>>(emptyList())
    val monthlySummary: LiveData<List<MonthlySummary>> get() = _monthlySummary

    /** 按月汇总收支趋势 */
    private val _monthlyTrend = MutableLiveData<List<MonthlyTrend>>(emptyList())
    val monthlyTrend: LiveData<List<MonthlyTrend>> get() = _monthlyTrend

    // 当前 tab 类型：0=支出, 1=收入
    private val _currentTabType = MutableLiveData(Record.TYPE_EXPENSE)
    val currentTabType: LiveData<Int> get() = _currentTabType

    // 搜索关键词
    private val _searchKeyword = MutableLiveData("")
    val searchKeyword: LiveData<String> get() = _searchKeyword

    // 筛选月份（null表示全部）
    private val _filterMonth = MutableLiveData<Pair<Int, Int>?>(null)
    val filterMonth: LiveData<Pair<Int, Int>?> get() = _filterMonth

    init {
        val dao = AppDatabase.getInstance(application).recordDao()
        repository = RecordRepository(dao)
        refreshData()
    }

    fun setMonth(year: Int, month: Int) {
        _currentYear.value = year
        _currentMonth.value = month
        refreshData()
    }

    fun prevMonth() {
        val year = _currentYear.value ?: return
        val month = _currentMonth.value ?: return
        if (month == Calendar.JANUARY) {
            setMonth(year - 1, Calendar.DECEMBER)
        } else {
            setMonth(year, month - 1)
        }
    }

    fun nextMonth() {
        val year = _currentYear.value ?: return
        val month = _currentMonth.value ?: return
        if (month == Calendar.DECEMBER) {
            setMonth(year + 1, Calendar.JANUARY)
        } else {
            setMonth(year, month + 1)
        }
    }

    fun setTabType(type: Int) {
        _currentTabType.value = type
        refreshData()
    }

    fun setSearchKeyword(keyword: String) {
        _searchKeyword.value = keyword
        refreshData()
    }

    fun setFilterMonth(year: Int?, month: Int?) {
        _filterMonth.value = if (year != null && month != null) Pair(year, month) else null
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            val year = _currentYear.value ?: return@launch
            val month = _currentMonth.value ?: return@launch
            val start = DateUtils.getMonthStart(year, month)
            val end = DateUtils.getMonthEnd(year, month)
            val type = _currentTabType.value ?: Record.TYPE_EXPENSE
            val keyword = _searchKeyword.value ?: ""
            val filterMonth = _filterMonth.value

            // 根据筛选条件获取记录
            val records = when {
                keyword.isNotEmpty() && filterMonth != null -> {
                    val filterStart = DateUtils.getMonthStart(filterMonth.first, filterMonth.second)
                    val filterEnd = DateUtils.getMonthEnd(filterMonth.first, filterMonth.second)
                    repository.searchRecordsByMonth(keyword, filterStart, filterEnd)
                }
                keyword.isNotEmpty() -> repository.searchRecords(keyword)
                filterMonth != null -> {
                    val filterStart = DateUtils.getMonthStart(filterMonth.first, filterMonth.second)
                    val filterEnd = DateUtils.getMonthEnd(filterMonth.first, filterMonth.second)
                    repository.getRecordsByMonth(filterStart, filterEnd)
                }
                else -> repository.getAllRecords()
            }
            _allRecords.postValue(records)

            _totalIncome.postValue(repository.getMonthIncome(start, end) ?: 0.0)
            _totalExpense.postValue(repository.getMonthExpense(start, end) ?: 0.0)
            _expenseCategorySummary.postValue(repository.getMonthCategorySummary(type, start, end))
            _dailySummary.postValue(repository.getDailySummary(type, start, end))
            _monthlyTrend.postValue(repository.getMonthlyTrend())
            refreshMonthlySummary()
        }
    }

    fun refreshMonthlySummary() {
        viewModelScope.launch {
            val calendar = java.util.Calendar.getInstance()
            val endYear = calendar.get(java.util.Calendar.YEAR)
            val endMonth = calendar.get(java.util.Calendar.MONTH)

            // 获取最近6个月的数据
            calendar.add(java.util.Calendar.MONTH, -5)
            val startYear = calendar.get(java.util.Calendar.YEAR)
            val startMonth = calendar.get(java.util.Calendar.MONTH)

            val startDate = DateUtils.getMonthStart(startYear, startMonth)
            val endDate = DateUtils.getMonthEnd(endYear, endMonth)

            val rawData = repository.getMonthlySummary(startDate, endDate)

            // 补全缺失的月份（显示为0）
            val fullList = mutableListOf<MonthlySummary>()
            val tempCal = java.util.Calendar.getInstance().apply {
                set(startYear, startMonth, 1)
            }
            val endCal = java.util.Calendar.getInstance().apply {
                set(endYear, endMonth, 1)
            }

            while (tempCal.before(endCal) || tempCal == endCal) {
                val y = tempCal.get(java.util.Calendar.YEAR)
                val m = tempCal.get(java.util.Calendar.MONTH)
                val existing = rawData.find { it.year == y && it.month == m }
                fullList.add(
                    existing ?: MonthlySummary(
                        year = y,
                        month = m,
                        income = 0.0,
                        expense = 0.0
                    )
                )
                tempCal.add(java.util.Calendar.MONTH, 1)
            }

            _monthlySummary.postValue(fullList)
        }
    }

    fun insert(record: Record) {
        viewModelScope.launch {
            repository.insert(record)
            refreshData()
        }
    }

    fun update(record: Record) {
        viewModelScope.launch {
            repository.update(record)
            refreshData()
        }
    }

    fun delete(record: Record) {
        viewModelScope.launch {
            repository.delete(record)
            refreshData()
        }
    }
}
