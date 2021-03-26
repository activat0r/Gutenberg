package com.activator.gutenberg;

import android.graphics.drawable.Drawable;

public class CategoryModel {
    public Drawable getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(Drawable categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryModel(Drawable categoryIcon, String categoryName) {
        this.categoryIcon = categoryIcon;
        this.categoryName = categoryName;
    }

    Drawable categoryIcon;
    String categoryName;
}
