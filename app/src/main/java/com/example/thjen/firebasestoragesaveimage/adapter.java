package com.example.thjen.firebasestoragesaveimage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class adapter extends BaseAdapter {

    Context context;
    List<HinhAnh> list;
    int layout;

    public adapter(Context context, List<HinhAnh> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {

        ImageView ivr;
        TextView tvr;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = new ViewHolder();

        if ( view == null) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layout, null);

            viewHolder.ivr = view.findViewById(R.id.ivr);
            viewHolder.tvr = view.findViewById(R.id.tvr);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Glide.with(context).load(list.get(i).getLink()).into(viewHolder.ivr);
        viewHolder.tvr.setText(list.get(i).getTen());

        return view;
    }
}
