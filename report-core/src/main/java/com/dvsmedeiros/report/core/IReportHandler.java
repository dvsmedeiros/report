package com.dvsmedeiros.report.core;

import java.io.File;
import java.util.Map;

import com.dvsmedeiros.report.core.business.impl.GenerateReport;
import com.dvsmedeiros.report.domain.ReportRequest;
import com.dvsmedeiros.report.domain.ReportResponse;

public interface IReportHandler {
	
	public static final String DEFAULT_SUB_REPORT_DIR = GenerateReport.class.getClassLoader().getResource("templates").getPath().concat(File.separator);
	
	public ReportResponse execute(ReportRequest request, Map<String, Object> params);

	public void compile();
	
	
}
