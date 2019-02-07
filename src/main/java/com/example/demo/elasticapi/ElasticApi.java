package com.example.demo.elasticapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.demo.restapi.Document;
import com.example.demo.restapi.IndexingEngine;
import com.example.demo.restapi.PostResult;
import com.example.demo.restapi.SearchResult;


@Component
public class ElasticApi implements IndexingEngine {
	
	@Value("${elasticsearch.host}")
	private String host;
	
	@Value("${elasticsearch.port}")
	private int port;
	
	@Value("${elasticsearch.port_node1}")
	private int port_node1;
	
	@Value("${elasticsearch.type}")
	private String type;
	
	@Value ("${elasticsearch.protocol}")
	private String protocol;
	
	public SearchResult search(String index, String keyword) {
		
		RestHighLevelClient client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost("localhost", port, protocol),
		                new HttpHost("localhost", port_node1, protocol)
		                )
		        );
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.termQuery("content", keyword));
		sourceBuilder.from(0);
		sourceBuilder.size(10);
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.indicesOptions(IndicesOptions.lenientExpandOpen());
		searchRequest.source(sourceBuilder);
		
		SearchResult result;
		try {
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			
			result = new SearchResultConverter()
										.source(searchResponse)
										.convert();
			client.close();
		} catch (IOException e) {
			result = new SearchResult(417, null);
		}
		
		return result;
	}
	
	public PostResult post(String index, Document document) {
		
		RestHighLevelClient client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost("localhost", port, protocol),
		                new HttpHost("localhost", port_node1, protocol)
		                )
		        );
		
		IndexRequest indexRequest = new IndexRequest(index, type)
				.source(new PostRequestConverter()
						.source(document)
						.convert());
		
		PostResult result;
		try {
			IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
			
			result = new PostResultConverter()
								.source(indexResponse)
								.convert();
			
			client.close();
		} catch (IOException e) {
			result = new PostResult(Result.STR_IOEXCEPT, e.toString(), "", "", "");
		}
		
		
		return result;
	}
}
