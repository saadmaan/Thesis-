package com.sachetan.controllers;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sachetan.domains.GenericResponse;
import com.sachetan.domains.IncidentType;
import com.sachetan.services.IncidentTypeService;

@RestController
@RequestMapping("/api/v1")
public class IncidentTypeController {
	@Autowired
	private IncidentTypeService mService;

	@RequestMapping(value = "/incident-type", method = RequestMethod.GET)
	public HttpEntity<?> getAllIncidentTypes() {
		return new ResponseEntity<Iterable<IncidentType>>(mService.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/incident-type", method = RequestMethod.POST)
	public HttpEntity<?> createIncidentType(@RequestBody IncidentType incidentType) {
		return new ResponseEntity<IncidentType>(mService.createIncidentType(incidentType), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/incident-type/{incidentTypeId}", method = RequestMethod.GET)
	public HttpEntity<?> getIncidentType(@PathVariable Long incidentTypeId) {
		return new ResponseEntity<IncidentType>(mService.findOne(incidentTypeId), HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/incident-type/{incidentTypeId}", method = RequestMethod.DELETE)
	public HttpEntity<?> deleteIncidentType(@PathVariable Long incidentTypeId) {
		mService.deleteIncidentType(incidentTypeId);
		return new ResponseEntity<GenericResponse>(new GenericResponse(1, "Incident Type Deleted"), HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/incident-type/{incidentTypeId}", method = RequestMethod.POST)
	public HttpEntity<?> updateIncidentType(@PathVariable Long incidentTypeId, @RequestBody IncidentType incidentType) {
		return new ResponseEntity<IncidentType>(mService.updateIncidenType(incidentTypeId, incidentType),
				HttpStatus.OK);
	}

}
