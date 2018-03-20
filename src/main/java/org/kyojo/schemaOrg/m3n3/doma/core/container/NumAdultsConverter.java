package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.NUM_ADULTS;
import org.kyojo.schemaOrg.m3n3.core.Container.NumAdults;

@ExternalDomain
public class NumAdultsConverter implements DomainConverter<NumAdults, String> {

	@Override
	public String fromDomainToValue(NumAdults domain) {
		return domain.getNativeValue();
	}

	@Override
	public NumAdults fromValueToDomain(String value) {
		return new NUM_ADULTS(value);
	}

}
