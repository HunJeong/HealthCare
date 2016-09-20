package com.example.jeonghun.heathcare;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by JeongHun on 2016. 9. 20..
 */
public class TwoData extends RealmObject{

    private int left;
    private int right;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "TwoData{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
