package org.kyojo.schemaOrg.m3n3.doma.healthLifesci.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.healthLifesci.impl.AVAILABLE_SERVICE;
import org.kyojo.schemaOrg.m3n3.healthLifesci.Container.AvailableService;

@ExternalDomain
public class AvailableServiceConverter implements DomainConverter<AvailableService, String> {

	@Override
	public String fromDomainToValue(AvailableService domain) {
		return domain.getNativeValue();
	}

	@Override
	public AvailableService fromValueToDomain(String value) {
		return new AVAILABLE_SERVICE(value);
	}

}
