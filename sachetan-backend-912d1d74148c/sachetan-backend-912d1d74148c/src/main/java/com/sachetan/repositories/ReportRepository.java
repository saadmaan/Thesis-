package com.sachetan.repositories;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

import com.sachetan.domains.Report;

public interface ReportRepository extends CrudRepository<Report, Long> {
	Collection<Report> findByUserId(Long id);
}
