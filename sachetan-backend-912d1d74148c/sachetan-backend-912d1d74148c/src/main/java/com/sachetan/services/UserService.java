package com.sachetan.services;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sachetan.domains.Report;
import com.sachetan.domains.Role;
import com.sachetan.domains.User;
import com.sachetan.repositories.RoleRepository;
import com.sachetan.repositories.UserRepository;
import com.sachetan.util.MapPoint;

@Service
public class UserService {
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_USER = "ROLE_USER";

	@Autowired
	private UserRepository mUserRepository;

	@Autowired
	private RoleRepository mRoleRepository;

	public Iterable<User> findAll() {
		return mUserRepository.findAll();
	}

	private User updateUserFields(User newUser, User oldUser) {
		MapPoint location = oldUser.getLastKnownLocation();
		if (location != null)
			newUser.setLastKnownLocation(location);

		String firstName = oldUser.getFirstName();
		if (firstName != null)
			newUser.setFirstName(firstName);

		String lastName = oldUser.getLastName();
		if (lastName != null) {
			newUser.setLastName(lastName);
		}

		String phone = oldUser.getPhone();
		if (phone != null) {
			newUser.setPhone(phone);
		}

		newUser.setRoles(oldUser.getRoles());

		return newUser;
	}

	public User createUser(User user) {
		User model = new User(user.getUsername(), user.getPassword(), user.getEmail());

		user.setRoles(new HashSet<Role>(mRoleRepository.findByName(ROLE_USER)));

		return mUserRepository.save(updateUserFields(model, user));
	}

	public User getUserById(Long userId) {
		return mUserRepository.findOne(userId);
	}

	public User updateUserById(Long userId, User user) {
		User model = mUserRepository.findOne(userId);
		if (model == null)
			return null;

		return mUserRepository.save(updateUserFields(model, user));
	}

	public void deleteUser(Long userId) {
		mUserRepository.delete(userId);
	}

	public Set<Report> getReportById(Long reportId) {
		return mUserRepository.findByReportsId(reportId);
	}
}
