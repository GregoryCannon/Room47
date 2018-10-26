package com.room.draw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RoomSelectionActivity extends AppCompatActivity {
    List<Room> rooms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_selection_activity);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        addRooms();
        gridview.setAdapter(new RoomAdapter(this, rooms));
    }

    public void addRooms() {
        rooms.add(new Room(1));
        rooms.add(new Room(2));
        rooms.add(new Room(3));
        rooms.add(new Room(4));
        rooms.add(new Room(5));
        rooms.add(new Room(6));
        rooms.add(new Room(7));
        rooms.add(new Room(8));
        rooms.add(new Room(9));
        rooms.add(new Room(10));
    }

}
