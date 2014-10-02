package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class FullfillmentTitleDataItem implements IData {

    private String title;
    private Integer pendingCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(Integer pendingCount) {
        this.pendingCount = pendingCount;
    }

    public Integer getFullfilledCount() {
        return fullfilledCount;
    }

    public void setFullfilledCount(Integer fullfilledCount) {
        this.fullfilledCount = fullfilledCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    private Integer fullfilledCount;
    private Integer totalCount;

}
