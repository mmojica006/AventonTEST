package com.moisesmojica.myaventon.models;

public class Result {

     private  int id;
     private String lt;
     private String lg;
     private String name;

    public Result(int id, String lt, String lg, String name) {
        this.id = id;
        this.lt = lt;
        this.lg = lg;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }

    public String getLg() {
        return lg;
    }

    public void setLg(String lg) {
        this.lg = lg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
