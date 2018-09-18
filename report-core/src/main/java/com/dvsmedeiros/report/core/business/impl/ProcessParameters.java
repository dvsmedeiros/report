package com.dvsmedeiros.report.core.business.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.IAdapter;
import com.dvsmedeiros.bce.core.controller.INavigationCase;
import com.dvsmedeiros.bce.core.controller.business.IStrategy;
import com.dvsmedeiros.report.domain.Param;
import com.dvsmedeiros.report.domain.ParamType;
import com.dvsmedeiros.report.domain.ReportRequest;

@Component
public class ProcessParameters implements IStrategy<ReportRequest> {

	@Resource(name = "parameterHandler")
	private Map<ParamType, IAdapter<Param, ?>> handlers;

	@Override
	public void process(ReportRequest aEntity, INavigationCase<ReportRequest> aCase) {

		aEntity.getReport()
			.getParams()
			.forEach(param -> {
				aEntity.addParameter(param.getName(), handlers.get(param.getType()).adapt(param));
		});
	}
}
