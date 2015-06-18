package com.b2b.walcum.sycub;

/**
 * Created by Deep on 6/15/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2b.walcum.sycub.model.GuestWaiting;


import io.realm.RealmResults;


public class MyListAdapter extends BaseAdapter{
    private RealmResults<GuestWaiting> results;
    private Context mContext;

    public MyListAdapter(RealmResults<GuestWaiting> results, Context mContext) {
        this.results=results;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.frag_waiting_list_items, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.textViewName);
            holder.numOfGuests = (TextView) convertView.findViewById(R.id.textViewNumOfGuests);
            holder.waitingTime = (TextView) convertView.findViewById(R.id.textViewWaitTime);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        GuestWaiting gw = results.get(position);
        holder.name.setText(gw.getName());
        holder.numOfGuests.setText(String.valueOf(gw.getNumOfGuests()));
        holder.waitingTime.setText(String.valueOf(gw.getWaitTime()));

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView numOfGuests;
        TextView waitingTime;
    }
}
