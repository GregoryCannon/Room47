package com.room.draw;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomSelectionActivity extends AppCompatActivity {
    private List<Room> rooms = new ArrayList<>();
    private RecyclerView recyclerView;
    private RoomAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_selection_activity);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_rooms);
        mAdapter = new RoomAdapter(this,rooms);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        addRooms();

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

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}