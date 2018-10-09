package com.dvsmedeiros.report.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dvsmedeiros.report.domain.Param;
import com.dvsmedeiros.rest.rest.controller.DomainEntityController;

@Controller
@RequestMapping("${server.controller.prefix}/report/param")
public class ReportParamController extends DomainEntityController<Param> {

	public ReportParamController() {
		super(Param.class);
	}
}
