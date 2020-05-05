package com.sachetan.services;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sachetan.domains.IncidentType;
import com.sachetan.domains.Report;
import com.sachetan.exceptions.InvalidParameterException;
import com.sachetan.repositories.IncidentTypeRepository;
import com.sachetan.repositories.ReportRepository;
import com.sachetan.repositories.UserRepository;
import com.sachetan.util.MapPoint;

@Service
public class ReportService {
	@Autowired
	private ReportRepository mReportRepository;

	@Autowired
	private UserRepository mUserRepository;

	@Autowired
	IncidentTypeRepository mIncidentTypeRepository;

	public Iterable<Report> findAll(Long userId) {
		return mReportRepository.findByUserId(userId);
	}
	
	public Report findOne(Long reportId){
		return mReportRepository.findOne(reportId);
	}

	public Report createReport(Long userId, Report report) throws InvalidParameterException {
		System.out.println("Save report " + report);
		Long incidentTypeId = report.getIncidentTypeId();
		if (incidentTypeId == null) {
			throw new InvalidParameterException("Incident Type ID is missing!");
		}

		IncidentType reportedIncidentType = mIncidentTypeRepository.findOne(report.getIncidentTypeId());
		if (reportedIncidentType == null) {
			throw new InvalidParameterException("Invalid Incident Type Id");
		}

		MapPoint location = report.getLocation();
		if (location == null) {
			throw new InvalidParameterException("Location is required!");
		}

		Report model = new Report(mUserRepository.findOne(userId));
		model.setIncidentType(reportedIncidentType);

		String description = report.getDescription();
		if (description != null)
			model.setDescription(description);

		Integer severity = report.getSeverity();
		if (severity != null)
			model.setSeverity(severity);

		model.setLocation(location);

		return mReportRepository.save(model);
	}
	
	public void deleteReportById(Long userId, Long reportId) throws InvalidParameterException{
		Report existingReport = mReportRepository.findOne(reportId);
		if (existingReport == null){
			throw new InvalidParameterException("Invalid report id");
		}
		
		if (existingReport.getUser().getID() != userId){
			throw new InvalidParameterException("You do not have sufficient privileges to modify this report!");
		}
		
		mReportRepository.delete(reportId);
	}

	public Report updateReportById(Long userId, Long reportId, Report report) throws InvalidParameterException {
		Report existingReport = mReportRepository.findOne(reportId);
		if (existingReport == null){
			throw new InvalidParameterException("Invalid report id");
		}
		
		if (existingReport.getUser().getID() != userId){
			throw new InvalidParameterException("You do not have sufficient privileges to modify this report!");
		}

		MapPoint reportLocation = report.getLocation();
		if (reportLocation != null) {
			existingReport.setLocation(reportLocation);
		}

		Long incidentTypeId = report.getIncidentTypeId();
		if (incidentTypeId != null) {
			IncidentType reportedIncidentType = mIncidentTypeRepository.findOne(report.getIncidentTypeId());
			if (reportedIncidentType != null) {
				existingReport.setIncidentType(reportedIncidentType);
			}
		}

		String description = report.getDescription();
		if (description != null) {
			existingReport.setDescription(description);
		}

		int severity = report.getSeverity();
		if (existingReport.getSeverity() != severity) {
			existingReport.setSeverity(severity);
		}

		return mReportRepository.save(existingReport);
	}

}
