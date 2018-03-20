package jp.nagoyakanet.ict.scm;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder;
import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

@ExternalDomain
public class DocTeamMemberOfConverter implements DomainConverter<DocTeamMemberOf, String>  {

	@Override
	public String fromDomainToValue(DocTeamMemberOf domain) {
		if(domain == null) {
			return "{\"teamList\":[]}";
		}
		return SimpleJsonBuilder.toJson(domain);
	}

	@Override
	public DocTeamMemberOf fromValueToDomain(String value) {
		if(StringUtils.isBlank(value)) {
			DocTeamMemberOf docTeamMemberOf = new DocTeamMemberOf();
			docTeamMemberOf.setTeamList(new ArrayList<DocTeam>());
			return docTeamMemberOf;
		}
		return My.deminion(value, DocTeamMemberOf.class);
	}

}
