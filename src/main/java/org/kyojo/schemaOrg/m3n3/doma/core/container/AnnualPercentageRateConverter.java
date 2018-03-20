package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.ANNUAL_PERCENTAGE_RATE;
import org.kyojo.schemaOrg.m3n3.core.Container.AnnualPercentageRate;

@ExternalDomain
public class AnnualPercentageRateConverter implements DomainConverter<AnnualPercentageRate, String> {

	@Override
	public String fromDomainToValue(AnnualPercentageRate domain) {
		return domain.getNativeValue();
	}

	@Override
	public AnnualPercentageRate fromValueToDomain(String value) {
		return new ANNUAL_PERCENTAGE_RATE(value);
	}

}
