package com.example.moconmcs.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

data class C005(
    val RESULT: RESULT,
    val row: List<Row>,
    val total_count: String
)