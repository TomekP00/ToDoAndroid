package com.example.pum_todo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<TodoItem> todoItems;
    private SQLiteDatabase db;
    private MainActivity mainActivity;
    private AdapterView.OnItemClickListener onItemClickListener;

    public Adapter(ArrayList<TodoItem> items, SQLiteDatabase db, MainActivity mainActivity) {
        this.todoItems = items;
        this.db = db;
        this.mainActivity = mainActivity;
    }

    public void setTodoItems(ArrayList<TodoItem> items) {
        todoItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TodoItem todoItem = todoItems.get(position);
        holder.titleTextView.setText(todoItem.getTitle());

        if (todoItem.isDone() == 1)
            holder.titleTextView.setTextColor(Color.GREEN);
        else
            holder.titleTextView.setTextColor(Color.BLACK);

        holder.materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int isDone = todoItem.isDone() == 0 ? 1 : 0;

                ContentValues values = new ContentValues();
                values.put(Todo.TodoEntry.COLUMN_TODO_DONE, isDone);

                String selection = Todo.TodoEntry._ID + " = ?";
                String[] selectionArgs = {String.valueOf(todoItem.getId())};

                db.update(Todo.TodoEntry.TABLE_TODO, values, selection, selectionArgs);

                todoItem.setDone(isDone);
                mainActivity.getTodoItems();
                setTodoItems(mainActivity.todoItems);
                notifyDataSetChanged();
            }
        });
        holder.titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TodoItem clickedItem = todoItems.get(position);
                int position = holder.getAdapterPosition();
                mainActivity.onItemClick(Integer.valueOf(clickedItem.getId()), position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MaterialTextView titleTextView;
        public MaterialButton materialButton;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            materialButton = itemView.findViewById(R.id.doneButton);
        }
    }

    public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = ItemTouchHelper.RIGHT;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            deleteItem(position);
        }
    }

    public void deleteItem(int position) {
        db.delete(Todo.TodoEntry.TABLE_TODO, Todo.TodoEntry._ID + " = ?", new String[]{todoItems.get(position).getId()});
        todoItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, todoItems.size());
    }

    public void editItem(String id, String title, String desc, String date, String time, String categoryId, String isDone) {
        ContentValues values = new ContentValues();
        values.put(Todo.TodoEntry.COLUMN_TODO_TITLE, title);
        values.put(Todo.TodoEntry.COLUMN_TODO_DESC, desc);
        values.put(Todo.TodoEntry.COLUMN_TODO_DUE_DATE, date);
        values.put(Todo.TodoEntry.COLUMN_TODO_DUE_TIME, time);
        values.put(Todo.TodoEntry.COLUMN_TODO_CATEGORY_ID, categoryId);
        values.put(Todo.TodoEntry.COLUMN_TODO_DONE, isDone);

        String selection = Todo.TodoEntry._ID + " = ?";

        db.update(Todo.TodoEntry.TABLE_TODO, values, selection, new String[]{id});
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = (AdapterView.OnItemClickListener) listener;
    }
}