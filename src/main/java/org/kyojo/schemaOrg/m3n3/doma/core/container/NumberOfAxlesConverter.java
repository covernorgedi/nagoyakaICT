package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.NUMBER_OF_AXLES;
import org.kyojo.schemaOrg.m3n3.core.Container.NumberOfAxles;

@ExternalDomain
public class NumberOfAxlesConverter implements DomainConverter<NumberOfAxles, String> {

	@Override
	public String fromDomainToValue(NumberOfAxles domain) {
		return domain.getNativeValue();
	}

	@Override
	public NumberOfAxles fromValueToDomain(String value) {
		return new NUMBER_OF_AXLES(value);
	}

}
