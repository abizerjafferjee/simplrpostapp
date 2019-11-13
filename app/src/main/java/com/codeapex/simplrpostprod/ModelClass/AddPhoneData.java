package com.codeapex.simplrpostprod.ModelClass;

public class AddPhoneData {

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public int getDayId() {
        return dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }

    private String dayName,openTime,closeTime;
    private int dayId;

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    private int isOpen;

    public AddPhoneData(String dayName, int dayId,String openTime,String closeTime ,int isOpen) {
        this.openTime = openTime;
        this.isOpen = isOpen;
        this.closeTime = closeTime;
        this.dayName = dayName;
        this.dayId = dayId;
    }



}
