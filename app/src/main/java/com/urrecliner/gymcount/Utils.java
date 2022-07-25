package com.urrecliner.gymcount;

import static com.urrecliner.gymcount.Vars.gymInfos;
import static com.urrecliner.gymcount.Vars.mActivity;
import static com.urrecliner.gymcount.Vars.mContext;
import static com.urrecliner.gymcount.Vars.prefer;
import static com.urrecliner.gymcount.Vars.sndSpecialTbl;
import static com.urrecliner.gymcount.Vars.sndStepTbl;
import static com.urrecliner.gymcount.Vars.sndTbl;
import static com.urrecliner.gymcount.Vars.sndTenTbl;
import static com.urrecliner.gymcount.Vars.soundSource;
import static com.urrecliner.gymcount.Vars.soundSpecial;
import static com.urrecliner.gymcount.Vars.soundStep;
import static com.urrecliner.gymcount.Vars.soundTenSource;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Utils {

    void log(String tag, String text) {
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String where = " " + traces[5].getMethodName() + " > " + traces[4].getMethodName() + " > " + traces[3].getMethodName() + " #" + traces[3].getLineNumber();
        Log.w(tag, where + " " + text);
    }

    private AudioManager mAudioManager = null;
    private AudioFocusRequest mFocusGain = null;
    private TextToSpeech mTTS;

    void ttsSpeak(String text) {
        initiateTTS();
        final String t = text;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                try {
                    readyAudioManager();
                    mTTS.setPitch(1.4f);
                    mTTS.setSpeechRate(1.4f);
                    try {
                        mTTS.speak(t, TextToSpeech.QUEUE_ADD, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
                    } catch (Exception e) {
                        //
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 100);
    }

    void initiateTTS() {
        if (mTTS == null) {
            mTTS = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = mTTS.setLanguage(Locale.KOREA);
                        if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            log("inittTS", "Language not supported");
                        }
                    }
                }
            });
        }
    }

    private void readyAudioManager() {
        if(mAudioManager == null) {
            try {
                mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                mFocusGain = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                        .build();
            } catch (Exception e) {
                log("err", "mAudioManager Error " + e.toString());
            }
        }
    }

    void saveSharedPrefTables() {

        SharedPreferences.Editor prefsEditor = prefer.edit();
        Gson gson = new Gson();
        String json = gson.toJson(gymInfos);
        prefsEditor.putString("GymInfo", json);
        prefsEditor.apply();
    }

    ArrayList<GymInfo> readSharedPrefTables() {

        ArrayList<GymInfo> list;
        Gson gson = new Gson();
        String json = prefer.getString("GymInfo", "");
        if (json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<GymInfo>>() {
            }.getType();
            list = gson.fromJson(json, type);
        }
        return list;
    }

    int getScreenWidth() {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private SoundPool soundPool = null;

    void soundInitiate() {

//        builder = new SoundPool.Builder();
//        builder.setAudioAttributes(audioAttributes).setMaxStreams(5);
//        soundPool = builder.build();

//        AudioAttributes attributes = new AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH) //.CONTENT_TYPE_MUSIC)
//                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION) // .USAGE_MEDIA)
//                .build();
//        soundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(5).build();
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);    // bluetooth is OK
        sndTbl = new int[soundSource.length];
        sndTenTbl = new int[soundTenSource.length];
        sndStepTbl = new int[soundStep.length];
        sndSpecialTbl = new int[soundSpecial.length];
//        log("s","sndTbl len "+soundSource.length+" sndTbl len "+ sndTbl.length);
        for (int i = 0; i < soundSource.length; i++) {
//            log("s" + i,"sndTbl "+soundSource[i]);
            sndTbl[i] = soundPool.load(mContext, soundSource[i], 1);
//            log("loaded","tbl"+sndTbl[i]);
        }
        for (int i = 1; i < soundTenSource.length; i++)
            sndTenTbl[i] = soundPool.load(mContext, soundTenSource[i], 1);
        for (int i = 0; i < soundStep.length; i++)
            sndStepTbl[i] = soundPool.load(mContext, soundStep[i], 1);
        for (int i = 0; i < soundSpecial.length; i++)
            sndSpecialTbl[i] = soundPool.load(mContext, soundSpecial[i], 1);
    }

    void beepSound(int soundId, float volume) {
        if (soundPool == null) {
            soundInitiate();
            Toast.makeText(mContext, "soundPool reInitiated",Toast.LENGTH_LONG).show();
            log("sound", " soundPool initiated");
        }
        soundPool.play(soundId, volume, volume, 1, 0, 1);
    }

}