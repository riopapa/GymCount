package com.urrecliner.gymcount;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.urrecliner.gymcount.Vars.cdtRunning;
import static com.urrecliner.gymcount.Vars.countUpDowns;
import static com.urrecliner.gymcount.Vars.currIdx;
import static com.urrecliner.gymcount.Vars.gymInfo;
import static com.urrecliner.gymcount.Vars.gymInfos;
import static com.urrecliner.gymcount.Vars.mActivity;
import static com.urrecliner.gymcount.Vars.mContext;
import static com.urrecliner.gymcount.Vars.nowCard;
import static com.urrecliner.gymcount.Vars.nowIVReady;
import static com.urrecliner.gymcount.Vars.nowIVShout;
import static com.urrecliner.gymcount.Vars.nowIVStart;
import static com.urrecliner.gymcount.Vars.nowIVStop;
import static com.urrecliner.gymcount.Vars.nowTVHoldCount;
import static com.urrecliner.gymcount.Vars.nowTVMainCount;
import static com.urrecliner.gymcount.Vars.nowTVStepCount;
import static com.urrecliner.gymcount.Vars.recyclerViewAdapter;
import static com.urrecliner.gymcount.Vars.shouter;
import static com.urrecliner.gymcount.Vars.spanCount;
import static com.urrecliner.gymcount.Vars.speakName;
import static com.urrecliner.gymcount.Vars.utils;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    @Override
    public int getItemCount() {
        return gymInfos.size();
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_train, parent, false);
        return new ViewHolder(view);
    }

    private static TextView tvNowSpeed, tvNowMainCount, tvNowStepCount, tvNowHoldCount;

    private final static int SPEED_MIN = 5, SPEED_MAX = 90;
    private final static int COUNT_MIN = 4, COUNT_MAX = 105;
    private final static int STEP_MIN = 2, STEP_MAX = 12;
    private final static int HOLD_MIN = 5, HOLD_MAX = 30;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTypeName, tvSpeed, tvSpeedTxt, tvUpDownCount, tvStepCount, tvHoldCount;
        ImageView ivHold, ivUpDown, ivStep, ivStart, ivReady, ivShout, ivStop, ivDelete;
        LinearLayout loReadyStart, loShoutStop;
//        GifView gifView;
        int wheelValue = 0;

        ViewHolder(final View itemView) {
            super(itemView);

            tvTypeName = itemView.findViewById(R.id.typeName);
            tvSpeed = itemView.findViewById(R.id.speed);
            tvSpeedTxt = itemView.findViewById(R.id.speedTxt);
            tvUpDownCount = itemView.findViewById(R.id.mainCount);
            ivStep = itemView.findViewById(R.id.step);
            tvStepCount = itemView.findViewById(R.id.stepCount);
            ivHold = itemView.findViewById(R.id.hold);
            tvHoldCount = itemView.findViewById(R.id.holdCount);
            ivUpDown = itemView.findViewById(R.id.up_down);
            ivStart = itemView.findViewById(R.id.start);
            ivReady = itemView.findViewById(R.id.ready);
            ivShout = itemView.findViewById(R.id.shout);
            ivStop = itemView.findViewById(R.id.stop);
            ivStop.setVisibility(View.GONE);
            ivDelete = itemView.findViewById(R.id.delete);
            loReadyStart = itemView.findViewById(R.id.readyStart);
            loShoutStop = itemView.findViewById(R.id.shoutStop);

            tvTypeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currIdx = getAdapterPosition();
                    gymInfo = gymInfos.get(currIdx);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("이름은? ");
                    final EditText input = new EditText(mContext);
                    input.setText(gymInfo.getTypeName());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        String s = input.getText().toString();
                        if (s.length() < 1)
                            s = "운동이름 "+(currIdx +1);
                        for (int i = 0; i < gymInfos.size(); i++)
                            if (i != currIdx && gymInfos.get(i).getTypeName().equals(s))
                                s += "1";
                        gymInfo.setTypeName(s);
                        gymInfos.set(currIdx, gymInfo);
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
                    gymInfo = gymInfos.get(currIdx);
                    tvNowSpeed = tvSpeed;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(gymInfo.getTypeName());
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" 운동 속도 ");

                    final List<String> wheelValues = getSpeedTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    int index = gymInfo.getSpeed();
                    for (int i = 0; i < wheelValues.size(); i++)
                        if (wheelValues.get(i).equals(""+index))
                            wV.selectIndex(i);    // index pointer

                    wV.setAdditionCenterMark("");     // whole space
//                    wV.setAdditionCenterMark("\u3040");     // whole space
                    wV.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                        @Override
                        public void onWheelItemSelected(WheelView wheelView, int position) {
                            wheelValue = Integer.parseInt(wheelValues.get(position));
                        }
                        @Override
                        public void onWheelItemChanged(WheelView wheelView, int position) {
                        }
                    });

                    builder.setView(theView)
                            .setPositiveButton("SET",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    gymInfo.setSpeed(wheelValue);
                                    gymInfos.set(currIdx, gymInfo);
                                    utils.saveSharedPrefTables();
                                    String s = wheelValue+"";
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
                    for (int i = SPEED_MIN; i < 25; i++) result.add("" + i);
                    for (int i = 25; i <= SPEED_MAX; i+= 5) result.add("" + i);
                    return result;
                }
            });

            tvUpDownCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gymInfo = gymInfos.get(currIdx);
                    tvNowMainCount = tvUpDownCount;
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(gymInfo.getTypeName());
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" 횟수 설정 ");

                    final List<String> wheelValues = setCountTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    wV.setAdditionCenterMark("회");
                    String val = ""+ gymInfo.getMainCount();
                    for (int i = 0; i < wheelValues.size(); i++)
                        if (wheelValues.get(i).equals(val))
                            wV.selectIndex(i);    // index pointer
                    wV.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                        @Override
                        public void onWheelItemSelected(WheelView wheelView, int position) {
                            wheelValue = Integer.parseInt(wheelValues.get(position));
                        }
                        @Override
                        public void onWheelItemChanged(WheelView wheelView, int position) {
                        }
                    });

                    builder.setView(theView)
                            .setPositiveButton("SET",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    gymInfo.setMainCount(wheelValue);
                                    gymInfos.set(currIdx, gymInfo);
                                    utils.saveSharedPrefTables();
                                    String s = ""+wheelValue;
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
                    for (int i = COUNT_MIN; i < 20; i++) result.add("" + i);
                    for (int i = 20; i < 60 ; i+=5) result.add(""+i);
                    for (int i = 60; i < COUNT_MAX ; i+=10) result.add(""+i);
                    return result;
                }
            });

            tvStepCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gymInfo = gymInfos.get(currIdx);
                    tvNowStepCount = tvStepCount;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(gymInfo.getTypeName());
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" 스텝수 설정 ");

                    final List<String> wheelValues = setCountTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    String val = ""+ gymInfo.getStepCount();
                    for (int i = 0; i < wheelValues.size(); i++)
                        if (wheelValues.get(i).equals(val))
                            wV.selectIndex(i);    // index pointer
                    wV.setAdditionCenterMark("회");
                    wV.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                        @Override
                        public void onWheelItemSelected(WheelView wheelView, int position) {
                            wheelValue = Integer.parseInt(wheelValues.get(position));
                        }
                        @Override
                        public void onWheelItemChanged(WheelView wheelView, int position) {
                        }
                    });

                    builder.setView(theView)
                            .setPositiveButton("SET",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    gymInfo.setStepCount(wheelValue);
                                    gymInfos.set(currIdx, gymInfo);
                                    utils.saveSharedPrefTables();
                                    String s = ""+wheelValue;
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
                    for (int i = STEP_MIN; i <= STEP_MAX; i++) result.add("" + i);
                    return result;
                }
            });

            tvHoldCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gymInfo = gymInfos.get(currIdx);
                    tvNowHoldCount = tvHoldCount;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View theView = inflater.inflate(R.layout.get_number, null);
                    final TextView tvt = theView.findViewById(R.id.title);
                    tvt.setText(gymInfo.getTypeName());
                    final TextView tvs = theView.findViewById(R.id.subtitle);
                    tvs.setText(" 버티기 설정 ");

                    final List<String> wheelValues = setCountTable();
                    WheelView wV = theView.findViewById(R.id.wheel);
                    wV.setItems(wheelValues);
                    String val = ""+ gymInfo.getHoldCount();
                    for (int i = 0; i < wheelValues.size(); i++)
                        if (wheelValues.get(i).equals(val))
                            wV.selectIndex(i);    // index pointer
                    wV.setAdditionCenterMark("회");
                    wV.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                        @Override
                        public void onWheelItemSelected(WheelView wheelView, int position) {
                            wheelValue = Integer.parseInt(wheelValues.get(position));
                        }
                        @Override
                        public void onWheelItemChanged(WheelView wheelView, int pos) {
                        }
                    });

                    builder.setView(theView)
                            .setPositiveButton("SET",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    gymInfo.setHoldCount(wheelValue);
                                    gymInfos.set(currIdx, gymInfo);
                                    utils.saveSharedPrefTables();
                                    String s = ""+wheelValue;
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
                    for (int i = HOLD_MIN; i <= HOLD_MAX; i++) result.add("" + i);
                    return result;
                }
            });

            ivStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gymInfo = gymInfos.get(currIdx);
                    boolean tf = !gymInfo.isStep();
                    gymInfo.setStep(tf);
                    ivStep.setImageResource((tf) ? R.mipmap.icon_step_on:R.mipmap.icon_step_off);
                    ivStep.invalidate();
                    gymInfos.set(currIdx, gymInfo);
                    utils.saveSharedPrefTables();
                    String s = (tf) ? (""+ gymInfo.getStepCount()) : "";
                    tvStepCount.setText(s);
                }
            });

            ivHold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gymInfo = gymInfos.get(currIdx);
                    boolean tf = !gymInfo.isHold();
                    gymInfo.setHold(tf);
                    gymInfos.set(currIdx, gymInfo);
                    utils.saveSharedPrefTables();
                    ivHold.setImageResource((tf) ? R.mipmap.icon_hold_on:R.mipmap.icon_hold_off);
                    ivHold.invalidate();
                    String s = (tf) ? (""+ gymInfo.getHoldCount()) : "";
                    tvHoldCount.setText(s);
                }
            });

            ivUpDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gymInfo = gymInfos.get(currIdx);
                    int count = (gymInfo.getCountUpDown() + 1) % 4;
                    gymInfo.setCountUpDown(count);
                    gymInfos.set(currIdx, gymInfo);
                    utils.saveSharedPrefTables();
                    ivUpDown.setImageResource(countUpDowns[count]);
                    ivUpDown.invalidate();
                    if (count > 1 && gymInfo.isStep())
                        Toast.makeText(mContext, "Count Up/Down 5 와 Step 을 같이 쓴다고? ",Toast.LENGTH_SHORT).show();
                }
            });

            ivReady.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gymInfo = gymInfos.get(currIdx);
                    boolean tf = !gymInfo.isSayReady();
                    gymInfo.setSayReady(tf);
                    gymInfos.set(currIdx, gymInfo);
                    utils.saveSharedPrefTables();
                    ivReady.setImageResource((tf)? R.mipmap.icon_ready_on:R.mipmap.icon_ready_off);
                    ivReady.invalidate();
                }
            });

            ivStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gymInfo = gymInfos.get(currIdx);
                    boolean tf = !gymInfo.isSayStart();
                    gymInfo.setSayStart(tf);
                    gymInfos.set(currIdx, gymInfo);
                    utils.saveSharedPrefTables();
                    ivStart.setImageResource((tf) ? R.mipmap.icon_start_on:R.mipmap.icon_start_off);
                    ivStart.invalidate();
                }
            });

            ivShout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdtRunning = true;
                    currIdx = getAdapterPosition();
                    gymInfo = gymInfos.get(currIdx);
                    nowTVMainCount = itemView.findViewById(R.id.mainCount);
                    nowTVStepCount = itemView.findViewById(R.id.stepCount);
                    nowTVHoldCount = itemView.findViewById(R.id.holdCount);
//                    nowGifView = itemView.findViewById(R.id.gif);
                    nowIVShout = itemView.findViewById(R.id.shout);
                    nowIVStop = itemView.findViewById(R.id.stop);
                    nowIVReady = itemView.findViewById(R.id.ready);
                    nowIVStart = itemView.findViewById(R.id.start);
                    nowCard = itemView.findViewById(R.id.card_view);
                    if (speakName) {
                        utils.ttsSpeak(gymInfo.getTypeName()+", , "+ gymInfo.getMainCount()+" 회애");
                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                shouter.start();
                            }
                        }, 2000);
                    }
                    else
                        shouter.start();
                }
            });
            ivStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currIdx = getAdapterPosition();
                    gymInfo = gymInfos.get(currIdx);
                    shouter.stop();
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gymInfos.remove(currIdx);
                    utils.saveSharedPrefTables();
                    recyclerViewAdapter.notifyItemRemoved(currIdx);
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int pos) {

        String s;
        LinearLayout.LayoutParams lpL, lpR;
        GymInfo gymInfo = gymInfos.get(pos);
        int textSize = (spanCount == 2) ? 20:16;
        lpL = (LinearLayout.LayoutParams) holder.tvTypeName.getLayoutParams();
        lpL.height = textSize * 60 / 10;
        lpL.weight = 5;    // no change
        holder.tvTypeName.setText(gymInfo.getTypeName());
        holder.tvTypeName.setTextSize(textSize);
        holder.tvTypeName.setLayoutParams(lpL);

        lpL = (LinearLayout.LayoutParams) holder.tvSpeedTxt.getLayoutParams();
        lpR = (LinearLayout.LayoutParams) holder.tvSpeed.getLayoutParams();
        lpL.height = textSize * 60 / 10;
        lpR.height = textSize * 60 / 10;
        lpL.weight = 4;
        lpR.weight = 4;
        holder.tvSpeedTxt.setLayoutParams(lpL);
        holder.tvSpeedTxt.setTextSize(textSize);
        holder.tvSpeed.setLayoutParams(lpR);
        holder.tvSpeed.setTextSize(textSize);
        s = "" + gymInfo.getSpeed(); holder.tvSpeed.setText(s);

        holder.ivUpDown.setLayoutParams(lpL);
        holder.ivUpDown.setImageResource(countUpDowns[gymInfo.getCountUpDown()]);
        holder.tvUpDownCount.setLayoutParams(lpR);
        holder.tvUpDownCount.setTextSize(textSize);
        s = "" + gymInfo.getMainCount(); holder.tvUpDownCount.setText(s);

        holder.ivStep.setLayoutParams(lpL);
        holder.ivStep.setImageResource((gymInfo.isStep()) ? R.mipmap.icon_step_on : R.mipmap.icon_step_off);
        holder.tvStepCount.setLayoutParams(lpR);
        holder.tvStepCount.setTextSize(textSize);
        s = (gymInfo.isStep()) ? ("" + gymInfo.getStepCount()) : ""; holder.tvStepCount.setText(s);

        holder.ivHold.setLayoutParams(lpL);
        holder.ivHold.setImageResource((gymInfo.isHold()) ? R.mipmap.icon_hold_on : R.mipmap.icon_hold_off);
        holder.tvHoldCount.setLayoutParams(lpR);
        holder.tvHoldCount.setTextSize(textSize);
        s = (gymInfo.isHold()) ? ("" + gymInfo.getHoldCount()) : ""; holder.tvHoldCount.setText(s);

        lpL = (LinearLayout.LayoutParams) holder.loReadyStart.getLayoutParams();
        lpL.weight = 45;
        lpL.height = textSize * 165 / 10;
        lpR = (LinearLayout.LayoutParams) holder.loShoutStop.getLayoutParams();
        lpR.weight = 55;
        lpR.height = textSize * 165 / 10;
        holder.loReadyStart.setLayoutParams(lpL);
        holder.loShoutStop.setLayoutParams(lpR);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.ivStart.setLayoutParams(lp);
        holder.ivReady.setLayoutParams(lp);
        holder.ivReady.setImageResource(gymInfo.isSayReady() ? R.mipmap.icon_ready_on : R.mipmap.icon_ready_off);
        holder.ivStart.setImageResource(gymInfo.isSayStart() ? R.mipmap.icon_start_on : R.mipmap.icon_start_off);
        holder.ivShout.setVisibility(View.VISIBLE);
        holder.ivStop.setVisibility(View.GONE);
    }
}