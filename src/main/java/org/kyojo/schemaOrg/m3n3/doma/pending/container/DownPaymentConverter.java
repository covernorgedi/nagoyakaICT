package org.kyojo.schemaOrg.m3n3.doma.pending.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.pending.impl.DOWN_PAYMENT;
import org.kyojo.schemaOrg.m3n3.pending.Container.DownPayment;

@ExternalDomain
public class DownPaymentConverter implements DomainConverter<DownPayment, String> {

	@Override
	public String fromDomainToValue(DownPayment domain) {
		return domain.getNativeValue();
	}

	@Override
	public DownPayment fromValueToDomain(String value) {
		return new DOWN_PAYMENT(value);
	}

}
