package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class ShipmentFulfillmentTitleDataItem implements IData {

    private String title;
    private Integer unShippedCount;
    private Integer fullfilledCount;
    private Integer totalCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUnShippedCount() {
        return unShippedCount;
    }

    public void setUnShippedCount(Integer unShippedCount) {
        this.unShippedCount = unShippedCount;
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
