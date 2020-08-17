package com.urrecliner.gymcount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import static com.urrecliner.gymcount.Vars.gymOptionList;
import static com.urrecliner.gymcount.Vars.gymInfos;
import static com.urrecliner.gymcount.Vars.mActivity;
import static com.urrecliner.gymcount.Vars.mContext;
import static com.urrecliner.gymcount.Vars.makeGymInfos;
import static com.urrecliner.gymcount.Vars.recyclerView;
import static com.urrecliner.gymcount.Vars.recyclerViewAdapter;
import static com.urrecliner.gymcount.Vars.prefer;
import static com.urrecliner.gymcount.Vars.shouter;
import static com.urrecliner.gymcount.Vars.sizeX;
import static com.urrecliner.gymcount.Vars.spanCount;
import static com.urrecliner.gymcount.Vars.speakName;
import static com.urrecliner.gymcount.Vars.utils;

public class MainActivity extends AppCompatActivity {

    private final static String logId = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mActivity = this;
        utils = new com.urrecliner.gymcount.Utils();
        utils.soundInitiate();
        prefer = getApplicationContext().getSharedPreferences("GymCount", MODE_PRIVATE);
        spanCount = prefer.getInt("spanCount", 3);
        speakName = prefer.getBoolean("speakName", true);
        recyclerViewAdapter = new com.urrecliner.gymcount.RecyclerViewAdapter();
        makeGymInfos = new MakeGymInfos();
        makeGymInfos.makeGymOptionList();
        gymInfos = utils.readSharedPrefTables();
        if (gymInfos.size() == 0) {
            for (GymInfo g : gymOptionList) {
                gymInfos.add(g);
            }
            utils.saveSharedPrefTables();
        }
        sizeX = utils.getScreenWidth();

        prepareCards();
        utils.log(logId,"Ready");
        utils.initiateTTS();

        MobileAds.initialize(this,  mContext.getString(R.string.adv_id)); //""ca-app-pub-3940256099942544/6300978111");
        AdView adView  = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    final static int MENU_DEFAULT = 100;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        for (int i = 0; i < gymOptionList.size(); i++) {
            menu.add(0, MENU_DEFAULT + i, Menu.NONE, "Add "+ gymOptionList.get(i).getTypeName());
        }
        menu.addSubMenu(0, MENU_DEFAULT + gymOptionList.size(), Menu.NONE, "RESET ALL");

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

        if (id >= MENU_DEFAULT && id < MENU_DEFAULT + gymOptionList.size()) {
            id = id - MENU_DEFAULT;
            GymInfo gymNew = gymOptionList.get(id);
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
        else if (id == MENU_DEFAULT + gymOptionList.size()) { // reset menu
            for (GymInfo g : gymOptionList) {
                gymInfos.add(g);
            }
            utils.saveSharedPrefTables();
            finish();
            Intent intent=new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_TwoThree) {
            if (spanCount == 2)
                spanCount = 3;
            else
                spanCount = 2;
            SharedPreferences.Editor editor = prefer.edit();
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
            SharedPreferences.Editor editor = prefer.edit();
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
