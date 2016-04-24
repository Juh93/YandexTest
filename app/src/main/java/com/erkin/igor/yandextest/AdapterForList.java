package com.erkin.igor.yandextest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class AdapterForList extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Artist> objects;

    AdapterForList(Context context, ArrayList<Artist> artists) {
        ctx = context;
        objects = artists;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int pos) {
        return objects.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        View view = convertView;
        if (view==null) {
            view = lInflater.inflate(R.layout.item, viewGroup, false);
            holder = new ViewHolder();
            holder.tvName = (TextView)view.findViewById(R.id.tvName);
            holder.tvGenre = (TextView)view.findViewById(R.id.tvGenre);
            holder.tvTracks = (TextView)view.findViewById(R.id.tvTracks);
            holder.ivCover = (ImageView)view.findViewById(R.id.ivCover);
            view.setTag(holder);
        } else holder = (ViewHolder)view.getTag();

        Artist artist = objects.get(pos);
        holder.tvName.setText(artist.name);
        holder.tvGenre.setText(artist.genre);
        holder.tvTracks.setText(artist.tracks);
        Glide.with(ctx)
                .load(artist.urlImage)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .crossFade(200)
                .into(holder.ivCover);
        return view;
    }

    //используем ссылки на view чтобы каждый раз не использовать findViewById
    public class ViewHolder{
        TextView tvName;
        TextView tvGenre;
        TextView tvTracks;
        ImageView ivCover;
    }

}
