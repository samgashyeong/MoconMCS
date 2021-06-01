package com.example.moconmcs.Main.FoodDiary;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moconmcs.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.CalenderViewHolder> {

    private final LocalDate selectedDate;
    private final ArrayList<LocalDate> days;
    private final OnItemListener itemListener;

    public CalenderAdapter(LocalDate selectedDate, ArrayList<LocalDate> days, OnItemListener itemListener) {
        this.selectedDate = selectedDate;
        this.days = days;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public CalenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_calender_cell, parent, false);

        return new CalenderViewHolder(view, itemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalenderViewHolder holder, int position) {
        if(days.get(position) == null) {
            holder.dayOfMonth.setText("");
            return;
        }
        holder.dayOfMonth.setText(String.valueOf(days.get(position).getDayOfMonth()));
        if(days.get(position).isEqual(selectedDate)) {
            holder.itemView.setBackgroundResource(R.drawable.selected_date);
        }
        else {
            int dayOfWeek = days.get(position).getDayOfWeek().getValue();
            if(dayOfWeek == 6) holder.dayOfMonth.setTextColor(Color.parseColor("#3333ff"));
            if(dayOfWeek == 7) holder.dayOfMonth.setTextColor(Color.parseColor("#ff3333"));
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class CalenderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected final TextView dayOfMonth;
        protected final View itemView;

        private final OnItemListener itemListener;

        public CalenderViewHolder(@NonNull View itemView, OnItemListener itemListener) {
            super(itemView);

            this.itemView = itemView;
            dayOfMonth = itemView.findViewById(R.id.cell_text);
            this.itemListener = itemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.onItemClick(getAdapterPosition(), dayOfMonth.getText().toString());
        }
    }

    public interface OnItemListener {
        void onItemClick(int pos, String day);
    }
}
