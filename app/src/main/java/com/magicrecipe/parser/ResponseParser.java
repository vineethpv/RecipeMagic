package com.magicrecipe.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.magicrecipe.model.Response;

public class ResponseParser {
	
	String responseString;
	
	public ResponseParser(String json){
		
		this.responseString = json;
		
	}
	
	public ArrayList<Response> parse(){
		ArrayList<Response> responseList = new ArrayList<Response>();
		
		try {
			
			JSONObject jsonObject = new JSONObject(responseString);
			JSONArray resultArray = jsonObject.getJSONArray("results");
			 
            // looping through response
            for (int i = 0; i < resultArray.length(); i++) {
            	Response response = new Response();
            	
                JSONObject obj = resultArray.getJSONObject(i);
                
                response.setTitle(stringFilter(obj.getString("title"))); //before setting title unwanted characters are removed..
                response.setUrl(obj.getString("href"));
                response.setIngredients(obj.getString("ingredients"));
                response.setThumburl(obj.getString("thumbnail"));
                
                responseList.add(response);
            }
                 
              			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return responseList;
	}
	
	// for removing \r\n\t and space characters
	private String stringFilter(String value){
		
		value = value.replaceAll("\r", "");
		value = value.replaceAll("\n", "");
		value = value.replaceAll("\t", "");
		
		return value.trim();
	}

}
