package com.example.jzy.helloword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 病情诊断Activity
 * Created by jzy on 2017/8/5.
 */

public class DiagnoseActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = DiagnoseActivity.class.getSimpleName();
    private ListView lvSicknessTypes;
    private List<String> sicknessTypes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_diagnose);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    private void init() {
        lvSicknessTypes = (ListView) findViewById(R.id.lv_sickness_types);
        sicknessTypes = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            sicknessTypes.add("病情" + i);
        }
        lvSicknessTypes.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sicknessTypes));
        lvSicknessTypes.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Android.R.id.home对应应用程序图标的id
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, sicknessTypes.get(position), Toast.LENGTH_SHORT).show();
    }
}
