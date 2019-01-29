package com.ruijiazha.reviewer.data;

public class ReviewShow {

    //this is the data structure for profile

    private String time;
    private int rates;
    private String appName;
    private int reviewid;

    public int getReviewid() {
        return reviewid;
    }

    public void setReviewid(int reviewid) {
        this.reviewid = reviewid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRates() {
        return rates;
    }

    public void setRates(int rates) {
        this.rates = rates;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
