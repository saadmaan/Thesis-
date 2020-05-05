package com.sachetan.controllers;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sachetan.domains.Report;
import com.sachetan.domains.User;
import com.sachetan.services.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
	@Autowired
	private UserService mUserService;

	@RequestMapping(method = RequestMethod.POST)
	public HttpEntity<?> createUser(@RequestBody User user) {
		return new ResponseEntity<User>(mUserService.createUser(user), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/me", method = RequestMethod.GET)
	public HttpEntity<?> getUserById(@AuthenticationPrincipal User user) {
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/me/report", method = RequestMethod.GET)
	public HttpEntity<?> getUserReports(@AuthenticationPrincipal User user) {
		return new ResponseEntity<Set<Report>>(user.getReports(), HttpStatus.OK);
	}

	@RequestMapping(value = "/me/report/{reportId}", method = RequestMethod.GET)
	public HttpEntity<?> getUserReportById(@AuthenticationPrincipal User user, @PathVariable Long reportId) {
		return new ResponseEntity<Set<Report>>(mUserService.getReportById(reportId), HttpStatus.OK);
	}

	@RequestMapping(value = "/me", method = { RequestMethod.PUT, RequestMethod.POST })
	public HttpEntity<?> updateUser(@AuthenticationPrincipal User authenticatedUser, @RequestBody User user) {
		return new ResponseEntity<User>(mUserService.updateUserById(authenticatedUser.getID(), user), HttpStatus.OK);
	}

	@RequestMapping(value = "/me/location", method = { RequestMethod.PUT, RequestMethod.POST })
	public HttpEntity<?> setLocation(@AuthenticationPrincipal User authenticatedUser) {
		return null;
	}
}
