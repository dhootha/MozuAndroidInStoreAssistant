package com.mozu.mozuandroidinstoreassistant.app.tasks;

public interface CategoryImageUpdateListener {
    public void onImageUpdateSucces(String categoryName,String categoryId);
    public void onImageUpdateFailure(String message);
}
