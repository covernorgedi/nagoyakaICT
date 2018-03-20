package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.PARTY_SIZE;
import org.kyojo.schemaOrg.m3n3.core.Container.PartySize;

@ExternalDomain
public class PartySizeConverter implements DomainConverter<PartySize, String> {

	@Override
	public String fromDomainToValue(PartySize domain) {
		return domain.getNativeValue();
	}

	@Override
	public PartySize fromValueToDomain(String value) {
		return new PARTY_SIZE(value);
	}

}
