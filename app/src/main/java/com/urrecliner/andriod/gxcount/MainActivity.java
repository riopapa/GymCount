package com.urrecliner.andriod.gxcount;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import static com.urrecliner.andriod.gxcount.Vars.isKeep;
import static com.urrecliner.andriod.gxcount.Vars.isUp;
import static com.urrecliner.andriod.gxcount.Vars.keepMax;
import static com.urrecliner.andriod.gxcount.Vars.mActivity;
import static com.urrecliner.andriod.gxcount.Vars.mContext;
import static com.urrecliner.andriod.gxcount.Vars.max;
import static com.urrecliner.andriod.gxcount.Vars.recyclerView;
import static com.urrecliner.andriod.gxcount.Vars.sayReady;
import static com.urrecliner.andriod.gxcount.Vars.sayStart;
import static com.urrecliner.andriod.gxcount.Vars.sharedPreferences;
import static com.urrecliner.andriod.gxcount.Vars.speed;
import static com.urrecliner.andriod.gxcount.Vars.typeName;
import static com.urrecliner.andriod.gxcount.Vars.utils;

public class MainActivity extends AppCompatActivity {

    RecyclerViewAdapter recyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        utils = new Utils();
        mContext = this;
        mActivity = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        typeName = utils.getStringArrayPref("typeName");
        speed = utils.getIntegerArrayPref("speed");
        max = utils.getIntegerArrayPref("max");
        isUp = utils.getBooleanArrayPref("isUp");
        sayStart = utils.getBooleanArrayPref("sayStart");
        sayReady = utils.getBooleanArrayPref("sayReady");
        isKeep = utils.getBooleanArrayPref("isKeep");
        keepMax = utils.getIntegerArrayPref("keepMax");

        if (typeName.size() < 6) {
            typeName.clear(); speed.clear(); max.clear(); isUp.clear(); sayStart.clear(); sayReady.clear();
            for (int i = 0; i < 6; i++) {
                typeName.add( "이름 "+(i+1));
                speed.add(10);
                max.add(10*(i+1));
                isUp.add(true);
                sayStart.add(true);
                sayReady.add(true);
                isKeep.add(false);
                keepMax.add(5);
            }
            utils.setStringArrayPref("typeName",typeName);
            utils.setIntegerArrayPref("speed", speed);
            utils.setIntegerArrayPref("max",max);
            utils.setBooleanArrayPref("isUp", isUp);
            utils.setBooleanArrayPref("sayStart", sayStart);
            utils.setBooleanArrayPref("sayReady", sayReady);
            utils.setBooleanArrayPref("isKeep", isKeep);
            utils.setIntegerArrayPref("keepMax",keepMax);
        }
        recyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(SGL);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        recyclerView.setLayoutManager(SGL);
        recyclerView.setBackgroundColor(0x88000000 + Color.GRAY);

        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_stop) {
            recyclerViewAdapter.stopHandler();
            Toast.makeText(getApplicationContext(),"STOP", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
