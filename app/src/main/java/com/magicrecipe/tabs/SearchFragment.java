package com.magicrecipe.tabs;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.magicrecipe.adapter.CustomSearchAdapter;
import com.magicrecipe.constants.Constants;
import com.magicrecipe.database.RecipeDB;
import com.magicrecipe.model.Response;
import com.magicrecipe.parser.FetchDataAsync;
import com.magicrecipe.parser.OnResponseListener;
import com.magicrecipe.ui.WebviewActivity;
import com.puppy.magicrecipe.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements OnResponseListener {
    private FetchDataAsync dataAsync;
    private ListView listView;
    private CustomSearchAdapter searchAdapter;
    private ArrayList<Response> responseList = new ArrayList<Response>();
	private ImageView removeButton;
	private LinearLayout searchFieldLayout;
	private TextView toolBarTitle;
	private EditText userInput;
	private int mPosition = 1;


	public SearchFragment() {
        // Required empty public constructor
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
    public void onStart() {
        super.onStart();
		IntentFilter filter = new IntentFilter(Constants.TOOLBAR_UPDATE_NOTIFIER);
		filter.addAction(Constants.LIST_UPDATE_NOTIFIER);
		filter.addAction(Constants.LIST_SEARCH_UPDATE_NOTIFIER);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, filter);
		toolBarTitle = (TextView) getActivity().findViewById(R.id.toolbar_title);
		userInput = (EditText) getActivity().findViewById(R.id.search_keys);
		listView = (ListView) getActivity().findViewById(R.id.list);
		removeButton = (ImageView) getActivity().findViewById((R.id.remove));
		searchFieldLayout = (LinearLayout) getActivity().findViewById((R.id.search_field_layout));

		searchAdapter = new CustomSearchAdapter(getActivity(), responseList);
		listView.setAdapter(searchAdapter);

		userInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				// TODO Auto-generated method stub

				if (dataAsync != null) {
					dataAsync.cancel(true); // cancel already started thread to
					// avoid response overlapping
				}

				dataAsync = new FetchDataAsync();
				dataAsync.setDataAsyncListener(SearchFragment.this);
				String query = s.toString();
				query = query.replaceAll("\n", "").trim();

				if(query.length() > 0)
					removeButton.setVisibility(View.VISIBLE);
				else if(removeButton.getVisibility() == View.VISIBLE)
					removeButton.setVisibility(View.INVISIBLE);

				if (query.length() > 2) // remove space and set a threshold
					dataAsync.execute(query);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				// TODO Auto-generated method stub
				if (responseList != null) {
					Intent intent = new Intent(getActivity(),
							WebviewActivity.class);
					String url = responseList.get(position).getUrl();
					intent.putExtra("URL", url);
					startActivity(intent); // Starts a new activity to show
					// recipe details in a webview
					RecipeDB.getInstance(getActivity()).saveHistory(responseList.get(position));
				}

			}
		});

		removeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				userInput.setText("");
			}
		});

    }


	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
//		Log.d("taag", "------setUserVisibleHint : "+ isVisibleToUser);
//		if(this.isVisible()){
//			if(!isVisibleToUser){
//				searchFieldLayout.setVisibility(View.GONE);
//			}else{
//				searchButton.setVisibility(View.VISIBLE);
//			}
//		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(mPosition == 1) {
			toolBarTitle.setVisibility(View.GONE);
			animate(searchFieldLayout);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
	}

	private BroadcastReceiver onNotice = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if(Constants.TOOLBAR_UPDATE_NOTIFIER.equals(intent.getAction())) {
				int position = intent.getIntExtra(Constants.UPDATE_NOTIFIER_KEY, 0);
				mPosition = position;
				if (position == 1) {
					toolBarTitle.setVisibility(View.GONE);
					animate(searchFieldLayout);
				} else {
					searchFieldLayout.setVisibility(View.GONE);
					toolBarTitle.setVisibility(View.VISIBLE);
				}
			} else if(Constants.LIST_UPDATE_NOTIFIER.equals(intent.getAction())){
				String clearFlag = intent.getStringExtra(Constants.UPDATE_NOTIFIER_KEY);
				if(clearFlag != null && clearFlag.equals(Constants.MENU_CLEAR_FAVORITES)) {
					searchAdapter.clearFavorites();
					searchAdapter.notifyDataSetChanged();
				}
			} else if(Constants.LIST_SEARCH_UPDATE_NOTIFIER.equals(intent.getAction())){
				String clearFlag = intent.getStringExtra(Constants.UPDATE_NOTIFIER_KEY);
				if (clearFlag != null) {
					if (clearFlag.equals(Constants.FAVORITES_MARK)) {
						searchAdapter.updateFavorite(intent.getStringExtra(Constants.UPDATE_NOTIFIER_TITLE_KEY), true);
					} else if (clearFlag.equals(Constants.FAVORITES_UNMARK)) {
						searchAdapter.updateFavorite(intent.getStringExtra(Constants.UPDATE_NOTIFIER_TITLE_KEY), false);
					}
					searchAdapter.notifyDataSetChanged();
				}
			}

		}
	};

	@Override
    public void onResponse(ArrayList<Response> responseList) {
        // TODO Auto-generated method stub

        if (responseList.size() > 0) {

            this.responseList.clear();
			ArrayList<String> titles = RecipeDB.getInstance(getActivity()).getFavoritesTitle();
            for (Response response : responseList) {
				if(titles.contains(response.getTitle()))
					response.setFavorite(true);

                this.responseList.add(response);
            }
            searchAdapter.notifyDataSetChanged();
        } else {
            Toast toast = Toast.makeText(getActivity(),
                    "No results found", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

	private void animate(final View v) {
		v.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		v.setVisibility(View.VISIBLE);

		ScaleAnimation anim = new ScaleAnimation(1, 1, 0, 1);
		anim.setDuration(200);
		v.startAnimation(anim);
	}


}
