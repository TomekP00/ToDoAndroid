package com.example.pum_todo;

import android.provider.BaseColumns;

public class Category {
    private Category() {
    }

    public static class CategoryEntry implements BaseColumns {
        public static final String TABLE_CATEGORY = "Category";
        public static final String COLUMN_CATEGORY_NAME = "name";
    }
}
