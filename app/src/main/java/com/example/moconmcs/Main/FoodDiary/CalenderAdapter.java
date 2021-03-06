package com.example.moconmcs.Main.FoodDiary;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moconmcs.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class CalenderAdapter extends BaseAdapter {

    private final LocalDate selectedDate;
    private final ArrayList<LocalDate> days;
    private final HashMap<Integer, Boolean> canClick = new HashMap<>();

    public CalenderAdapter(LocalDate selectedDate, ArrayList<LocalDate> days) {
        this.selectedDate = selectedDate;
        this.days = days;
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public boolean isCanClick(int position) {
        return canClick.get(position) != Boolean.valueOf(false);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_calender_cell, parent, false);
        TextView dayOfMonth = view.findViewById(R.id.cell_text);
        ImageView checkCircle = view.findViewById(R.id.cell_selected_circle);

        if(days.get(position) == null) {
            dayOfMonth.setText("");
            canClick.put(position, false);
        }
        else {
            dayOfMonth.setText(String.valueOf(days.get(position).getDayOfMonth()));
            if(days.get(position).atTime(0, 0).isEqual(selectedDate.atTime(0, 0))) {
                checkCircle.setVisibility(View.VISIBLE);
            }
            else {
                checkCircle.setVisibility(View.INVISIBLE);
                if(days.get(position).atTime(0, 0)
                        .isAfter(LocalDate.now().atTime(0, 0))) {
                    dayOfMonth.setTextColor(Color.parseColor("#e4e4e4")); //gray
                    canClick.put(position, false);
                }
            }
        }
        return view;
    }

    //
//    @NonNull
//    @Override
//    public CalenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CalenderViewHolder holder, int position) {
//        if(days.get(position) == null) {
//            holder.dayOfMonth.setText("");
//            return;
//        }
//        holder.dayOfMonth.setText(String.valueOf(days.get(position).getDayOfMonth()));
//        if(days.get(position).atTime(0, 0).isEqual(selectedDate.atTime(0, 0))) {
//            holder.itemView.setBackgroundResource(R.drawable.selected_date);
//        }
//        else {
//            int dayOfWeek = days.get(position).getDayOfWeek().getValue();
//            if(days.get(position).atTime(0, 0)
//                    .isAfter(LocalDate.now().atTime(0, 0))) {
//                holder.dayOfMonth.setTextColor(Color.parseColor("#e4e4e4")); //gray
//                holder.canListenEvent = false;
//            }
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return days.size();
//    }
//
//    public static class CalenderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        private final TextView dayOfMonth;
//        private final View itemView;
//
//        private boolean canListenEvent = true;
//        private final OnItemListener itemListener;
//
//        public CalenderViewHolder(@NonNull View itemView, OnItemListener itemListener) {
//            super(itemView);
//
//            this.itemView = itemView;
//            dayOfMonth = itemView.findViewById(R.id.cell_text);
//            this.itemListener = itemListener;
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            if(itemListener != null && canListenEvent)
//                itemListener.onItemClick(getAdapterPosition(), dayOfMonth.getText().toString());
//        }
//    }
//
    public interface OnItemListener {
        void onItemClick(int pos, String day);
    }
}
