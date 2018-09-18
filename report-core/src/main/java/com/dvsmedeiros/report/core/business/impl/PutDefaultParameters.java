package com.dvsmedeiros.report.core.business.impl;

import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.INavigationCase;
import com.dvsmedeiros.bce.core.controller.business.IStrategy;
import com.dvsmedeiros.report.domain.Format;
import com.dvsmedeiros.report.domain.ReportRequest;

@Component
public class PutDefaultParameters implements IStrategy<ReportRequest> {
	
	@Override
	public void process(ReportRequest aEntity, INavigationCase<ReportRequest> aCase) {

		aEntity.addParameter("TITLE", aEntity.getReport().getTitle());
		aEntity.addParameter("DESCRIPTION", aEntity.getReport().getDescription());
		aEntity.addParameter("VERSION", aEntity.getReport().getVersion());
		aEntity.setFormat(aEntity.getFormat() != null ? aEntity.getFormat() : Format.PDF);
		
	}
}
