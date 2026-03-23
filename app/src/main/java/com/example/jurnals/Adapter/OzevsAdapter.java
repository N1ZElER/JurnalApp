package com.example.jurnals.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jurnals.API.ApiService;
import com.example.jurnals.Models.Ozevs;
import com.example.jurnals.R;

import java.util.ArrayList;
import java.util.List;

public class OzevsAdapter extends RecyclerView.Adapter<OzevsAdapter.ViewHolder> {

    private final ApiService apiService;
    private List<Ozevs> ozevsList = new ArrayList<>();


    public OzevsAdapter(ApiService apiService, List<Ozevs> ozevsList) {
        this.apiService = apiService;
        this.ozevsList = ozevsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ozevs, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ozevs item = ozevsList.get(position);

        holder.teacher.setText(item.getTeacher());
        holder.message.setText(item.getMessage());
        holder.itemView.setOnClickListener(v ->{});
    }


    @Override
    public int getItemCount() {
        return ozevsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView teacher, message, detailTextView;

        public ViewHolder(@NonNull View view) {
            super(view);

            teacher = view.findViewById(R.id.teacher);
            message = view.findViewById(R.id.message);
            detailTextView = view.findViewById(R.id.detailTextView);
        }
    }
}