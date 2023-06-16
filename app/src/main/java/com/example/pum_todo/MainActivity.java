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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private FloatingActionButton addTodoBtn;

    List<String> titles;
    List<String> subtitles;

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        dbHelper = new DBHelper(this);

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        titles = Arrays.asList("Tytuł 1", "Tytuł 2", "Tytuł 3");
        subtitles = Arrays.asList("Podtytuł 1", "Podtytuł 2", "Podtytuł 3");
        Adapter adapter = new Adapter(titles, subtitles);
        //adapter.addData("test", "test2");
        recyclerView.setAdapter(adapter);

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

        startActivity(intent);


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


    public void onToDoResult(Intent intent) {
            if (intent != null) {
                int duration = Toast.LENGTH_SHORT;
               String title = intent.getStringExtra("title");
               String date = intent.getStringExtra("date");
                Toast toast = Toast.makeText(this , 'D', duration);
                toast.show();
                //przetwarzanie
                //adapter.addData(title, date);
        }
    }


}