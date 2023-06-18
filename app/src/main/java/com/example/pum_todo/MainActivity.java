package com.example.pum_todo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private FloatingActionButton addTodoBtn;

    List<String> titles;
    List<String> subtitles;
    Adapter adapter;

    private static final int TODO_ACTIVITY_REQUEST_CODE = 1;

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        dbHelper = new DBHelper(this);

        initRecyclerView();

        addTodoBtn = findViewById(R.id.floatingActionButton);
        addTodoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTodoActivity();
            }
        });

    }

    private void openTodoActivity() {
        Intent intent = new Intent(this, AddTodoActivity.class);
        intent.putExtra("test", "TuDziała");
        startActivityForResult(intent, TODO_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TODO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                Adapter adapter = new Adapter(titles, subtitles);
                recyclerView.setAdapter(adapter);
                int duration = Toast.LENGTH_SHORT;
                String title = data.getStringExtra("title");
                String date = data.getStringExtra("date");
                titles.add(title);
                subtitles.add(date);
                adapter.notifyDataSetChanged();
                CharSequence text = (CharSequence) "Dodano nowe zadanie";
                Toast toast = Toast.makeText(MainActivity.this, text, duration);
                toast.show();
            }
            else if (resultCode == RESULT_CANCELED) {
                int duration = Toast.LENGTH_SHORT;
                CharSequence text = (CharSequence) "Blad";
                Toast toast = Toast.makeText(MainActivity.this, text, duration);
                toast.show();
            }
        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        titles = new ArrayList<>();
        subtitles = new ArrayList<>();
        adapter = new Adapter(titles, subtitles);
        recyclerView.setAdapter(adapter);

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

}