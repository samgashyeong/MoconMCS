package com.example.moconmcs.Main.SearchFood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moconmcs.Main.SearchFood.db.FoodListEntity
import com.example.moconmcs.R

class  PrevResultFoodList(val DataList:ArrayList<FoodListEntity>): RecyclerView.Adapter<PrevResultFoodList.MyViewHolder>(){
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        //ex)val 변수명 = itemView.findViewById<xml이름>(아이디네임)
        val foodName = itemView.findViewById<TextView>(R.id.foodName)
        val foodResult = itemView.findViewById<TextView>(R.id.resultTv)
        val foodResultIv = itemView.findViewById<ImageView>(R.id.resultIv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_prev_result, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //ex)holder.(홀더클래스변수).text = DataList[position].name
        holder.foodName.text  = DataList[position].foodName
        holder.foodResult.text = DataList[position].foodResult
        if(DataList[position].foodResult == "안좋음"){
            holder.foodResultIv.setImageResource(R.drawable.ic_eggs_icon)
        }
        else{
            holder.foodResultIv.setImageResource(R.drawable.ic_ovo_icon)
        }
    }
    override fun getItemCount() = DataList.size

}