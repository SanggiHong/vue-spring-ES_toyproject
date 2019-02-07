package com.example.demo.elasticapi;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;

import com.example.demo.restapi.PostResult;

public class PostResultConverter {
	
	IndexResponse response;
	
	public PostResultConverter() { }

	public PostResultConverter source(IndexResponse response) {
		this.response = response;
		
		return this;
	}
	
	public PostResult convert() {
		
		StringBuffer message = new StringBuffer();
		
		if (existFailedShard(response.getShardInfo())) {
			for (ReplicationResponse.ShardInfo.Failure failure :
				response.getShardInfo().getFailures())
				message.append('[' + failure.reason() + ']');
		}
		
		return new PostResult(
				getResultString(response.getResult()),
				message.toString(),
				response.getIndex(),
				response.getType(),
				response.getId());
	}
	
	private boolean existFailedShard(ReplicationResponse.ShardInfo shardInfo) {
		return 0 < shardInfo.getFailed();
	}
	
	private String getResultString(DocWriteResponse.Result result) {
		if (result == DocWriteResponse.Result.CREATED)
			return Result.STR_CREATED;
		else if (result == DocWriteResponse.Result.UPDATED)
			return Result.STR_UPDATED;
		
		return Result.STR_ILLEGAL;
	}
}
