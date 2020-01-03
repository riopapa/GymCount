package com.urrecliner.gxcount;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.view.View;

import static com.urrecliner.gxcount.Vars.cdtRunning;
import static com.urrecliner.gxcount.Vars.currIdx;
import static com.urrecliner.gxcount.Vars.gxInfo;
import static com.urrecliner.gxcount.Vars.gxInfos;
import static com.urrecliner.gxcount.Vars.mContext;
import static com.urrecliner.gxcount.Vars.nowCard;
import static com.urrecliner.gxcount.Vars.nowIVGo;
import static com.urrecliner.gxcount.Vars.nowIVRun;
import static com.urrecliner.gxcount.Vars.nowIVStop;
import static com.urrecliner.gxcount.Vars.nowTVHoldCount;
import static com.urrecliner.gxcount.Vars.nowTVMainCount;
import static com.urrecliner.gxcount.Vars.nowTVStepCount;
import static com.urrecliner.gxcount.Vars.recyclerViewAdapter;
import static com.urrecliner.gxcount.Vars.sndShortTbl;
import static com.urrecliner.gxcount.Vars.sndSpecialTbl;
import static com.urrecliner.gxcount.Vars.sndTbl;
import static com.urrecliner.gxcount.Vars.sndTenTbl;
import static com.urrecliner.gxcount.Vars.soundTable;
import static com.urrecliner.gxcount.Vars.soundText;
import static com.urrecliner.gxcount.Vars.soundTime;
import static com.urrecliner.gxcount.Vars.utils;

class Shouter {
    private final static String MAIN_PREFIX = "m";
    private final static String STEP_PREFIX = "s";
    private final static String HOLD_PREFIX = "h";
    private final static String INIT_PREFIX = "i";
    private final static String NONE_PREFIX = "xx";

    private int delayTime, sIdx;

    void start() {
        calcDelayTime();
        setupSoundTable();
        try {
            new shouting().execute("start");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calcDelayTime() {
        gxInfo = gxInfos.get(currIdx);
        delayTime = 1000 * 60 / gxInfo.getSpeed();
    }


    private void setupSoundTable() {
        int tblSize;
        int countUpDown;
        gxInfo = gxInfos.get(currIdx);
        if (gxInfo.isStep())
            tblSize = (gxInfo.getStepCount()+1) * gxInfo.getMainCount();
        else
            tblSize = gxInfo.getMainCount();
        tblSize += ((gxInfo.isHold())? gxInfo.getHoldCount() : 0) + 5;
        soundTable = new int[tblSize];
        soundText = new String[tblSize];
        soundTime = new int[tblSize];
        sIdx = 0;
        if (gxInfo.isSayReady()) {
            soundTable[sIdx] = sndSpecialTbl[3];   // R.raw.i_ready
            soundText[sIdx] = NONE_PREFIX;
            soundTime[sIdx] = 2500;
            sIdx++;
        }
        if (gxInfo.isSayStart()) {
            soundTable[sIdx] = sndSpecialTbl[2];   // R.raw.i_start
            soundText[sIdx] = NONE_PREFIX;
            soundTime[sIdx] = delayTime + delayTime;
            sIdx++;
        }

        countUpDown = gxInfo.getCountUpDown();
        if ( countUpDown == 0 || countUpDown == 2) {    // count up
            int max = gxInfo.getMainCount() + (gxInfo.isStep() ? 1:0);
            utils.log("count","tblsize "+tblSize+" , count "+gxInfo.getMainCount()+", countUpDown "+countUpDown+" max "+max);
            for (int i = 1; i < max; i++) {
                if (gxInfo.isStep())
                    addStepSound(gxInfo.getStepCount());
                if (i < 21) {
                    soundTable[sIdx] = (countUpDown == 0 || i >= (max-5)) ? sndTbl[i]: sndTbl[0];
                }
                else {
                    int mod = i % 10;
                    if (mod == 0) {
                        int j = i / 10;
                        soundTable[sIdx] = (countUpDown == 0) ? sndTenTbl[j]: sndTbl[0];
                    } else {
                        soundTable[sIdx] = (countUpDown == 0 || i >= (max-5)) ? sndTbl[mod]: sndTbl[0];
                    }
                }
                soundText[sIdx] = MAIN_PREFIX + i;
                soundTime[sIdx] = delayTime;
                sIdx++;
            }
        }
        else {
            for (int i = gxInfo.getMainCount(); i > 0; i--) {
                if (gxInfo.isStep())
                    addStepSound(gxInfo.getStepCount());
                if (i < 21) {
                    soundTable[sIdx] = (countUpDown == 1 || i <= 5) ? sndTbl[i]: sndTbl[0];
                }
                else {
                    int mod = i % 10;
                    if (mod == 0) {
                        int j = i / 10;
                        soundTable[sIdx] = (countUpDown == 1) ? sndTenTbl[j]:sndTbl[0];
                    } else {
                        soundTable[sIdx] = (countUpDown == 1) ? sndTbl[mod]:sndTbl[0];
                    }
                }
                soundText[sIdx] = MAIN_PREFIX + i;
                soundTime[sIdx] = delayTime;
                sIdx++;
            }
        }
        if (gxInfo.isStep())
            sIdx--;
        if (gxInfo.isHold()) {
            soundTable[sIdx] = sndSpecialTbl[0];
            soundText[sIdx] = NONE_PREFIX;
            soundTime[sIdx] = 1000;
            sIdx++;
            for (int i = gxInfo.getHoldCount(); i >= 1; i--) {
                int mod = i%10;
                if (mod == 0) {
                    int j = i / 10;
                    soundTable[sIdx] = sndTenTbl[j];
                }
                else {
                    soundTable[sIdx] = sndTbl[mod];
                }
                soundText[sIdx] = HOLD_PREFIX + i;
                soundTime[sIdx] = 1000;
                sIdx++;
            }
        }
        soundTable[sIdx] = sndSpecialTbl[1]; // R.raw.i_nomore;
        soundText[sIdx] = NONE_PREFIX;
        soundTime[sIdx] = 1000;
        sIdx++;
    }

    private void addStepSound(int maxi) {
        for (int i = 1; i < maxi; i++) {
            soundTable[sIdx] = sndShortTbl[i];
            soundText[sIdx] = STEP_PREFIX + i;
            soundTime[sIdx] = delayTime;
            sIdx++;
        }
    }

    static private class shouting extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            nowIVGo.setEnabled(false);

        }

        @Override
        protected String doInBackground(String... inputParams) {

            publishProgress(INIT_PREFIX + INIT_PREFIX);
            SystemClock.sleep(1500);
            int idx = 0;
            while (soundTime[idx] > 0) {
                if (cdtRunning) {
                    publishProgress(soundText[idx]);
                    utils.beepSound(soundTable[idx], 1f);
                    SystemClock.sleep(soundTime[idx]);
                    idx++;
                } else
                    break;
            }

            return "done";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String cnt = values[0].substring(1);
            String tv = values[0].substring(0, 1);
            switch (tv) {
                case MAIN_PREFIX:
                    nowTVMainCount.setText(cnt);
                    nowTVMainCount.invalidate();
                    break;
                case STEP_PREFIX:
                    nowTVStepCount.setText(cnt);
                    nowTVStepCount.invalidate();
                    break;
                case HOLD_PREFIX:
                    nowTVHoldCount.setText(cnt);
                    nowTVHoldCount.invalidate();
                    break;
                case INIT_PREFIX:
                    nowIVGo.setVisibility(View.GONE);
                    nowIVRun.setVisibility(View.VISIBLE);
                    nowIVStop.setVisibility(View.VISIBLE);
                    nowCard.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardRun));
                    nowCard.invalidate();
                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onPostExecute(String doI) {
            finishHandler();
        }

    }
    void stop() {
        finishHandler();
    }
    private static void finishHandler() {
        cdtRunning = false;
        SystemClock.sleep(500);
        nowCard.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardBack));
        recyclerViewAdapter.notifyItemChanged(currIdx);
    }

}
