package com.example.pum_todo;

import android.provider.BaseColumns;

public final class Todo {
    private Todo() {
    }

    public static class TodoEntry implements BaseColumns {
        public static final String TABLE_TODO = "Todo";
        public static final String COLUMN_TODO_TITLE = "title";
        public static final String COLUMN_TODO_DESC = "description";
        public static final String COLUMN_TODO_DUE_DATE = "due_date";
        public static final String COLUMN_TODO_DONE = "done";
        public static final String COLUMN_TODO_CREATED_AT = "created_at";
        public static final String COLUMN_TODO_CATEGORY_ID = "category_id";
    }
}
