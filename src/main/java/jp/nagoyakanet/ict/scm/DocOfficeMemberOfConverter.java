package jp.nagoyakanet.ict.scm;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder;
import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

@ExternalDomain
public class DocOfficeMemberOfConverter implements DomainConverter<DocOfficeMemberOf, String>  {

	@Override
	public String fromDomainToValue(DocOfficeMemberOf domain) {
		if(domain == null) {
			return "{\"officeList\":[]}";
		}
		return SimpleJsonBuilder.toJson(domain);
	}

	@Override
	public DocOfficeMemberOf fromValueToDomain(String value) {
		if(StringUtils.isBlank(value)) {
			DocOfficeMemberOf docOfficeMemberOf = new DocOfficeMemberOf();
			docOfficeMemberOf.setOfficeList(new ArrayList<DocOffice>());
			return docOfficeMemberOf;
		}
		return My.deminion(value, DocOfficeMemberOf.class);
	}

}
