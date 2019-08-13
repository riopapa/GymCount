package com.urrecliner.andriod.gxcount;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

import static com.urrecliner.andriod.gxcount.Vars.mContext;
import static com.urrecliner.andriod.gxcount.Vars.sharedPreferences;
import static com.urrecliner.andriod.gxcount.Vars.sndTenTbl;
import static com.urrecliner.andriod.gxcount.Vars.sndShortTbl;
import static com.urrecliner.andriod.gxcount.Vars.sndTbl;
import static com.urrecliner.andriod.gxcount.Vars.sndSpecialTbl;
import static com.urrecliner.andriod.gxcount.Vars.soundTenSource;
import static com.urrecliner.andriod.gxcount.Vars.soundShort;
import static com.urrecliner.andriod.gxcount.Vars.soundSource;
import static com.urrecliner.andriod.gxcount.Vars.soundSpecial;

class Utils {

    void log(String tag, String text) {
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String where = " " + traces[5].getMethodName() + " > " + traces[4].getMethodName() + " > " + traces[3].getMethodName() + " #" + traces[3].getLineNumber();
        Log.w(tag, where + " " + text);
    }

    void setStringArrayPref(String key, ArrayList<String> values) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    ArrayList<String> getStringArrayPref(String key) {
        String json = sharedPreferences.getString(key, null);
        ArrayList<String> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    void setIntegerArrayPref(String key, ArrayList<Integer> values) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    ArrayList<Integer> getIntegerArrayPref(String key) {
        String json = sharedPreferences.getString(key, null);
        ArrayList<Integer> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    int url = a.optInt(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    void setBooleanArrayPref(String key, ArrayList<Boolean> values) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    ArrayList<Boolean> getBooleanArrayPref(String key) {
        String json = sharedPreferences.getString(key, null);
        ArrayList<Boolean> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    boolean url = a.optBoolean(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    AudioManager mAudioManager = null;
    AudioFocusRequest mFocusGain = null;
    TextToSpeech mTTS;

    void ttsSpeak(String text) {
        Log.w("TTS","ttsspeak");
        initiateTTS();
        final String t = text;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Log.w("TTS1","ttsspeak");
                try {
                    readyAudioManager();
                    mTTS.setPitch(1.4f);
                    mTTS.setSpeechRate(1.4f);
                    try {
                        Log.w("TTS6","ttsspeak");
                        mTTS.speak(t, TextToSpeech.QUEUE_ADD, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
                    } catch (Exception e) {
                        log("speak", "justSpeak:" + e.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    void initiateTTS() {
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

    void readyAudioManager() {
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


    private SoundPool soundPool = null;

    void soundInitiate() {

        SoundPool.Builder builder;
//        AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .build();
//
//        builder = new SoundPool.Builder();
//        builder.setAudioAttributes(audioAttributes).setMaxStreams(5);
//        soundPool = builder.build();

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();
        soundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(5).build();

        sndTbl = new int[soundSource.length];
        sndTenTbl = new int[soundTenSource.length];
        sndShortTbl = new int[soundShort.length];
        sndSpecialTbl = new int[soundSpecial.length];
//        log("s","sndTbl len "+soundSource.length+" sndTbl len "+ sndTbl.length);
        for (int i = 1; i < soundSource.length; i++) {
//            log("s" + i,"sndTbl "+soundSource[i]);
            sndTbl[i] = soundPool.load(mContext, soundSource[i], 1);
//            log("loaded","tbl"+sndTbl[i]);
        }
        for (int i = 1; i < soundTenSource.length; i++)
            sndTenTbl[i] = soundPool.load(mContext, soundTenSource[i], 1);
        for (int i = 1; i < soundShort.length; i++)
            sndShortTbl[i] = soundPool.load(mContext, soundShort[i], 1);
        for (int i = 0; i < soundSpecial.length; i++)
            sndSpecialTbl[i] = soundPool.load(mContext, soundSpecial[i], 1);
    }

    void beepSound(int soundId, float volume) {
//        if (soundPool == null) {
//            soundInitiate();
//            final int id = soundId;
//            final float vol = volume;
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    soundPool.play(soundId, volume, volume, 1, 0, speed);
//                }
//            }, 1000);
//        }
        if (soundPool == null)
            log("sound"," is null");
        soundPool.play(soundId, volume, volume, 1, 0, 1);
    }

}
