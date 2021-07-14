package com.example.moconmcs.Main.SearchFood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moconmcs.Main.SearchFood.db.FoodListEntity
import com.example.moconmcs.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        var userKind = ""
        val auth = FirebaseAuth.getInstance()
        val Fdb = FirebaseFirestore.getInstance()
        Fdb.collection("User").document(auth.currentUser!!.uid).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    userKind = it.result.data!!.getValue("userKind").toString()
                    when(FoodResultLoading.getCauseCantEat(userKind, DataList[position].foodResult)){
                        "egg"->{
                            holder.foodResult.text = "계란이 포함되어있음."
                            holder.foodResultIv.setImageResource(R.drawable.ic_eggs_icon)
                        }
                        "fish"->{
                            holder.foodResult.text = "해산물이 포함되어있음."
                            holder.foodResultIv.setImageResource(R.drawable.ic_fish)
                        }
                        "meat"->{
                            holder.foodResult.text = "동물성 성분이 포함되어있음."
                            holder.foodResultIv.setImageResource(R.drawable.ic_meat_icon)

                        }
                        "meat|fish"->{
                            holder.foodResult.text = "동물성 성분과 해산물이 포함되어있음."
                            holder.foodResultIv.setImageResource(R.drawable.ic_fish_meat)
                        }
                        "safe"->{
                            holder.foodResult.text = "먹을 수 있음."
                            when(userKind) {
                                "비건" -> holder.foodResultIv.setImageResource(R.drawable.ic_vegan_icon)
                                "락토" -> holder.foodResultIv.setImageResource(R.drawable.ic_locto_icon)
                                "오보" -> holder.foodResultIv.setImageResource(R.drawable.ic_ovo_icon)
                                "락토오보" -> holder.foodResultIv.setImageResource(R.drawable.ic_locto_ovo_icon)
                            }

                        }
                    }
                    holder.itemView.setOnClickListener {
                        click.onClick(position)
                    }
                }
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