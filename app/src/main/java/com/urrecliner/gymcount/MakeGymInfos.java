package com.urrecliner.gymcount;

import java.util.ArrayList;

class MakeGymInfos {


    ArrayList<GymInfo> getGymArray() {
        ArrayList<GymInfo> gymDefault;

        gymDefault = new ArrayList<>();

        gymDefault.add(new GymInfo("Squat", 22, 20, false, 4, true, 10, 0, true, true, 0, 0));
        gymDefault.add(new GymInfo("플랭크", 60, 30, false, 4, false, 10, 1, true, true, 0, 0));
        gymDefault.add(new GymInfo("Jumping Jack", 90, 12, true, 4, false, 10, 0, true, true, 0, 0));
        gymDefault.add(new GymInfo("Lunge", 30, 20, false, 4, false, 10, 2, true, true, 0, 0));
        gymDefault.add(new GymInfo("푸시업", 20, 12, false, 4, false, 10, 0, true, true, 0, 0));
        gymDefault.add(new GymInfo("Burpee", 40, 10, true, 4, false, 10, 3, true, true, 0, 0));
        gymDefault.add(new GymInfo("Plank Jack", 30, 10, true, 4, false, 10, 0, true, true, 0, 0));
        gymDefault.add(new GymInfo("Crunch", 30, 10, true, 2, false, 10, 0, true, true, 0, 0));
        gymDefault.add(new GymInfo("Mount Climber", 60, 20, true, 2, false, 10, 0, true, true, 0, 0));
        gymDefault.add(new GymInfo("Leg Raise", 30, 15, false, 4, true, 10, 0, true, true, 0, 0));
        return gymDefault;
    }
}