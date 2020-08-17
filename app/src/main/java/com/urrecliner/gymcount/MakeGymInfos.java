package com.urrecliner.gymcount;

import java.util.ArrayList;

import static com.urrecliner.gymcount.Vars.gymOptionList;
import static com.urrecliner.gymcount.Vars.gymInfos;

class MakeGymInfos {

    void makeGymOptionList() {

        gymOptionList = new ArrayList<>();
        gymOptionList.add(new GymInfo("Squat", 22, 20, false, 4, true, 10, 0, true, true, 0, 0));
        gymOptionList.add(new GymInfo("플랭크", 60, 30, false, 4, false, 10, 1, true, true, 0, 0));
        gymOptionList.add(new GymInfo("Jumping Jack", 90, 12, true, 4, false, 10, 0, true, true, 0, 0));
        gymOptionList.add(new GymInfo("Lunge", 30, 20, false, 4, false, 10, 2, true, true, 0, 0));
        gymOptionList.add(new GymInfo("푸시업", 20, 12, false, 4, false, 10, 0, true, true, 0, 0));
        gymOptionList.add(new GymInfo("Burpee", 40, 10, true, 4, false, 10, 3, true, true, 0, 0));
        gymOptionList.add(new GymInfo("Plank Jack", 30, 10, true, 4, false, 10, 0, true, true, 0, 0));
        gymOptionList.add(new GymInfo("Crunch", 30, 10, true, 2, false, 10, 0, true, true, 0, 0));
        gymOptionList.add(new GymInfo("Mount Climber", 60, 20, true, 2, false, 10, 0, true, true, 0, 0));
        gymOptionList.add(new GymInfo("Leg Raise", 30, 15, false, 4, true, 10, 0, true, true, 0, 0));
    }

    int getNextId(int id) {
        id++;
        while (true) {
            if (gymInfos != null) {
                for (GymInfo g : gymInfos) {
                    if (id == g.getId()) {
                        id++;
                        continue;
                    }
                }
                break;
            }
            else
                break;
        }
        return id;
    }
}