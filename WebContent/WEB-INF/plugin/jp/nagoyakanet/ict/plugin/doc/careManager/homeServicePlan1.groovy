package jp.nagoyakanet.ict.plugin.doc.careManager

import java.text.SimpleDateFormat
import java.util.Map

import jp.nagoyakanet.ict.dao.DocMinionDao
import jp.nagoyakanet.ict.plugin.AbstractDocumentPage
import jp.nagoyakanet.ict.plugin.reg.basic.Office
import jp.nagoyakanet.ict.plugin.reg.basic.Team
import jp.nagoyakanet.ict.plugin.reg.basic.User
import jp.nagoyakanet.ict.scm.DocMinion
import jp.nagoyakanet.ict.scm.DocOffice
import jp.nagoyakanet.ict.scm.DocTeam
import jp.nagoyakanet.ict.scm.DocUser
import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.kyojo.core.Cache
import org.kyojo.core.Constants
import org.kyojo.core.GlobalData
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.schemaOrg.m3n3.core.Clazz.ConsumeAction
import org.kyojo.schemaOrg.m3n3.core.Clazz.CreateAction
import org.kyojo.schemaOrg.m3n3.core.Clazz.CreativeWork
import org.kyojo.schemaOrg.m3n3.core.impl.AGENT
import org.kyojo.schemaOrg.m3n3.core.impl.CONSUME_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.CREATE_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.CREATIVE_WORK
import org.kyojo.schemaOrg.m3n3.core.impl.CREATOR
import org.kyojo.schemaOrg.m3n3.core.impl.DATE_CREATED
import org.kyojo.schemaOrg.m3n3.core.impl.DATE_MODIFIED
import org.kyojo.schemaOrg.m3n3.core.impl.DATE_PUBLISHED

class HomeServicePlan1 extends AbstractDocumentPage {

	private static final Log logger = LogFactory.getLog(HomeServicePlan1.class)

	CreateAction createAction
	ConsumeAction consumeAction
	CreativeWork creativeWork

	List<String> continuousCategory // 認定済み・申請中
	String certificationCategory // 認定済み・申請中
	Date certifiedDate // 認定日
	Date certifiedFrom // 認定の有効期間開始日
	Date certifiedTo // 認定の有効期間終了日
	String careLevel // 要介護状態区分
	String userIntention // 利用者及び家族の生活に対する意向
	String suggestionAndServiceType // 介護認定審査会の意見及びサービスの種類の指定
	String assistancePolicy // 総合的な援助の方針
	List<String> calculationReason // 生活援助中心型の算定理由
	String calculationReasonOther // 生活援助中心型の算定理由その他

	String remarks

	@Override
	String[] getMiddleMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String[] getMinorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"居宅サービス計画書（1）"
	}

	String getPdfFileName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		Date now = new Date()
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyyMMdd_HHmm")
		String cdnm = ""
		if(consumeAction != null && consumeAction.agent != null
			&& consumeAction.agent.personList != null && consumeAction.agent.personList.size() > 0 && consumeAction.agent.personList.get(0) != null) {
			cdnm = consumeAction.agent.personList.get(0).getNativeValue()
		}
		return (getName(gbd, ssd, rqd, rpd) + "_" + cdnm + "_" + sdfYMDHMS.format(now) + ".pdf").replaceAll("[ 　]", "")
	}

	@Override
	void clear(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		super.clear(args, gbd, ssd, rqd, rpd)
		DocUser signInUser = getSignInUser(gbd, ssd)
		DocOffice signInOffice = getSignInOffice(gbd, ssd)
		DocTeam signInTeam = getSignInTeam(gbd, ssd)
		DocUser tgtUser = getTargetUser(gbd, ssd, signInTeam)
		clear(signInUser, signInOffice, signInTeam, tgtUser)
	}

	void clear(DocUser signInUser, DocOffice signInOffice, DocTeam signInTeam, DocUser tgtUser) {
		Date now = new Date()
		createAction = new CREATE_ACTION()
		createAction.createdAt = now
		createAction.agent = new AGENT()
		createAction.agent.organizationList = [ signInOffice ]
		createAction.agent.personList = [ signInUser ]
		creativeWork = new CREATIVE_WORK()
		creativeWork.createdAt = now
		creativeWork.creator = new CREATOR(signInUser)
		creativeWork.dateCreated = new DATE_CREATED(now)
		creativeWork.dateModified = new DATE_MODIFIED(now)
		consumeAction = new CONSUME_ACTION()
		consumeAction.agent = new AGENT()
		consumeAction.agent.personList = [ tgtUser ]
	}

	boolean confirm(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced) {
		boolean res = super.confirm(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
		// Date now = new Date()
		// creativeWork.dateModified = new DATE_MODIFIED(now)

		return res
	}

	static boolean insertSamples(Map<String, User> users, Map<String, Office> offices, Map<String, Team> teams,
			Map<String, DocUser> docUsers, Map<String, DocOffice> docOffices, Map<String, DocTeam> docTeams,
			DocMinionDao docMinionDao) {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

		HomeServicePlan1 homeServicePlan101 = new HomeServicePlan1()
		homeServicePlan101.clear(docUsers.get("武田　拓夫"), docOffices.get("山中病院"),
				docTeams.get("新川　光彦さんのケアチーム"), docUsers.get("新川　光彦"))
		Date createdAt01 = sdfYMDHMS.parse("2017-12-15 19:24:21")

		DocMinion dscdt01 = new DocMinion("doc.careManager.homeServicePlan1", "mnn", homeServicePlan101,
			docUsers.get("武田　拓夫"), docOffices.get("山中病院"), docTeams.get("新川　光彦さんのケアチーム"), null, null, null)
		docMinionDao.insert(dscdt01)

		return true;
	}

}
