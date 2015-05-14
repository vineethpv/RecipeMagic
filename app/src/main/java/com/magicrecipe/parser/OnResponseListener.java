package com.magicrecipe.parser;

import java.util.ArrayList;

import com.magicrecipe.model.Response;

/*
 * Interface for webservice response callback
 */
public interface OnResponseListener {

	public void onResponse(ArrayList<Response> response);
}