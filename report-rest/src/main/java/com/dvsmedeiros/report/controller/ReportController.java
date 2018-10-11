package com.dvsmedeiros.report.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dvsmedeiros.bce.core.controller.impl.BusinessCase;
import com.dvsmedeiros.bce.core.controller.impl.BusinessCaseBuilder;
import com.dvsmedeiros.report.domain.ExecutionStatus;
import com.dvsmedeiros.report.domain.Report;
import com.dvsmedeiros.report.domain.ReportRequest;
import com.dvsmedeiros.report.domain.ReportResponse;
import com.dvsmedeiros.report.domain.Template;
import com.dvsmedeiros.rest.domain.ResponseMessage;
import com.dvsmedeiros.rest.rest.controller.DomainSpecificEntityController;

@Controller
@RequestMapping("${server.controller.prefix}/report")
public class ReportController extends DomainSpecificEntityController<Report> {
	
	@Autowired
	@Qualifier("businessCaseBuilder")
	private BusinessCaseBuilder<ReportController> businessCaseBuilder;
	
	public ReportController() {
		super(Report.class);
	}
	
	
	
	/* Exemplo de controller multipart/mixed recebendo json e file
	 * 1 - A Ordem dos @RequestPart importa o JSON tem que vir primeiro, depois o Multpart file
	 * 2 - Necessário configurar consumes = { "multipart/form-data" }
	 * 3 - Na requição é importante mandar o ContentType: undefined para que o browser resolva de forma adequada o 
	 *     boundory da requisição
	 * Referência  : https://stackoverflow.com/questions/21329426/spring-mvc-multipart-request-with-json
	 * Req Snippet :   	 
	
	@RequestMapping(value = "upload", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public @ResponseBody ResponseEntity<?> createEntity(@RequestPart("config") Configuration config, @RequestPart(name = "file", required = false) MultipartFile file) {		
		return ResponseEntity.ok().build();
	}
	*/
	
	@RequestMapping(value = "upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody ResponseEntity<?> createEntity(
			@RequestPart("report") Report report,
			@RequestParam(name = "file", required = false) MultipartFile file) {
		try {
			
			BusinessCase<?> aCase = businessCaseBuilder.withName("COMPILE_REPORT");			
			Optional.ofNullable(file).ifPresent(template -> {
				try {					
					report.setTemplate(new Template(template.getBytes(), template.getOriginalFilename()));
					navigator.run(report, aCase);
				} catch (IOException e) {				
					e.printStackTrace();
				}				
			});						
			ResponseEntity<?> response = super.createEntity(report);						
			if (!aCase.getResult().hasError()) {
				return response;
			}
			return ResponseMessage.serverError(aCase.getResult().getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseMessage.serverError("Erro interno ao salvar: " + Report.class.getName());
		}
	}
	
	@Override
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<?> updateEntity(@RequestBody Report entity) {
		ResponseEntity<?> response = super.updateEntity(entity);
		BusinessCase<?> aCase = new BusinessCaseBuilder<Report>().withName("COMPILE_REPORT");
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

			BusinessCase<?> aCase = new BusinessCaseBuilder<Report>().withName("GENERATE_REPORT");
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
