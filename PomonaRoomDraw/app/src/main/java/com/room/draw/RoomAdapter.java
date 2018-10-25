package com.room.draw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RoomAdapter extends BaseAdapter {
    private Context mContext;
    List<Room> rooms;

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
                System.out.println("hereeer");
                Toast.makeText(mContext, "Congratulations on picking a room!", Toast.LENGTH_LONG).show();
            }
        });
        return imageView;
    }
}
