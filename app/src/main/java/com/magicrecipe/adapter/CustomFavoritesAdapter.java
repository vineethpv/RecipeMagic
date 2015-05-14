package com.magicrecipe.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magicrecipe.database.RecipeDB;
import com.magicrecipe.lazyloading.ImageLoader;
import com.magicrecipe.model.Response;
import com.puppy.magicrecipe.R;

import java.util.ArrayList;

/**
 * Created by Vineeth on 5/12/2015.
 */
public class CustomFavoritesAdapter extends BaseAdapter {
    private Context context;
    private  ArrayList<Response> responses;
    private LayoutInflater inflater;
    public ImageLoader imageLoader;

    public CustomFavoritesAdapter(Context context){
        this.context = context;
        imageLoader = new ImageLoader(context);
    }

    public void setData(ArrayList<Response> responses){
        this.responses = responses;
    }

    public static class ViewHolder
    {
        TextView title;
        ImageView thumbNail;
        ImageView favorite;
    }

    @Override
    public int getCount() {
        return responses.size();
    }

    @Override
    public Object getItem(int position) {
        return responses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return responses.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row, null);

            holder.title     = (TextView) convertView.findViewById(R.id.title);
            holder.thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.favorite  = (ImageView) convertView.findViewById(R.id.favorite);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        imageLoader.DisplayImage(responses.get(position).getThumburl(), holder.thumbNail);   //lazy loading

        holder.title.setText(responses.get(position).getTitle());

        holder.favorite.setVisibility(View.GONE);

        return convertView;
    }
}
