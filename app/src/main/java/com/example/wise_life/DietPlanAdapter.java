package com.example.wise_life;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class DietPlanAdapter extends RecyclerView.Adapter<DietPlanAdapter.DietPlanViewHolder> {

    private ArrayList<DietPlanHelperClass> dietPlans;
    private OnItemClickListener listener;

    public DietPlanAdapter(ArrayList<DietPlanHelperClass> dietPlans, OnItemClickListener listener) {
        this.dietPlans = dietPlans;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return dietPlans.size();
    }

    @NonNull
    @Override
    public DietPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_plan, parent, false);
        return new DietPlanViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DietPlanViewHolder holder, int position) {
        DietPlanHelperClass dietPlanHelperClass = dietPlans.get(position);
        holder.image.setImageResource(dietPlanHelperClass.getImage());
        holder.title.setText(dietPlanHelperClass.getTitle());
        holder.desc.setText(dietPlanHelperClass.getDescription());
    }

    public static class DietPlanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView title, desc;
        OnItemClickListener listener;

        public DietPlanViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            this.listener = listener;

            //Hooks
            image = itemView.findViewById(R.id.featured_image);
            title = itemView.findViewById(R.id.featured_title);
            desc = itemView.findViewById(R.id.featured_desc);

            // Set click listener for the whole item view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }
    }
}
