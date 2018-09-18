package com.dvsmedeiros.report.domain;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.dvsmedeiros.bce.domain.ApplicationEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JsonDataSource;

public class ReportRequest extends ApplicationEntity {

	private Report report;
	private JsonNode dataSource;
	private Map<String, Object> parameters;
	private Format format;
	private String owner;
	private String outputFileName;
	
	public ReportRequest() {
		this.report = new Report();
		this.parameters = new HashMap<>();
	}
	
	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public JsonNode getDataSource() {
		return dataSource;
	}

	public void setDataSource(JsonNode dataSource) {
		this.dataSource = dataSource;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public Map<String, Object> getParameters() {
		return new HashMap<>(parameters);
	}

	public void addParameter(String key, Object value) {
		if (this.parameters == null) {
			this.parameters = new HashMap<>();
		}
		this.parameters.put(key, value);
	}

	public JsonDataSource getJsonDataSource() {
		try {
			String encodedJsonString = new String(
					new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this.dataSource).getBytes(),
					StandardCharsets.UTF_8);
			ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(encodedJsonString.getBytes());
			return new JsonDataSource(jsonDataStream);
		} catch (JRException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
