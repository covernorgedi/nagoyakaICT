package org.kyojo.schemaOrg.m3n3.doma.pending.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.pending.impl.ESTIMATED_SALARY;
import org.kyojo.schemaOrg.m3n3.pending.Container.EstimatedSalary;

@ExternalDomain
public class EstimatedSalaryConverter implements DomainConverter<EstimatedSalary, String> {

	@Override
	public String fromDomainToValue(EstimatedSalary domain) {
		return domain.getNativeValue();
	}

	@Override
	public EstimatedSalary fromValueToDomain(String value) {
		return new ESTIMATED_SALARY(value);
	}

}
