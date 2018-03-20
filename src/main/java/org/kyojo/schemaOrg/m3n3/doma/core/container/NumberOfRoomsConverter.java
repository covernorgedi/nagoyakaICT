package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.NUMBER_OF_ROOMS;
import org.kyojo.schemaOrg.m3n3.core.Container.NumberOfRooms;

@ExternalDomain
public class NumberOfRoomsConverter implements DomainConverter<NumberOfRooms, String> {

	@Override
	public String fromDomainToValue(NumberOfRooms domain) {
		return domain.getNativeValue();
	}

	@Override
	public NumberOfRooms fromValueToDomain(String value) {
		return new NUMBER_OF_ROOMS(value);
	}

}
