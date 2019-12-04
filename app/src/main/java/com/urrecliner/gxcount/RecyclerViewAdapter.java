package com.urrecliner.gxcount;

import android.content.DialogInterface;
import android.media.MediaPlayer;
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
import static com.urrecliner.gxcount.Vars.gxCDT;
import static com.urrecliner.gxcount.Vars.gxIdx;
import static com.urrecliner.gxcount.Vars.holdCounts;
import static com.urrecliner.gxcount.Vars.holds;
import static com.urrecliner.gxcount.Vars.isUps;
import static com.urrecliner.gxcount.Vars.mActivity;
import static com.urrecliner.gxcount.Vars.mContext;
import static com.urrecliner.gxcount.Vars.mainCounts;
import static com.urrecliner.gxcount.Vars.sayReadys;
import static com.urrecliner.gxcount.Vars.sayStarts;
import static com.urrecliner.gxcount.Vars.sndShortTbl;
import static com.urrecliner.gxcount.Vars.sndSpecialTbl;
import static com.urrecliner.gxcount.Vars.sndTbl;
import static com.urrecliner.gxcount.Vars.sndTenTbl;
import static com.urrecliner.gxcount.Vars.speeds;
import static com.urrecliner.gxcount.Vars.stepCounts;
import static com.urrecliner.gxcount.Vars.steps;
import static com.urrecliner.gxcount.Vars.typeNames;
import static com.urrecliner.gxcount.Vars.utils;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    private static TextView nowTVCount;
    private static ImageView nowIVGo, nowIVRun, nowIVStop;
    private static CardView nowCard;
    private static MediaPlayer mediaPlayer;
    private static String sReady = "<준비>";
    private static String sStart = "<시작>";
    private static String sHolding = "<버티기>";
    private static String sNoMore = "<그만>";

    @Override
    public int getItemCount() {
        return typeNames.size();
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_timer, parent, false);
        return new ViewHolder(view);
    }

    private static TextView tvNowSpeed, tvNowMainCount, tvNowStepCount, tvNowHoldCount;

//    @Override
//    public void onValueChange(NumberPicker picker, int type, int newVal) {
//        String s;
//        int val = picker.getValue();
//        utils.log("onValueChange","val"+val+", newVal"+newVal);
//        switch (type) {
//            case 1:
//                holdCounts.set(gxIdx, newVal);
//                utils.setIntegerArrayPref("holdCounts", holdCounts);
//                s = ""+newVal;
//                tvNowHoldCount.setText(s);
//                tvNowHoldCount.invalidate();
//                break;
//
//            case 3:
//                stepCounts.set(gxIdx, newVal);
//                utils.setIntegerArrayPref("stepCounts", stepCounts);
//                s = ""+newVal;
//                tvNowStepCount.setText(s);
//                tvNowStepCount.invalidate();
//                break;
//
//            case 5:
//                mainCounts.set(gxIdx, newVal);
//                utils.setIntegerArrayPref("mainCounts", mainCounts);
//                s = ""+newVal;
//                tvNowMainCount.setText(s);
//                tvNowMainCount.invalidate();
//                break;
//        }
//    }

    final static int STEP_MIN = 2;
    final static int HOLD_MIN = 10;

    static class ViewHolder extends RecyclerView.ViewHolder {

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
                gxIdx = getAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("이름은? ");
                final EditText input = new EditText(mContext);
                input.setText(typeNames.get(gxIdx));
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    String s = input.getText().toString();
                    if (s.length() < 1)
                        s = "이름 "+(gxIdx+1);
                    typeNames.set(gxIdx, s);
                    utils.setStringArrayPref("typeNames", typeNames);
                    tvTypeName.setText(typeNames.get(gxIdx));
                    tvTypeName.invalidate();
                    }
                });
                builder.show();
                }
            });

            tvSpeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    tvNowSpeed = tvSpeed;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(typeNames.get(gxIdx));
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" [운동 속도] ");

                    final List<String> wheelValues = getSpeedTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    int val = (speeds.get(gxIdx) - 20) / 5;
                    wV.setAdditionCenterMark(" ");
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
                                    interval = wheelResult * 5 + 20;
                                    speeds.set(gxIdx, interval);
                                    utils.setIntegerArrayPref("speeds", speeds);
                                    s = interval+"";
                                    tvNowSpeed.setText(s);
                                    tvNowSpeed.invalidate();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
//                    utils.ttsSpeak(typeNames.get(gxIdx)+" 입니다. 오르내리기 회수를 설정합니다");
                    builder.show();
                }

                List <String> getSpeedTable() {

                    List<String> result = new ArrayList<>();
                    for (int i = 20; i <= 90; i+= 5) result.add("" + i);
                    return result;
                }
            });

            tvMainCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    tvNowMainCount = tvMainCount;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(typeNames.get(gxIdx));
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" [횟수 설정] ");

                    final List<String> wheelValues = setCountTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    wV.setAdditionCenterMark("회");
                    int val = mainCounts.get(gxIdx);
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
                                    if (wheelResult > 20)
                                        wheelResult = 20 + (wheelResult - 20) * 5;
                                    String s;
                                    mainCounts.set(gxIdx, wheelResult);
                                    utils.setIntegerArrayPref("mainCounts", mainCounts);
                                    s = ""+wheelResult;
                                    tvNowMainCount.setText(s);
                                    tvNowMainCount.invalidate();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
//                    utils.ttsSpeak(typeNames.get(gxIdx)+" 입니다. 오르내리기 회수를 설정합니다");
                    builder.show();
                }

                List <String> setCountTable() {

                    List<String> result = new ArrayList<>();
                        for (int i = 0; i < 20; i++) result.add("" + i);
                        for (int i = 0; i < 9 ; i++) result.add(""+(20 + i * 5));
                    return result;
                }
            });

            tvStepCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    tvNowStepCount = tvStepCount;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(typeNames.get(gxIdx));
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" [스텝수 설정] ");

                    final List<String> wheelValues = setCountTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    wV.setAdditionCenterMark("Step");
                    int val = stepCounts.get(gxIdx);
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
                            .setPositiveButton("SET",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String s;
                                    stepCounts.set(gxIdx, wheelResult + STEP_MIN);
                                    utils.setIntegerArrayPref("stepCounts", stepCounts);
                                    s = ""+(wheelResult+STEP_MIN);
                                    tvNowStepCount.setText(s);
                                    tvNowStepCount.invalidate();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
//                    utils.ttsSpeak(typeNames.get(gxIdx)+" 입니다. 오르내리기 회수를 설정합니다");
                    builder.show();
                }

                List <String> setCountTable() {

                    List<String> result = new ArrayList<>();
                    for (int i = STEP_MIN; i <= 12; i++) result.add("" + i);
                    return result;
                }
            });

            tvHoldCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    tvNowHoldCount = tvHoldCount;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(typeNames.get(gxIdx));
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" 버티기 설정 ");

                    final List<String> wheelValues = setCountTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    wV.setAdditionCenterMark("회");
                    int val = holdCounts.get(gxIdx);
                    wV.selectIndex(val);
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
                                    holdCounts.set(gxIdx, wheelResult + HOLD_MIN);
                                    utils.setIntegerArrayPref("holdCounts", holdCounts);
                                    s = ""+(wheelResult+HOLD_MIN);
                                    tvNowHoldCount.setText(s);
                                    tvNowHoldCount.invalidate();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
//                    utils.ttsSpeak(typeNames.get(gxIdx)+" 입니다. 오르내리기 회수를 설정합니다");
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
                    gxIdx = getAdapterPosition();
                    boolean tf = !steps.get(gxIdx);
                    steps.set(gxIdx, tf);
                    ivStep.setImageResource((tf) ? R.mipmap.i_step_true:R.mipmap.i_step_false);
                    ivStep.invalidate();
                    utils.setBooleanArrayPref("steps", steps);
                    String s = (tf) ? ""+stepCounts.get(gxIdx) : "";
                    tvStepCount.setText(s);
                }
            });

            ivHold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    boolean tf = !holds.get(gxIdx);
                    holds.set(gxIdx, tf);
                    ivHold.setImageResource((tf) ? R.mipmap.i_hold_true:R.mipmap.i_hold_false);
                    utils.setBooleanArrayPref("holds", holds);
                    ivHold.invalidate();
                    String s = (tf) ? ""+holdCounts.get(gxIdx) : "";
                    tvHoldCount.setText(s);
                }
            });

            ivUpDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    boolean tf = !isUps.get(gxIdx);
                    isUps.set(gxIdx, tf);
                    ivUpDown.setImageResource((tf) ? R.mipmap.i_up_true:R.mipmap.i_up_false);
                    utils.setBooleanArrayPref("isUps", isUps);
                    ivUpDown.invalidate();
                }
            });

            ivReady.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                gxIdx = getAdapterPosition();
                boolean tf = !sayReadys.get(gxIdx);
                sayReadys.set(gxIdx, tf);
                ivReady.setImageResource((tf)? R.mipmap.i_ready_true:R.mipmap.i_ready_false);
                utils.setBooleanArrayPref("sayReadys", sayReadys);
                ivReady.invalidate();
                }
            });

            ivStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                gxIdx = getAdapterPosition();
                boolean tf = !sayStarts.get(gxIdx);
                sayStarts.set(gxIdx, tf);
                ivStart.setImageResource((tf) ? R.mipmap.i_start_true:R.mipmap.i_start_false);
                utils.setBooleanArrayPref("sayStarts", sayStarts);
                ivStart.invalidate();
                }
            });

            ivGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                gxIdx = getAdapterPosition();
                nowTVCount = itemView.findViewById(R.id.mainCount);
                nowIVGo = itemView.findViewById(R.id.go);
                nowIVRun = itemView.findViewById(R.id.run);
                nowIVStop = itemView.findViewById(R.id.stop);
                nowCard = itemView.findViewById(R.id.card_view);
                nowIVGo.setVisibility(View.GONE);
                nowIVRun.setVisibility(View.VISIBLE);
                nowIVStop.setVisibility(View.VISIBLE);
                nowCard.setCardBackgroundColor(ContextCompat.getColor(mContext,R.color.cardRun));
                nowCard.invalidate();
                calcInterval();
                setupSoundTable();
                sNow = 0;
                cdtRunning = true;
                int cdtDownTime = (soundText.length+2) * interval;
                utils.log("x","cdtDownTime "+cdtDownTime);
                runCountDownTimer(cdtDownTime);
                }
            });

            ivStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                gxIdx = getAdapterPosition();
                finishHandler();
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    deleteThisType();
                }
            });
        }

        int count, display, increase, interval, sIdx, sNow;
        int [] soundTable;
        String [] soundText;

        void calcInterval() {
            if (!cdtRunning) {
                count = mainCounts.get(gxIdx);
                display = isUps.get(gxIdx) ? 0 : mainCounts.get(gxIdx);
            }
            increase = isUps.get(gxIdx) ? 1:-1;
            interval = 1000 * 60 / speeds.get(gxIdx);
            utils.log("interval","interval "+interval+" speeds "+ speeds.get(gxIdx));
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
            if (stepCounts.get(gxIdx)== 2)
                tblSize = count* holdCounts.get(gxIdx) + 5 ;
            else
                tblSize = count + holdCounts.get(gxIdx) + 5;
            soundTable = new int[tblSize];
            soundText = new String[tblSize];
            sIdx = 0;
            if (!cdtRunning) {
                if (sayReadys.get(gxIdx)) {
                    soundTable[sIdx] = sndSpecialTbl[3];   // R.raw.i_ready
                    soundText[sIdx] = sReady;
                    sIdx++;
                }
                if (sayStarts.get(gxIdx)) {
                    soundTable[sIdx] = sndSpecialTbl[2];   // R.raw.i_start
                    soundText[sIdx] = sStart;
                    sIdx++;
                }
            }
            if (isUps.get(gxIdx)) {
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
            if (stepCounts.get(gxIdx) == 1) {
                soundTable[sIdx] = sndSpecialTbl[0]; //R.raw.i_keep;
                soundText[sIdx] = sHolding;
                sIdx++;
                for (int i = holdCounts.get(gxIdx); i > 0; i--) {
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
            sIdx--;
            soundTable[sIdx] = sndSpecialTbl[1]; // R.raw.i_nomore;
            soundText[sIdx] = sNoMore;
            sIdx++;
        }

        private void addSound123() {
            if (stepCounts.get(gxIdx) == 2) {
                for (int i = 1; i < holdCounts.get(gxIdx); i++) {
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

    void addNewType () {
        int size = typeNames.size();
        typeNames.add("이름 "+(size+1));
        speeds.add(60);
        mainCounts.add(10);
        steps.add(true);
        stepCounts.add(4);
        holds.add(true);
        holdCounts.add(5);
        isUps.add(false);
        sayStarts.add(true);
        sayReadys.add(true);
        Message msg = Message.obtain();
        msg.obj = "a";
        updatePreference.sendMessage(msg);

    }

    private static void deleteThisType () {

        typeNames.remove(gxIdx);
        speeds.remove(gxIdx);
        mainCounts.remove(gxIdx);
        steps.remove(gxIdx);
        stepCounts.remove(gxIdx);
        holds.remove(gxIdx);
        holdCounts.remove(gxIdx);
        isUps.remove(gxIdx);
        sayStarts.remove(gxIdx);
        sayReadys.remove(gxIdx);
        Message msg = Message.obtain();
        msg.obj = "d";
        updatePreference.sendMessage(msg);
    }

    private static Handler updatePreference = new Handler() {
        public void handleMessage (Message msg) {
            String s = msg.obj.toString();
            if (s.equals("d") || s.equals("a")) {
                utils.setStringArrayPref("typeNames", typeNames);
                utils.setIntegerArrayPref("speeds", speeds);
                utils.setIntegerArrayPref("mainCounts", mainCounts);
                utils.setIntegerArrayPref("stepCounts", stepCounts);
                utils.setIntegerArrayPref("holdCounts", holdCounts);
                utils.setBooleanArrayPref("steps", steps);
                utils.setBooleanArrayPref("holds", holds);
                utils.setBooleanArrayPref("isUps", isUps);
                utils.setBooleanArrayPref("sayReadys", sayReadys);
                utils.setBooleanArrayPref("sayStarts", sayStarts);
            }
        } };

    private static Handler displayCount = new Handler() {
        public void handleMessage(Message msg) {
            nowTVCount.setText(msg.obj.toString());
        } };

    private static void finishHandler() {
        SystemClock.sleep(800);
        if (gxCDT != null) {
            gxCDT.cancel();
            gxCDT = null;
        }
        if (cdtRunning) {
            cdtRunning = false;
            Message msg = Message.obtain();
            msg.obj = "" + mainCounts.get(gxIdx);
            displayCount.sendMessage(msg);
            nowIVGo.setVisibility(View.VISIBLE);
            nowIVRun.setVisibility(View.GONE);
            nowIVStop.setVisibility(View.GONE);
//            nowIVGo.setImageResource(R.mipmap.i_go_green);
            nowCard.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardBack));
        }
    }

    void stopHandler() {
        finishHandler();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int pos) {

        String s;
        holder.tvTypeName.setText(typeNames.get(pos));
        holder.tvSpeed.setText(""+ speeds.get(pos));
        s = ""+ mainCounts.get(pos); holder.tvMainCount.setText(s);

        holder.ivStep.setImageResource((steps.get(pos) ? R.mipmap.i_step_true :R.mipmap.i_step_false));
        s = (steps.get(pos)) ? ""+stepCounts.get(pos) : "";
        holder.tvStepCount.setText(s);

        holder.ivHold.setImageResource((holds.get(pos) ? R.mipmap.i_hold_true :R.mipmap.i_hold_false));
        s = (holds.get(pos)) ? ""+holdCounts.get(pos) : "";
        holder.tvHoldCount.setText(s);

        holder.ivUpDown.setImageResource(isUps.get(pos) ? R.mipmap.i_up_true : R.mipmap.i_up_false);
        holder.ivReady.setImageResource(sayReadys.get(pos)? R.mipmap.i_ready_true:R.mipmap.i_ready_false);
        holder.ivStart.setImageResource(sayStarts.get(pos)? R.mipmap.i_start_true:R.mipmap.i_start_false);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(holder.ivRun);
//        Glide.with(mActivity).load(R.drawable.i_now_running).into(gifImage);
        Glide.with(mActivity).load(R.drawable.running_gifmaker).into(gifImage);
        holder.ivRun.setVisibility(View.GONE);
        holder.ivStop.setVisibility(View.GONE);

//        holder.tvStepCount.setTextColor(stepCounts.get(position) != 0 ?
//                ContextCompat.getColor(mContext, R.color.countFore): ContextCompat.getColor(mContext, R.color.countBack));
    }

}
//                    final NumberPicker np = theView.findViewById(R.id.getNumber);
//                    String[] myValues = getKeepMaxTable();
//
//                    np.setMinValue(0);
//                    np.setMaxValue(myValues.length - 1);
//                    np.setDisplayedValues(myValues);
//                    int val = holdCounts.get(gxIdx);
//                    np.setValue(val);    // index pointer
//                    np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
