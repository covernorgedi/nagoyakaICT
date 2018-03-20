package org.kyojo.schemaOrg.m3n3.doma.pending.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.pending.impl.EXCHANGE_RATE_SPREAD;
import org.kyojo.schemaOrg.m3n3.pending.Container.ExchangeRateSpread;

@ExternalDomain
public class ExchangeRateSpreadConverter implements DomainConverter<ExchangeRateSpread, String> {

	@Override
	public String fromDomainToValue(ExchangeRateSpread domain) {
		return domain.getNativeValue();
	}

	@Override
	public ExchangeRateSpread fromValueToDomain(String value) {
		return new EXCHANGE_RATE_SPREAD(value);
	}

}
