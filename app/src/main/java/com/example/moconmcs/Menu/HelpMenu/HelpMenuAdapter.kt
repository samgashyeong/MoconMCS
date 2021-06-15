package com.example.moconmcs.Menu.HelpMenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moconmcs.R
import com.example.moconmcs.data.CommonData.QnA

class HelpMenuAdapter(val DataList:ArrayList<QnA>): RecyclerView.Adapter<HelpMenuAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val Qtext = itemView.findViewById<TextView>(R.id.QTV)
        val Atext = itemView.findViewById<TextView>(R.id.ATV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_qna, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.Qtext.text = DataList[position].Q
        holder.Atext.text = DataList[position].A
    }
    override fun getItemCount() = DataList.size

}