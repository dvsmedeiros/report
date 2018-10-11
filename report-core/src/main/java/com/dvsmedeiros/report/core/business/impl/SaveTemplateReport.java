package com.dvsmedeiros.report.core.business.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.dvsmedeiros.bce.core.controller.INavigationCase;
import com.dvsmedeiros.bce.core.controller.business.IStrategy;
import com.dvsmedeiros.configuration.core.IConfigurationFacade;
import com.dvsmedeiros.report.core.IReportHandler;
import com.dvsmedeiros.report.domain.Report;
import com.google.common.base.Strings;
import com.google.common.io.Files;

@Component
public class SaveTemplateReport implements IStrategy<Report> {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("configurationFacade")
	private IConfigurationFacade config;

	@Override
	public void process(Report aEntity, INavigationCase<Report> aCase) {

		String path = config.find(null, "SUB_REPORT_DIR", IReportHandler.DEFAULT_SUB_REPORT_DIR).get().getValue();

		try {

			boolean hasReport = aEntity != null;
			boolean hasTemplate = hasReport && aEntity.getTemplate() != null;
			boolean hasFile = hasTemplate && aEntity.getTemplate().getFile() != null;
			boolean hasName = hasTemplate && !Strings.isNullOrEmpty(aEntity.getTemplate().getName());

			if (!hasFile) {
				aCase.suspendExecution("Arquivo inexistente ou inválido");
				return;
			}
			
			if (!hasName) {
				aCase.suspendExecution("Nome do arquivo inexistente ou inválido");
				return;
			}
			
			Files.write(aEntity.getTemplate().getFile(), new File(path.concat(aEntity.getTemplate().getName())));
			logger.info("template: " + aEntity.getTemplate().getName() + " salvo com suceso em: " + path);
			
		} catch (IOException e) {
			aCase.suspendExecution("Erro ao salvar template: " + aEntity.getTemplate().getName() + " em: " + path);
			return;
		}
	}
}
