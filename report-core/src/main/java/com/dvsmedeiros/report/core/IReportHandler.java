package com.dvsmedeiros.report.core;

import java.util.Map;

import com.dvsmedeiros.report.domain.ReportRequest;
import com.dvsmedeiros.report.domain.ReportResponse;

public interface IReportHandler {
	
	public ReportResponse execute(ReportRequest request, Map<String, Object> params);

	public void compile();
}
