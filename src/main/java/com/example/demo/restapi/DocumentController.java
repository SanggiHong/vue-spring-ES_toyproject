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

@RestController
public class DocumentController {
	
	private final String INDEX = "tuto-index";

	@Autowired
	ElasticApi elasticApi;
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public SearchResult search(@RequestParam(value="keyword", defaultValue="") String keyword) {
		
		IndexingEngine engine = elasticApi;
		
		return engine.search(INDEX, keyword.toLowerCase());
	}
	
	@RequestMapping(value = "/post", method = RequestMethod.POST)
	public PostResult post(@RequestBody Document document) {
		
		IndexingEngine engine = elasticApi;
		
		return engine.post(INDEX, document);
	}
}
