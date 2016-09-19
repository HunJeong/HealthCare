package com.example.jeonghun.heathcare.DB;

/**
 * Created by JeongHun on 16. 5. 24..
 */
public class DataTable {

    private String name;
    private int age;
    private double height;
    private double weight;
    private double bmi;

    public DataTable(String name, int age, double height, double weight, double bmi) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.bmi = bmi;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
