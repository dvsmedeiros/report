package com.dvsmedeiros.report.core.business;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dvsmedeiros.bce.core.controller.IAdapter;
import com.dvsmedeiros.bce.core.controller.impl.Navigation;
import com.dvsmedeiros.bce.core.controller.impl.NavigationBuilder;
import com.dvsmedeiros.report.core.IReportHandler;
import com.dvsmedeiros.report.core.business.impl.CompileReport;
import com.dvsmedeiros.report.core.business.impl.FindReport;
import com.dvsmedeiros.report.core.business.impl.GenerateReport;
import com.dvsmedeiros.report.core.business.impl.ProcessParameters;
import com.dvsmedeiros.report.core.business.impl.PutDefaultParameters;
import com.dvsmedeiros.report.core.business.impl.SaveTemplateReport;
import com.dvsmedeiros.report.domain.Format;
import com.dvsmedeiros.report.domain.Param;
import com.dvsmedeiros.report.domain.ParamType;
import com.dvsmedeiros.report.domain.Report;
import com.dvsmedeiros.report.domain.ReportRequest;

@Configuration
public class ReportNavigation {

	@Autowired
	@Qualifier("jasperHandler")
	private IReportHandler jasper;
	
	@Autowired
	@Qualifier("paramToStringAdapter")
	private IAdapter<Param, String> paramToString;
	@Autowired
	@Qualifier("paramToIntegerAdapter")
	private IAdapter<Param, Integer> paramToInteger;
	@Autowired
	@Qualifier("paramToLongAdapter")
	private IAdapter<Param, Long> paramToLong;
	@Autowired
	@Qualifier("paramToDoubleAdapter")
	private IAdapter<Param, Double> paramToDouble;
	@Autowired
	@Qualifier("paramToBooleanAdapter")
	private IAdapter<Param, Boolean> paramToBoolean;
	@Autowired
	@Qualifier("paramToDateAdapter")
	private IAdapter<Param, Date> paramToDate;
	
	@Autowired
	private FindReport findReport;
	@Autowired
	private PutDefaultParameters putDefaultParameters;
	@Autowired
	private ProcessParameters processParameters;
	@Autowired
	private GenerateReport generateReport;
	
	@Autowired
	private CompileReport compileReport;
	@Autowired
	private SaveTemplateReport saveTemplate;
	
	@Bean(name = "reportHandler")
	public Map<Format, IReportHandler> getReportExecutions() {
		ConcurrentHashMap<Format, IReportHandler> executions = new ConcurrentHashMap<>();
		executions.put(Format.PDF, jasper);
		executions.put(Format.HTML, jasper);
		executions.put(Format.JASPER, jasper);
		return executions;
	}

	@Bean(name = "parameterHandler")
	public Map<ParamType, IAdapter<Param, ?>> getReportParameterHandlers() {

		ConcurrentHashMap<ParamType, IAdapter<Param, ?>> parameterHandlers = new ConcurrentHashMap<>();

		parameterHandlers.put(ParamType.STRING, paramToString);
		parameterHandlers.put(ParamType.INTEGER, paramToInteger);
		parameterHandlers.put(ParamType.LONG, paramToLong);
		parameterHandlers.put(ParamType.DOUBLE, paramToDouble);
		parameterHandlers.put(ParamType.BOOLEAN, paramToBoolean);
		parameterHandlers.put(ParamType.DATE, paramToDate);
		parameterHandlers.put(ParamType.BASE64, paramToString);

		return parameterHandlers;
	}
	
	@Bean(name = "GENERATE_REPORT")
	public Navigation<ReportRequest> generateReport() {
		return new NavigationBuilder<ReportRequest>()
				.next(findReport)
				.next(putDefaultParameters)
				.next(processParameters)
				.next(generateReport)
				.build();
	}
	
	@Bean(name = "COMPILE_REPORT")
	public Navigation<Report> compileReport() {
		return new NavigationBuilder<Report>()
				.next(saveTemplate)
				.next(compileReport)
				.build();
	}
}
