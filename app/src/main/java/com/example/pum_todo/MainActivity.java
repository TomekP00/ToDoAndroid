package com.example.pum_todo;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private FloatingActionButton addTodoBtn;
    ArrayList<TodoItem> todoItems;
    ArrayList<CategoryItem> categoryItems;
    static Adapter adapter;
    private static final int TODO_ACTIVITY_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_2 = 2;
    private static final int REQUEST_CODE_3 = 3;
    RecyclerView recyclerView;
    NavigationView navigationView;
    TextView textViewCategory;
    ChipGroup chipGroup;
    String IDitemID;
    private int chooseCategoryID = 0;
    private int selectedTaskState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        navigationView = findViewById(R.id.navigationView);
        textViewCategory = findViewById(R.id.textViewCategory);
        chipGroup = findViewById(R.id.chipGroup);
        addTodoBtn = findViewById(R.id.floatingActionButton);

        textViewCategory.setText("Domyślna");

        createNotificationChannel();

        dbHelper = new DBHelper(this);

        initRecyclerView();
        loadCategoryToMenu();

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

                findViewById(item.getItemId()).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (id == R.id.addCategory || id == R.id.allCategory || id == 1)
                            return false;

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                        builder.setTitle("Czy na pewno chcesz usunąć tą kategorię");

                        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeCategory(item.getItemId());
                            }
                        });
                        builder.setNegativeButton("Nie", null);
                        builder.show();
                        return true;
                    }
                });

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
                    return false;
                } else if (id == R.id.allCategory) {
                    chooseCategoryID = 0;
                    getTodoItems();
                    textViewCategory.setText(item.getTitle());
                    adapter.setTodoItems(todoItems);
                    adapter.notifyDataSetChanged();
                    return true;
                } else {
                    chooseCategoryID = id;
                    getTodoItems();
                    textViewCategory.setText(item.getTitle());
                    adapter.setTodoItems(todoItems);
                    adapter.notifyDataSetChanged();
                    return true;
                }
            }
        });
        findViewById(R.id.chipDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTaskState = 1;
                getTodoItems();
                adapter.setTodoItems(todoItems);
                adapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.chipUndone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTaskState = 0;
                getTodoItems();
                adapter.setTodoItems(todoItems);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void openTodoActivity() {
        Intent intent = new Intent(this, AddTodoActivity.class);
        intent.putExtra("test", "TuDziała");
        startActivityForResult(intent, TODO_ACTIVITY_REQUEST_CODE);
    }

    public void onItemClick(int itemID, int position) {
        Intent intent = new Intent(MainActivity.this, AllInfo.class);
        intent.putExtra("IDitemutest", String.valueOf(itemID));
        intent.putExtra("position", position);
        startActivityForResult(intent, REQUEST_CODE_2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TODO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String title = data.getStringExtra("title");
                String note = data.getStringExtra("note");
                String date = data.getStringExtra("date");
                String time = data.getStringExtra("time");
                String categoryID = data.getStringExtra("categoryID");
                String categoryIDCorrect = !Objects.equals(data.getStringExtra("categoryID"), "1") ? categoryID : "1";
                LocalDateTime now = LocalDateTime.now();

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(Todo.TodoEntry.COLUMN_TODO_TITLE, title);
                values.put(Todo.TodoEntry.COLUMN_TODO_DESC, note);
                values.put(Todo.TodoEntry.COLUMN_TODO_DUE_DATE, date);//dd-MM-yyyy
                values.put(Todo.TodoEntry.COLUMN_TODO_DUE_TIME, time);//HH:mm
                values.put(Todo.TodoEntry.COLUMN_TODO_DONE, 0);
                values.put(Todo.TodoEntry.COLUMN_TODO_CREATED_AT, now.toString());
                values.put(Todo.TodoEntry.COLUMN_TODO_CATEGORY_ID, categoryIDCorrect);

                db.insert(Todo.TodoEntry.TABLE_TODO, null, values);

                getTodoItems();
                adapter.setTodoItems(todoItems);
                adapter.notifyDataSetChanged();

                if (!date.isEmpty() && !time.isEmpty()) {
                    long timeAt = timeMillis(date, time, now);
                    setAlarmNotification(title, "Popraw się", timeAt);
                }

                CharSequence text = "Dodano nowe zadanie";
                showToast(text, Toast.LENGTH_SHORT);
            }
        } else if (requestCode == REQUEST_CODE_2) {

            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position", -1);
                adapter.deleteItem(position);
            } else if (resultCode == 420) {
                String id = data.getStringExtra("IDitemu");
                int position = data.getIntExtra("position", -1);


                IDitemID = id;
                Intent edit = new Intent(MainActivity.this, EditToDoItemActivity.class);
                edit.putExtra("IDitemu", id);
                edit.putExtra("position2", position);
                startActivityForResult(edit, REQUEST_CODE_3);

            }
        } else if (requestCode == REQUEST_CODE_3) {
            if (resultCode == 2115) {
                getTodoItems();
                initRecyclerView();
            }
        }
    }

    private void showToast(CharSequence text, int duration) {
        Toast.makeText(MainActivity.this, text, duration).show();
    }

    private void setAlarmNotification(String title, String text, long timeAt) {
        Intent intent = new Intent(this, ReminderBroadcast.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, Intent.FILL_IN_DATA | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeAt, pendingIntent);
    }

    private long timeMillis(String date, String time, LocalDateTime now) {
        LocalDate reminderDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        LocalTime reminderTime = LocalTime.parse(time);

        LocalDateTime reminderDateTime = LocalDateTime.of(reminderDate, reminderTime);
        Duration duration = Duration.between(now, reminderDateTime);
        return duration.toMillis();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getTodoItems();
        adapter = new Adapter(todoItems, dbHelper.getWritableDatabase(), this);
        recyclerView.setAdapter(adapter);

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.notifyDataSetChanged();
    }

    public void getTodoItems() {
        todoItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] todoList = {
                Todo.TodoEntry._ID,
                Todo.TodoEntry.COLUMN_TODO_TITLE,
                Todo.TodoEntry.COLUMN_TODO_DESC,
                Todo.TodoEntry.COLUMN_TODO_DONE
        };

        Cursor cursor;
        if (chooseCategoryID == 0) {
            cursor = db.query(Todo.TodoEntry.TABLE_TODO, todoList,
                    Todo.TodoEntry.COLUMN_TODO_DONE + " = ?",
                    new String[]{String.valueOf(selectedTaskState)},
                    null,
                    null,
                    null);
        } else {
            cursor = db.query(Todo.TodoEntry.TABLE_TODO, todoList,
                    Todo.TodoEntry.COLUMN_TODO_CATEGORY_ID + " = ? AND " +
                            Todo.TodoEntry.COLUMN_TODO_DONE + " = ?",
                    new String[]{String.valueOf(chooseCategoryID), String.valueOf(selectedTaskState)},
                    null,
                    null,
                    null);
        }

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
        menu.clear();

        MenuItem addCategoryItem = menu.add(Menu.NONE, R.id.addCategory, Menu.NONE, "Dodaj kategorię");
        addCategoryItem.setCheckable(true);
        MenuItem allCategoryItem = menu.add(Menu.NONE, R.id.allCategory, Menu.NONE, "Wszystko");
        allCategoryItem.setCheckable(true);

        for (CategoryItem category : categoryItems) {
            MenuItem menuItem = menu.add(Menu.NONE, category.getId(), Menu.NONE, category.getName());
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
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Category.CategoryEntry._ID));
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

    private void removeCategory(int categoryID) {
        boolean todosExist = checkIfTodosExistForCategory(categoryID);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (todosExist) {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Todo.TodoEntry.COLUMN_TODO_CATEGORY_ID, 1);
            String selection = Todo.TodoEntry.COLUMN_TODO_CATEGORY_ID + " = ?";
            String[] selectionArgs = {String.valueOf(categoryID)};
            db.update(Todo.TodoEntry.TABLE_TODO, values, selection, selectionArgs);
        }

        String selection = Category.CategoryEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(categoryID)};
        db.delete(Category.CategoryEntry.TABLE_CATEGORY, selection, selectionArgs);

        loadCategoryToMenu();
        getTodoItems();
        adapter.setTodoItems(todoItems);
        adapter.notifyDataSetChanged();
    }

    private boolean checkIfTodosExistForCategory(int categoryID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = Todo.TodoEntry.COLUMN_TODO_CATEGORY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(categoryID)};
        Cursor cursor = db.query(
                Todo.TodoEntry.TABLE_TODO,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean todosExist = cursor.moveToFirst();
        cursor.close();
        return todosExist;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PumReminderChannel";
            String description = "Channel for Pum Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyPum", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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