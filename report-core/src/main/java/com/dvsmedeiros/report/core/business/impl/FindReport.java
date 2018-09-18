package com.dvsmedeiros.report.core.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.INavigationCase;
import com.dvsmedeiros.bce.core.controller.business.IStrategy;
import com.dvsmedeiros.bce.core.dao.IDAO;
import com.dvsmedeiros.report.domain.Report;
import com.dvsmedeiros.report.domain.ReportRequest;

@Component
public class FindReport implements IStrategy<ReportRequest> {
	
	@Autowired
	@Qualifier("genericDAO")
	private IDAO<Report> dao;
	
	@Override
	public void process(ReportRequest aEntity, INavigationCase<ReportRequest> aCase) {
		Report report = dao.find(aEntity.getReport().getId(), Report.class);
		if(report == null) {
			aCase.suspendExecution("Relatório ID: " + aEntity.getReport().getId() + " inexistente ou inválido");
			return;
		}
		report.setParams(aEntity.getReport().getParams());
		aEntity.setReport(report);
	}
}
