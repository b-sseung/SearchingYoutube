package org.techtown.search.main_adapter;

import android.view.View;

import org.techtown.search.main_adapter.ListAdapter;

public interface OnListClickListener {

    public void onItemClick(ListAdapter.ViewHolder holder, View view, int position);

}
