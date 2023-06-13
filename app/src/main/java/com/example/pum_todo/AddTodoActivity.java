package com.example.pum_todo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTodoActivity extends AppCompatActivity {

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

        Button button = findViewById(R.id.button);
        TextInputLayout textFiledCalendar = findViewById(R.id.textField3);

        textFiledCalendar.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
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
    }
}