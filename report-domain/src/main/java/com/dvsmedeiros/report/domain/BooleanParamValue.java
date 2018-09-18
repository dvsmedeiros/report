package com.dvsmedeiros.report.domain;

public class BooleanParamValue extends Param {

	private Boolean value;

	public BooleanParamValue() {
	}

	public BooleanParamValue(ParamType type, String name, String label, Boolean required, Boolean value) {
		super(type, name, label, required);
		this.value = value;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

}
