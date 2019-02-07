package com.example.demo.restapi;

public interface IndexingEngine {
	public SearchResult search(String index, String keyword);
	public PostResult post(String index, Document document);
}
