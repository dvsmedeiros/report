package com.dvsmedeiros.report.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dvsmedeiros.bce.core.controller.INavigator;
import com.dvsmedeiros.bce.core.controller.impl.BusinessCase;
import com.dvsmedeiros.bce.core.controller.impl.BusinessCaseBuilder;
import com.dvsmedeiros.bce.domain.IEntity;
import com.dvsmedeiros.report.domain.ExecutionStatus;
import com.dvsmedeiros.report.domain.Report;
import com.dvsmedeiros.report.domain.ReportRequest;
import com.dvsmedeiros.report.domain.ReportResponse;
import com.dvsmedeiros.rest.domain.ResponseMessage;
import com.dvsmedeiros.rest.rest.controller.DomainSpecificEntityController;

@Controller
@RequestMapping("report")
public class ReportController extends DomainSpecificEntityController<Report> {

	@Autowired
	@Qualifier("navigator")
	private INavigator<ReportRequest> navigator;

	public ReportController() {
		super(Report.class);
	}
	
	@Override
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> createEntity(@RequestBody Report entity) {
		ResponseEntity<?> response = super.createEntity(entity);
		BusinessCase<IEntity> aCase = new BusinessCaseBuilder<>().withName("COMPILE_REPORT").build();
		navigator.run(entity, aCase);
		if(!aCase.getResult().hasError()) {
			return response;
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(Boolean.TRUE, aCase.getResult().getMessage()));
	}
	
	@Override
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<?> updateEntity(@RequestBody Report entity) {
		ResponseEntity<?> response = super.updateEntity(entity);
		BusinessCase<IEntity> aCase = new BusinessCaseBuilder<>().withName("COMPILE_REPORT").build();
		navigator.run(entity, aCase);
		if(!aCase.getResult().hasError()) {
			return response;
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(Boolean.TRUE, aCase.getResult().getMessage()));
	}
	
	@RequestMapping(value = "/{reportId}/generate", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> genarate(@PathVariable Long reportId, @RequestBody ReportRequest reportRequest) {
		try {

			reportRequest.getReport().setId(reportId);

			BusinessCase<IEntity> aCase = new BusinessCaseBuilder<>().withName("GENERATE_REPORT").build();
			navigator.run(reportRequest, aCase);
			Optional<ReportResponse> report = aCase.getResult().getEntity();
			
			if (!aCase.getResult().hasError() && report.isPresent() && report.get().getStatus().equals(ExecutionStatus.SUCESS) && report.get().getFile().length > 0) {
				
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.parseMediaType("application/pdf"));
				headers.setContentDispositionFormData(report.get().getName(), report.get().getName());
				headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

				return ResponseEntity.ok().headers(headers).body(report.get().getFile());
			}

			if (aCase.getResult().hasError() || !report.isPresent() || report.get().getStatus().equals(ExecutionStatus.ERROR)) {
				ResponseMessage responseMessage = new ResponseMessage(Boolean.TRUE, aCase.getResult().getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
			}
			
			return ResponseEntity.noContent().build();

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
