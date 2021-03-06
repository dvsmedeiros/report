package com.dvsmedeiros.report.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.dvsmedeiros.bce.domain.DomainSpecificEntity;

@Entity
@Table(name = "REPORT")
public class Report extends DomainSpecificEntity {

	private String name;
	private String title;
	private String version;
	@Transient
	private Template template;
	@Transient
	private List<Param> params;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<Param> getParams() {
		return params != null ? params : new ArrayList<>();
	}

	public void setParams(List<Param> params) {
		this.params = params;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

}
