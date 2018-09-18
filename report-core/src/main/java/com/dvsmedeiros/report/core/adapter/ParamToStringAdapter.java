package com.dvsmedeiros.report.core.adapter;

import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.IAdapter;
import com.dvsmedeiros.report.domain.Param;
import com.dvsmedeiros.report.domain.StringParamValue;

@Component
public class ParamToStringAdapter implements IAdapter<Param, String> {

	@Override
	public String adapt(Param source) {
		if (source instanceof StringParamValue) {
			StringParamValue parsed = (StringParamValue) source;
			return parsed.getValue();
		}
		return null;
	}
}
