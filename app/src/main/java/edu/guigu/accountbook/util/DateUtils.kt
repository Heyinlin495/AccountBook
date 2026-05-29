package edu.guigu.accountbook.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val DATE_PATTERN = "yyyy年MM月dd日"

    /** 时间戳 → "2025年05月24日" */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATE_PATTERN, Locale.CHINA)
        return sdf.format(Date(timestamp))
    }

    /** 金额 → "1234.50"（保留两位小数） */
    fun formatAmount(amount: Double): String {
        return String.format(Locale.CHINA, "%.2f", amount)
    }

    /** 指定年月起始时间戳 */
    fun getMonthStart(year: Int, month: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** 指定年月结束时间戳 */
    fun getMonthEnd(year: Int, month: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.MONTH, 1)
        return cal.timeInMillis - 1
    }

    /** 指定年月的天数 */
    fun getDaysInMonth(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /** 获取星期几的中文名 */
    fun getWeekdayName(year: Int, month: Int, day: Int): String {
        val cal = Calendar.getInstance()
        cal.set(year, month, day)
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "星期一"
            Calendar.TUESDAY -> "星期二"
            Calendar.WEDNESDAY -> "星期三"
            Calendar.THURSDAY -> "星期四"
            Calendar.FRIDAY -> "星期五"
            Calendar.SATURDAY -> "星期六"
            Calendar.SUNDAY -> "星期日"
            else -> ""
        }
    }

    /** 获取指定天的时间戳范围 */
    fun getDayRange(year: Int, month: Int, day: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(year, month, day, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val end = cal.timeInMillis - 1
        return start to end
    }
}
