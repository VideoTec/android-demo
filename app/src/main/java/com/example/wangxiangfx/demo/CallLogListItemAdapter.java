package com.example.wangxiangfx.demo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.feinno.rongfly.core.modules.calllog.CallItem;
import com.feinno.rongfly.core.modules.calllog.CallItems;

public class CallLogListItemAdapter extends BaseAdapter {

    private CallItems mCallItems;

    private Context context;
    private LayoutInflater layoutInflater;

    public CallLogListItemAdapter(Context context, CallItems items) {
        mCallItems = items;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mCallItems.getItemCount(true);
    }

    @Override
    public CallItems.CombineRecord getItem(int position) {
        return mCallItems.getItem(position, true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.call_log_list_item, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews(getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(CallItems.CombineRecord record, ViewHolder holder) {
        CallItem item = record.getCallItems().get(0);
        String number = item.getDay() + " - Missed:" + item.isMissed() + " - " + item.getNumber();
        number += " (" + record.getCallItems().size() + ")";
        holder.setItemNumber(number);
    }

    protected class ViewHolder {
        private TextView itemNumber;
        public void setItemNumber(String number) {
            itemNumber.setText(number);
        }

        public ViewHolder(View view) {
            itemNumber = (TextView) view.findViewById(R.id.item_number);
        }
    }
}
