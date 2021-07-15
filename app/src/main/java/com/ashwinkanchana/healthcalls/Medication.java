package com.ashwinkanchana.healthcalls;


import android.util.Log;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;


//Medication model, Serializable to store as string via GSON
public class Medication implements Serializable {
    private String name;
    private String unit;
    private int frequency;
    private int doseage;
    private int inventory;
    private int expiryMonth;
    private int expiryYear;
    private String imageName;
    private Calendar c1;
    private Calendar c2;
    private Calendar c3;
    private int id1,id2,id3;

    public void setName(String name) {
        this.name = name;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setDoseage(int doseage) {
        this.doseage = doseage;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setC1(Calendar c1) {
        this.c1 = c1;
    }



    public void setC2(Calendar c2) {
        this.c2 = c2;
    }

    public void setC3(Calendar c3) {
        this.c3 = c3;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getDoseage() {
        return doseage;
    }

    public int getInventory() {
        return inventory;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public String getImageName() {
        return imageName;
    }

    public Calendar getC1() {
        return c1;
    }

    public Calendar getC2() {
        return c2;
    }

    public Calendar getC3() {
        return c3;
    }



    public Medication(String name, String unit, int frequency, int doseage, int inventory, int expiryMonth, int expiryYear, String imageName, Calendar c1, Calendar c2, Calendar c3) {
        this.name = name;
        this.unit = unit;
        this.frequency = frequency;
        this.doseage = doseage;
        this.inventory = inventory;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.imageName = imageName;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.id1 = (int) Math.abs(System.currentTimeMillis());
        this.id2 = (int) Math.abs(System.currentTimeMillis()+1);
        this.id3 = (int) Math.abs(System.currentTimeMillis()+5);
    }

    public Medication(String name, String unit, int frequency, int doseage, int inventory, int expiryMonth, int expiryYear, String imageName, Calendar c1, Calendar c2) {
        this.name = name;
        this.unit = unit;
        this.frequency = frequency;
        this.doseage = doseage;
        this.inventory = inventory;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.imageName = imageName;
        this.c1 = c1;
        this.c2 = c2;
        this.id1 = (int) Math.abs(System.currentTimeMillis());
        this.id2 = (int) Math.abs(System.currentTimeMillis()+8);
    }

    public int getId1() {
        return id1;
    }

    public int getId2() {
        return id2;
    }

    public int getId3() {
        return id3;
    }

    public Medication(String name, String unit, int frequency, int doseage, int inventory, int expiryMonth, int expiryYear, String imageName, Calendar c1) {
        this.name = name;
        this.unit = unit;
        this.frequency = frequency;
        this.doseage = doseage;
        this.inventory = inventory;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.imageName = imageName;
        this.c1 = c1;
        this.id1 = (int) Math.abs(System.currentTimeMillis()+12);

    }

    public String getReminders() {
        Log.i("object ",String.valueOf(this.frequency));
        if(this.frequency == 1)
            return "Reminder 1 - "+DateFormat.getTimeInstance(DateFormat.SHORT).format(c1.getTime());
        else if(this.frequency==2)
            return "Reminder 1 - "+DateFormat.getTimeInstance(DateFormat.SHORT).format(c1.getTime())
                    +"\nReminder 2 - "+DateFormat.getTimeInstance(DateFormat.SHORT).format(c2.getTime());

        return "Reminder 1 - "+DateFormat.getTimeInstance(DateFormat.SHORT).format(c1.getTime())
                        +"\nReminder 2 - "+DateFormat.getTimeInstance(DateFormat.SHORT).format(c2.getTime())
                        +"\nReminder 3 - "+DateFormat.getTimeInstance(DateFormat.SHORT).format(c3.getTime());
    }

    public String getRem1() {
        return "Reminder 1 - "+DateFormat.getTimeInstance(DateFormat.SHORT).format(c1.getTime());
    }

    public String getRem2() {
        try {
            return "Reminder 2 - "+DateFormat.getTimeInstance(DateFormat.SHORT).format(c2.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public String getRem3() {
        try {
            return "Reminder 3 - "+DateFormat.getTimeInstance(DateFormat.SHORT).format(c3.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";

    }
}
