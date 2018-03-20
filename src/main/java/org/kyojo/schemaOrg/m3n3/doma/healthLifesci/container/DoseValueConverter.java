package org.kyojo.schemaOrg.m3n3.doma.healthLifesci.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.healthLifesci.impl.DOSE_VALUE;
import org.kyojo.schemaOrg.m3n3.healthLifesci.Container.DoseValue;

@ExternalDomain
public class DoseValueConverter implements DomainConverter<DoseValue, String> {

	@Override
	public String fromDomainToValue(DoseValue domain) {
		return domain.getNativeValue();
	}

	@Override
	public DoseValue fromValueToDomain(String value) {
		return new DOSE_VALUE(value);
	}

}
