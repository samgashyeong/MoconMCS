package com.example.moconmcs.Main.SearchFood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moconmcs.Main.SearchFood.db.FoodListEntity
import com.example.moconmcs.R

class  PrevResultFoodListAdapter(private val DataList:ArrayList<FoodListEntity>, private val click : OnClickList): RecyclerView.Adapter<PrevResultFoodListAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
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
        when(DataList[position].foodResult){
            "bad_egg"->{
                holder.foodResult.text = "계란이 포함되어있음."
                holder.foodResultIv.setImageResource(R.drawable.ic_eggs_icon)
            }
            "bad_fish"->{
                holder.foodResult.text = "해산물이 포함되어있음."
                holder.foodResultIv.setImageResource(R.drawable.ic_fish)
            }
            "bad_meat"->{
                holder.foodResult.text = "동물성 성분이 포함되어있음."
                holder.foodResultIv.setImageResource(R.drawable.ic_meat_icon)

            }
            "bad_meatAndFish"->{
                holder.foodResult.text = "동물성 성분과 해산물이 포함되어있음."
                holder.foodResultIv.setImageResource(R.drawable.ic_fish_meat)

            }
            "good_vegan"->{
                holder.foodResult.text = "먹을 수 있음."
                holder.foodResultIv.setImageResource(R.drawable.ic_vegan_icon)

            }
            "good_locto"->{
                holder.foodResult.text = "먹을 수 있음."
                holder.foodResultIv.setImageResource(R.drawable.ic_locto_icon)
            }
            "good_ovo"->{
                holder.foodResult.text = "먹을 수 있음."
                holder.foodResultIv.setImageResource(R.drawable.ic_ovo_icon)
            }
            "good_loctoovo"->{
                holder.foodResult.text = "먹을 수 있음."
                holder.foodResultIv.setImageResource(R.drawable.ic_locto_ovo_icon)
            }

        }
        holder.itemView.setOnClickListener {
            click.onClick(position)
        }
    }
    override fun getItemCount() = DataList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface OnClickList {
        fun onClick(position: Int)
    }
}