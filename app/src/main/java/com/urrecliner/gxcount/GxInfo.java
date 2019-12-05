package com.urrecliner.gxcount;

public class GxInfo {

    private String typeName;
    private int speed;
    private int mainCount;
    private boolean step;
    private int stepCount;
    private boolean hold;
    private int holdCount;
    private boolean isUp;
    private boolean sayStart;
    private boolean sayReady;

    GxInfo() {
        this.speed = 60;
        this.mainCount = 10;
        this.step = true;
        this.stepCount = 4;
        this.hold = false;
        this.holdCount = 10;
        this.isUp = true;
        this.sayStart = true;
        this.sayReady = true;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMainCount() {
        return mainCount;
    }

    public void setMainCount(int mainCount) {
        this.mainCount = mainCount;
    }

    public boolean isStep() {
        return step;
    }

    public void setStep(boolean step) {
        this.step = step;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public boolean isHold() {
        return hold;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }

    public int getHoldCount() {
        return holdCount;
    }

    public void setHoldCount(int holdCount) {
        this.holdCount = holdCount;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean up) {
        isUp = up;
    }

    public boolean isSayStart() {
        return sayStart;
    }

    public void setSayStart(boolean sayStart) {
        this.sayStart = sayStart;
    }

    public boolean isSayReady() {
        return sayReady;
    }

    public void setSayReady(boolean sayReady) {
        this.sayReady = sayReady;
    }
}
