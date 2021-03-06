package org.kyojo.schemaOrg.m3n3.doma.core.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.core.impl.BROKER;
import org.kyojo.schemaOrg.m3n3.core.Container.Broker;

@ExternalDomain
public class BrokerConverter implements DomainConverter<Broker, String> {

	@Override
	public String fromDomainToValue(Broker domain) {
		return domain.getNativeValue();
	}

	@Override
	public Broker fromValueToDomain(String value) {
		return new BROKER(value);
	}

}
