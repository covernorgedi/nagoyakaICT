package jp.nagoyakanet.ict.plugin.auth.master

import jp.nagoyakanet.ict.dao.DocMinionDao
import jp.nagoyakanet.ict.plugin.AbstractAuthorityPage
import jp.nagoyakanet.ict.scm.DocOccupation
import jp.nagoyakanet.ict.scm.DocMinion
import jp.nagoyakanet.ict.scm.DocOffice
import jp.nagoyakanet.ict.scm.DocOfficeMemberOf
import jp.nagoyakanet.ict.scm.DocTeamMemberOf
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
import org.kyojo.schemaOrg.m3n3.core.Container.Identifier
import org.kyojo.schemaOrg.m3n3.core.Container.Name

class CodeAndLang extends AbstractAuthorityPage {

	private static final Log logger = LogFactory.getLog(CodeAndLang.class)

	String act
	String type
	String code
	Integer sort
	String en
	String ja
	Identifier identifier;
	String remarks
	java.util.Date createdAt
	Long createdBy
	java.util.Date updatedAt
	Long updatedBy
	java.util.Date expiredAt
	Long expiredBy
	Name name;

	@Override
	String[] getMiddleMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String[] getMinorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"事業所"
	}

	@Override
	void clear(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		clear()
	}

	void clear() {
		seq = null
		act = ""
		type = ""
		code = ""
		sort = 0
		en = ""
		ja = ""
		identifier = null
		remarks = ""
		createdAt = null
		createdBy = 0
		updatedAt = null
		updatedBy = 0
		expiredAt = null
		expiredBy = 0
		name = null
	}

}
