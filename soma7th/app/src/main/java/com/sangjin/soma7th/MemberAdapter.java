package com.sangjin.soma7th;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sangjin.soma7th.beans.Member;

import java.util.ArrayList;

/**
 * Created by user on 2016. 3. 21..
 */
public class MemberAdapter extends BaseAdapter {
    private ArrayList<Member> adapterFriend = new ArrayList<Member>();
    private LayoutInflater mInflator;

    public MemberAdapter(Context ctx) {
        super();
        adapterFriend = new ArrayList<Member>();
        mInflator = ((Activity)ctx).getLayoutInflater();
    }

    public void addOrderList(ArrayList<Member> log) {
        adapterFriend.addAll(log);
    }

    public void clear() {
        adapterFriend.clear();
    }

    @Override
    public int getCount() {
        return adapterFriend.size();
    }

    @Override
    public Object getItem(int i) {
        return adapterFriend.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.listitem_friend, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.tvListItemName = (TextView) view.findViewById(R.id.item_name);
            viewHolder.tvListItemRecord = (TextView) view.findViewById(R.id.item_record);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Member info = adapterFriend.get(i);
        if (info == null)
            return view;

        viewHolder.tvListItemName.setText(info.getId());
        String record = info.getWin()+"승 "+info.getLose()+"패";
        viewHolder.tvListItemRecord.setText(record);
        return view;
    }

    private class ViewHolder {
        TextView tvListItemName;
        TextView tvListItemRecord;
    }
}