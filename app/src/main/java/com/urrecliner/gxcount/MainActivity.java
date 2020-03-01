package com.urrecliner.gxcount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.ArrayList;

import static com.urrecliner.gxcount.Vars.gxInfos;
import static com.urrecliner.gxcount.Vars.mActivity;
import static com.urrecliner.gxcount.Vars.mContext;
import static com.urrecliner.gxcount.Vars.recyclerView;
import static com.urrecliner.gxcount.Vars.recyclerViewAdapter;
import static com.urrecliner.gxcount.Vars.shouter;
import static com.urrecliner.gxcount.Vars.sizeX;
import static com.urrecliner.gxcount.Vars.spanCount;
import static com.urrecliner.gxcount.Vars.speakName;
import static com.urrecliner.gxcount.Vars.utils;

public class MainActivity extends AppCompatActivity {

    private final static String logId = "main";
    ArrayList<GxInfo> gxInfoArrayList;
    SharedPreferences sharePrefer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mActivity = this;
        utils = new Utils();
        utils.soundInitiate();
        sharePrefer = getApplicationContext().getSharedPreferences("gxCount", MODE_PRIVATE);
        spanCount = sharePrefer.getInt("spanCount", 3);
        speakName = sharePrefer.getBoolean("speakName", true);
        recyclerViewAdapter = new RecyclerViewAdapter();
        gxInfoArrayList = new MakeGxInfos().getGxArray();
        gxInfos = utils.readSharedPrefTables();
        if (gxInfos.size() == 0)
            gxInfos = new MakeGxInfos().getGxArray();
        sizeX = utils.getScreenWidth();

        prepareCards();
        utils.log(logId,"Ready");
        utils.initiateTTS();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    final static int MENU_DEFAULT = 100;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        for (int i = 0; i < gxInfoArrayList.size(); i++) {
            menu.add(0, MENU_DEFAULT + i, Menu.NONE, "Add "+gxInfoArrayList.get(i).getTypeName());
        }
        menu.addSubMenu(0, MENU_DEFAULT + gxInfoArrayList.size(), Menu.NONE, "RESET ALL");

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

        if (id >= MENU_DEFAULT && id < MENU_DEFAULT +gxInfoArrayList.size()) {
            id = id - MENU_DEFAULT;
            GxInfo gxNew = gxInfoArrayList.get(id);
            String s = gxNew.getTypeName();
            for (int i = 0; i < gxInfos.size(); i++)
                if (gxInfos.get(i).getTypeName().equals(s))
                    s += "1";
            gxNew.setTypeName(s);
            gxInfos.add(gxInfos.size(),gxNew);
            utils.saveSharedPrefTables();
            recyclerViewAdapter.notifyItemChanged(gxInfos.size());
            return true;
        }
        else if (id == MENU_DEFAULT +gxInfoArrayList.size()) { // reset menu
            gxInfos = new MakeGxInfos().getGxArray();
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
