package jp.nagoyakanet.ict.scm;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder;
import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

@ExternalDomain
public class DocAgentConverter implements DomainConverter<DocAgent, String> {

	@Override
	public String fromDomainToValue(DocAgent domain) {
		if(domain == null) {
			return "{\"userList\":[]}";
		}
		return SimpleJsonBuilder.toJson(domain);
	}

	@Override
	public DocAgent fromValueToDomain(String value) {
		if(StringUtils.isBlank(value)) {
			DocAgent docAgent = new DocAgent();
			docAgent.setUserList(new ArrayList<DocUser>());
			return docAgent;
		}
		return My.deminion(value, DocAgent.class);
	}

}
