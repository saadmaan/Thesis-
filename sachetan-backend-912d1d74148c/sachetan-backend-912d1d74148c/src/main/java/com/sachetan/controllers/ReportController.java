package com.sachetan.controllers;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sachetan.domains.GenericResponse;
import com.sachetan.domains.Report;
import com.sachetan.domains.User;
import com.sachetan.exceptions.InvalidParameterException;
import com.sachetan.services.ReportService;

@RestController
@RequestMapping("/api/v1")
public class ReportController {
	@Autowired
	private ReportService mReportService;

	@RequestMapping(value = "/me/incident", method = RequestMethod.GET)
	public HttpEntity<?> getReports(@AuthenticationPrincipal User authenticatedUser) {
		return new ResponseEntity<Iterable<Report>>(mReportService.findAll(authenticatedUser.getID()), HttpStatus.OK);
	}

	@RequestMapping(value = "/me/incident", method = RequestMethod.POST)
	public HttpEntity<?> createReport(@AuthenticationPrincipal User authenticatedUser, @RequestBody Report report) {
		try {
			return new ResponseEntity<Report>(mReportService.createReport(authenticatedUser.getID(), report),
					HttpStatus.OK);
		} catch (InvalidParameterException e) {
			return new ResponseEntity<GenericResponse>(new GenericResponse(0, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/me/incident/{incidentId}", method = { RequestMethod.PUT, RequestMethod.POST })
	public HttpEntity<?> updateIncidentByIncidentId(@AuthenticationPrincipal User authenticatedUser,
			@PathVariable Long incidentId, @RequestBody Report report) {
		try {
			return new ResponseEntity<Report>(
					mReportService.updateReportById(authenticatedUser.getID(), incidentId, report), HttpStatus.OK);
		} catch (InvalidParameterException e) {
			return new ResponseEntity<GenericResponse>(new GenericResponse(0, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/me/incident/{incidentId}", method = RequestMethod.DELETE)
	public HttpEntity<?> deleteReportByIncidentId(@AuthenticationPrincipal User authenticatedUser,
			@PathVariable Long incidentId) {
		try {
			mReportService.deleteReportById(authenticatedUser.getID(), incidentId);
			return new ResponseEntity<GenericResponse>(new GenericResponse(1, "Report deleted!"), HttpStatus.OK);
		} catch (InvalidParameterException e) {
			return new ResponseEntity<GenericResponse>(new GenericResponse(0, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/incident/within-bounds", method = RequestMethod.GET)
	public HttpEntity<?> getReportsWithinBounds(@RequestParam Float boundsFromLat, @RequestParam Float boundsFromLon,
			@RequestParam Float boundsToLat, @RequestParam Float boundToLon, @RequestParam Integer[] filters) {
		return new ResponseEntity<String>("Query isn't working. Update codes!", HttpStatus.OK);
	}

	@RequestMapping(value = "/incident/from-center", method = RequestMethod.GET)
	public HttpEntity<?> getReportsFromCenter(@RequestParam Float lat, @RequestParam Float lon,
			@RequestParam Float distance, @RequestParam Integer[] filters) {
		return new ResponseEntity<String>("Query isn't working. Update codes!", HttpStatus.OK);
	}

	@RequestMapping(value = "/incident/{incidentId}", method = RequestMethod.GET)
	public HttpEntity<?> getReportByIncidentId(@PathVariable Long incidentId) {
		return new ResponseEntity<Report>(mReportService.findOne(incidentId), HttpStatus.OK);
	}
}
