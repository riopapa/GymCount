package com.urrecliner.gxcount;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import static com.urrecliner.gxcount.Vars.currIdx;
import static com.urrecliner.gxcount.Vars.gxInfo;
import static com.urrecliner.gxcount.Vars.gxInfos;
import static com.urrecliner.gxcount.Vars.mActivity;
import static com.urrecliner.gxcount.Vars.mContext;
import static com.urrecliner.gxcount.Vars.nowCard;
import static com.urrecliner.gxcount.Vars.nowIVGo;
import static com.urrecliner.gxcount.Vars.nowIVRun;
import static com.urrecliner.gxcount.Vars.nowIVStop;
import static com.urrecliner.gxcount.Vars.nowTVHoldCount;
import static com.urrecliner.gxcount.Vars.nowTVMainCount;
import static com.urrecliner.gxcount.Vars.nowTVStepCount;
import static com.urrecliner.gxcount.Vars.recyclerViewAdapter;
import static com.urrecliner.gxcount.Vars.shouter;
import static com.urrecliner.gxcount.Vars.utils;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

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
    private final static int HOLD_MIN = 5;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTypeName, tvMainCount, tvStepCount, tvSpeed, tvHoldCount;
        ImageView ivHold, ivCountUp, ivStep, ivStart, ivReady, ivGo, ivRun, ivStop, ivDelete;
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

            ivCountUp = itemView.findViewById(R.id.up_down);
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
                    for (int i = SPEED_MIN; i <= 120; i+= 5) result.add("" + i);
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
                    wV.selectIndex(gxInfo.getStepCount()-STEP_MIN);
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
                    ivStep.setImageResource((tf) ? R.mipmap.icon_step_on:R.mipmap.icon_step_off);
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
                    ivHold.setImageResource((tf) ? R.mipmap.icon_hold_on:R.mipmap.icon_hold_off);
                    ivHold.invalidate();
                    String s = (tf) ? (""+gxInfo.getHoldCount()) : "";
                    tvHoldCount.setText(s);
                }
            });

            ivCountUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cdtRunning)
                        return;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    boolean tf = !gxInfo.isCountUp();
                    gxInfo.setCountUp(tf);
                    gxInfos.set(currIdx, gxInfo);
                    utils.saveSharedPrefTables();
                    ivCountUp.setImageResource((tf) ? R.mipmap.icon_up_on:R.mipmap.icon_up_off);
                    ivCountUp.invalidate();
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
                    gxInfo = gxInfos.get(currIdx);
                    boolean tf = !gxInfo.isSayStart();
                    gxInfo.setSayStart(tf);
                    gxInfos.set(currIdx, gxInfo);
                    utils.saveSharedPrefTables();
                    ivStart.setImageResource((tf) ? R.mipmap.icon_start_on:R.mipmap.icon_start_off);
                    ivStart.invalidate();
                }
            });

            ivGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdtRunning = true;
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    nowTVMainCount = itemView.findViewById(R.id.mainCount);
                    nowTVStepCount = itemView.findViewById(R.id.stepCount);
                    nowTVHoldCount = itemView.findViewById(R.id.holdCount);
                    nowIVGo = itemView.findViewById(R.id.go);
                    nowIVRun = itemView.findViewById(R.id.run);
                    nowIVStop = itemView.findViewById(R.id.stop);
                    nowCard = itemView.findViewById(R.id.card_view);
                    shouter.start();
                }
            });

            ivStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currIdx = getAdapterPosition();
                    gxInfo = gxInfos.get(currIdx);
                    shouter.stop();
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
                    recyclerViewAdapter.notifyItemRemoved(currIdx);
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int pos) {

        String s;
        GxInfo gxInfo = gxInfos.get(pos);
        holder.tvTypeName.setText(gxInfo.getTypeName());
        s = "" + gxInfo.getMainCount(); holder.tvMainCount.setText(s);
        s = "" + gxInfo.getSpeed();     holder.tvSpeed.setText(s);

        holder.ivStep.setImageResource((gxInfo.isStep()) ? R.mipmap.icon_step_on : R.mipmap.icon_step_off);
        s = (gxInfo.isStep()) ? ("" + gxInfo.getStepCount()) : ""; holder.tvStepCount.setText(s);

        holder.ivHold.setImageResource((gxInfo.isHold()) ? R.mipmap.icon_hold_on : R.mipmap.icon_hold_off);
        s = (gxInfo.isHold()) ? ("" + gxInfo.getHoldCount()) : ""; holder.tvHoldCount.setText(s);

        holder.ivCountUp.setImageResource(gxInfo.isCountUp() ? R.mipmap.icon_up_on : R.mipmap.icon_up_off);
        holder.ivReady.setImageResource(gxInfo.isSayReady() ? R.mipmap.icon_ready_on : R.mipmap.icon_ready_off);
        holder.ivStart.setImageResource(gxInfo.isSayStart() ? R.mipmap.icon_start_on : R.mipmap.icon_start_off);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(holder.ivRun);
//        Glide.with(mActivity).load(R.drawable.i_now_running).into(gifImage);
        Glide.with(mActivity).load(R.drawable.running_gifmaker).into(gifImage);
        holder.ivGo.setVisibility(View.VISIBLE);
        holder.ivRun.setVisibility(View.GONE);
        holder.ivStop.setVisibility(View.GONE);
    }
}