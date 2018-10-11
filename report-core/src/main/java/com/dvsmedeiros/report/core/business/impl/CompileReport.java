package com.dvsmedeiros.report.core.business.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.INavigationCase;
import com.dvsmedeiros.bce.core.controller.business.IStrategy;
import com.dvsmedeiros.report.core.IReportHandler;
import com.dvsmedeiros.report.domain.Format;
import com.dvsmedeiros.report.domain.Report;

@Component
public class CompileReport implements IStrategy<Report> {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource(name = "reportHandler")
	private Map<Format, IReportHandler> handlers;
	
	@Override
	public void process(Report aEntity, INavigationCase<Report> aCase) {
		IReportHandler handler = handlers.get(Format.JASPER);
		if(handler == null) {
			aCase.suspendExecution("Handler para compilar formato: " + Format.JASPER.getExtension() + " inexistente ou inválido");			
			return;
		}
		handler.compile();		
	}
}
