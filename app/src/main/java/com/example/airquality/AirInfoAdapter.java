package com.example.airquality;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AirInfoAdapter extends RecyclerView.Adapter<AirInfoAdapter.ViewHolder> {

    List<AirInfo> airInfoList;

    LayoutInflater inflater;

    public AirInfoAdapter(List<AirInfo> airInfoList, Context context)
    {
        this.airInfoList = airInfoList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AirInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.index_level, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AirInfoAdapter.ViewHolder holder, int position) {
        AirInfo airInfo = airInfoList.get(position);
        holder.airInfo = airInfo;
        holder.indexName.setText(airInfo.getName());
        holder.indexDesc.setText(airInfo.getDescription());
        holder.distance.setText("Odległość od pomiaru: "+(String.format("%.2f",airInfo.getDistance()))+" Km");
        if(holder.airInfo.getLevel()+1>=0) {
            holder.progressBar.setProgress(airInfo.getLevel()+1);
        }


    }

    @Override
    public int getItemCount() {
        return airInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AirInfo airInfo;
        ProgressBar progressBar;
        TextView indexName;
        TextView indexDesc;

        TextView distance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            indexName = itemView.findViewById(R.id.indexName);
            indexDesc = itemView.findViewById(R.id.indexDesc);
            progressBar = itemView.findViewById(R.id.progressBar);
            distance = itemView.findViewById(R.id.distance);

        }
    }
}



