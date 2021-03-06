package com.example.moconmcs.Main.SearchFood

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moconmcs.R
import com.example.moconmcs.data.KyungrokApi.Material

class FoodAdapter(val DataList:ArrayList<Material>, val failLength:Int): RecyclerView.Adapter<FoodAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val foodName = itemView.findViewById<TextView>(R.id.conductName)
        val kindName = itemView.findViewById<TextView>(R.id.conductKind)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_food_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.foodName.text = DataList[position].matName
        holder.kindName.text = DataList[position].MLSFC_NM
        if(DataList[position].MLSFC_NM == "not-found"){
            holder.kindName.text = "성분을 아직 찾지 못하였습니다."
        }
        if(position<failLength){
            holder.kindName.setTextColor(Color.RED)
            holder.foodName.setTextColor(Color.RED)
        }
    }

    override fun getItemCount() = DataList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }
}