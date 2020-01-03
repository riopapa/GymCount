package com.urrecliner.gxcount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import static com.urrecliner.gxcount.Vars.gxInfos;
import static com.urrecliner.gxcount.Vars.mActivity;
import static com.urrecliner.gxcount.Vars.mContext;
import static com.urrecliner.gxcount.Vars.recyclerView;
import static com.urrecliner.gxcount.Vars.recyclerViewAdapter;
import static com.urrecliner.gxcount.Vars.shouter;
import static com.urrecliner.gxcount.Vars.utils;

public class MainActivity extends AppCompatActivity {

    private final static String logId = "main";
    ArrayList<GxInfo> gxInfoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mActivity = this;
        utils = new Utils();
        utils.soundInitiate();
        recyclerViewAdapter = new RecyclerViewAdapter();

        gxInfoArrayList = new MakeGxInfos().getGxArray();

        gxInfos = utils.readSharedPrefTables();
        if (gxInfos.size() == 0)
            gxInfos = new MakeGxInfos().getGxArray();

        recyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(SGL);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        recyclerView.setLayoutManager(SGL);
        recyclerView.setBackgroundColor(0x88000000 + ContextCompat.getColor(mContext, R.color.cardBack));
        recyclerView.setAdapter(recyclerViewAdapter);


        utils.log(logId,"Ready");
    }

    final static int MENU_GXDEFAULT = 100;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        for (int i = 0; i < gxInfoArrayList.size(); i++) {
            menu.add(0, MENU_GXDEFAULT + i, Menu.NONE, "Add "+gxInfoArrayList.get(i).getTypeName());
        }
        menu.addSubMenu(0, MENU_GXDEFAULT + gxInfoArrayList.size(), Menu.NONE, "RESET ALL");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_stop) {
            shouter.stop();
            return true;
        }

        if (id >=  MENU_GXDEFAULT && id < MENU_GXDEFAULT+gxInfoArrayList.size()) {
            id = id - MENU_GXDEFAULT;
            GxInfo gxNew = gxInfoArrayList.get(id);
            gxInfos.add(gxInfos.size(),gxNew);
            utils.saveSharedPrefTables();
            recyclerViewAdapter.notifyItemChanged(gxInfos.size());
            return true;
        }
        else if (id == MENU_GXDEFAULT+gxInfoArrayList.size()) { // reset menu
            gxInfos = new MakeGxInfos().getGxArray();
            utils.saveSharedPrefTables();
            finish();
            Intent intent=new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
