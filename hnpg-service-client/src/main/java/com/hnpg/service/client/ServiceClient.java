package com.hnpg.service.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({ "deprecation", "unchecked" })
public class ServiceClient {

	static final String BASE_URL = "http://54.86.143.9:8080/hnpg/service/";
	static final String FIND_ALL = "location/findall.htm";
	static final String FIND_ONE = "location/findone.htm";
	static final String INSERT = "location/insert.htm";
	static final String UPDATE = "location/update.htm";

	public static ServiceResponse getLocation(boolean all, String id) throws ClientProtocolException, IOException {
		String url = "";
		if (all) {
			url = BASE_URL + FIND_ALL;
		} else {
			url = BASE_URL + FIND_ONE + "?id=" + id;
		}

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(url);
		getRequest.addHeader("accept", "application/json");
		HttpResponse response = httpClient.execute(getRequest);
		if (response.getStatusLine().getStatusCode() != 200) {
			httpClient.close();
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		StringBuilder builder = new StringBuilder();
		String output = "";
		while ((output = br.readLine()) != null) {
			builder.append(output);
		}
		ObjectMapper mapper = new ObjectMapper();
		ServiceResponse sReponse = mapper.readValue(builder.toString().getBytes(), ServiceResponse.class);
		httpClient.getConnectionManager().shutdown();
		httpClient.close();

		return sReponse;
	}

	public static ServiceResponse updateLocation(LinkedHashMap<String, Object> data, boolean update)
			throws ClientProtocolException, IOException {
		String url = "";
		if (update) {
			url = BASE_URL + UPDATE;
		} else {
			url = BASE_URL + INSERT;
		}

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(data);

		HttpPost request = new HttpPost(url);
		StringEntity params = new StringEntity(json);
		request.addHeader("content-type", "application/json");
		request.setEntity(params);
		CloseableHttpResponse response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() != 200) {
			httpClient.close();
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		StringBuilder builder = new StringBuilder();
		String output = "";
		while ((output = br.readLine()) != null) {
			builder.append(output);
		}
		ServiceResponse sReponse = mapper.readValue(builder.toString().getBytes(), ServiceResponse.class);
		httpClient.close();
		httpClient.close();

		return sReponse;
	}

	public static void main(String[] args) {
		try {
//			ServiceResponse response = getLocation(false, "5713f7d5dbbc08c2dbaea56a");
//			LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) response.getData();
//			data.put("NewVal", "I M New Val");
//
//			updateLocation(data, true);

			LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("TYPE", "HOSTEL");
			updateLocation(data,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
