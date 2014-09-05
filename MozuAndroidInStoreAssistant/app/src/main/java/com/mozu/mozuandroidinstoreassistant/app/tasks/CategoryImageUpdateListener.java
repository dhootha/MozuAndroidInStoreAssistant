package com.mozu.mozuandroidinstoreassistant.app.tasks;

/**
 * Created by santhosh_mankala on 9/5/14.
 */
public interface CategoryImageUpdateListener {
    public void onImageUpdateSucces(String categoryName,String categoryId);
    public void onImageUpdateFailure(String message);
}
