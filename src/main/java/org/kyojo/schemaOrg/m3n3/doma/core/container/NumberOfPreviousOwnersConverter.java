package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.NUMBER_OF_PREVIOUS_OWNERS;
import org.kyojo.schemaOrg.m3n3.core.Container.NumberOfPreviousOwners;

@ExternalDomain
public class NumberOfPreviousOwnersConverter implements DomainConverter<NumberOfPreviousOwners, String> {

	@Override
	public String fromDomainToValue(NumberOfPreviousOwners domain) {
		return domain.getNativeValue();
	}

	@Override
	public NumberOfPreviousOwners fromValueToDomain(String value) {
		return new NUMBER_OF_PREVIOUS_OWNERS(value);
	}

}
