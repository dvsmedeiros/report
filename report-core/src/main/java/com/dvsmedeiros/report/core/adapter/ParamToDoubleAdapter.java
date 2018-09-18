package com.dvsmedeiros.report.core.adapter;

import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.IAdapter;
import com.dvsmedeiros.report.domain.DoubleParamValue;
import com.dvsmedeiros.report.domain.Param;

@Component
public class ParamToDoubleAdapter implements IAdapter<Param, Double> {

	@Override
	public Double adapt(Param source) {
		if (source instanceof DoubleParamValue) {
			DoubleParamValue parsed = (DoubleParamValue) source;
			return parsed.getValue();
		}
		return null;
	}
}
