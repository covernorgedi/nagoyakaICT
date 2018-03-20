package org.kyojo.schemaOrg.m3n3.doma.auto.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.auto.impl.SEATING_CAPACITY;
import org.kyojo.schemaOrg.m3n3.auto.Container.SeatingCapacity;

@ExternalDomain
public class SeatingCapacityConverter implements DomainConverter<SeatingCapacity, String> {

	@Override
	public String fromDomainToValue(SeatingCapacity domain) {
		return domain.getNativeValue();
	}

	@Override
	public SeatingCapacity fromValueToDomain(String value) {
		return new SEATING_CAPACITY(value);
	}

}
