package jp.nagoyakanet.ict.scm;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder;
import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

@ExternalDomain
public class DocHasOccupationConverter implements DomainConverter<DocHasOccupation, String>  {

	@Override
	public String fromDomainToValue(DocHasOccupation domain) {
		if(domain == null) {
			return "{\"occupationList\":[]}";
		}
		return SimpleJsonBuilder.toJson(domain);
	}

	@Override
	public DocHasOccupation fromValueToDomain(String value) {
		if(StringUtils.isBlank(value)) {
			DocHasOccupation docHasOccupation = new DocHasOccupation();
			docHasOccupation.setOccupationList(new ArrayList<DocOccupation>());
			return docHasOccupation;
		}
		return My.deminion(value, DocHasOccupation.class);
	}

}
