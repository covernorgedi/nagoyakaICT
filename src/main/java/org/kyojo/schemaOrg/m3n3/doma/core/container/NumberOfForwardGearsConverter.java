package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.NUMBER_OF_FORWARD_GEARS;
import org.kyojo.schemaOrg.m3n3.core.Container.NumberOfForwardGears;

@ExternalDomain
public class NumberOfForwardGearsConverter implements DomainConverter<NumberOfForwardGears, String> {

	@Override
	public String fromDomainToValue(NumberOfForwardGears domain) {
		return domain.getNativeValue();
	}

	@Override
	public NumberOfForwardGears fromValueToDomain(String value) {
		return new NUMBER_OF_FORWARD_GEARS(value);
	}

}
