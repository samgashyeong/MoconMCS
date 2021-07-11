package com.example.moconmcs.Main.FoodMap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moconmcs.R;

import java.util.List;

public class PlaceSearchAdapter extends RecyclerView.Adapter<PlaceSearchAdapter.CustomViewHolder> {

    private final List<Placemark> arrayList;
    private final OnSearchItemClick onSearchItemClick;

    public PlaceSearchAdapter(List<Placemark> arrayList, OnSearchItemClick onSearchItemClick) {
        this.arrayList = arrayList;
        this.onSearchItemClick = onSearchItemClick;
    }

    @NonNull
    @Override
    public PlaceSearchAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_place_item, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaceSearchAdapter.CustomViewHolder holder, int position) {
        holder.name.setText(arrayList.get(position).getName());
        holder.desc.setText(arrayList.get(position).getDesc());
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

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView name, desc;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.name = itemView.findViewById(R.id.searchPlaceName);
            this.desc = itemView.findViewById(R.id.searchPlaceDesc);

            itemView.setOnClickListener(v -> onSearchItemClick.onClick(getAdapterPosition()));
        }
    }

    public static interface OnSearchItemClick {
        void onClick(int position);
    }

}
