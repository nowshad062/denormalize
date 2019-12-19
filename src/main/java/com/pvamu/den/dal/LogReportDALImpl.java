package com.pvamu.den.dal;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObjectBuilder;
import com.pvamu.den.model.LogReport;
import com.pvamu.den.model.User;


@Repository
public class LogReportDALImpl {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	public void addLog(LogReport logReport) {
		mongoTemplate.save(logReport);
	}
	
	public long collectionCount() {
		Query query = new Query();
		query.getLimit();
		return mongoTemplate.count(query, LogReport.class);
	}
	
	public void clearAllRecord() {
		mongoTemplate.dropCollection(LogReport.class);
	}
	
	public List<LogReport> getAllRecord() {
		
		return mongoTemplate.findAll(LogReport.class);
	}
	/*
	 * 
	 * In Mongodb, db.getCollection('log_de_anonymization').find({"timeStamp": { $gte: new Date("2018-12-10T00:00:00"), $lte: new Date("2018-12-30T23:59:59")}})
	 * https://www.saturnringstation.com/2018/05/15/spring-data-mongodb-custom-location-date-query-make-easy-with-mongotemplate/
	 */
	public List<LogReport> filterLogsByDates(String begin, String end){
		// format begin and end and convert them to Date type
		
		//System.out.println("begin:"+begin);
		//System.out.println("end:"+end);
		String bMonth = begin.substring(0, begin.indexOf("-"));
		if(bMonth.length()==1)
			bMonth = "0"+bMonth;
		String remainafterMonth = begin.substring(begin.indexOf("-")+1);
		String bDate = remainafterMonth.substring(0, remainafterMonth.indexOf("-"));
		if(bDate.length()==1)
			bDate = "0"+bDate;
		String bYear = remainafterMonth.substring(remainafterMonth.indexOf("-")+1);
		if(bYear.length()!=4)
			for(int i = 1; i<= 4-bYear.length(); i++)
				bYear = "0"+bYear;
		String beginstr = bYear+"-"+bMonth+"-"+bDate;
		
		String bMontha = end.substring(0, end.indexOf("-"));
		if(bMontha.length()==1)
			bMontha = "0"+bMontha;
		String remainafterMontha = end.substring(end.indexOf("-")+1);
		String bDatea = remainafterMontha.substring(0, remainafterMontha.indexOf("-"));
		if(bDatea.length()==1)
			bDatea = "0"+bDatea;
		String bYeara = remainafterMontha.substring(remainafterMontha.indexOf("-")+1);
		if(bYeara.length()!=4)
			for(int i = 1; i<= 4-bYeara.length(); i++)
				bYeara = "0"+bYeara;
		String endstr = bYeara+"-"+bMontha+"-"+bDatea;

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		Query query = new Query();
		query.addCriteria(Criteria.where("timeStamp").gte(LocalDate.parse(beginstr, dtf).atStartOfDay()).lt(LocalDate.parse(endstr, dtf).plusDays(1).atStartOfDay()));
		List<LogReport> logs =mongoTemplate.find(query, LogReport.class);
		//System.out.println(logs.size());
		return logs;

	}
}
