package com.sachetan.services;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sachetan.domains.IncidentType;
import com.sachetan.repositories.IncidentTypeRepository;

@Service
public class IncidentTypeService {
	@Autowired
	private IncidentTypeRepository mRepository;

	public Iterable<IncidentType> findAll() {
		return mRepository.findAll();
	}

	public IncidentType findOne(Long id) {
		return mRepository.findOne(id);
	}

	public void deleteIncidentType(Long id) {
		mRepository.delete(id);
	}

	public IncidentType createIncidentType(IncidentType incidentType) {
		return mRepository.save(incidentType);
	}

	public IncidentType updateIncidenType(Long id, IncidentType incidentType) {
		IncidentType originalIncidentType = mRepository.findOne(id);
		if (originalIncidentType == null)
			return null;
		String incidentDescription = incidentType.getDescription();
		String incidentName = incidentType.getName();
		if (incidentDescription != null) {
			originalIncidentType.setDescription(incidentDescription);
		}
		if (incidentName != null) {
			originalIncidentType.setName(incidentName);
		}

		return mRepository.save(originalIncidentType);
	}
}
