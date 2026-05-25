package edu.guigu.accountbook.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class BillsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TextView(requireContext()).apply {
            text = "📋 账单页\n\n这里将会显示你的记账记录"
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(Color.DKGRAY)
            setBackgroundColor(Color.WHITE)
        }
    }
}
