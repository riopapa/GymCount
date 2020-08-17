package com.urrecliner.gymcount;

import static com.urrecliner.gymcount.Vars.makeGymInfos;
import static com.urrecliner.gymcount.Vars.nextOKId;

public class GymInfo {

    private int id;
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
    private int repeatCount;
    private int pauseSeconds;

    GymInfo(String typeName, int speed, int mainCount, boolean step, int stepCount, boolean hold, int holdCount,
            int countUpDown, boolean sayStart, boolean sayReady, int repeatCount, int pauseSeconds) {
        this.id = makeGymInfos.getNextId(nextOKId);
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
        this.repeatCount = repeatCount;
        this.pauseSeconds = pauseSeconds;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    public int getRepeatCount() { return repeatCount; }
    public void setRepeatCount(int repeatCount) { this.repeatCount = repeatCount; }

    public int getPauseSeconds() { return pauseSeconds; }
    public void setPauseSeconds(int pauseSeconds) { this.pauseSeconds = pauseSeconds; }


}
