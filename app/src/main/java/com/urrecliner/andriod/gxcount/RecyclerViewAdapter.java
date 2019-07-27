package com.urrecliner.andriod.gxcount;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.urrecliner.andriod.gxcount.Vars.cdtRunning;
import static com.urrecliner.andriod.gxcount.Vars.gxCDT;
import static com.urrecliner.andriod.gxcount.Vars.gxIdx;
import static com.urrecliner.andriod.gxcount.Vars.isKeep;
import static com.urrecliner.andriod.gxcount.Vars.isUp;
import static com.urrecliner.andriod.gxcount.Vars.keepMax;
import static com.urrecliner.andriod.gxcount.Vars.mActivity;
import static com.urrecliner.andriod.gxcount.Vars.mContext;
import static com.urrecliner.andriod.gxcount.Vars.max;
import static com.urrecliner.andriod.gxcount.Vars.sayReady;
import static com.urrecliner.andriod.gxcount.Vars.sayStart;
import static com.urrecliner.andriod.gxcount.Vars.sound10Source;
import static com.urrecliner.andriod.gxcount.Vars.soundSource;
import static com.urrecliner.andriod.gxcount.Vars.speed;
import static com.urrecliner.andriod.gxcount.Vars.typeName;
import static com.urrecliner.andriod.gxcount.Vars.utils;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    static TextView nowTVCount;
    static ImageView nowIVGo;
    static CardView nowCard;
    static MediaPlayer mediaPlayer;
    @Override
    public int getItemCount() {
        return typeName.size();
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_timer, parent, false);
        return new ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTypeName;
        TextView tvNowCount;
        SeekBar sbSpeed;
        ImageView imUpDown, imKeep, imStart, imReady, imGo;
        TextView tvKeepCount;

        ViewHolder(final View itemView) {
            super(itemView);

            tvTypeName = itemView.findViewById(R.id.typeName);
            sbSpeed = itemView.findViewById(R.id.speed);
            tvNowCount = itemView.findViewById(R.id.nowCount);
            imUpDown = itemView.findViewById(R.id.up_down);
            imKeep = itemView.findViewById(R.id.keep);
            tvKeepCount = itemView.findViewById(R.id.keepCount);
            imStart = itemView.findViewById(R.id.start);
            imReady = itemView.findViewById(R.id.ready);
            imGo = itemView.findViewById(R.id.go);

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
                        gxCDT.cancel();
                        interval = speed.get(gxIdx) * 100;
                        runGXCounter();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("몇 번? (4~120)");
                    final EditText input = new EditText(mContext);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    String s = ""+max.get(gxIdx);
                    input.setText(s);
                    input.setTextSize(32);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String s = input.getText().toString();
                            int i = Integer.parseInt(s);
                            if (i >= 4 && i <= 120) {
                                max.set(gxIdx, i);
                                utils.setIntegerArrayPref("max", max);
                                tvNowCount.setText(s);
                                tvNowCount.invalidate();
                            }
                        }
                    });
                    builder.show();
                }
            });


            imKeep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    boolean tf = !isKeep.get(gxIdx);
                    isKeep.set(gxIdx, tf);
                    imKeep.setImageResource((tf) ? R.mipmap.i_keep_true:R.mipmap.i_keep_false);
                    tvKeepCount.setTextColor((tf) ? mActivity.getResources().getColor(R.color.countFore):mActivity.getResources().getColor(R.color.countBack));
                    utils.setBooleanArrayPref("isKeep", isKeep);
                    imKeep.invalidate();
                }
            });

            tvKeepCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("몇 번? (4~30)");
                    final EditText input = new EditText(mContext);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    String s = ""+keepMax.get(gxIdx);
                    input.setText(s);
                    input.setTextSize(32);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String s = input.getText().toString();
                            int i = Integer.parseInt(s);
                            if (i >= 4 && i <= 30) {
                                keepMax.set(gxIdx, i);
                                utils.setIntegerArrayPref("keepMax", keepMax);
                                tvKeepCount.setText(s);
                                tvKeepCount.invalidate();
                            }
                        }
                    });
                    builder.show();
                }
            });

            imUpDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    boolean tf = !isUp.get(gxIdx);
                    isUp.set(gxIdx, tf);
                    imUpDown.setImageResource((tf) ? R.mipmap.i_up_true:R.mipmap.i_up_false);
                    utils.setBooleanArrayPref("isUp", isUp);
                    imUpDown.invalidate();
                }
            });

            imStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    boolean tf = !sayStart.get(gxIdx);
                    sayStart.set(gxIdx, tf);
                    imStart.setImageResource((tf) ? R.mipmap.i_start_true:R.mipmap.i_start_false);
                    utils.setBooleanArrayPref("sayStart", sayStart);
                    imStart.invalidate();
                }
            });

            imReady.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    boolean tf = !sayReady.get(gxIdx);
                    sayReady.set(gxIdx, tf);
                    imReady.setImageResource((tf)? R.mipmap.i_ready_true:R.mipmap.i_ready_false);
                    utils.setBooleanArrayPref("sayReady", sayReady);
                    imReady.invalidate();
                }
            });

            imGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gxIdx = getAdapterPosition();
                    if (cdtRunning) {
                        finishHandler();
                    }
                    else {
                        imGo.setImageResource(R.mipmap.i_go_red);
                        nowTVCount = itemView.findViewById(R.id.nowCount);
                        nowIVGo = itemView.findViewById(R.id.go);
                        nowCard = itemView.findViewById(R.id.card_view);
                        calcInterval();
                        runGXCounter();
                    }
                }
            });
        }

        int count, display, increase, interval, sIdx, sNow;
        int [] soundTable;
        String [] soundText;

        void runGXCounter() {
            int color = mActivity.getResources().getColor(R.color.cardRun);
            nowCard.setCardBackgroundColor(color);
            setupSoundTable();
            sNow = 0;
            gxCDT = new CountDownTimer(sIdx * interval, interval) {
                public void onTick(long millisUntilFinished) {
                    count--;
                    display += increase;
                    Message msg = Message.obtain();
                    msg.obj = "< "+soundText[sNow]+ " >";
                    displayCount.sendMessage(msg);
                    mediaPlayer = MediaPlayer.create(mActivity, soundTable[sNow++]);
                    mediaPlayer.start();
//                    utils.soundPlay(soundTable[sNow++]);
                    utils.log("CDT","interval:"+interval+ ", count "+count+ ", disp["+display+"], mili:"+millisUntilFinished);
                }
                public void onFinish() {
                    finishHandler();
                }
            }.start();
            cdtRunning = true;
        }
        void calcInterval() {
            display = isUp.get(gxIdx) ? 0:max.get(gxIdx);
            count = max.get(gxIdx);
            increase = isUp.get(gxIdx) ? 1:-1;
            interval = speed.get(gxIdx) * 100;
        }
        private void setupSoundTable() {
            soundTable = new int[count+40];     // count + ready + start + keep 30
            soundText = new String[count+40];
            sIdx = 0;
            if (sayReady.get(gxIdx)) {
                soundTable[sIdx] = R.raw.nready;
                soundText[sIdx] = "준비";
                sIdx++;
            }
            if (sayStart.get(gxIdx)) {
                soundTable[sIdx] = R.raw.nstart;
                soundText[sIdx] = "시작";
                sIdx++;
            }
            if (isUp.get(gxIdx)) {
                for (int i = 1; i <= count; i++) {
                    int mod = i%10;
                    if (mod == 0) {
                        soundTable[sIdx] = R.raw.n10;
                        soundText[sIdx] = ""+i;
                        sIdx++;
                    }
                    else {
                        soundTable[sIdx] = soundSource[mod];
                        soundText[sIdx] = ""+i;
                        sIdx++;
                    }
                }
            }
            else {
                for (int i = count; i > 0; i--) {
                    int mod = i%10;
                    if (mod == 0) {
                        int j = i / 10;
                        soundTable[sIdx] = sound10Source[j];
                    }
                    else {
                        soundTable[sIdx] = soundSource[mod];
                    }
                    soundText[sIdx] = ""+i;
                    sIdx++;
                }
            }
            if (isKeep.get(gxIdx)) {
                soundTable[sIdx] = R.raw.nstart;    // R.raw.nkeep;
                soundText[sIdx] = "버티기";
                sIdx++;
                for (int i = keepMax.get(gxIdx); i > 0; i--) {
                    int mod = i%10;
                    if (mod == 0) {
                        int j = i / 10;
                        soundTable[sIdx] = sound10Source[j];
                    }
                    else {
                        soundTable[sIdx] = soundSource[mod];
                    }
                    soundText[sIdx] = ""+i;
                    sIdx++;
                }

            }
            soundTable[sIdx] = R.raw.nok;
            soundText[sIdx] = "OK";
            sIdx++;
        }
    }

    private static void finishHandler() {
        if (gxCDT != null) {
            gxCDT.cancel();
            gxCDT = null;
        }
        cdtRunning = false;
        Message msg = Message.obtain();
        msg.obj = ""+max.get(gxIdx);
        displayCount.sendMessage(msg);
        nowIVGo.setImageResource(R.mipmap.i_go_green);
        int color = mActivity.getResources().getColor(R.color.cardBack);
        nowCard.setCardBackgroundColor(color);
        mediaPlayer.release();
    }

    static final Handler displayCount = new Handler() {
        public void handleMessage(Message msg) {
            nowTVCount.setText(msg.obj.toString());
        }
    };

    void stopHandler() {
        finishHandler();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String s;
        holder.tvTypeName.setText(typeName.get(position));
        holder.sbSpeed.setProgress(speed.get(position));
        s = ""+max.get(position); holder.tvNowCount.setText(s);
        holder.imUpDown.setImageResource(isUp.get(position) ? R.mipmap.i_up_true : R.mipmap.i_up_false);
        holder.imStart.setImageResource(sayStart.get(position)? R.mipmap.i_start_true:R.mipmap.i_start_false);
        holder.imReady.setImageResource(sayReady.get(position)? R.mipmap.i_ready_true:R.mipmap.i_ready_false);
        holder.imKeep.setImageResource(isKeep.get(position)? R.mipmap.i_keep_true:R.mipmap.i_keep_false);
        s = ""+keepMax.get(position); holder.tvKeepCount.setText(s);
        holder.tvKeepCount.setTextColor(isKeep.get(position)? mActivity.getResources().getColor(R.color.countFore):mActivity.getResources().getColor(R.color.countBack));
    }

}
