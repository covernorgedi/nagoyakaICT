package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.VEHICLE_SEATING_CAPACITY;
import org.kyojo.schemaOrg.m3n3.core.Container.VehicleSeatingCapacity;

@ExternalDomain
public class VehicleSeatingCapacityConverter implements DomainConverter<VehicleSeatingCapacity, String> {

	@Override
	public String fromDomainToValue(VehicleSeatingCapacity domain) {
		return domain.getNativeValue();
	}

	@Override
	public VehicleSeatingCapacity fromValueToDomain(String value) {
		return new VEHICLE_SEATING_CAPACITY(value);
	}

}
