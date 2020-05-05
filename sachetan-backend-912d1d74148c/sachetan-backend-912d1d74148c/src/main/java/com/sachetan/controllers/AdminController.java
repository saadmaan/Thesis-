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
import com.sachetan.domains.User;
import com.sachetan.services.UserService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
	@Autowired
	private UserService mUserService;

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public HttpEntity<Iterable<User>> findAll() {
		return new ResponseEntity<Iterable<User>>(mUserService.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public User findOne(@PathVariable long id) {
		return mUserService.getUserById(id);
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	public HttpEntity<?> deleteUser(@PathVariable long id) {
		mUserService.deleteUser(id);
		return new ResponseEntity<GenericResponse>(new GenericResponse(1, "User deleted!"), HttpStatus.OK);
	}

	@RequestMapping(value = "/users/{id}", method = { RequestMethod.PUT, RequestMethod.POST })
	public User updateUser(@PathVariable long id, @RequestBody User user) {
		return mUserService.updateUserById(id, user);
	}
}
