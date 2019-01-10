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


@Component
public class ElasticApi {
	
	@Value("${elasticsearch.host}")
	private String host;
	
	@Value("${elasticsearch.port}")
	private int port;
	
	private final String STR_CREATED = "CREATED";
	private final String STR_UPDATED = "UPDATED";
	private final String STR_IOEXCEPT = "IO_EXCEPTION";
	private final String STR_ILLEGAL = "ILLEGAL_RESULT";
	
	@FunctionalInterface
	public interface SearchResultBuilder {
		public Map<String, Object> build(SearchHit hit);
	}
	
	public SearchResult searchElasticApi(String index, String keyword, SearchResultBuilder builder) {
		
		RestHighLevelClient client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost("localhost", 9200, "http"),
		                new HttpHost("localhost", 9201, "http")
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
		
			RestStatus status = searchResponse.status();
			ArrayList<Map<String, Object>> documents = new ArrayList<>();
			
			for (SearchHit hit : searchResponse.getHits())
				documents.add(builder.build(hit));
			
			result = new SearchResult(status.getStatus(), documents);
			client.close();
		} catch (IOException e) {
			result = new SearchResult(417, null);
		}
		
		return result;
	}
	
	public PostResult postElasticApi(String index, String type, Map<String, Object> jsonMap) {
		
		RestHighLevelClient client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost("localhost", 9200, "http"),
		                new HttpHost("localhost", 9201, "http")
		                )
		        );
		
		PostResult result;
		try {
			IndexRequest indexRequest = new IndexRequest(index, type)
											.source(jsonMap);
			IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
			
			StringBuffer message = new StringBuffer();
			if (existFailedShard(indexResponse.getShardInfo())) {
				for (ReplicationResponse.ShardInfo.Failure failure :
					indexResponse.getShardInfo().getFailures())
					message.append('[' + failure.reason() + ']');
			}
			
			result = new PostResult(
					getResultString(indexResponse.getResult()),
					message.toString(),
					indexResponse.getIndex(),
					indexResponse.getType(),
					indexResponse.getId());
			
			client.close();
		} catch (IOException e) {
			result = new PostResult(STR_IOEXCEPT, e.toString(), "", "", "");
		}
		
		
		return result;
	}
	
	private boolean existFailedShard(ReplicationResponse.ShardInfo shardInfo) {
		return 0 < shardInfo.getFailed();
	}
	
	private String getResultString(DocWriteResponse.Result result) {
		if (result == DocWriteResponse.Result.CREATED)
			return STR_CREATED;
		else if (result == DocWriteResponse.Result.UPDATED)
			return STR_UPDATED;
		
		return STR_ILLEGAL;
	}
}
