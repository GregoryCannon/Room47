package com.room.draw;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import SSLPackage.Connection;
import SSLPackage.ServerPacket;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DormsAdapter extends RecyclerView.Adapter<DormsAdapter.MyViewHolder> {
    public static String dormName;
    private static List<Dorm> dorms;
    private static ServerPacket response;
    private Context mContext;

    public DormsAdapter(Context context, List<Dorm> dorms) {
        this.mContext = context;
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

                    dormName = dorm.getText().toString();
                    try {
                        new SslClientToServer().execute((Object) null).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    setOccupiedRooms(dorm, view);
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

    public static String getDormName() {
        return dormName;
    }

    public void setOccupiedRooms(Button dorm, View view) {
        Set<String> occupiedRooms = response.occupiedRooms;
        RoomAdapter.setOccupiedRooms(occupiedRooms);
        Context context = view.getContext();
        context.startActivity(new Intent(context, RoomSelectionActivity.class));
    }

    public static List<Dorm> getDorms() {
        return dorms;
    }

    private class SslClientToServer extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                response = Connection.getOccupiedRooms(DashboardActivity.getUsername(), DashboardActivity.getPassword(), dormName);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
