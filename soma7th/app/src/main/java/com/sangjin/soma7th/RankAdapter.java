package com.sangjin.soma7th;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sangjin.soma7th.beans.Rank;

import java.util.ArrayList;


public class RankAdapter extends BaseAdapter {
    private ArrayList<Rank> adapterRank = new ArrayList<Rank>();
    private LayoutInflater mInflator;

    public RankAdapter(Context ctx) {
        super();
        adapterRank = new ArrayList<Rank>();
        mInflator = ((Activity)ctx).getLayoutInflater();
    }

    public void addOrderList(ArrayList<Rank> rank) {
        adapterRank.addAll(rank);
    }

    public void clear() {
        adapterRank.clear();
    }

    @Override
    public int getCount() {
        return adapterRank.size();
    }

    @Override
    public Object getItem(int i) {
        return adapterRank.get(i);
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
            view = mInflator.inflate(R.layout.listitem_rank, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.tvListItemRank = (TextView) view.findViewById(R.id.item_rank);
            viewHolder.tvListItemName = (TextView) view.findViewById(R.id.item_name);
            viewHolder.tvListItemRecord = (TextView) view.findViewById(R.id.item_record);
            viewHolder.tvListItemPercent = (TextView) view.findViewById(R.id.item_percent);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Rank info = adapterRank.get(i);
        if (info == null)
            return view;

        if(info.getRank() <= 3) {
            viewHolder.tvListItemRecord.setPaintFlags(viewHolder.tvListItemRecord.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
            viewHolder.tvListItemName.setPaintFlags(viewHolder.tvListItemName.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
            viewHolder.tvListItemPercent.setPaintFlags(viewHolder.tvListItemPercent.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
            viewHolder.tvListItemRank.setPaintFlags(viewHolder.tvListItemRank.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        }

        if(i == 0) {
            viewHolder.tvListItemRank.setText("Rank");
            viewHolder.tvListItemName.setText("Name");
            viewHolder.tvListItemRecord.setText("Record");
            viewHolder.tvListItemPercent.setText("Percent");
        }
        else {
            viewHolder.tvListItemRank.setText(Integer.toString(info.getRank()));
            viewHolder.tvListItemName.setText(info.getId());
            viewHolder.tvListItemRecord.setText(info.getRecord());
            viewHolder.tvListItemPercent.setText(Integer.toString(info.getPercent())+"%");
        }
        return view;
    }

    private class ViewHolder {
        TextView tvListItemRank;
        TextView tvListItemName;
        TextView tvListItemRecord;
        TextView tvListItemPercent;
    }
}