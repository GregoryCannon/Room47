package com.room.draw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DormSelectionActivity extends AppCompatActivity {
    private List<Dorm> dorms = new ArrayList<>();
    private RecyclerView recyclerView;
    private DormsAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dorm_selection_activity);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new DormsAdapter(this,dorms);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        addDorms();
    }

    public void addDorms() {
        Dorm dorm = new Dorm("Gibson");
        dorms.add(dorm);
        dorm = new Dorm("Harwood");
        dorms.add(dorm);
        dorm = new Dorm("Lyon");
        dorms.add(dorm);
        dorm = new Dorm("Mudd-Blaisdell");
        dorms.add(dorm);
        dorm = new Dorm("Oldenberg");
        dorms.add(dorm);
        dorm = new Dorm("Smiley");
        dorms.add(dorm);
        dorm = new Dorm("Wig");
        dorms.add(dorm);
        dorm = new Dorm("Clark I");
        dorms.add(dorm);
        dorm = new Dorm("Clark V");
        dorms.add(dorm);
        dorm = new Dorm("Lawry Court");
        dorms.add(dorm);
        dorm = new Dorm("Norton-Clark III");
        dorms.add(dorm);
        dorm = new Dorm("Dialynas");
        dorms.add(dorm);
        dorm = new Dorm("Sontag");
        dorms.add(dorm);
        dorm = new Dorm("Walker");
        dorms.add(dorm);

        mAdapter.notifyDataSetChanged();
    }
}
