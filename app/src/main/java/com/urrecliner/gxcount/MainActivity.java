package com.urrecliner.gxcount;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import static com.urrecliner.gxcount.Vars.holds;
import static com.urrecliner.gxcount.Vars.mainCounts;
import static com.urrecliner.gxcount.Vars.holdCounts;
import static com.urrecliner.gxcount.Vars.isUps;
import static com.urrecliner.gxcount.Vars.mActivity;
import static com.urrecliner.gxcount.Vars.mContext;
import static com.urrecliner.gxcount.Vars.recyclerView;
import static com.urrecliner.gxcount.Vars.sayReadys;
import static com.urrecliner.gxcount.Vars.sayStarts;
import static com.urrecliner.gxcount.Vars.sharedPreferences;
import static com.urrecliner.gxcount.Vars.speeds;
import static com.urrecliner.gxcount.Vars.stepCounts;
import static com.urrecliner.gxcount.Vars.steps;
import static com.urrecliner.gxcount.Vars.typeNames;
import static com.urrecliner.gxcount.Vars.utils;
import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity {

    private final static String logId = "main";
    RecyclerViewAdapter recyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mActivity = this;
        utils = new Utils();
        utils.soundInitiate();
        recyclerViewAdapter = new RecyclerViewAdapter();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        typeNames = utils.getStringArrayPref("typeNames");
        speeds = utils.getIntegerArrayPref("speeds");
        mainCounts = utils.getIntegerArrayPref("mainCounts");
        stepCounts = utils.getIntegerArrayPref("stepCounts");
        steps = utils.getBooleanArrayPref("steps");
        holdCounts = utils.getIntegerArrayPref("holdCounts");
        holds = utils.getBooleanArrayPref("holds");
        isUps = utils.getBooleanArrayPref("isUps");
        sayStarts = utils.getBooleanArrayPref("sayStarts");
        sayReadys = utils.getBooleanArrayPref("sayReadys");

//        utils.log("size","typeNames:"+typeNames.size()+" speeds:"+speeds.size()+" mainCounts:"+mainCounts.size()+" stepCounts"+stepCounts.size()+" steps"+steps.size());
        int minSize = min(typeNames.size(), speeds.size());
        minSize = min(minSize, mainCounts.size());
        minSize = min(minSize, stepCounts.size());
        minSize = min(minSize, steps.size());
        minSize = min(minSize, holdCounts.size());
        minSize = min(minSize, holds.size());
        minSize = min(minSize, isUps.size());
        minSize = min(minSize, sayStarts.size());
        minSize = min(minSize, sayReadys.size());
        if (minSize == 0) {
            typeNames.clear(); mainCounts.clear(); steps.clear(); stepCounts.clear(); holds.clear(); holdCounts.clear(); isUps.clear(); sayStarts.clear(); sayReadys.clear();
            recyclerViewAdapter.addNewType();
            minSize = 1;
        }
        recyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(SGL);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        recyclerView.setLayoutManager(SGL);
        recyclerView.setBackgroundColor(0x88000000 + ContextCompat.getColor(mContext, R.color.cardBack));
        recyclerView.setAdapter(recyclerViewAdapter);
        utils.log(logId,"Ready "+minSize);
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
            return true;
        }
        if (item.getItemId() == R.id.action_add) {
            recyclerViewAdapter.addNewType();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
