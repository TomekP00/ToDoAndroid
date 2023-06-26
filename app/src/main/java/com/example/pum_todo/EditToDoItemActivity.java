package com.example.pum_todo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditToDoItemActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    Button saveButton;
    Button DiscardChanges;
    private String categoryId = "1";
    private String itemId;
    private EditText date;
    private EditText time;
    private String isDone;
    private int position;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_to_do_item);

        Toolbar toolbar2 = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar2);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Edycja");
        }

        saveButton = findViewById(R.id.saveButton);
        DiscardChanges = findViewById(R.id.DiscardChanges);
        EditText title = findViewById(R.id.title);
        EditText note = findViewById(R.id.note);
        TextInputLayout textFiledCalendar = findViewById(R.id.textField3);
        TextInputLayout textFiledTime = findViewById(R.id.textField4);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        spinner = findViewById(R.id.spinner);

        List<String> options = new ArrayList<>();
        options.add("Wykonano");
        options.add("Nie wykonano");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter2);

        dbHelper = new DBHelper(this);
        itemId = getIntent().getStringExtra("IDitemu");
        position = getIntent().getIntExtra("position2", -1);
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

            int selectedPosition;
            if (ToDodone == 1) {
                selectedPosition = 0;
            } else {
                selectedPosition = 1;
            }

            spinner.setSelection(selectedPosition);



            title.setText(ToDotitle);

            note.setText(ToDodescription);

            date = textFiledCalendar.getEditText();
            date.setText(ToDodueDate);

            time = textFiledTime.getEditText();
            time.setText(ToDodueTime);

            autoCompleteTextView.setText(ToDocategory);

            /*if(ToDodone == 1)
                done.setText("Zrobione");
            else
                done.setText("Nie zrobione");*/

        }
        cursor.close();

        cursor = db.query(Category.CategoryEntry.TABLE_CATEGORY, null, null, null, null, null, null);
        ArrayList<CategoryItem> categories = new ArrayList<CategoryItem>();

        while (cursor.moveToNext()) {
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(Category.CategoryEntry.COLUMN_CATEGORY_NAME));
            int categoryID = cursor.getInt(cursor.getColumnIndexOrThrow(Category.CategoryEntry._ID));

            CategoryItem item = new CategoryItem(categoryID, categoryName);
            categories.add(item);
        }

        cursor.close();


        if (categories.size() > 0) {
            CategoryItem defaultCategory = categories.get(0);
            autoCompleteTextView.setText(defaultCategory.getName());
            categoryId = String.valueOf(defaultCategory.getId());
        }

        ArrayAdapter<CategoryItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryItem selectedCategory = (CategoryItem) parent.getItemAtPosition(position);
                categoryId = String.valueOf(selectedCategory.getId());
            }
        });

        textFiledCalendar.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = new SimpleDateFormat("MM-dd-yyy", Locale.getDefault()).format(new Date(selection));
                        TextInputEditText editText = textFiledCalendar.findViewById(R.id.calendarInput);
                        editText.setText(date);
                    }
                });
                datePicker.show(getSupportFragmentManager(), "tag");
            }
        });

        textFiledTime.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(45)
                        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                        .build();
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int newHour = timePicker.getHour();
                        int newMinute = timePicker.getMinute();

                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", newHour, newMinute);
                        TextInputEditText editText = textFiledTime.findViewById(R.id.timeInput);
                        editText.setText(formattedTime);
                    }
                });
                timePicker.show(getSupportFragmentManager(), "tag");
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = options.get(position);

                if(selectedOption.equals("Wykonano"))
                isDone = "1";
                else
                    isDone = "0";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                try {
                    ContentValues values = new ContentValues();
                    String correctTitle = title.getText().toString();
                    values.put(Todo.TodoEntry.COLUMN_TODO_TITLE, correctTitle);
                    values.put(Todo.TodoEntry.COLUMN_TODO_DESC, note.getText().toString());
                    values.put(Todo.TodoEntry.COLUMN_TODO_DUE_DATE, date.getText().toString());
                    values.put(Todo.TodoEntry.COLUMN_TODO_DUE_TIME, time.getText().toString());
                    values.put(Todo.TodoEntry.COLUMN_TODO_CATEGORY_ID, categoryId);
                    values.put(Todo.TodoEntry.COLUMN_TODO_DONE, isDone);

                    String selection = Todo.TodoEntry._ID + " = ?";
                    String[] selectionArgs = {itemId};

                    int rowsAffected = db.update(Todo.TodoEntry.TABLE_TODO, values, selection, selectionArgs);

                    if (rowsAffected > 0) {
                        Toast.makeText(EditToDoItemActivity.this, "Dane zaktualizowane", Toast.LENGTH_SHORT).show();
                        MainActivity.adapter.notifyItemChanged(position);
                        //MainActivity.adapter.notifyDataSetChanged();
                        setResult(2115);
                        finish();
                    } else {
                        Toast.makeText(EditToDoItemActivity.this, "Wystąpił problem podczas aktualizacji danych.", Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    db.close();
                }
            }
        });

        DiscardChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.close();
                finish();
            }
        });
        db.close();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            dbHelper.close();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}