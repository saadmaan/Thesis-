package com.sachetan.repositories;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.sachetan.domains.Report;
import com.sachetan.domains.User;

public interface UserRepository extends CrudRepository<User, Long> {
	List<User> findByEmail(String email);

	Set<Report> findByReportsId(Long reportId);

	User findByUsername(String username);
}
