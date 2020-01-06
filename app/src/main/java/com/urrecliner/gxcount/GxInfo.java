package com.urrecliner.gxcount;

public class GxInfo {

    private String typeName;
    private int speed;
    private int mainCount;
    private boolean step;     // 0: up, 1: down, 2: up5, 3:down5
    private int stepCount;
    private boolean hold;
    private int holdCount;
    private int countUpDown;
    private boolean sayStart;
    private boolean sayReady;

    GxInfo(String typeName, int speed, int mainCount, boolean step, int stepCount, boolean hold, int holdCount,
           int countUpDown, boolean sayStart, boolean sayReady) {
        this.typeName = typeName;
        this.speed = speed;
        this.mainCount = mainCount;
        this.step = step;
        this.stepCount = stepCount;
        this.hold = hold;
        this.holdCount = holdCount;
        this.countUpDown = countUpDown;
        this.sayStart = sayStart;
        this.sayReady = sayReady;
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

     int getCountUpDown() {
        return countUpDown;
    }
     void setCountUpDown(int countUpDown) {
        this.countUpDown = countUpDown;
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
