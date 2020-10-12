package com.davenotdavid.samplepuzzle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.ArrayList;

public class TileImageAdapter extends BaseAdapter {
    private ArrayList<ImageView> mTileImages;
    private int mColumnWidth, mColumnHeight;

    public TileImageAdapter(ArrayList<ImageView> tileImages, int columnWidth, int columnHeight) {
        mTileImages = tileImages;
        mColumnWidth = columnWidth;
        mColumnHeight = columnHeight;
    }

    @Override
    public int getCount() {
        return mTileImages.size();
    }

    @Override
    public Object getItem(int position) {return (Object) mTileImages.get(position);}

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView tileImageView;

        if (convertView == null) {
            tileImageView = mTileImages.get(position);
        } else {
            tileImageView = (ImageView) convertView;
        }

        android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(mColumnWidth, mColumnHeight);
        tileImageView.setLayoutParams(params);

        return tileImageView;
    }
}
