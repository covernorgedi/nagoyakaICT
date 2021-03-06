package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.NUM_CHILDREN;
import org.kyojo.schemaOrg.m3n3.core.Container.NumChildren;

@ExternalDomain
public class NumChildrenConverter implements DomainConverter<NumChildren, String> {

	@Override
	public String fromDomainToValue(NumChildren domain) {
		return domain.getNativeValue();
	}

	@Override
	public NumChildren fromValueToDomain(String value) {
		return new NUM_CHILDREN(value);
	}

}
