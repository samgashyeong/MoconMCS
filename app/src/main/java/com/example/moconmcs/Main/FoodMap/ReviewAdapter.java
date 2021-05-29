package com.example.moconmcs.Main.FoodMap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moconmcs.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.CustomViewHolder> {

    ArrayList<ReviewInfo> arrayList;

    public ReviewAdapter(ArrayList<ReviewInfo> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ReviewAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_review_item, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewAdapter.CustomViewHolder holder, int position) {
        holder.name.setText(arrayList.get(position).getName());
        holder.rate.setRating(arrayList.get(position).getRate());
        holder.timeStamp.setText(SimpleDateFormat.getDateTimeInstance()
                .format(new Date(arrayList.get(position).getTimestamp())));
        holder.reviewText.setTextIsSelectable(false);
        holder.reviewText.setText(arrayList.get(position).getReview());

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return arrayList != null ? arrayList.size() : 0;
    }

    public void remove(int position) {
        try {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView name, timeStamp, reviewText;
        protected RatingBar rate;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.name = itemView.findViewById(R.id.reviewer_name);
            this.timeStamp = itemView.findViewById(R.id.review_timestamp);
            this.reviewText = itemView.findViewById(R.id.review_text);
            this.rate = itemView.findViewById(R.id.review_personal_rate);
        }
    }

}
