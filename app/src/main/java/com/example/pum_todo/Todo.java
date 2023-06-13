package com.example.pum_todo;

import android.provider.BaseColumns;

public final class Todo {
    private Todo() {
    }

    public static class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "Todo";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESC = "description";
        public static final String COLUMN_NAME_DONE = "done";
    }

}
