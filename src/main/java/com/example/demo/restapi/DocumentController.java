package com.example.demo.restapi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.elasticapi.ElasticApi;
import com.example.demo.elasticapi.PostResult;
import com.example.demo.elasticapi.SearchResult;

@RestController
public class DocumentController {
	
	private final String ELASTIC_INDEX = "tuto-index";
	private final String ELASTIC_TYPE = "tuto-type";
	
	@Autowired
	ElasticApi elasticApi;
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public SearchResult search(@RequestParam(value="keyword", defaultValue="") String keyword) {
		return elasticApi.searchElasticApi(
				ELASTIC_INDEX,
				keyword.toLowerCase(),
				(h) -> { 
					Map<String, Object> source = h.getSourceAsMap();
					source.put("id", h.getId());
					return source;
					}
				);
	}
	
	@RequestMapping(value = "/post", method = RequestMethod.POST)
	public PostResult post(@RequestBody Document document) {
		
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("title", document.getTitle());
		jsonMap.put("content", document.getContent());
		
		return elasticApi.postElasticApi(ELASTIC_INDEX, ELASTIC_TYPE, jsonMap);
	}
}
