package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class PickupFulfillmentTitleDataItem implements IData {

    private String title;
    private Integer unFulfilledCount;
    private Integer fullfilledCount;
    private Integer totalCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUnfullfilledCount() {
        return unFulfilledCount;
    }

    public void setUnfullfilledCount(Integer unShippedCount) {
        this.unFulfilledCount = unShippedCount;
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



}
