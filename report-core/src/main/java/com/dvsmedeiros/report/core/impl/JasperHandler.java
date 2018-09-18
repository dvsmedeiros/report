package com.dvsmedeiros.report.core.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.dvsmedeiros.configuration.core.IConfigurationFacade;
import com.dvsmedeiros.report.core.IReportHandler;
import com.dvsmedeiros.report.core.business.impl.GenerateReport;
import com.dvsmedeiros.report.domain.ExecutionStatus;
import com.dvsmedeiros.report.domain.FileExtention;
import com.dvsmedeiros.report.domain.ReportRequest;
import com.dvsmedeiros.report.domain.ReportResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Component
public class JasperHandler implements IReportHandler {
	
	private static final String DEFAULT_SUB_REPORT_DIR = GenerateReport.class.getClassLoader().getResource("templates").getPath().concat(File.separator);
	
	@Autowired
	@Qualifier("configurationFacade")
	private IConfigurationFacade config;
	private String templatePath;
	
	@PostConstruct
	public void init() {
		this.templatePath = config.find(null, "SUB_REPORT_DIR", DEFAULT_SUB_REPORT_DIR).getValue();
	}
	
	@Override
	public ReportResponse execute(ReportRequest request, Map<String, Object> params) {

		ReportResponse response = new ReportResponse();
		response.setStatus(ExecutionStatus.PROCESSING);

		try {
			
			params.put("SUB_REPORT_DIR", templatePath);
			
			String jasperFileName = templatePath.concat(request.getReport().getName()).concat(FileExtention.JASPER.getExtention());
			JasperReport report = (JasperReport) JRLoader.loadObject(new File(jasperFileName));
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, request.getJsonDataSource());
			String responseFileName = request.getReport().getName().concat("_").concat(UUID.randomUUID().toString()).concat(request.getFormat().getExtension());

			byte[] exported = JasperExportManager.exportReportToPdf(jasperPrint);

			response.setName(responseFileName);
			response.setFile(exported);
			response.setStatus(ExecutionStatus.SUCESS);

		} catch (JRException e) {
			response.setStatus(ExecutionStatus.ERROR);
			e.printStackTrace();
		}

		return response;
	}

	public void compile() {

		try {
			Files
			.list(Paths.get(templatePath))
			.filter(file -> FileExtention.JRXML.getExtention().endsWith(getFileExtension(file)))
			.forEach(file -> {
				try {
					String source = file.toAbsolutePath().toString();
					JasperCompileManager.compileReportToFile(source);
				} catch (JRException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getFileExtension(Path file) {
		String fileName = file.getFileName().toString();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);

		}
		return "";
	}

}
