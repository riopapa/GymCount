package com.urrecliner.gymcount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import static com.urrecliner.gymcount.Vars.gymInfos;
import static com.urrecliner.gymcount.Vars.mActivity;
import static com.urrecliner.gymcount.Vars.mContext;
import static com.urrecliner.gymcount.Vars.makeGymInfos;
import static com.urrecliner.gymcount.Vars.recyclerView;
import static com.urrecliner.gymcount.Vars.recyclerViewAdapter;
import static com.urrecliner.gymcount.Vars.shouter;
import static com.urrecliner.gymcount.Vars.sizeX;
import static com.urrecliner.gymcount.Vars.spanCount;
import static com.urrecliner.gymcount.Vars.speakName;
import static com.urrecliner.gymcount.Vars.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class TrainActivity extends AppCompatActivity {

    private final static String logId = "main";
    ArrayList<GymInfo> gymInfoArrayList;
    SharedPreferences sharePrefer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mActivity = this;
        utils = new Utils();
        utils.soundInitiate();
        sharePrefer = getApplicationContext().getSharedPreferences("GymCount", MODE_PRIVATE);
        spanCount = sharePrefer.getInt("spanCount", 3);
        speakName = sharePrefer.getBoolean("speakName", true);
        recyclerViewAdapter = new RecyclerViewAdapter();
        makeGymInfos.makeGymOptionList();
        gymInfos = utils.readSharedPrefTables();
//        if (gymInfos.size() == 0)
//            gymInfos = new MakeGymInfos().makeGymDefaultList();
        sizeX = utils.getScreenWidth();

        prepareCards();
        utils.log(logId,"Ready");
        utils.initiateTTS();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MobileAds.initialize(this,  mContext.getString(R.string.adv_id)); //""ca-app-pub-3940256099942544/6300978111");
        AdView adView  = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
    }

    final static int MENU_DEFAULT = 100;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        for (int i = 0; i < gymInfoArrayList.size(); i++) {
            menu.add(0, MENU_DEFAULT + i, Menu.NONE, "Add "+ gymInfoArrayList.get(i).getTypeName());
        }
        menu.addSubMenu(0, MENU_DEFAULT + gymInfoArrayList.size(), Menu.NONE, "RESET ALL");

        MenuItem item = menu.findItem(R.id.action_TwoThree);
        if (spanCount == 3)
            item.setIcon(R.mipmap.icon_two);
        else
            item.setIcon(R.mipmap.icon_three);

        item = menu.findItem(R.id.action_speak);
        item.setIcon((speakName)? R.mipmap.i_speak_off:R.mipmap.i_speak_on);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_stop) {
            shouter.stop();
            return true;
        }

        if (id >= MENU_DEFAULT && id < MENU_DEFAULT + gymInfoArrayList.size()) {
            id = id - MENU_DEFAULT;
            GymInfo gymNew = gymInfoArrayList.get(id);
            String s = gymNew.getTypeName();
            for (int i = 0; i < gymInfos.size(); i++)
                if (gymInfos.get(i).getTypeName().equals(s))
                    s += "1";
            gymNew.setTypeName(s);
            gymInfos.add(gymInfos.size(),gymNew);
            utils.saveSharedPrefTables();
            recyclerViewAdapter.notifyItemChanged(gymInfos.size());
            return true;
        }
        else if (id == MENU_DEFAULT + gymInfoArrayList.size()) { // reset menu
            makeGymInfos.makeGymOptionList();
            utils.saveSharedPrefTables();
            finish();
            Intent intent=new Intent(TrainActivity.this, TrainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_TwoThree) {
            if (spanCount == 2)
                spanCount = 3;
            else
                spanCount = 2;
            SharedPreferences.Editor editor = sharePrefer.edit();
            editor.putInt("spanCount", spanCount);
            editor.apply();
            if (spanCount == 3)
                item.setIcon(R.mipmap.icon_two);
            else
                item.setIcon(R.mipmap.icon_three);
            prepareCards();
        }
        if (id == R.id.action_speak) {
            speakName = !speakName;
            SharedPreferences.Editor editor = sharePrefer.edit();
            editor.putBoolean("speakName", speakName);
            editor.apply();
            item.setIcon((speakName)? R.mipmap.i_speak_off:R.mipmap.i_speak_on);
            prepareCards();
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareCards() {

        recyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(SGL);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        recyclerView.setLayoutManager(SGL);
        recyclerView.setBackgroundColor(0x88000000 + ContextCompat.getColor(mContext, R.color.cardBack));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

}