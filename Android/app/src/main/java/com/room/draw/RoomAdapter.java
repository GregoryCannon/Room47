package com.room.draw;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
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
import java.util.concurrent.ExecutionException;

import SSLPackage.Connection;
import SSLPackage.ServerPacket;

public class RoomAdapter extends BaseAdapter {
    private Context mContext;
    List<Room> rooms;
    private static String username;
    private static String password;
    private static String dormName;
    private static String roomNumber;
    private static Button currentRoom;

    private static ServerPacket response=null;

    public RoomAdapter(Context mContext, List<Room> rooms) {
        this.mContext = mContext;
        this.rooms = rooms;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, final ViewGroup viewGroup) {
        Button imageView;

        if (view == null) {
            imageView = new Button(mContext);
            System.out.println(imageView);
            imageView.setText(String.valueOf(rooms.get(i).getNumber()));

        } else {
            imageView = (Button) view;
            imageView.setText(String.valueOf(rooms.get(i).getNumber()));
        }

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                currentRoom = imageView;
                Toast.makeText(mContext, "Congratulations on picking a room!", Toast.LENGTH_LONG).show();
                dormName = DormsAdapter.getDormName();
                roomNumber = imageView.getText().toString();
                try {
                    new SslClientToServer().execute((Object) null).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }
        });
        return imageView;
    }

    public static void setUsername(String data) {
        username = data;
    }

    public static void setPassword(String pass) {
        password = pass;
    }

    private class SslClientToServer extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                response = Connection.requestRoom(username, password, dormName, roomNumber);
                String message = response.message;
                if (message.equals("Room reserved!")) {
                    currentRoom.setTextColor(Color.BLUE);
                }
                rooms.remove(roomNumber);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
