package com.pvamu.den.controller;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pvamu.den.dal.UserDALImpl;
import com.pvamu.den.filehandle.ReadUserProfile;
import com.pvamu.den.filehandle.ReadUserRelation;
import com.pvamu.den.model.Anonymization;
import com.pvamu.den.model.AuxUser;
import com.pvamu.den.model.DeAnonymizationResponse;
import com.pvamu.den.model.Elements;
import com.pvamu.den.model.LogReport;
import com.pvamu.den.model.ResponseBody;
import com.pvamu.den.model.TargetedUsers;
import com.pvamu.den.service.Auxiliary;
import com.pvamu.den.service.DenormalationNodes;
import com.pvamu.den.service.LogReportService;
import com.pvamu.den.service.Target;
import com.pvamu.den.service.UserAxis;
@SuppressWarnings("rawtypes")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/")
public class UserController {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private final UserDALImpl userDAL;
	
	private final ReadUserProfile readFile;
	
	private final ReadUserRelation readUserRelation;
	
	private final Target target;
	
	private final Auxiliary auxiliary;	
	
	private final UserAxis userAxis;
	
	private final DenormalationNodes ele ;
	
	private final LogReportService logReportService ;

	public UserController( UserDALImpl userDAL , ReadUserProfile readFile , ReadUserRelation readUserRelation , Target target , Auxiliary auxiliary , UserAxis userAxis , DenormalationNodes ele , LogReportService logReportService) {
		this.userDAL = userDAL;
		this.readFile = readFile;
		this.readUserRelation = readUserRelation;
		this.target = target;
		this.auxiliary = auxiliary;
		this.userAxis = userAxis;
		this.ele = ele;
		this.logReportService = logReportService;
	}
	
	@RequestMapping(value = "/sourceUpload", method = RequestMethod.GET)
	public ResponseEntity addNewUsers() {
		LOG.info("Saving user.");
		readFile.userProfileRead();
		readUserRelation.userRelationRead();
		ResponseEntity resp = new ResponseEntity(new ResponseBody() , HttpStatus.OK);
		return resp;
	}	
	
	@RequestMapping(value = "/target/{numNodes}", method = RequestMethod.GET)
	public void setTarget(@PathVariable int numNodes) {
		LOG.info("grouping by : {}.", numNodes);
		userDAL.resetUsedId();// wipe the existing nodes in useredid list
		target.generate(numNodes);
	}
	
	@RequestMapping(value = "/dataGenerate", method = RequestMethod.POST , produces=MediaType.APPLICATION_JSON_VALUE )
	//@ResponseStatus( HttpStatus.OK )
	public ResponseEntity dataGenerate(@RequestBody AuxUser  obj) {
		setTarget(obj.getTargetNode());
		//System.out.println("Test the decimal value:"+obj.getOverlapPresentage());
		auxiliary.generate(obj.getAuxNode(), obj.getOverlapPresentage());
		ResponseEntity resp = new ResponseEntity(new ResponseBody() , HttpStatus.OK);
		return resp;
	}
	
	@RequestMapping(value = "/anonymization", method = RequestMethod.POST)
	public ResponseEntity anonymization(@RequestBody Anonymization  anonymization) {	
		target.anonymize(anonymization);
		ResponseEntity resp = new ResponseEntity(new ResponseBody() , HttpStatus.OK);
		return resp;
	}
	//The HTTPRESTFul API for de-anonymization component
	@RequestMapping(value = "/getTargetNode/{noSeed}/{config}/{keepPrev}", method = RequestMethod.GET)
	public ResponseEntity<DeAnonymizationResponse> getTargetNode(@PathVariable("noSeed") int noSeed , @PathVariable("config") int config , @PathVariable("keepPrev") boolean keepPrev) {		
		System.out.println("config:"+config);
		ResponseEntity<DeAnonymizationResponse> resp = new ResponseEntity<DeAnonymizationResponse>(ele.generate(noSeed,config,keepPrev) , HttpStatus.OK);
		return resp;
	}
	
	@RequestMapping(value = "/getLogReport/{begin}/{end}", method = RequestMethod.GET)
	public ResponseEntity<List<LogReport>> getLogReport(@PathVariable("begin") String begin , @PathVariable("end") String end) {
		@SuppressWarnings("unchecked")
		//System.out.println(begin.toString());
		ResponseEntity<List<LogReport>>  resp = new ResponseEntity<List<LogReport>>(logReportService.filterLogsByDates(begin, end) , HttpStatus.OK);
		//System.out.println("begin:"+begin+"; end:"+end);
		//new SimpleDateFormat("yyyy-MM-dd").parse(begin);
		//System.out.println("--"+begin.toString());
		//System.out.println("--"+end.toString());
		
		return resp;
	}
	
	@RequestMapping(value = "/getOverloapSize", method = RequestMethod.GET)
	public ResponseEntity<Long> getOverloapSize() {
		@SuppressWarnings("unchecked")
		ResponseEntity<Long>  resp = new ResponseEntity<Long>(logReportService.getoverlapcount() , HttpStatus.OK);
		return resp;
	}
	/*
	@RequestMapping(value = "/getXaxis/{userid}", method = RequestMethod.GET)
	public ResponseEntity<Long> getXaxis(@PathVariable("userid") long config) {
		//@SuppressWarnings("unchecked")
		auxiliary.getAlias(config);
		//ResponseEntity<Long>  resp = new ResponseEntity<Long>( , HttpStatus.OK);
		return null;
	}*/
}