package com.dvsmedeiros.report.core.adapter;

import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.IAdapter;
import com.dvsmedeiros.report.domain.BooleanParamValue;
import com.dvsmedeiros.report.domain.Param;

@Component
public class ParamToBooleanAdapter implements IAdapter<Param, Boolean> {

	@Override
	public Boolean adapt(Param source) {
		if (source instanceof BooleanParamValue) {
			BooleanParamValue parsed = (BooleanParamValue) source;
			return parsed.getValue();
		}
		return Boolean.FALSE;
	}
}
