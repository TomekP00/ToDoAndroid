package com.example.pum_todo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private FloatingActionButton addTodoBtn;
    ArrayList<TodoItem> todoItems;
    ArrayList<CategoryItem> categoryItems;
    Adapter adapter;
    private static final int TODO_ACTIVITY_REQUEST_CODE = 1;
    RecyclerView recyclerView;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        navigationView = findViewById(R.id.navigationView);

        dbHelper = new DBHelper(this);

        initRecyclerView();
        loadCategoryToMenu();

        addTodoBtn = findViewById(R.id.floatingActionButton);
        addTodoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTodoActivity();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.addCategory) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                    builder.setTitle("Dodaj nową kategorię");

                    final EditText inputEditText = new EditText(MainActivity.this);
                    inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(inputEditText);

                    builder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String inputValue = inputEditText.getText().toString().trim();
                            addCategory(inputValue);
                        }
                    });
                    builder.setNegativeButton("Anuluj", null);
                    builder.show();
                    return true;
                } else {
                    showToast("id: " + id, Toast.LENGTH_SHORT);
                    return false;
                }
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
        if (requestCode == TODO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                Adapter adapter = new Adapter(todoItems, dbHelper.getWritableDatabase());
                recyclerView.setAdapter(adapter);

                String title = data.getStringExtra("title");
                String note = data.getStringExtra("note");
                String date = data.getStringExtra("date");

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(Todo.TodoEntry.COLUMN_TODO_TITLE, title);
                values.put(Todo.TodoEntry.COLUMN_TODO_DESC, note);
                values.put(Todo.TodoEntry.COLUMN_TODO_DUE_DATE, date);
                values.put(Todo.TodoEntry.COLUMN_TODO_DONE, 0);

                db.insert(Todo.TodoEntry.TABLE_TODO, null, values);

                getTodoItems();
                adapter.setTodoItems(todoItems);
                adapter.notifyDataSetChanged();

                CharSequence text = "Dodano nowe zadanie";
                showToast(text, Toast.LENGTH_SHORT);
            } else if (resultCode == RESULT_CANCELED) {
                CharSequence text = "Blad";
                showToast(text, Toast.LENGTH_SHORT);
            }
        }
    }

    private void showToast(CharSequence text, int duration) {
        Toast.makeText(MainActivity.this, text, duration).show();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getTodoItems();
        adapter = new Adapter(todoItems, dbHelper.getWritableDatabase());
        recyclerView.setAdapter(adapter);

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.notifyDataSetChanged();
    }

    private void getTodoItems() {
        todoItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] todoList = {
                Todo.TodoEntry._ID,
                Todo.TodoEntry.COLUMN_TODO_TITLE,
                Todo.TodoEntry.COLUMN_TODO_DESC,
                Todo.TodoEntry.COLUMN_TODO_DONE
        };

        Cursor cursor = db.query(Todo.TodoEntry.TABLE_TODO, todoList, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(Todo.TodoEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(Todo.TodoEntry.COLUMN_TODO_TITLE));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(Todo.TodoEntry.COLUMN_TODO_DESC));
            int done = cursor.getInt(cursor.getColumnIndexOrThrow(Todo.TodoEntry.COLUMN_TODO_DONE));

            TodoItem item = new TodoItem(id, title, desc, done);
            todoItems.add(item);
        }
        cursor.close();
    }

    private void loadCategoryToMenu() {
        getCategoryItems();

        Menu menu = navigationView.getMenu();

        for (CategoryItem category : categoryItems) {
            MenuItem menuItem = menu.add(Menu.NONE, Integer.parseInt(category.getId()), Menu.NONE, category.getName());
            menuItem.setCheckable(true);
        }
    }

    private void getCategoryItems() {
        categoryItems = new ArrayList<CategoryItem>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] categoryList = {
                Category.CategoryEntry._ID,
                Category.CategoryEntry.COLUMN_CATEGORY_NAME
        };

        Cursor cursor = db.query(Category.CategoryEntry.TABLE_CATEGORY, categoryList, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(Category.CategoryEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(Category.CategoryEntry.COLUMN_CATEGORY_NAME));

            CategoryItem item = new CategoryItem(id, name);
            categoryItems.add(item);
        }
        cursor.close();
    }

    private void addCategory(String categoryName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Category.CategoryEntry.COLUMN_CATEGORY_NAME, categoryName);
        db.insert(Category.CategoryEntry.TABLE_CATEGORY, null, values);
        loadCategoryToMenu();
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

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}