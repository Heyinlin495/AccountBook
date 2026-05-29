package edu.guigu.accountbook.ui.component

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import edu.guigu.accountbook.R
import edu.guigu.accountbook.data.dao.CategorySummary
import edu.guigu.accountbook.util.DateUtils

class ChartMarkerView(
    context: Context,
    private val summary: List<CategorySummary>
) : MarkerView(context, R.layout.marker_chart) {

    private val tvContent: TextView = findViewById(R.id.tvMarkerContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val index = it.x.toInt()
            if (index in summary.indices) {
                val item = summary[index]
                tvContent.text = "${item.category}: ¥${DateUtils.formatAmount(item.total)}"
            }
        }
        super.refreshContent(e, highlight)
    }
}
