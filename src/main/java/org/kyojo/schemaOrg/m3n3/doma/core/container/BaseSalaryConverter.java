package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.BASE_SALARY;
import org.kyojo.schemaOrg.m3n3.core.Container.BaseSalary;

@ExternalDomain
public class BaseSalaryConverter implements DomainConverter<BaseSalary, String> {

	@Override
	public String fromDomainToValue(BaseSalary domain) {
		return domain.getNativeValue();
	}

	@Override
	public BaseSalary fromValueToDomain(String value) {
		return new BASE_SALARY(value);
	}

}
