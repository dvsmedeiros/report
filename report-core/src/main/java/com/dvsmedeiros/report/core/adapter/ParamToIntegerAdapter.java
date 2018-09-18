package com.dvsmedeiros.report.core.adapter;

import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.IAdapter;
import com.dvsmedeiros.report.domain.IntegerParamValue;
import com.dvsmedeiros.report.domain.Param;

@Component
public class ParamToIntegerAdapter implements IAdapter<Param, Integer> {

	@Override
	public Integer adapt(Param source) {
		if (source instanceof IntegerParamValue) {
			IntegerParamValue parsed = (IntegerParamValue) source;
			return parsed.getValue();
		}
		return null;
	}
}
