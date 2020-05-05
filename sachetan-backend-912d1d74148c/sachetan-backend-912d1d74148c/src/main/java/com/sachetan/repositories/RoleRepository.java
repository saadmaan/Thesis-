package com.sachetan.repositories;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sachetan.domains.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	List<Role> findByName(String name);
}
