package com.sonetag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class formats the social networks display in the bottom sheet
 * @version 1
 * @author Benjamin BOURG
 */
public class ListAdapter extends BaseAdapter {

    /**
     * Attributes
     */
    private final LayoutInflater mInflater;
    private final ArrayList<Map<String, String>> listAccounts;

    /**
     * Constructor
     */
    public ListAdapter(Context context) {
        this.listAccounts = SingletonUserData.getInstance().getListSocialAccounts();
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listAccounts.size();
    }

    @Override
    public Object getItem(int position) {
        return listAccounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConstraintLayout layoutItem;

        if (convertView == null) {
            layoutItem = (ConstraintLayout) mInflater.inflate(R.layout.network_layout, parent, false);
        } else {
            layoutItem = (ConstraintLayout) convertView;
        }

        CheckBox cB = layoutItem.findViewWithTag("checkBox");
        cB.setId(position);
        cB.setText(listAccounts.get(position).get("accountName"));

        return layoutItem;
    }

}
