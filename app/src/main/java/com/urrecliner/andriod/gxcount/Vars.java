package com.urrecliner.andriod.gxcount;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class Vars {
    static SharedPreferences sharedPreferences;

    static ArrayList<String> typeName;
    static ArrayList<Integer> speed;
    static ArrayList<Integer> max;
    static ArrayList<Boolean> isUp;
    static ArrayList<Boolean> sayStart;
    static ArrayList<Boolean> sayReady;

    static Context mContext;
    static Activity mActivity;
    static RecyclerView recyclerView;
    static Utils utils;
    static int gxIdx;
    static CountDownTimer gxCDT;
    static boolean cdtRunning;

    static int[] soundSource = {
            0, R.raw.n01, R.raw.n02, R.raw.n03, R.raw.n04, R.raw.n05, R.raw.n06, R.raw.n07, R.raw.n08, R.raw.n09};
    static int[] sound10Source = {
            0, R.raw.n10, R.raw.n10, R.raw.n10, R.raw.n04, R.raw.n05, R.raw.n06, R.raw.n07, R.raw.n08, R.raw.n09};

}
