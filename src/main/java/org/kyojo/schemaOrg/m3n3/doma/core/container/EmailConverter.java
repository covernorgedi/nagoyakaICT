package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.EMAIL;
import org.kyojo.schemaOrg.m3n3.core.Container.Email;

@ExternalDomain
public class EmailConverter implements DomainConverter<Email, String> {

	@Override
	public String fromDomainToValue(Email domain) {
		return domain.getNativeValue();
	}

	@Override
	public Email fromValueToDomain(String value) {
		return new EMAIL(value);
	}

}
