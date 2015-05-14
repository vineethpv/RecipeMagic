package com.magicrecipe.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magicrecipe.constants.Constants;
import com.magicrecipe.database.RecipeDB;
import com.magicrecipe.lazyloading.ImageLoader;
import com.magicrecipe.model.Response;
import com.puppy.magicrecipe.R;

public class CustomSearchAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Response> responses;
	public ImageLoader imageLoader;
	private long rowId;

	public CustomSearchAdapter(Context context, ArrayList<Response> responses) {
		this.context = context;
		this.responses = responses;
		imageLoader = new ImageLoader(context);
	}

	public void clearFavorites() {
		for (Response response : responses) {
			response.setFavorite(false);
		}
	}

	public void updateFavorite(String title, boolean mark) {
		for (Response response : responses) {
			if (response.getTitle().equals(title)) {
				response.setFavorite(mark);
			}
		}
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
	public Object getItem(int location) {
		return responses.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
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

		if (responses.get(position).isFavorite()) {
			holder.favorite.setImageResource(R.drawable.ic_action_important);
		} else {
			holder.favorite.setImageResource(R.drawable.ic_action_not_important);
		}

		holder.favorite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Constants.LIST_HISTORY_UPDATE_NOTIFIER);
				RecipeDB recipeDB = RecipeDB.getInstance(context);
				if(holder.favorite.getTag().equals(context.getResources().getString(R.string.tag_not_favorite))) {
					holder.favorite.setImageResource(R.drawable.ic_action_important);
					holder.favorite.setTag(context.getResources().getString(R.string.tag_favorite));
					rowId = recipeDB.saveFavorites(responses.get(position));
					responses.get(position).setId(rowId);
					responses.get(position).setFavorite(true);
					intent.putExtra(Constants.UPDATE_NOTIFIER_KEY, Constants.FAVORITES_MARK);
					intent.putExtra(Constants.UPDATE_NOTIFIER_TITLE_KEY, responses.get(position).getTitle());
					LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				} else {
					holder.favorite.setImageResource(R.drawable.ic_action_not_important);
					holder.favorite.setTag(context.getResources().getString(R.string.tag_not_favorite));
					recipeDB.deleteFavorite(responses.get(position).getTitle());
					responses.get(position).setFavorite(false);
					intent.putExtra(Constants.UPDATE_NOTIFIER_KEY, Constants.FAVORITES_UNMARK);
					intent.putExtra(Constants.UPDATE_NOTIFIER_TITLE_KEY, responses.get(position).getTitle());
					LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				}
				notifyDataSetChanged();
			}
		});


		return convertView;
	}

}