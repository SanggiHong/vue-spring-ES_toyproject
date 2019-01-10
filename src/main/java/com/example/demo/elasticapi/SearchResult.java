package com.example.demo.elasticapi;

import java.util.List;
import java.util.Map;

public class SearchResult {
	
	final int status;
	final List<Map<String, Object>> documents;

	public SearchResult(int status, List<Map<String, Object>> documents) {
		super();
		this.status = status;
		this.documents = documents;
	}
	
	public int getStatus() {
		return status;
	}

	public List<Map<String, Object>> getDocuments() {
		return documents;
	}
}
