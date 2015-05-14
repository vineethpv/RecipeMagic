package com.magicrecipe.tabs;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.magicrecipe.adapter.CustomFavoritesAdapter;
import com.magicrecipe.adapter.CustomHistoryAdapter;
import com.magicrecipe.constants.Constants;
import com.magicrecipe.database.RecipeDB;
import com.magicrecipe.model.Response;
import com.magicrecipe.ui.WebviewActivity;
import com.puppy.magicrecipe.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {
    private CustomFavoritesAdapter favoritesAdapter;
    private ListView listView;
    private ArrayList<Response> responseList;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        listView = (ListView) getActivity().findViewById(R.id.list_favorites);
        responseList = RecipeDB.getInstance(getActivity()).getFavorites();
        favoritesAdapter = new CustomFavoritesAdapter(getActivity());
        favoritesAdapter.setData(responseList);
        listView.setAdapter(favoritesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                String url = responseList.get(position).getUrl();
                intent.putExtra("URL", url);
                startActivity(intent);
                RecipeDB.getInstance(getActivity()).saveHistory(responseList.get(position));
            }
        });

        IntentFilter filter = new IntentFilter(Constants.LIST_UPDATE_NOTIFIER);
        filter.addAction(Constants.LIST_HISTORY_UPDATE_NOTIFIER);
        filter.addAction(Constants.LIST_SEARCH_UPDATE_NOTIFIER);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, filter);

    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            responseList = RecipeDB.getInstance(getActivity()).getFavorites();
            favoritesAdapter.setData(responseList);
            favoritesAdapter.notifyDataSetChanged();
        }
    };
}
