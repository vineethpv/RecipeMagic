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
import android.widget.TextView;

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
public class HistoryFragment extends Fragment {
    private CustomHistoryAdapter historyAdapter;
    private ListView listView;
    private ArrayList<Response> responseList;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        listView = (ListView) getActivity().findViewById(R.id.list_history);
        responseList = RecipeDB.getInstance(getActivity()).getHistory();
        ArrayList<String> titles = RecipeDB.getInstance(getActivity()).getFavoritesTitle();
        for (Response response : responseList) {
            if(titles.contains(response.getTitle()))
                response.setFavorite(true);
        }
        historyAdapter = new CustomHistoryAdapter(getActivity());
        historyAdapter.setData(responseList);
        listView.setAdapter(historyAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                String url = responseList.get(position).getUrl();
                intent.putExtra("URL", url);
                startActivity(intent);
            }
        });

        IntentFilter filter = new IntentFilter(Constants.LIST_HISTORY_UPDATE_NOTIFIER);
        filter.addAction(Constants.LIST_UPDATE_NOTIFIER);
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
            if (Constants.LIST_UPDATE_NOTIFIER.equals(intent.getAction())) {
                String clearFlag = intent.getStringExtra(Constants.UPDATE_NOTIFIER_KEY);
                if (clearFlag != null) {
                    if (clearFlag.equals(Constants.MENU_CLEAR_FAVORITES)) {
                        historyAdapter.clearFavorites();
                    } else if (clearFlag.equals(Constants.MENU_CLEAR_HISTORY)) {
                        historyAdapter.setData(RecipeDB.getInstance(getActivity()).getHistory());
                    }
                }
            } else if (Constants.LIST_HISTORY_UPDATE_NOTIFIER.equals(intent.getAction())) {
                String clearFlag = intent.getStringExtra(Constants.UPDATE_NOTIFIER_KEY);
                if (clearFlag != null) {
                    if (clearFlag.equals(Constants.FAVORITES_MARK)) {
                        historyAdapter.updateFavorite(intent.getStringExtra(Constants.UPDATE_NOTIFIER_TITLE_KEY), true);
                    } else if (clearFlag.equals(Constants.FAVORITES_UNMARK)) {
                        historyAdapter.updateFavorite(intent.getStringExtra(Constants.UPDATE_NOTIFIER_TITLE_KEY), false);
                    }
                }
            }
            historyAdapter.notifyDataSetChanged();
        }
    };
}
