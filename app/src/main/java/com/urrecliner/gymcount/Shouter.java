package com.urrecliner.gymcount;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.view.View;

import static com.urrecliner.gymcount.Vars.cdtRunning;
import static com.urrecliner.gymcount.Vars.currIdx;
import static com.urrecliner.gymcount.Vars.gymInfo;
import static com.urrecliner.gymcount.Vars.gymInfos;
import static com.urrecliner.gymcount.Vars.mContext;
import static com.urrecliner.gymcount.Vars.nowCard;
import static com.urrecliner.gymcount.Vars.nowIVShout;
import static com.urrecliner.gymcount.Vars.nowIVStop;
import static com.urrecliner.gymcount.Vars.nowTVHoldCount;
import static com.urrecliner.gymcount.Vars.nowTVMainCount;
import static com.urrecliner.gymcount.Vars.nowTVStepCount;
import static com.urrecliner.gymcount.Vars.recyclerViewAdapter;
import static com.urrecliner.gymcount.Vars.sndStepTbl;
import static com.urrecliner.gymcount.Vars.sndSpecialTbl;
import static com.urrecliner.gymcount.Vars.sndTbl;
import static com.urrecliner.gymcount.Vars.sndTenTbl;
import static com.urrecliner.gymcount.Vars.soundTable;
import static com.urrecliner.gymcount.Vars.soundText;
import static com.urrecliner.gymcount.Vars.soundTime;
import static com.urrecliner.gymcount.Vars.utils;

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
        gymInfo = gymInfos.get(currIdx);
        delayTime = 1000 * 60 / gymInfo.getSpeed();
    }


    private void setupSoundTable() {
        int tblSize;
        int countUpDown;
        gymInfo = gymInfos.get(currIdx);
        if (gymInfo.isStep())
            tblSize = (gymInfo.getStepCount()+1) * gymInfo.getMainCount();
        else
            tblSize = gymInfo.getMainCount();
        tblSize += ((gymInfo.isHold())? gymInfo.getHoldCount() : 1) + 5;
        soundTable = new int[tblSize];
        soundText = new String[tblSize];
        soundTime = new int[tblSize];
        sIdx = 0;
        if (gymInfo.isSayReady()) {
            soundTable[sIdx] = sndSpecialTbl[3];   // R.raw.i_ready
            soundText[sIdx] = NONE_PREFIX;
            soundTime[sIdx] = 2500;
            sIdx++;
        }
        if (gymInfo.isSayStart()) {
            soundTable[sIdx] = sndSpecialTbl[2];   // R.raw.i_start
            soundText[sIdx] = NONE_PREFIX;
            soundTime[sIdx] = 2500;
            sIdx++;
        }

        countUpDown = gymInfo.getCountUpDown();
        if ( countUpDown == 0 || countUpDown == 2) {    // count up
            int max = gymInfo.getMainCount() + (gymInfo.isStep() ? 1:0);
            for (int i = 1; i <= max; i++) {
                if (gymInfo.isStep())
                    addStepSound(gymInfo.getStepCount());
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
            for (int i = gymInfo.getMainCount(); i > 0; i--) {
                if (gymInfo.isStep())
                    addStepSound(gymInfo.getStepCount());
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
        if (gymInfo.isStep())
            sIdx--;
        if (gymInfo.isHold()) {
            soundTable[sIdx] = sndSpecialTbl[0];
            soundText[sIdx] = NONE_PREFIX;
            soundTime[sIdx] = 1000;
            sIdx++;
            for (int i = gymInfo.getHoldCount(); i >= 1; i--) {
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
        soundTime[sIdx] = 500;
        sIdx++;
    }

    private void addStepSound(int maxi) {
        for (int i = 1; i < maxi; i++) {
            soundTable[sIdx] = sndStepTbl[i%4];
            soundText[sIdx] = STEP_PREFIX + i;
            soundTime[sIdx] = delayTime;
            sIdx++;
        }
    }

    static private class shouting extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... inputParams) {

            publishProgress(INIT_PREFIX + INIT_PREFIX);
            SystemClock.sleep(1000);
            int idx = 0;
            while (soundTime[idx] > 0) {
                if (cdtRunning)
                    SystemClock.sleep(soundTime[idx]);
                if (cdtRunning) {
                    publishProgress(soundText[idx]);
                    utils.beepSound(soundTable[idx], 1f);
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
//                    nowGifView.setVisibility(View.VISIBLE);
//                    nowGifView.setGifResource(R.drawable.running_gifmaker);
//                    nowGifView.play();
//                    nowIVStart.setVisibility(View.INVISIBLE);
//                    nowIVReady.setVisibility(View.INVISIBLE);
                    nowIVStop.setVisibility(View.VISIBLE);
                    nowIVShout.setVisibility(View.GONE);
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
//        nowGifView.pause();
//        nowGifView.setVisibility(View.GONE);
        nowIVShout.setVisibility(View.VISIBLE);
        nowIVStop.setVisibility(View.GONE);
        nowCard.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardBack));
        recyclerViewAdapter.notifyItemChanged(currIdx);
//        SystemClock.sleep(500);
    }

}
