package org.kyojo.schemaOrg.m3n3.doma.pending.container;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import org.kyojo.schemaOrg.m3n3.pending.impl.MONTHLY_MINIMUM_REPAYMENT_AMOUNT;
import org.kyojo.schemaOrg.m3n3.pending.Container.MonthlyMinimumRepaymentAmount;

@ExternalDomain
public class MonthlyMinimumRepaymentAmountConverter implements DomainConverter<MonthlyMinimumRepaymentAmount, String> {

	@Override
	public String fromDomainToValue(MonthlyMinimumRepaymentAmount domain) {
		return domain.getNativeValue();
	}

	@Override
	public MonthlyMinimumRepaymentAmount fromValueToDomain(String value) {
		return new MONTHLY_MINIMUM_REPAYMENT_AMOUNT(value);
	}

}
