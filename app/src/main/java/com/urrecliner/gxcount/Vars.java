package com.urrecliner.gxcount;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

class Vars {
    static SharedPreferences sharedPreferences;

    static ArrayList<String> typeName;
    static ArrayList<Integer> speed;
    static ArrayList<Integer> countMax;
    static ArrayList<Boolean> isUp;
    static ArrayList<Boolean> sayStart;
    static ArrayList<Boolean> sayReady;
    static ArrayList<Integer> keep123;
    static ArrayList<Integer> keepMax;

    @SuppressLint("StaticFieldLeak")
    static Context mContext;
    @SuppressLint("StaticFieldLeak")
    static Activity mActivity;
    @SuppressLint("StaticFieldLeak")
    static RecyclerView recyclerView;
    static Utils utils;
    static int gxIdx;
    static CountDownTimer gxCDT;
    static boolean cdtRunning;

    static int[] soundSource = {
            0, R.raw.n01, R.raw.n02, R.raw.n03, R.raw.n04, R.raw.n05, R.raw.n06, R.raw.n07, R.raw.n08, R.raw.n09};
    static int[] soundTenSource = {
            0, R.raw.n_10, R.raw.n_20, R.raw.n_30, R.raw.n_40, R.raw.n_50, R.raw.n_60};
    static int[] soundShort = {
            0, R.raw.n_s1, R.raw.n_s2, R.raw.n_s3, R.raw.n_s4, R.raw.n_s5, R.raw.n_s6, R.raw.n_s7, R.raw.n_s8, R.raw.n_s9};
                                    // 0            1               2           3               4
    static int[] soundSpecial = { R.raw.i_keep, R.raw.i_nomore, R.raw.i_start, R.raw.i_ready, R.raw.i_last};

    static int[] sndTbl, sndTenTbl, sndShortTbl, sndSpecialTbl;

}
