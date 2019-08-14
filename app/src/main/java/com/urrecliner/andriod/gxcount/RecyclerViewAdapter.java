package com.urrecliner.andriod.gxcount;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import static com.urrecliner.andriod.gxcount.Vars.cdtRunning;
import static com.urrecliner.andriod.gxcount.Vars.countMax;
import static com.urrecliner.andriod.gxcount.Vars.gxCDT;
import static com.urrecliner.andriod.gxcount.Vars.gxIdx;
import static com.urrecliner.andriod.gxcount.Vars.isUp;
import static com.urrecliner.andriod.gxcount.Vars.keep123;
import static com.urrecliner.andriod.gxcount.Vars.keepMax;
import static com.urrecliner.andriod.gxcount.Vars.mActivity;
import static com.urrecliner.andriod.gxcount.Vars.mContext;
import static com.urrecliner.andriod.gxcount.Vars.sayReady;
import static com.urrecliner.andriod.gxcount.Vars.sayStart;
import static com.urrecliner.andriod.gxcount.Vars.sndShortTbl;
import static com.urrecliner.andriod.gxcount.Vars.sndSpecialTbl;
import static com.urrecliner.andriod.gxcount.Vars.sndTbl;
import static com.urrecliner.andriod.gxcount.Vars.sndTenTbl;
import static com.urrecliner.andriod.gxcount.Vars.speed;
import static com.urrecliner.andriod.gxcount.Vars.typeName;
import static com.urrecliner.andriod.gxcount.Vars.utils;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements NumberPicker.OnValueChangeListener {


    private static TextView nowTVCount;
    private static ImageView nowIVGo;
    private static CardView nowCard;
    private static MediaPlayer mediaPlayer;
    private static String sReady = "<준비>";
    private static String sStart = "<시작>";
    private static String sKeep = "<버티기>";
    private static String sNoMore = "<그만>";

    @Override
    public int getItemCount() {
        return typeName.size();
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_timer, parent, false);
        return new ViewHolder(view);
    }

    static TextView currTVKeep, currTVCount;
    @Override
    public void onValueChange(NumberPicker picker, int type, int newVal) {
        String s;
        int val = picker.getValue();
        utils.log("onValueChange","val"+val+", newVal"+newVal);
        switch (type) {
            case 1:
                keepMax.set(gxIdx, newVal);
                utils.setIntegerArrayPref("keepMax", keepMax);
                s = ""+newVal;
                currTVKeep.setText(s);
                currTVKeep.invalidate();
                break;

            case 5:
                countMax.set(gxIdx, newVal);
                utils.setIntegerArrayPref("countMax", countMax);
                s = ""+newVal;
                currTVCount.setText(s);
                currTVCount.invalidate();
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTypeName;
        TextView tvNowCount;
        SeekBar sbSpeed;
        ImageView ivUpDown, ivKeep, ivStart, ivReady, ivGo;
        TextView tvKeepCount;
        int wheelResult = 0;

        ViewHolder(final View itemView) {
            super(itemView);

            tvTypeName = itemView.findViewById(R.id.typeName);
            sbSpeed = itemView.findViewById(R.id.speed);
            tvNowCount = itemView.findViewById(R.id.nowCount);
            ivUpDown = itemView.findViewById(R.id.up_down);
            ivKeep = itemView.findViewById(R.id.keep);
            tvKeepCount = itemView.findViewById(R.id.keepCount);
            ivStart = itemView.findViewById(R.id.start);
            ivReady = itemView.findViewById(R.id.ready);
            ivGo = itemView.findViewById(R.id.go);

            tvTypeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                gxIdx = getAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("이름은? ");
                final EditText input = new EditText(mContext);
                input.setText(typeName.get(gxIdx));
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    String s = input.getText().toString();
                    if (s.length() < 1)
                        s = "이름 "+(gxIdx+1);
                    typeName.set(gxIdx, s);
                    utils.setStringArrayPref("typeName", typeName);
                    tvTypeName.setText(typeName.get(gxIdx));
                    tvTypeName.invalidate();
                    }
                });
                builder.show();
                }
            });

            sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    gxIdx = getAdapterPosition();
                    speed.set(gxIdx, seekBar.getProgress());
                    utils.setIntegerArrayPref("speed", speed);
                    if (cdtRunning) {
                        SystemClock.sleep(interval/2);
                        interval = progress * 100;
                        int cdtDownTime = (soundText.length-sNow) * interval + 10;
                        gxCDT.cancel();
                        runCountDownTimer(cdtDownTime);
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            tvNowCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    currTVCount = tvNowCount;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(typeName.get(gxIdx));
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText("오르내리기 횟수 설정");

                    final List<String> wheelValues = getCountMaxTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    int val = countMax.get(gxIdx);
                    wV.selectIndex((val > 20) ? 20 + (val - 20) / 5 : val);    // index pointer
                    wV.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                        @Override
                        public void onWheelItemSelected(WheelView wheelView, int position) {
                            wheelResult = position;
                        }
                        @Override
                        public void onWheelItemChanged(WheelView wheelView, int position) {
                        }
                    });

                    builder.setView(theView)
                            .setPositiveButton("SET",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    int val = np.getValue();
                                    int val = wheelResult;
                                    if (val > 20)
                                        val = 20 + (val - 20) * 5;
                                    String s;
                                    countMax.set(gxIdx, val);
                                    utils.setIntegerArrayPref("countMax", countMax);
                                    s = ""+val;
                                    currTVCount.setText(s);
                                    currTVCount.invalidate();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
//                    utils.ttsSpeak(typeName.get(gxIdx)+" 입니다. 오르내리기 회수를 설정합니다");
                    builder.show();
                }

                List <String> getCountMaxTable() {

                    List<String> result = new ArrayList<>();
                        for (int i = 0; i < 20; i++) result.add("" + i);
                        for (int i = 0; i < 9 ; i++) result.add(""+(20 + i * 5));
                    return result;
                }
            });

            ivKeep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    int keep_123 = keep123.get(gxIdx) + 1;
                    if (keep_123 > 2)
                        keep_123 = 0;
                    keep123.set(gxIdx, keep_123);
                    if (keep_123 == 0)
                        ivKeep.setImageResource(R.mipmap.i_keep_none);
                    else if (keep_123 == 1)
                        ivKeep.setImageResource(R.mipmap.i_keep_true);
                    else
                        ivKeep.setImageResource(R.mipmap.i_keep_123);

                    tvKeepCount.setTextColor((keep_123 == 0) ? mActivity.getResources().getColor(R.color.countBack):mActivity.getResources().getColor(R.color.countFore));
                    utils.setIntegerArrayPref("keep123", keep123);
                    ivKeep.invalidate();
                }
            });

            tvKeepCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    currTVKeep = tvKeepCount;
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);

                    final TextView tv = theView.findViewById(R.id.title);
                    tv.setText(typeName.get(gxIdx));
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    String s = "버티기/반복 횟수 설정";
                    tvs.setText(s);

                    final List<String> wheelValues = getKeepMaxTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    if (wV == null)
                        Log.e("wV"," is null");
                    wV.setItems(wheelValues);
                    int val = keepMax.get(gxIdx);
                    wV.selectIndex(val);
                    wV.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                        @Override
                        public void onWheelItemSelected(WheelView wheelView, int position) {
                            wheelResult = position;
                        }
                        @Override
                        public void onWheelItemChanged(WheelView wheelView, int position) {
                        }
                    });

                    builder.setView(theView)
                            .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int val = wheelResult;
                                    String s;
                                    keepMax.set(gxIdx, val);
                                    utils.setIntegerArrayPref("keepMax", keepMax);
                                    s = ""+val;
                                    tvKeepCount.setText(s);
                                    tvKeepCount.invalidate();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
//                    utils.ttsSpeak(typeName.get(gxIdx)+" 입니다. 버티기 또는 반복 회수를 설정합니다");
                    builder.show();
                }

                List <String> getKeepMaxTable() {

                    List<String> result = new ArrayList<>();
                    for (int i = 0; i < 21; i++) result.add("" + i);
                    return result;
                }

            });

            ivUpDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    boolean tf = !isUp.get(gxIdx);
                    isUp.set(gxIdx, tf);
                    ivUpDown.setImageResource((tf) ? R.mipmap.i_up_true:R.mipmap.i_up_false);
                    utils.setBooleanArrayPref("isUp", isUp);
                    ivUpDown.invalidate();
                }
            });

            ivReady.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    boolean tf = !sayReady.get(gxIdx);
                    sayReady.set(gxIdx, tf);
                    ivReady.setImageResource((tf)? R.mipmap.i_ready_true:R.mipmap.i_ready_false);
                    utils.setBooleanArrayPref("sayReady", sayReady);
                    ivReady.invalidate();
                }
            });

            ivStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    boolean tf = !sayStart.get(gxIdx);
                    sayStart.set(gxIdx, tf);
                    ivStart.setImageResource((tf) ? R.mipmap.i_start_true:R.mipmap.i_start_false);
                    utils.setBooleanArrayPref("sayStart", sayStart);
                    ivStart.invalidate();
                }
            });

            ivGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    if (cdtRunning) {
                        finishHandler();
                    }
                    else {
                        nowTVCount = itemView.findViewById(R.id.nowCount);
                        nowIVGo = itemView.findViewById(R.id.go);
                        nowCard = itemView.findViewById(R.id.card_view);
//                        nowIVGo.setImageResource(R.mipmap.i_go_red);
                        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(nowIVGo);
                        Glide.with(mActivity).load(R.drawable.i_now_running).into(gifImage);

                        int color = mActivity.getResources().getColor(R.color.cardRun);
                        nowCard.setCardBackgroundColor(color);
                        nowCard.invalidate();
                        calcInterval();
                        setupSoundTable();
                        sNow = 0;
                        cdtRunning = true;
                        int cdtDownTime = (soundText.length+2) * interval + 10;
                        runCountDownTimer(cdtDownTime);
                    }
                }
            });

        }


        int count, display, increase, interval, sIdx, sNow;
        int [] soundTable;
        String [] soundText;

        void calcInterval() {
            if (!cdtRunning) {
                count = countMax.get(gxIdx);
                display = isUp.get(gxIdx) ? 0 : countMax.get(gxIdx);
            }
            increase = isUp.get(gxIdx) ? 1:-1;
            interval = speed.get(gxIdx) * 100;
        }

        private void runCountDownTimer(int cdtDownTime) {
            nowIVGo.setEnabled(false);
            gxCDT = new CountDownTimer(cdtDownTime, interval) {
                public void onTick(long millisUntilFinished) {
                    if (soundText[sNow] != null) {
                        display += increase;
                        Message msg = Message.obtain();
                        msg.obj = soundText[sNow];
                        displayCount.sendMessage(msg);
                        utils.beepSound(soundTable[sNow], 1f);
                        if (soundText[sNow].equals(sReady))
                            SystemClock.sleep((2000));
                        else if (soundText[sNow].equals(sKeep))
                            SystemClock.sleep((1000));
                        sNow++;
                        if (sNow == sIdx)
                            finishHandler();
                    }
                }
                public void onFinish() {
                    finishHandler();
                }
            }.start();
            nowIVGo.setEnabled(true);
        }

        private void setupSoundTable() {
            int tblSize;
            if (keep123.get(gxIdx)== 2)
                tblSize = count*keepMax.get(gxIdx) + 5 ;
            else
                tblSize = count + keepMax.get(gxIdx) + 5;
            soundTable = new int[tblSize];
            soundText = new String[tblSize];
            sIdx = 0;
            if (!cdtRunning) {
                if (sayReady.get(gxIdx)) {
                    soundTable[sIdx] = sndSpecialTbl[3];   // R.raw.i_ready
                    soundText[sIdx] = sReady;
                    sIdx++;
                }
                if (sayStart.get(gxIdx)) {
                    soundTable[sIdx] = sndSpecialTbl[2];   // R.raw.i_start
                    soundText[sIdx] = sStart;
                    sIdx++;
                }
            }
            if (isUp.get(gxIdx)) {
                for (int i = 1; i <= count; i++) {
                    addSound123();
                    int mod = i%10;
                    if (mod == 0) {
                        int j = i / 10;
                        soundTable[sIdx] = sndTenTbl[j];
                    }
                    else {
                        soundTable[sIdx] = sndTbl[mod];
                    }
                    soundText[sIdx] = "< " + i + " >";
                    sIdx++;
                }
            }
            else {
                for (int i = count; i > 0; i--) {
                    addSound123();
                    int mod = i%10;
                    if (mod == 0) {
                        int j = i / 10;
                        soundTable[sIdx] = sndTenTbl[j];
                    }
                    else {
                        soundTable[sIdx] = sndTbl[mod];
                    }
                    soundText[sIdx] = "< " + i + " >";
                    sIdx++;
                }
            }
            if (keep123.get(gxIdx) == 1) {
                soundTable[sIdx] = sndSpecialTbl[0]; //R.raw.i_keep;
                soundText[sIdx] = sKeep;
                sIdx++;
                for (int i = keepMax.get(gxIdx); i > 0; i--) {
                    int mod = i%10;
                    if (mod == 0) {
                        int j = i / 10;
                        soundTable[sIdx] = sndTenTbl[j];
                    }
                    else {
                        soundTable[sIdx] = sndTbl[mod];
                    }
                    soundText[sIdx] = "> "+ i + " <";
                    sIdx++;
                }
            }
            soundTable[sIdx] = sndSpecialTbl[1]; // R.raw.i_nomore;
            soundText[sIdx] = sNoMore;
            sIdx++;
        }

        private void addSound123() {
            if (keep123.get(gxIdx) == 2) {
                for (int i = 1; i < keepMax.get(gxIdx); i++) {
                    int mod = i%10;
                    if (mod == 0) {
                        int j = i / 10;
                        soundTable[sIdx] = sndTenTbl[j];
                    }
                    else {
                        soundTable[sIdx] = sndShortTbl[mod];
                    }
                    soundText[sIdx] = "." + i + ".";
                    sIdx++;
                }
            }
        }
    }

    private static final Handler displayCount = new Handler() {
        public void handleMessage(Message msg) {
            nowTVCount.setText(msg.obj.toString());
        }
    };

    private static void finishHandler() {
        SystemClock.sleep(800);
        if (gxCDT != null) {
            gxCDT.cancel();
            gxCDT = null;
        }
        cdtRunning = false;
        Message msg = Message.obtain();
        msg.obj = ""+ countMax.get(gxIdx);
        displayCount.sendMessage(msg);
        nowIVGo.setImageResource(R.mipmap.i_go_green);
        int color = mActivity.getResources().getColor(R.color.cardBack);
        nowCard.setCardBackgroundColor(color);
    }

    void stopHandler() {
        finishHandler();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String s;
        holder.tvTypeName.setText(typeName.get(position));
        holder.sbSpeed.setProgress(speed.get(position));
        s = ""+ countMax.get(position); holder.tvNowCount.setText(s);
        holder.ivUpDown.setImageResource(isUp.get(position) ? R.mipmap.i_up_true : R.mipmap.i_up_false);
        holder.ivReady.setImageResource(sayReady.get(position)? R.mipmap.i_ready_true:R.mipmap.i_ready_false);
        holder.ivStart.setImageResource(sayStart.get(position)? R.mipmap.i_start_true:R.mipmap.i_start_false);

        if (keep123.get(position) == 0)
            holder.ivKeep.setImageResource(R.mipmap.i_keep_none);
        else if (keep123.get(position) == 1)
            holder.ivKeep.setImageResource(R.mipmap.i_keep_true);
        else
            holder.ivKeep.setImageResource(R.mipmap.i_keep_123);
        s = ""+keepMax.get(position); holder.tvKeepCount.setText(s);
        holder.tvKeepCount.setTextColor(keep123.get(position) != 0 ? mActivity.getResources().getColor(R.color.countFore):mActivity.getResources().getColor(R.color.countBack));
    }

}
//                    final NumberPicker np = theView.findViewById(R.id.getNumber);
//                    String[] myValues = getKeepMaxTable();
//
//                    np.setMinValue(0);
//                    np.setMaxValue(myValues.length - 1);
//                    np.setDisplayedValues(myValues);
//                    int val = keepMax.get(gxIdx);
//                    np.setValue(val);    // index pointer
//                    np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
