package com.magicrecipe.parser;

import java.util.ArrayList;

import android.os.AsyncTask;

import com.magicrecipe.model.Response;

public class FetchDataAsync extends AsyncTask<String, Void, ArrayList<Response>> {

	public static final String URL = "http://www.recipepuppy.com/api/";
	
	OnResponseListener responseListener;
	
	public void setDataAsyncListener(OnResponseListener listener){
		
		this.responseListener = listener;
		
	}

	@Override
	protected ArrayList<Response> doInBackground(String...ingredients) {
		// TODO Auto-generated method stub

	
		 RestClient client = new RestClient(URL);
		 client.AddParam("i", ingredients[0]);
		

		 try {
		     client.Execute(RestClient.GET);
		 } catch (Exception e) {
		     e.printStackTrace();
		 }

		 ResponseParser responseParser = new ResponseParser(client.getResponse());
		
		return responseParser.parse();
	}

	@Override
	protected void onPostExecute(ArrayList<Response> responseList) {
		// TODO Auto-generated method stub
		super.onPostExecute(responseList);
		
		if(responseListener != null){
			responseListener.onResponse(responseList); //Returns the result to the Activity
		}
	}
	
	
}
