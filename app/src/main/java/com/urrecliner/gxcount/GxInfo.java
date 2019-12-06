package com.urrecliner.gxcount;

public class GxInfo {

    private String typeName;
    private int speed;
    private int mainCount;
    private boolean step;
    private int stepCount;
    private boolean hold;
    private int holdCount;
    private boolean countUp;
    private boolean sayStart;
    private boolean sayReady;

    GxInfo() {
        this.speed = 60;
        this.mainCount = 10;
        this.step = true;
        this.stepCount = 4;
        this.hold = false;
        this.holdCount = 10;
        this.countUp = true;
        this.sayStart = true;
        this.sayReady = true;
    }

    String getTypeName() {
        return typeName;
    }

     void setTypeName(String typeName) {
        this.typeName = typeName;
    }

     int getSpeed() {
        return speed;
    }

     void setSpeed(int speed) {
        this.speed = speed;
    }

     int getMainCount() {
        return mainCount;
    }

     void setMainCount(int mainCount) {
        this.mainCount = mainCount;
    }

     boolean isStep() {
        return step;
    }

     void setStep(boolean step) {
        this.step = step;
    }

     int getStepCount() {
        return stepCount;
    }

     void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

     boolean isHold() {
        return hold;
    }

     void setHold(boolean hold) {
        this.hold = hold;
    }

     int getHoldCount() {
        return holdCount;
    }

     void setHoldCount(int holdCount) {
        this.holdCount = holdCount;
    }

     boolean isCountUp() {
        return countUp;
    }

     void setCountUp(boolean countUp) {
        this.countUp = countUp;
    }

     boolean isSayStart() {
        return sayStart;
    }

     void setSayStart(boolean sayStart) {
        this.sayStart = sayStart;
    }

     boolean isSayReady() {
        return sayReady;
    }

     void setSayReady(boolean sayReady) {
        this.sayReady = sayReady;
    }
}
