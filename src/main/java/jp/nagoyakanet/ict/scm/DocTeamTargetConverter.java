package jp.nagoyakanet.ict.scm;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder;
import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

@ExternalDomain
public class DocTeamTargetConverter implements DomainConverter<DocTeamTarget, String> {

	@Override
	public String fromDomainToValue(DocTeamTarget domain) {
		if(domain == null) {
			return "{\"userList\":[]}";
		}
		return SimpleJsonBuilder.toJson(domain);
	}

	@Override
	public DocTeamTarget fromValueToDomain(String value) {
		if(StringUtils.isBlank(value)) {
			DocTeamTarget docTeamTarget = new DocTeamTarget();
			docTeamTarget.setUserList(new ArrayList<DocUser>());
			return docTeamTarget;
		}
		return My.deminion(value, DocTeamTarget.class);
	}

}
