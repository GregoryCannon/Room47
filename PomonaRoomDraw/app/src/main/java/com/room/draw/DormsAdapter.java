package com.room.draw;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DormsAdapter extends RecyclerView.Adapter<DormsAdapter.MyViewHolder> {
    List<Dorm> dorms;

    public DormsAdapter(List<Dorm> dorms) {
        this.dorms = dorms;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Button dorm;
        public MyViewHolder(View view) {
            super(view);
            dorm = view.findViewById(R.id.dorm);
            dorm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    context.startActivity(new Intent(context, RoomSelectionActivity.class));
                }
            });
    }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dorm, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Dorm pDorm = dorms.get(position);
        holder.dorm.setText(pDorm.getName());
    }

    @Override
    public int getItemCount() {
        return dorms.size();
    }
    }