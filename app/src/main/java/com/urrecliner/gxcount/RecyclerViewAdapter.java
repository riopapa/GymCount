package com.urrecliner.gxcount;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import static com.urrecliner.gxcount.Vars.cdtRunning;
import static com.urrecliner.gxcount.Vars.countDownTimer;
import static com.urrecliner.gxcount.Vars.currIdx;
import static com.urrecliner.gxcount.Vars.gxInfos;
import static com.urrecliner.gxcount.Vars.mActivity;
import static com.urrecliner.gxcount.Vars.mContext;
import static com.urrecliner.gxcount.Vars.sndShortTbl;
import static com.urrecliner.gxcount.Vars.sndSpecialTbl;
import static com.urrecliner.gxcount.Vars.sndTbl;
import static com.urrecliner.gxcount.Vars.sndTenTbl;
import static com.urrecliner.gxcount.Vars.utils;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    private static TextView nowTVMainCount, nowTVStepCount, nowTVHoldCount;
    private static ImageView nowIVGo, nowIVRun, nowIVStop;
    private static CardView nowCard;
    private static MediaPlayer mediaPlayer;
    private static String sReady = "<준비>";
    private static String sStart = "<시작>";
    private static String sHolding = "<버티기>";
    private static String sNoMore = "<그만>";

    @Override
    public int getItemCount() {
        return gxInfos.size();
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_timer, parent, false);
        return new ViewHolder(view);
    }

    private static TextView tvNowSpeed, tvNowMainCount, tvNowStepCount, tvNowHoldCount;

    private final static int SPEED_MIN = 20;
    private final static int STEP_MIN = 2;
    private final static int HOLD_MIN = 10;
    private final static String MAIN_PREFIX = "m";
    private final static String STEP_PREFIX = "s";
    private final static String HOLD_PREFIX = "h";


    static class ViewHolder extends RecyclerView.ViewHolder {

        private GxInfo gxInfo;
        TextView tvTypeName, tvMainCount, tvStepCount, tvSpeed, tvHoldCount;
        ImageView ivHold, ivUpDown, ivStep, ivStart, ivReady, ivGo, ivRun, ivStop, ivDelete;
        int wheelResult = 0;

        ViewHolder(final View itemView) {
            super(itemView);

            tvTypeName = itemView.findViewById(R.id.typeName);
            tvSpeed = itemView.findViewById(R.id.speed);
            tvMainCount = itemView.findViewById(R.id.mainCount);
            ivStep = itemView.findViewById(R.id.step);
            tvStepCount = itemView.findViewById(R.id.stepCount);
            ivHold = itemView.findViewById(R.id.hold);
            tvHoldCount = itemView.findViewById(R.id.holdCount);

            ivUpDown = itemView.findViewById(R.id.up_down);
            ivStart = itemView.findViewById(R.id.start);
            ivReady = itemView.findViewById(R.id.ready);
            ivGo = itemView.findViewById(R.id.go);
            ivRun = itemView.findViewById(R.id.run);
            ivRun.setVisibility(View.GONE);
            ivStop = itemView.findViewById(R.id.stop);
            ivStop.setVisibility(View.GONE);
            ivDelete = itemView.findViewById(R.id.delete);

            tvTypeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("이름은? ");
                    final EditText input = new EditText(mContext);
                    input.setText(gxInfo.getTypeName());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        String s = input.getText().toString();
                        if (s.length() < 1)
                            s = "운동이름 "+(currIdx +1);
                        gxInfo.setTypeName(s);
                        gxInfos.set(currIdx, gxInfo);
                        utils.saveSharedPrefTables();
                        tvTypeName.setText(s);
                        tvTypeName.invalidate();
                        }
                    });
                    builder.show();
                }
            });

            tvSpeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    tvNowSpeed = tvSpeed;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(gxInfo.getTypeName());
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" 운동 속도 ");

                    final List<String> wheelValues = getSpeedTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    int val = (gxInfo.getSpeed() - SPEED_MIN) / 5;
                    wV.setAdditionCenterMark("\u3040");     // whole space
                    wV.selectIndex(val);    // index pointer
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
                                    String s;
                                    wheelResult = wheelResult * 5 + SPEED_MIN;
                                    gxInfo.setSpeed(wheelResult);
                                    gxInfos.set(currIdx, gxInfo);
                                    utils.saveSharedPrefTables();
                                    s = wheelResult+"";
                                    tvNowSpeed.setText(s);
                                    tvNowSpeed.invalidate();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }

                List <String> getSpeedTable() {
                    List<String> result = new ArrayList<>();
                    for (int i = SPEED_MIN; i <= 90; i+= 5) result.add("" + i);
                    return result;
                }
            });

            tvMainCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    tvNowMainCount = tvMainCount;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(gxInfo.getTypeName());
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" 횟수 설정 ");

                    final List<String> wheelValues = setCountTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    wV.setAdditionCenterMark("회");
                    int val = gxInfo.getMainCount();
                    wV.selectIndex((val > SPEED_MIN) ? SPEED_MIN + (val - SPEED_MIN) / 5 : val);    // index pointer
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
                                    if (wheelResult > SPEED_MIN)
                                        wheelResult = SPEED_MIN + (wheelResult - SPEED_MIN) * 5;
                                    String s;
                                    gxInfo.setMainCount(wheelResult);
                                    gxInfos.set(currIdx, gxInfo);
                                    utils.saveSharedPrefTables();
                                    s = ""+wheelResult;
                                    tvNowMainCount.setText(s);
                                    tvNowMainCount.invalidate();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }

                List <String> setCountTable() {
                    List<String> result = new ArrayList<>();
                        for (int i = 0; i < SPEED_MIN; i++) result.add("" + i);
                        for (int i = 0; i < 9 ; i++) result.add(""+(SPEED_MIN + i * 5));
                    return result;
                }
            });

            tvStepCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    tvNowStepCount = tvStepCount;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(gxInfo.getTypeName());
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" 스텝수 설정 ");

                    final List<String> wheelValues = setCountTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    wV.setAdditionCenterMark("회");
                    wV.selectIndex(gxInfo.getStepCount() - SPEED_MIN);
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
                                    String s;
                                    gxInfo.setStepCount(wheelResult+STEP_MIN);
                                    gxInfos.set(currIdx, gxInfo);
                                    utils.saveSharedPrefTables();
                                    s = ""+gxInfo.getStepCount();
                                    tvNowStepCount.setText(s);
                                    tvNowStepCount.invalidate();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }

                List <String> setCountTable() {

                    List<String> result = new ArrayList<>();
                    for (int i = STEP_MIN; i <= 16; i++) result.add("" + i);
                    return result;
                }
            });

            tvHoldCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    tvNowHoldCount = tvHoldCount;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(gxInfo.getTypeName());
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" 버티기 설정 ");

                    final List<String> wheelValues = setCountTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    wV.setAdditionCenterMark("회");
                    wV.selectIndex(gxInfo.getHoldCount()-HOLD_MIN);
                    wV.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                        @Override
                        public void onWheelItemSelected(WheelView wheelView, int pos) {
                            wheelResult = pos;
                        }
                        @Override
                        public void onWheelItemChanged(WheelView wheelView, int pos) {
                        }
                    });

                    builder.setView(theView)
                            .setPositiveButton("SET",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String s;
                                    gxInfo.setHoldCount(wheelResult+HOLD_MIN);
                                    gxInfos.set(currIdx, gxInfo);
                                    utils.saveSharedPrefTables();
                                    s = ""+(wheelResult+HOLD_MIN);
                                    tvNowHoldCount.setText(s);
                                    tvNowHoldCount.invalidate();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }

                List <String> setCountTable() {

                    List<String> result = new ArrayList<>();
                    for (int i = HOLD_MIN; i <= 20; i++) result.add("" + i);
                    return result;
                }
            });

            ivStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    boolean tf = !gxInfo.isStep();
                    gxInfo.setStep(tf);
                    ivStep.setImageResource((tf) ? R.mipmap.i_step_true:R.mipmap.i_step_false);
                    ivStep.invalidate();
                    gxInfos.set(currIdx, gxInfo);
                    utils.saveSharedPrefTables();
                    String s = (tf) ? (""+gxInfo.getStepCount()) : "";
                    tvStepCount.setText(s);
                }
            });

            ivHold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    boolean tf = !gxInfo.isHold();
                    gxInfo.setHold(tf);
                    gxInfos.set(currIdx, gxInfo);
                    utils.saveSharedPrefTables();
                    ivHold.setImageResource((tf) ? R.mipmap.i_hold_true:R.mipmap.i_hold_false);
                    ivHold.invalidate();
                    String s = (tf) ? (""+gxInfo.getHoldCount()) : "";
                    tvHoldCount.setText(s);
                }
            });

            ivUpDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    boolean tf = !gxInfo.isUp();
                    gxInfo.setUp(tf);
                    gxInfos.set(currIdx, gxInfo);
                    utils.saveSharedPrefTables();
                    ivUpDown.setImageResource((tf) ? R.mipmap.i_up_true:R.mipmap.i_up_false);
                    ivUpDown.invalidate();
                }
            });

            ivReady.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    boolean tf = !gxInfo.isSayReady();
                    gxInfo.setSayReady(tf);
                    gxInfos.set(currIdx, gxInfo);
                    utils.saveSharedPrefTables();
                    ivReady.setImageResource((tf)? R.mipmap.i_ready_true:R.mipmap.i_ready_false);
                    ivReady.invalidate();
                }
            });

            ivStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    boolean tf = !gxInfo.isSayStart();
                    gxInfo.setSayStart(tf);
                    gxInfos.set(currIdx, gxInfo);
                    utils.saveSharedPrefTables();
                    ivStart.setImageResource((tf) ? R.mipmap.i_start_true:R.mipmap.i_start_false);
                    ivStart.invalidate();
                }
            });

            ivGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    nowTVMainCount = itemView.findViewById(R.id.mainCount);
                    nowTVStepCount = itemView.findViewById(R.id.stepCount);
                    nowTVHoldCount = itemView.findViewById(R.id.holdCount);
                    nowIVGo = itemView.findViewById(R.id.go);
                    nowIVRun = itemView.findViewById(R.id.run);
                    nowIVStop = itemView.findViewById(R.id.stop);
                    nowCard = itemView.findViewById(R.id.card_view);
                    nowIVGo.setVisibility(View.GONE);
                    nowIVRun.setVisibility(View.VISIBLE);
                    nowIVStop.setVisibility(View.VISIBLE);
                    nowCard.setCardBackgroundColor(ContextCompat.getColor(mContext,R.color.cardRun));
                    nowCard.invalidate();
                    calcDelayTime();
                    setupSoundTable();
                    sNow = 0;
                    cdtRunning = true;
                    int cdtDownTime = (soundText.length+2) * delayTime;
                    utils.log("x","cdtDownTime "+cdtDownTime);
                    runCountDownTimer(cdtDownTime);
                    }
            });

            ivStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    finishHandler();
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfos.remove(currIdx);
                    utils.saveSharedPrefTables();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mActivity.startActivity(intent);

                }
            });
        }

        int display, upOrDown, delayTime, sIdx, sNow;
        int [] soundTable;
        String [] soundText;

        void calcDelayTime() {
            if (cdtRunning)
                return;
            gxInfo = gxInfos.get(currIdx);
            upOrDown = gxInfo.isUp() ? 1:-1;
            delayTime = 1000 * 60 / gxInfo.getSpeed();
        }

        private void runCountDownTimer(int cdtDownTime) {
            nowIVGo.setEnabled(false);
            countDownTimer = new CountDownTimer(cdtDownTime, delayTime) {
                public void onTick(long millisUntilFinished) {
                    if (soundText[sNow] != null) {
                        display += upOrDown;
                        Message msg = Message.obtain();
                        msg.obj = soundText[sNow];
                        displayCount.sendMessage(msg);
                        utils.beepSound(soundTable[sNow], 1f);
                        if (soundText[sNow].equals(sReady))
                            SystemClock.sleep((2000));
                        else if (soundText[sNow].equals(sHolding))
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
            gxInfo = gxInfos.get(currIdx);
            if (gxInfo.isStep())
                tblSize = gxInfo.getStepCount() * gxInfo.getMainCount() + 6;
            else
                tblSize = gxInfo.getMainCount();
            tblSize += ((gxInfo.isHold())? gxInfo.getHoldCount() : 0);
            soundTable = new int[tblSize];
            soundText = new String[tblSize];
            sIdx = 0;
            if (gxInfo.isSayReady()) {
                soundTable[sIdx] = sndSpecialTbl[3];   // R.raw.i_ready
                soundText[sIdx] = "_";
                sIdx++;
            }
            if (gxInfo.isSayStart()) {
                soundTable[sIdx] = sndSpecialTbl[2];   // R.raw.i_start
                soundText[sIdx] = "_";
                sIdx++;
            }
            if (gxInfo.isUp()) {
                for (int i = 1; i < gxInfo.getMainCount(); i++) {
                    if (gxInfo.isStep())
                        addStepSound();
                    int mod = i%10;
                    if (mod == 0) {
                        int j = i / 10;
                        soundTable[sIdx] = sndTenTbl[j];
                    }
                    else {
                        soundTable[sIdx] = sndTbl[mod];
                    }
                    soundText[sIdx] = MAIN_PREFIX + i;
                    sIdx++;
                }
            }
            else {
                for (int i = gxInfo.getMainCount(); i > 0; i--) {
                    if (gxInfo.isStep())
                        addStepSound();
                    int mod = i%10;
                    if (mod == 0) {
                        int j = i / 10;
                        soundTable[sIdx] = sndTenTbl[j];
                    }
                    else {
                        soundTable[sIdx] = sndTbl[mod];
                    }
                    soundText[sIdx] = MAIN_PREFIX + i;
                    sIdx++;
                }
            }
            if (gxInfo.isHold()) {
                soundTable[sIdx] = sndSpecialTbl[0]; //R.raw.i_keep;
                soundText[sIdx] = sHolding;
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
                    sIdx++;
                }
            }
            soundTable[sIdx] = sndSpecialTbl[1]; // R.raw.i_nomore;
            soundText[sIdx] = sNoMore;
            sIdx++;
        }

        private void addStepSound() {
            for (int i = 1; i < gxInfo.getStepCount(); i++) {
                int mod = i%10;
                if (mod == 0) {
                    int j = i / 10;
                    soundTable[sIdx] = sndTenTbl[j];
                }
                else {
                    soundTable[sIdx] = sndShortTbl[mod];
                }
                soundText[sIdx] = STEP_PREFIX + i;
                sIdx++;
            }
        }
    }

    class refreshScreen extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(String... inputParams) {

            String type = inputParams[0];
            int val = Integer.parseInt(inputParams[1]);
            switch (type) {
                case "d":
                    notifyItemRemoved(val);
                    break;
            }
            return "done";
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }
        @Override
        protected void onCancelled(String result) {
        }

        @Override
        protected void onPostExecute(String doI) {
        }
    }

    private static Handler displayCount = new Handler() {
        public void handleMessage(Message msg) {
            String cnt = msg.obj.toString().substring(1);
            String tv = msg.obj.toString().substring(0,1);
            switch (tv) {
                case MAIN_PREFIX:
                    nowTVMainCount.setText(cnt); break;
                case STEP_PREFIX:
                    nowTVStepCount.setText(cnt); break;
                case HOLD_PREFIX:
                    nowTVHoldCount.setText(cnt); break;
            }
        } };

    private static void finishHandler() {
        SystemClock.sleep(500);
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (cdtRunning) {
            cdtRunning = false;
            GxInfo gxInfo = gxInfos.get(currIdx);
            nowIVGo.setVisibility(View.VISIBLE);
            nowIVRun.setVisibility(View.GONE);
            nowIVStop.setVisibility(View.GONE);
            nowTVMainCount.setText(""+gxInfo.getMainCount());
            nowTVStepCount.setText(""+gxInfo.getStepCount());
            nowTVHoldCount.setText(""+gxInfo.getHoldCount());
            nowCard.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardBack));
        }
    }

    void stopHandler() {
        finishHandler();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int pos) {

        String s;
        GxInfo gxInfo = gxInfos.get(pos);
        holder.tvTypeName.setText(gxInfo.getTypeName());
        s = "" + gxInfo.getMainCount(); holder.tvMainCount.setText(s);
        s = "" + gxInfo.getSpeed();     holder.tvSpeed.setText(s);

        holder.ivStep.setImageResource((gxInfo.isStep()) ? R.mipmap.i_step_true : R.mipmap.i_step_false);
        s = (gxInfo.isStep()) ? ("" + gxInfo.getStepCount()) : ""; holder.tvStepCount.setText(s);

        holder.ivHold.setImageResource((gxInfo.isHold()) ? R.mipmap.i_hold_true : R.mipmap.i_hold_false);
        s = (gxInfo.isHold()) ? ("" + gxInfo.getHoldCount()) : ""; holder.tvHoldCount.setText(s);

        holder.ivUpDown.setImageResource(gxInfo.isUp() ? R.mipmap.i_up_true : R.mipmap.i_up_false);
        holder.ivReady.setImageResource(gxInfo.isSayReady() ? R.mipmap.i_ready_true : R.mipmap.i_ready_false);
        holder.ivStart.setImageResource(gxInfo.isSayStart() ? R.mipmap.i_start_true : R.mipmap.i_start_false);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(holder.ivRun);
//        Glide.with(mActivity).load(R.drawable.i_now_running).into(gifImage);
        Glide.with(mActivity).load(R.drawable.running_gifmaker).into(gifImage);
        holder.ivGo.setVisibility(View.VISIBLE);
        holder.ivRun.setVisibility(View.GONE);
        holder.ivStop.setVisibility(View.GONE);
    }
}