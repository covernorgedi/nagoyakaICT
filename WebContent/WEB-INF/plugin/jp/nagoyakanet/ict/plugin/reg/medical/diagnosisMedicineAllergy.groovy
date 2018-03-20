package jp.nagoyakanet.ict.plugin.reg.medical

import java.text.SimpleDateFormat
import java.util.Map

import jp.nagoyakanet.ict.dao.DocMinionDao
import jp.nagoyakanet.ict.plugin.AbstractRegistryPage
import jp.nagoyakanet.ict.plugin.reg.basic.Office
import jp.nagoyakanet.ict.plugin.reg.basic.Team
import jp.nagoyakanet.ict.plugin.reg.basic.User
import jp.nagoyakanet.ict.scm.DocFormula
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

class DiagnosisMedicineAllergy extends AbstractRegistryPage {

	private static final Log logger = LogFactory.getLog(DiagnosisMedicineAllergy.class)

	CreateAction createAction
	ConsumeAction consumeAction
	CreativeWork creativeWork

	// 診断
	String diagnosisName // 診断名
	String diagnosisDesc // 診断内容
	String medicalHistory // 既往歴

	// 重要事項
	String emergentChanges // 病態変化時の対処
	String emergencyDirections // 指示したこと
	String emergencyNotes // 留意すべきこと
	String hasArrhythmia // 不整脈の有無

	// 感染症
	String hasInfectiousDisease // 感染症の有無
	String infectiousDiseaseKindDesc // 種別
	String infectiousDiseaseIssues // 伝達事項など

	// アレルギー
	List<String> hasAllergie // アレルギーの有無
	String allergieKindDesc // 種別
	String allergieIssues // 伝達事項など

	// 薬
	List<DocFormula> necessaryMedicineList

	String remarks

	@Override
	String[] getMiddleMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String[] getMinorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"診断・薬・アレルギー"
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
		Date now = new Date()
		creativeWork.dateModified = new DATE_MODIFIED(now)

		return res
	}

	static boolean insertSamples(Map<String, User> users, Map<String, Office> offices, Map<String, Team> teams,
			Map<String, DocUser> docUsers, Map<String, DocOffice> docOffices, Map<String, DocTeam> docTeams,
			DocMinionDao docMinionDao) {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

		DiagnosisMedicineAllergy diagnosisMedicineAllergy01 = new DiagnosisMedicineAllergy()
		diagnosisMedicineAllergy01.clear(docUsers.get("武田　拓夫"), docOffices.get("山中病院"),
				docTeams.get("新川　光彦さんのケアチーム"), docUsers.get("新川　光彦"))
		Date createdAt01 = sdfYMDHMS.parse("2017-05-02 12:34:27")

		DocMinion dscdt01 = new DocMinion("reg.medical.diagnosisMedicineAllergy", "mnn", diagnosisMedicineAllergy01,
			docUsers.get("武田　拓夫"), docOffices.get("山中病院"), docTeams.get("新川　光彦さんのケアチーム"), null, null, null)
		docMinionDao.insert(dscdt01)

		return true;
	}

}
