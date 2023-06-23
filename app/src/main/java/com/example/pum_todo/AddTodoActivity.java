package com.example.pum_todo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.Locale;

public class AddTodoActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private String categoryId = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_todo);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Dodaj zadanie");
        }

        EditText title = findViewById(R.id.title);
        EditText note = findViewById(R.id.note);
        Button button = findViewById(R.id.button);
        TextInputLayout textFiledCalendar = findViewById(R.id.textField3);
        TextInputLayout textFiledTime = findViewById(R.id.textField4);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Category.CategoryEntry.TABLE_CATEGORY, null, null, null, null, null, null);
        ArrayList<CategoryItem> categories = new ArrayList<CategoryItem>();

        while (cursor.moveToNext()) {
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(Category.CategoryEntry.COLUMN_CATEGORY_NAME));
            int categoryID = cursor.getInt(cursor.getColumnIndexOrThrow(Category.CategoryEntry._ID));

            CategoryItem item = new CategoryItem(categoryID, categoryName);
            categories.add(item);
        }

        cursor.close();
        dbHelper.close();

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
                        .setTitleText("Wybierz date")
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
                        .setTitleText("Wybierz datę")
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();

                String correctTitle = title.getText().toString();
                String correctNote = note.getText().toString();
                EditText editText = textFiledCalendar.getEditText();
                EditText timeText = textFiledTime.getEditText();
                String correctDate = editText.getText().toString();
                String correctTime = timeText.getText().toString();

                resultIntent.putExtra("title", correctTitle);
                resultIntent.putExtra("note", correctNote);
                resultIntent.putExtra("date", correctDate);
                resultIntent.putExtra("time", correctTime);
                resultIntent.putExtra("categoryID", categoryId);

                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });
    }
}