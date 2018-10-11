package com.dvsmedeiros.report.domain;

import com.dvsmedeiros.bce.domain.ApplicationEntity;

public class Template extends ApplicationEntity {

	private byte[] file;
	private String name;

	public Template(byte[] file, String name) {
		this.file = file;
		this.name = name;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
