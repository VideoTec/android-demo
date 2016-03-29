package com.example.wangxiangfx.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class CallLogAct extends AppCompatActivity {
    private ListView mAllCallsListView;
    private CallLogListItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);

        mAdapter = new CallLogListItemAdapter(this, DemoACT.sCallItems);
        mAllCallsListView = (ListView)findViewById(R.id.all_call_log_list);
        mAllCallsListView.setAdapter(mAdapter);
    }
}
