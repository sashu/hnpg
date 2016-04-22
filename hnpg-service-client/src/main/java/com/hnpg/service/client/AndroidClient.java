package com.hnpg.service.client;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class AndroidClient {
	
	
	public static void main(String[] args) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("asdasd", "asdasdas");
		final String url = "http://54.86.143.9:8080/hnpg/service/location/insert.htm";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpEntity<?> entity = new HttpEntity<>(data);
        ResponseEntity greeting = restTemplate.postForEntity(url,entity,ServiceResponse.class);
        System.out.println(greeting);
	}
}
