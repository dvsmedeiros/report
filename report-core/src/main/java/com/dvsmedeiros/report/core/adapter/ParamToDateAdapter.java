package com.dvsmedeiros.report.core.adapter;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.IAdapter;
import com.dvsmedeiros.report.domain.CalendarParamValue;
import com.dvsmedeiros.report.domain.Param;

@Component
public class ParamToDateAdapter implements IAdapter<Param, Date> {

	@Override
	public Date adapt(Param source) {
		if (source instanceof CalendarParamValue) {
			CalendarParamValue parsed = (CalendarParamValue) source;
			return parsed.getValue();
		}
		return null;
	}
}
