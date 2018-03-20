package jp.nagoyakanet.ict.plugin.reg.basic

import jp.nagoyakanet.ict.plugin.AbstractRegistryPage
import jp.nagoyakanet.ict.plugin.cmn.LangJa
import jp.nagoyakanet.ict.scm.DocHasOccupation
import jp.nagoyakanet.ict.scm.DocOccupation
import jp.nagoyakanet.ict.scm.DocOfficeMemberOf
import jp.nagoyakanet.ict.scm.DocTeamMemberOf
import jp.nagoyakanet.ict.scm.DocTeamTarget
import jp.nagoyakanet.ict.scm.DocUser
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.kyojo.core.Cache
import org.kyojo.core.Constants
import org.kyojo.core.GlobalData
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.schemaOrg.m3n3.core.Clazz.JoinAction
import org.kyojo.schemaOrg.m3n3.core.Clazz.LeaveAction
import org.kyojo.schemaOrg.m3n3.core.Container.Address
import org.kyojo.schemaOrg.m3n3.core.Container.BirthDate
import org.kyojo.schemaOrg.m3n3.core.Container.Email
import org.kyojo.schemaOrg.m3n3.core.Container.Gender
import org.kyojo.schemaOrg.m3n3.core.Container.Image
import org.kyojo.schemaOrg.m3n3.core.Container.Name
import org.kyojo.schemaOrg.m3n3.core.Container.NameRuby
import org.kyojo.schemaOrg.m3n3.core.Container.Telephone
import org.kyojo.schemaOrg.m3n3.core.impl.ADDRESS
import org.kyojo.schemaOrg.m3n3.core.impl.BIRTH_DATE
import org.kyojo.schemaOrg.m3n3.core.impl.EMAIL
import org.kyojo.schemaOrg.m3n3.core.impl.GENDER
import org.kyojo.schemaOrg.m3n3.core.impl.IMAGE
import org.kyojo.schemaOrg.m3n3.core.impl.JOIN_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.LEAVE_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.NAME
import org.kyojo.schemaOrg.m3n3.core.impl.NAME_RUBY
import org.kyojo.schemaOrg.m3n3.core.impl.TELEPHONE

class Team extends AbstractRegistryPage {

	private static final Log logger = LogFactory.getLog(Team.class)

	String code
	Name name
	NameRuby nameRuby
	DocTeamTarget teamTarget
	DocOfficeMemberOf officeMemberOf
	Telephone telephone
	Email email
	String remarks
	JoinAction joinAction
	LeaveAction leaveAction

	@Override
	String[] getMiddleMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String[] getMinorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"チーム"
	}

	@Override
	void clear(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		super.clear(args, gbd, ssd, rqd, rpd)
		clear()
	}

	void clear() {
		seq = null
		code = ""
		name = new NAME()
		nameRuby = new NAME_RUBY()
		teamTarget = new DocTeamTarget()
		officeMemberOf = new DocOfficeMemberOf()
		telephone = new TELEPHONE()
		email = new EMAIL()
		joinAction = new JOIN_ACTION()
		leaveAction = new LEAVE_ACTION()
	}

}
