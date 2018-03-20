package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.NUMBER_OF_DOORS;
import org.kyojo.schemaOrg.m3n3.core.Container.NumberOfDoors;

@ExternalDomain
public class NumberOfDoorsConverter implements DomainConverter<NumberOfDoors, String> {

	@Override
	public String fromDomainToValue(NumberOfDoors domain) {
		return domain.getNativeValue();
	}

	@Override
	public NumberOfDoors fromValueToDomain(String value) {
		return new NUMBER_OF_DOORS(value);
	}

}
