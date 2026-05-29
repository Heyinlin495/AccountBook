package edu.guigu.accountbook.data.dao

import androidx.room.*
import edu.guigu.accountbook.data.model.Record

/** 分类汇总数据（用于饼图） */
data class CategorySummary(
    val category: String,
    val total: Double
)

/** 月度收支趋势数据 */
data class MonthlyTrend(
    val month: String,          // "2025-05"
    val income: Double,         // 该月收入
    val expense: Double         // 该月支出
)

/** 每日支出汇总（用于柱状图） */
data class DailySummary(
    val day: Int,
    val total: Double
)

/** 月度收支汇总（用于折线图） */
data class MonthlySummary(
    val year: Int,
    val month: Int,
    val income: Double,
    val expense: Double
)

@Dao
interface RecordDao {

    // ===== 查询 =====

    @Query("SELECT * FROM records ORDER BY date DESC")
    suspend fun getAllRecords(): List<Record>

    @Query("SELECT * FROM records WHERE category LIKE '%' || :keyword || '%' OR note LIKE '%' || :keyword || '%' ORDER BY date DESC")
    suspend fun searchRecords(keyword: String): List<Record>

    @Query("SELECT * FROM records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getRecordsByMonth(startDate: Long, endDate: Long): List<Record>

    @Query("SELECT * FROM records WHERE (category LIKE '%' || :keyword || '%' OR note LIKE '%' || :keyword || '%') AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun searchRecordsByMonth(keyword: String, startDate: Long, endDate: Long): List<Record>

    @Query("SELECT SUM(amount) FROM records WHERE type = ${Record.TYPE_INCOME}")
    suspend fun getTotalIncome(): Double?

    @Query("SELECT SUM(amount) FROM records WHERE type = ${Record.TYPE_EXPENSE}")
    suspend fun getTotalExpense(): Double?

    @Query("SELECT SUM(amount) FROM records WHERE type = ${Record.TYPE_INCOME} AND date BETWEEN :startDate AND :endDate")
    suspend fun getMonthIncome(startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amount) FROM records WHERE type = ${Record.TYPE_EXPENSE} AND date BETWEEN :startDate AND :endDate")
    suspend fun getMonthExpense(startDate: Long, endDate: Long): Double?

    @Query("SELECT category, SUM(amount) AS total FROM records WHERE type = :type AND date BETWEEN :startDate AND :endDate GROUP BY category ORDER BY total DESC")
    suspend fun getMonthCategorySummary(type: Int, startDate: Long, endDate: Long): List<CategorySummary>

    @Query("SELECT * FROM records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getRecordsByDateRange(startDate: Long, endDate: Long): List<Record>

    @Query("SELECT category, SUM(amount) AS total FROM records WHERE type = :type GROUP BY category ORDER BY total DESC")
    suspend fun getCategorySummary(type: Int): List<CategorySummary>

    /** 按天汇总（用于柱状图），day 为当月第几天 */
    @Query("SELECT CAST(strftime('%d', date / 1000, 'unixepoch', 'localtime') AS INTEGER) AS day, SUM(amount) AS total FROM records WHERE type = :type AND date BETWEEN :startDate AND :endDate GROUP BY day ORDER BY day")
    suspend fun getDailySummary(type: Int, startDate: Long, endDate: Long): List<DailySummary>

    /** 月度收支汇总（用于折线图） */
    @Query("SELECT CAST(strftime('%Y', date / 1000, 'unixepoch', 'localtime') AS INTEGER) AS year, CAST(strftime('%m', date / 1000, 'unixepoch', 'localtime') AS INTEGER) - 1 AS month, SUM(CASE WHEN type = 1 THEN amount ELSE 0 END) AS income, SUM(CASE WHEN type = 0 THEN amount ELSE 0 END) AS expense FROM records WHERE date BETWEEN :startDate AND :endDate GROUP BY year, month ORDER BY year, month")
    suspend fun getMonthlySummary(startDate: Long, endDate: Long): List<MonthlySummary>

    /**
     * 按月汇总收支趋势
     */
    @Query("""
        SELECT
            strftime('%Y-%m', date / 1000, 'unixepoch') AS month,
            SUM(CASE WHEN type = 1 THEN amount ELSE 0 END) AS income,
            SUM(CASE WHEN type = 0 THEN amount ELSE 0 END) AS expense
        FROM records
        GROUP BY month
        ORDER BY month ASC
    """)
    suspend fun getMonthlyTrend(): List<MonthlyTrend>

    // ===== 增删改 =====

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record): Long

    @Update
    suspend fun update(record: Record)

    @Delete
    suspend fun delete(record: Record)

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
