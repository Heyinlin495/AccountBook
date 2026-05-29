package edu.guigu.accountbook.data.repository

import edu.guigu.accountbook.data.dao.CategorySummary
import edu.guigu.accountbook.data.dao.DailySummary
import edu.guigu.accountbook.data.dao.MonthlySummary
import edu.guigu.accountbook.data.dao.MonthlyTrend
import edu.guigu.accountbook.data.dao.RecordDao
import edu.guigu.accountbook.data.model.Record

class RecordRepository(private val dao: RecordDao) {

    suspend fun getAllRecords(): List<Record> = dao.getAllRecords()
    suspend fun searchRecords(keyword: String): List<Record> = dao.searchRecords(keyword)
    suspend fun getRecordsByMonth(startDate: Long, endDate: Long): List<Record> = dao.getRecordsByMonth(startDate, endDate)
    suspend fun searchRecordsByMonth(keyword: String, startDate: Long, endDate: Long): List<Record> = dao.searchRecordsByMonth(keyword, startDate, endDate)
    suspend fun getTotalIncome(): Double? = dao.getTotalIncome()
    suspend fun getTotalExpense(): Double? = dao.getTotalExpense()
    suspend fun getCategorySummary(type: Int): List<CategorySummary> = dao.getCategorySummary(type)
    suspend fun getMonthIncome(startDate: Long, endDate: Long): Double? = dao.getMonthIncome(startDate, endDate)
    suspend fun getMonthExpense(startDate: Long, endDate: Long): Double? = dao.getMonthExpense(startDate, endDate)
    suspend fun getMonthCategorySummary(type: Int, startDate: Long, endDate: Long): List<CategorySummary> = dao.getMonthCategorySummary(type, startDate, endDate)
    suspend fun getDailySummary(type: Int, startDate: Long, endDate: Long): List<DailySummary> = dao.getDailySummary(type, startDate, endDate)
    suspend fun getMonthlySummary(startDate: Long, endDate: Long): List<MonthlySummary> = dao.getMonthlySummary(startDate, endDate)
    /** 获取按月汇总的收支趋势 */
    suspend fun getMonthlyTrend(): List<MonthlyTrend> = dao.getMonthlyTrend()

    suspend fun insert(record: Record): Long = dao.insert(record)
    suspend fun update(record: Record) = dao.update(record)
    suspend fun delete(record: Record) = dao.delete(record)
    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
