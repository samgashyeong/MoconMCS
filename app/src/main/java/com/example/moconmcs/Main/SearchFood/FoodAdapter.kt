package com.example.moconmcs.Main.SearchFood

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moconmcs.R
import com.example.moconmcs.data.KyungrokApi.Material

class FoodAdapter(val DataList:List<Material>): RecyclerView.Adapter<FoodAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val foodName = itemView.findViewById<TextView>(R.id.productTv)
        val kindName = itemView.findViewById<TextView>(R.id.kindName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_food_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.foodName.text = DataList[position].matName
        if(DataList[position].MLSFC_NM == "수산물" || DataList[position].MLSFC_NM == "축산물"){
            holder.kindName.setTextColor(Color.RED)
            holder.foodName.setTextColor(Color.RED)
        }
        if(DataList[position].MLSFC_NM == "not-found"){
            holder.kindName.text = "성분을 아직 찾지 못하였습니다."
        }
        else{
            holder.kindName.text = DataList[position].MLSFC_NM
        }
    }

    override fun getItemCount() = DataList.size

}