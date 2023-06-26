package com.example.pum_todo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

public class AllInfo extends AppCompatActivity {
    private DBHelper dbHelper;
    Button deleteBtn;
    Button editBtn;

    private String itemId;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_info);

        deleteBtn = findViewById(R.id.DeleteBtn);
        editBtn = findViewById(R.id.EditBtn);
        Toolbar toolbar2 = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar2);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Info");
        }

        EditText title = findViewById(R.id.title);
        EditText note = findViewById(R.id.note);
        TextInputLayout textFiledCalendar = findViewById(R.id.textField3);
        TextInputLayout textFiledTime = findViewById(R.id.textField4);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        TextView done = findViewById(R.id.done);

        dbHelper = new DBHelper(this);
        itemId = getIntent().getStringExtra("IDitemutest");
        position = getIntent().getIntExtra("position", -1);
        String[] projection = {
                "title",
                "description",
                "due_date",
                "due_time",
                "done",
                "category_name"
        };
        String selection = "_id = ?";
        String[] selectionArgs = { String.valueOf(itemId) };
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT Todo.title, Todo.description, Todo.due_date, Todo.due_time, Todo.done, Category.name " +
                "FROM Todo " +
                "LEFT JOIN Category ON Todo.category_id = Category._id " +
                "WHERE Todo._id = ?";
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            String ToDotitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String ToDodescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String ToDodueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"));
            String ToDodueTime = cursor.getString(cursor.getColumnIndexOrThrow("due_time"));
            int ToDodone = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("done")));
            String ToDocategory = cursor.getString(cursor.getColumnIndexOrThrow("name"));




            title.setText(ToDotitle);

            note.setText(ToDodescription);

            EditText date = textFiledCalendar.getEditText();
            date.setText(ToDodueDate);

            EditText time = textFiledTime.getEditText();
            time.setText(ToDodueTime);

            autoCompleteTextView.setText(ToDocategory);

            if(ToDodone == 1)
                done.setText("Zrobione");
            else
                done.setText("Nie zrobione");

        }
        cursor.close();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("position", position);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent();
                editIntent.putExtra("IDitemu", itemId);
                editIntent.putExtra("position", position);
                setResult(420, editIntent);
                finish();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}