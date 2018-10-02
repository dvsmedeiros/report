package com.dvsmedeiros.report.controller;

import java.util.Optional;

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

	public ReportController() {
		super(Report.class);
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> createEntity(@RequestBody Report entity) {
		ResponseEntity<?> response = super.createEntity(entity);
		BusinessCase<?> aCase = BusinessCaseBuilder.withName("COMPILE_REPORT");
		navigator.run(entity, aCase);
		if (!aCase.getResult().hasError()) {
			return response;
		}
		return ResponseMessage.serverError(aCase.getResult().getMessage());
	}

	@Override
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<?> updateEntity(@RequestBody Report entity) {
		ResponseEntity<?> response = super.updateEntity(entity);
		BusinessCase<?> aCase = BusinessCaseBuilder.withName("COMPILE_REPORT");
		navigator.run(entity, aCase);
		if (!aCase.getResult().hasError()) {
			return response;
		}
		return ResponseMessage.serverError(aCase.getResult().getMessage());
	}

	@RequestMapping(value = "/{reportId}/generate", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> genarate(@PathVariable Long reportId, @RequestBody ReportRequest reportRequest) {
		try {

			reportRequest.getReport().setId(reportId);

			BusinessCase<?> aCase = BusinessCaseBuilder.withName("GENERATE_REPORT");
			navigator.run(reportRequest, aCase);
			Optional<ReportResponse> report = aCase.getResult().getEntity();
			
			boolean hasError = aCase.getResult().hasError();
			boolean hasReport = !hasError && report.isPresent();
			boolean sucess = hasReport && report.get().getStatus().equals(ExecutionStatus.SUCESS);
			boolean hasFile = sucess && report.get().getFile().length > 0;
			
			if (!hasError && hasReport && sucess && hasFile) {

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.parseMediaType("application/pdf"));
				headers.setContentDispositionFormData(report.get().getName(), report.get().getName());
				headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

				return ResponseEntity.ok().headers(headers).body(report.get().getFile());
			}

			if (hasError || !hasReport || !sucess) {
				return ResponseMessage.serverError(aCase.getResult().getMessage());				
			}
			return ResponseEntity.noContent().build();

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseMessage.serverError("Erro interno ao emitir relatório, id: ".concat(String.valueOf(reportId)));
		}
	}

}
