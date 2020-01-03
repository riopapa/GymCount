package com.urrecliner.gxcount;

import java.util.ArrayList;

class MakeGxInfos {

    private ArrayList<GxInfo> gxDefault;

    void makeDefaults () {

        gxDefault = new ArrayList<GxInfo>();

        gxDefault.add(new GxInfo("Squat", 40, 20, false, 4, true, 10, 0, true, true));
        gxDefault.add(new GxInfo("Plank", 60, 30, false, 4, false, 10, 1, true, true));
        gxDefault.add(new GxInfo("Jumping Jack", 70, 12, true, 4, false, 10, 0, true, true));
        gxDefault.add(new GxInfo("Lunge", 50, 20, false, 4, false, 10, 2, true, true));
        gxDefault.add(new GxInfo("Push Up", 50, 10, false, 4, false, 10, 0, true, true));
        gxDefault.add(new GxInfo("Burpee", 50, 10, true, 4, false, 10, 3, true, true));
        gxDefault.add(new GxInfo("Plank Jack", 50, 10, true, 4, false, 10, 0, true, true));
        gxDefault.add(new GxInfo("Crunch", 45, 10, true, 2, false, 10, 0, true, true));
        gxDefault.add(new GxInfo("Mount Climber", 70, 20, true, 2, false, 10, 0, true, true));
        gxDefault.add(new GxInfo("Leg Raise", 50, 15, false, 4, true, 10, 0, true, true));
    }

    public ArrayList<GxInfo> getGxArray() {
        makeDefaults();
        return gxDefault;
    }
}