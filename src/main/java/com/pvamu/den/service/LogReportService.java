package com.pvamu.den.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvamu.den.dal.LogReportDALImpl;
import com.pvamu.den.dal.OverLpaUserDALImpl;
import com.pvamu.den.model.LogReport;

@Service
public class LogReportService {
	
	@Autowired
	private  LogReportDALImpl logReportDALImpl; 
	
	@Autowired
	private  OverLpaUserDALImpl overLpaUserDALImpl;

	public List<LogReport> getLogReport() {
		return logReportDALImpl.getAllRecord();
	}
	
	public List<LogReport> filterLogsByDates(String begin, String end){
		
		return logReportDALImpl.filterLogsByDates(begin, end);
	}
	
	public long getoverlapcount() {
		return (overLpaUserDALImpl.collectionCount());
	}


}
