package com.dvsmedeiros.report.core.business.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.INavigationCase;
import com.dvsmedeiros.bce.core.controller.business.IStrategy;
import com.dvsmedeiros.report.core.IReportHandler;
import com.dvsmedeiros.report.domain.ExecutionStatus;
import com.dvsmedeiros.report.domain.Format;
import com.dvsmedeiros.report.domain.ReportRequest;
import com.dvsmedeiros.report.domain.ReportResponse;

@Component
public class GenerateReport implements IStrategy<ReportRequest> {

	@Resource(name = "reportHandler")
	private Map<Format, IReportHandler> handlers;
	
	@Override
	public void process(ReportRequest aEntity, INavigationCase<ReportRequest> aCase) {
		IReportHandler handler = handlers.get(aEntity.getFormat());
		if(handler == null) {
			aCase.suspendExecution("Handler para formato: " + aEntity.getFormat() + " inexistente ou inválido");
			return;
		}
		ReportResponse report = handler.execute(aEntity, aEntity.getParameters());
		if(report != null && report.getStatus() != null && report.getStatus().equals(ExecutionStatus.ERROR)) {
			aCase.suspendExecution("Houve um erro no processamento do relatório: " + aEntity.getReport().getName());
			return;
		}
		aCase.getResult().addEntity(report);
	}
}
