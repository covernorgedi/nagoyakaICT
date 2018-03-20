package org.kyojo.schemaOrg.m3n3.doma.pending.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.pending.impl.BROADCAST_FREQUENCY_VALUE;
import org.kyojo.schemaOrg.m3n3.pending.Container.BroadcastFrequencyValue;

@ExternalDomain
public class BroadcastFrequencyValueConverter implements DomainConverter<BroadcastFrequencyValue, String> {

	@Override
	public String fromDomainToValue(BroadcastFrequencyValue domain) {
		return domain.getNativeValue();
	}

	@Override
	public BroadcastFrequencyValue fromValueToDomain(String value) {
		return new BROADCAST_FREQUENCY_VALUE(value);
	}

}
