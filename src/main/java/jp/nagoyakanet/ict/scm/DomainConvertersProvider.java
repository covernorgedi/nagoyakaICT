package jp.nagoyakanet.ict.scm;

import org.seasar.doma.DomainConverters;

@DomainConverters({
	jp.nagoyakanet.ict.scm.DocOfficeMemberOfConverter.class,
	jp.nagoyakanet.ict.scm.DocTeamMemberOfConverter.class,
	jp.nagoyakanet.ict.scm.DocHasOccupationConverter.class,
	jp.nagoyakanet.ict.scm.DocAgentConverter.class,
	jp.nagoyakanet.ict.scm.DocTeamTargetConverter.class
})
public class DomainConvertersProvider {

}
