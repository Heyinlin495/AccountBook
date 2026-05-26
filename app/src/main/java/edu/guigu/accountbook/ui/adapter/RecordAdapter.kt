package edu.guigu.accountbook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.guigu.accountbook.R
import edu.guigu.accountbook.databinding.ItemRecordBinding

class RecordAdapter : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    data class FakeRecord(
        val icon: String,
        val iconColor: Int,
        val category: String,
        val date: String,
        val note: String?,
        val amount: String,
        val amountColor: Int
    )

    private val records = listOf(
        FakeRecord("餐", 0xFFFF6B6B.toInt(), "何胤霖", "2025年05月24日", "午餐外卖",
            "-¥35.50", 0xFFE74C3C.toInt()),
        FakeRecord("薪", 0xFF2ECC71.toInt(), "工资", "2025年05月15日", null,
            "+¥8000.00", 0xFF2ECC71.toInt()),
        FakeRecord("交", 0xFF4ECDC4.toInt(), "交通", "2025年05月23日", "地铁通勤",
            "-¥12.00", 0xFFE74C3C.toInt()),
        FakeRecord("购", 0xFF45B7D1.toInt(), "购物", "2025年05月22日", null,
            "-¥258.00", 0xFFE74C3C.toInt()),
        FakeRecord("娱", 0xFF96CEB4.toInt(), "娱乐", "2025年05月20日", "电影票",
            "-¥79.90", 0xFFE74C3C.toInt()),
    )

    override fun getItemCount(): Int = records.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(records[position])
    }

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRecordBinding.bind(itemView)

        fun bind(record: FakeRecord) {
            binding.tvCategoryIcon.text = record.icon
            binding.tvCategoryIcon.background.mutate()?.let { drawable ->
                (drawable as? android.graphics.drawable.GradientDrawable)?.setColor(record.iconColor)
            }

            binding.tvCategoryName.text = record.category
            binding.tvDate.text = record.date

            if (record.note != null) {
                binding.tvNote.text = "备注：${record.note}"
                binding.tvNote.visibility = View.VISIBLE
            } else {
                binding.tvNote.visibility = View.GONE
            }

            binding.tvAmount.text = record.amount
            binding.tvAmount.setTextColor(record.amountColor)
        }
    }
}
