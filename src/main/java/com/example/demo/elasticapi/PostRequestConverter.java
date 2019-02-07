package com.example.demo.elasticapi;

import java.util.HashMap;
import java.util.Map;

import com.example.demo.restapi.Document;

public class PostRequestConverter {
	
	Document document;
	
	public PostRequestConverter() {}
	
	public PostRequestConverter source(Document document) {
		this.document = document;
		
		return this;
	}
	
	public Map<String, Object> convert() {
		
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		
		jsonMap.put("title", document.getTitle());
		jsonMap.put("content", document.getContent());
		
		return jsonMap;
	}

}
