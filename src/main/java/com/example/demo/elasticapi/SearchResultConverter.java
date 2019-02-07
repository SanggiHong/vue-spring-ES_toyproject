package com.example.demo.elasticapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;

import com.example.demo.restapi.SearchResult;

public class SearchResultConverter {
	
	SearchResponse response;
	
	public SearchResultConverter() { }
	
	public SearchResultConverter source(SearchResponse response) {
		this.response = response;
		
		return this;
	}
	
	public SearchResult convert() {
		
		ArrayList<Map<String, Object>> documents = new ArrayList<>();
		
		for (SearchHit hit : response.getHits()) {
			Map<String, Object> source = hit.getSourceAsMap();
			source.put("id", hit.getId());
			
			documents.add(source);
		}
		
		RestStatus status = response.status();
		
		return new SearchResult(status.getStatus(), documents);
	}

}
