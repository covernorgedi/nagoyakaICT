package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.INTEREST_RATE;
import org.kyojo.schemaOrg.m3n3.core.Container.InterestRate;

@ExternalDomain
public class InterestRateConverter implements DomainConverter<InterestRate, String> {

	@Override
	public String fromDomainToValue(InterestRate domain) {
		return domain.getNativeValue();
	}

	@Override
	public InterestRate fromValueToDomain(String value) {
		return new INTEREST_RATE(value);
	}

}
