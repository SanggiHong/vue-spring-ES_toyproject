package com.example.demo.elasticapi;

public class PostResult {
	
	private final String result;
	private final String message;
	private final String index;
	private final String type;
	private final String id;
	
	public PostResult(String result, String message, String index, String type, String id) {
		this.result = result;
		this.message = message;
		this.index = index;
		this.type = type;
		this.id = id;
	}
	
	public String getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}

	public String getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}
}
