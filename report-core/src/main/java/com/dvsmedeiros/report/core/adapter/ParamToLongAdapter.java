package com.dvsmedeiros.report.core.adapter;

import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.IAdapter;
import com.dvsmedeiros.report.domain.LongParamValue;
import com.dvsmedeiros.report.domain.Param;

@Component
public class ParamToLongAdapter implements IAdapter<Param, Long> {

	@Override
	public Long adapt(Param source) {
		if (source instanceof LongParamValue) {
			LongParamValue parsed = (LongParamValue) source;
			return parsed.getValue();
		}
		return null;
	}
}
