package com.example.jeonghun.heathcare;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by JeongHun on 2016. 9. 20..
 */
public class RecordData extends RealmObject {

    private Date today;
    private String sportTime;
    private RealmList<TwoData> twoDatas;

    public String getSportTime() {
        return sportTime;
    }

    public void setSportTime(String sportTime) {
        this.sportTime = sportTime;
    }
    
    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    public RealmList<TwoData> getTwoDatas() {
        return twoDatas;
    }

    public void setTwoDatas(RealmList<TwoData> twoDatas) {
        this.twoDatas = twoDatas;
    }
}
