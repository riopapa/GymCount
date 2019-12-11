package com.urrecliner.gxcount;

import java.util.ArrayList;

class MakeGxInfos {

    private ArrayList<GxInfo> gxDefault;

    void makeDefaults () {

        gxDefault = new ArrayList<GxInfo>();
//        while (gxDefault.size() > 0)
//            gxDefault.remove(0);

        gxDefault.add(new GxInfo("Squat", 40, 20, false, 4, true, 10, true, true, true));
        gxDefault.add(new GxInfo("Plank", 60, 30, false, 4, false, 10, false, true, true));
        gxDefault.add(new GxInfo("Jumping Jack", 70, 12, true, 4, false, 10, true, true, true));
        gxDefault.add(new GxInfo("Lunge", 50, 20, false, 4, false, 10, true, true, true));
        gxDefault.add(new GxInfo("Push Up", 50, 10, false, 4, false, 10, true, true, true));
        gxDefault.add(new GxInfo("Burpee", 50, 10, true, 4, false, 10, true, true, true));
        gxDefault.add(new GxInfo("Plank Jack", 50, 10, true, 4, false, 10, true, true, true));
        gxDefault.add(new GxInfo("Crunch", 45, 10, true, 2, false, 10, true, true, true));
        gxDefault.add(new GxInfo("Mount Climber", 70, 20, true, 2, false, 10, true, true, true));
        gxDefault.add(new GxInfo("Leg Raise", 50, 15, false, 4, true, 10, true, true, true));
    }

//    MakeGxInfos() {
//        gxInfos = gxDefault;
//        utils.saveSharedPrefTables();
//    }

    public ArrayList<GxInfo> getGxArray() {
        makeDefaults();
        return gxDefault;
    }
}