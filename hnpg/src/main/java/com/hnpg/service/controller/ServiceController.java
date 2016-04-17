package com.hnpg.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hnpg.service.bean.ServiceResponse;
import com.hnpg.service.constants.Constants;
import com.hnpg.service.db.HnPGDataServices;
import com.mongodb.BasicDBObject;

@RestController
@RequestMapping("/service")
public class ServiceController {

	@Autowired
	private HnPGDataServices dataService;

	@RequestMapping("/ping.htm")
	public Object ping(@RequestParam String msg) {
		return "Hello ! " + msg;
	}

	@RequestMapping(value = "/location/insert.htm", method = RequestMethod.POST)
	public Object saveLocation(@RequestBody BasicDBObject object) {
		ServiceResponse response = new ServiceResponse();
		try {
			response.setMessage("Location Saved Successfully");
			response.setStatus(200);
			response.setData(dataService.save(object, Constants.Documents.LOCATION));
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(500);
		}
		return response;
	}

	@RequestMapping(value = "/location/update.htm", method = RequestMethod.POST)
	public Object updateLocation(@RequestBody BasicDBObject object) {
		ServiceResponse response = new ServiceResponse();
		try {
			response.setMessage("Location Updated Successfully");
			response.setStatus(200);
			response.setData(dataService.update(object, Constants.Documents.LOCATION));
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(500);
		}
		return response;
	}

	@RequestMapping("/location/findone.htm")
	public Object findOne(@RequestParam String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			response.setStatus(200);
			response.setData(dataService.fetchOne(id, Constants.Documents.LOCATION));
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(500);
		}
		return response;
	}

	@RequestMapping("/location/findall.htm")
	public Object findAll() {
		ServiceResponse response = new ServiceResponse();
		try {
			response.setStatus(200);
			response.setData(dataService.fetchAll(Constants.Documents.LOCATION));
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(500);
		}
		return response;
	}
}
