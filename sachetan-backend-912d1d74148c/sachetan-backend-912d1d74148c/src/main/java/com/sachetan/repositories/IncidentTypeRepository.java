package com.sachetan.repositories;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import org.springframework.data.repository.CrudRepository;

import com.sachetan.domains.IncidentType;

public interface IncidentTypeRepository extends CrudRepository<IncidentType, Long> {

}
