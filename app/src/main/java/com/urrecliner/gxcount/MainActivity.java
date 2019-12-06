package com.urrecliner.gxcount;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import static com.urrecliner.gxcount.Vars.gxInfos;
import static com.urrecliner.gxcount.Vars.mActivity;
import static com.urrecliner.gxcount.Vars.mContext;
import static com.urrecliner.gxcount.Vars.recyclerView;
import static com.urrecliner.gxcount.Vars.shouter;
import static com.urrecliner.gxcount.Vars.utils;

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

        gxInfos = utils.readSharedPrefTables();
        if (gxInfos.size() == 0) {
            create_gxInfos();
        }

        recyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(SGL);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        recyclerView.setLayoutManager(SGL);
        recyclerView.setBackgroundColor(0x88000000 + ContextCompat.getColor(mContext, R.color.cardBack));
        recyclerView.setAdapter(recyclerViewAdapter);
        utils.log(logId,"Ready");
    }

    void create_gxInfos() {
        GxInfo gxInfo = new GxInfo();
        gxInfo.setTypeName("운동이름");
        gxInfos.add(gxInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_stop) {
            shouter.stop();
            return true;
        }
        if (item.getItemId() == R.id.action_add) {
            GxInfo gxInfo = new GxInfo();
            gxInfo.setTypeName("운동이름");
            gxInfos.add(gxInfos.size(),gxInfo);
            utils.saveSharedPrefTables();
            recyclerViewAdapter.notifyItemChanged(gxInfos.size());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
