package com.room.draw;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import SSLPackage.Connection;
import SSLPackage.ServerPacket;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.MyViewHolder> {
    private Context mContext;
    List<Room> rooms;
    private static String dormName;
    private static String roomNumber;
    private static Button currentRoom;
    private static Set<String> occupiedRooms;

    private static ServerPacket response = null;

    public RoomAdapter(Context mContext, List<Room> rooms) {
        this.mContext = mContext;
        this.rooms = rooms;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        Button room;

        public MyViewHolder(View view) {
            super(view);
            room = view.findViewById(R.id.room);
            room.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentRoom = room;
                    dormName = DormsAdapter.getDormName();
                    roomNumber = room.getText().toString();
                    try {
                        new SslClientToServer().execute((Object) null).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    requestRoom();
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.room, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Room pRoom = rooms.get(position);
        holder.room.setText(Integer.toString(pRoom.getNumber()));
        if (occupiedRooms.size() > 0) {
            String pos = position+1+"";
            if (occupiedRooms.contains(pos)) {
                holder.room.setTextColor(Color.BLUE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void requestRoom() {
        String message = response.message;
        if (message.equals("Room reserved!")) {
            currentRoom.setTextColor(Color.BLUE);
            final ProgressDialog progressDialog = new ProgressDialog(mContext,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Choosing Room...");
            progressDialog.show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 3000);
            mContext.startActivity(new Intent(mContext, DashboardActivity.class));
        } else {
            Toast.makeText(mContext, "The room is occupied or your registration window has passed", Toast.LENGTH_LONG).show();

        }
    }

    public static void setOccupiedRooms(Set<String> set) {
        occupiedRooms = set;
    }

    private class SslClientToServer extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                response = Connection.requestRoom(DashboardActivity.getUsername(), DashboardActivity.getPassword(), dormName, roomNumber);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
