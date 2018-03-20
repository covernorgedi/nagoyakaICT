package jp.nagoyakanet.ict.plugin.doc.doctor

import java.text.SimpleDateFormat
import java.util.Map

import jp.nagoyakanet.ict.dao.DocMinionDao
import jp.nagoyakanet.ict.plugin.AbstractDocumentPage
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
import org.kyojo.minion.My
import org.kyojo.schemaOrg.m3n3.core.Clazz.ConsumeAction
import org.kyojo.schemaOrg.m3n3.core.Clazz.CreateAction
import org.kyojo.schemaOrg.m3n3.core.Clazz.CreativeWork
import org.kyojo.schemaOrg.m3n3.core.Clazz.ReviewAction
import org.kyojo.schemaOrg.m3n3.core.impl.AGENT
import org.kyojo.schemaOrg.m3n3.core.impl.CONSUME_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.CREATE_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.CREATIVE_WORK
import org.kyojo.schemaOrg.m3n3.core.impl.CREATOR
import org.kyojo.schemaOrg.m3n3.core.impl.DATE_CREATED
import org.kyojo.schemaOrg.m3n3.core.impl.DATE_MODIFIED
import org.kyojo.schemaOrg.m3n3.core.impl.DATE_PUBLISHED
import org.kyojo.schemaOrg.m3n3.core.impl.REVIEW_ACTION

class Prescription extends AbstractDocumentPage {

	private static final Log logger = LogFactory.getLog(Prescription.class)

	CreateAction createAction
	ConsumeAction consumeAction
	ReviewAction reviewAction
	CreativeWork creativeWork

	String publiclyFundedDefrayerNo1 // 第一公費負担者番号
	String publiclyFundedRecipientNo1 // 第一公費負担医療の受給者番号
	String publiclyFundedDefrayerNo2 // 第二公費負担者番号
	String publiclyFundedRecipientNo2 // 第二公費負担医療の受給者番号
	String insuranceNo // 保険者番号
	String insuredCode // 被保険者証・被保険者手帳の記号
	String insuredNo // 被保険者証・被保険者手帳の番号
	String dependent // 区分 0-なし 1-被保険者 2-被扶養者
	List<DocFormula> formulaList // 処方
	String remarks // 備考

	@Override
	String[] getMiddleMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String[] getMinorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"処方箋"
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
		reviewAction = new REVIEW_ACTION()
		consumeAction = new CONSUME_ACTION()
		consumeAction.agent = new AGENT()
		consumeAction.agent.personList = [ tgtUser ]
		publiclyFundedDefrayerNo1 = ""
		publiclyFundedRecipientNo1 = ""
		publiclyFundedDefrayerNo2 = ""
		publiclyFundedRecipientNo2 = ""
		insuranceNo = ""
		insuredCode = ""
		insuredNo = ""
		dependent = "0"
		formulaList = []
		remarks = ""
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

		Prescription prescription01 = new Prescription()
		prescription01.clear(docUsers.get("武田　拓夫"), docOffices.get("山中病院"),
			docTeams.get("新川　光彦さんのケアチーム"), docUsers.get("新川　光彦"))
		Date createdAt01 = sdfYMDHMS.parse("2017-08-08 12:31:52")
		prescription01.createAction.createdAt = createdAt01
		prescription01.creativeWork.createdAt = createdAt01
		prescription01.creativeWork.dateCreated = new DATE_CREATED(createdAt01)
		prescription01.creativeWork.dateModified = new DATE_MODIFIED(createdAt01)
		prescription01.creativeWork.datePublished = new DATE_PUBLISHED(createdAt01)
		prescription01.publiclyFundedDefrayerNo1 = "01234567"
		prescription01.publiclyFundedRecipientNo1 = ""
		prescription01.insuranceNo = "123456"
		prescription01.insuredCode = "123"
		prescription01.insuredNo = "4567"
		prescription01.dependent = "1"
		prescription01.formulaList = []
		DocFormula formula0101 = new DocFormula()
		formula0101.canGeneric = false
		formula0101.entry = "ケイツーカプセル 5mg 2CP"
		prescription01.formulaList.add(formula0101)
		prescription01.formulaList.add(new DocFormula())
		prescription01.formulaList.add(new DocFormula())
		prescription01.formulaList.add(new DocFormula())
		prescription01.reviewAction.agent = new AGENT()
		prescription01.reviewAction.agent.organizationList = [ docOffices.get("寺島調剤薬局") ]
		prescription01.reviewAction.agent.personList = [ docUsers.get("坪井　美希") ]
		DocMinion dscdt01 = new DocMinion("doc.doctor.prescription", "mnn", prescription01,
			docUsers.get("武田　拓夫"), docOffices.get("山中病院"), docTeams.get("新川　光彦さんのケアチーム"), null, null, null)
		docMinionDao.insert(dscdt01)

		Prescription prescription02 = new Prescription();
		prescription02.clear(docUsers.get("武田　拓夫"), docOffices.get("山中病院"),
			docTeams.get("新川　光彦さんのケアチーム"), docUsers.get("新川　光彦"))
		Date createdAt02 = sdfYMDHMS.parse("2017-08-06 09:01:11")
		prescription02.createAction.createdAt = createdAt02
		prescription02.creativeWork.createdAt = createdAt02
		prescription02.creativeWork.dateCreated = new DATE_CREATED(createdAt02)
		prescription02.creativeWork.dateModified = new DATE_MODIFIED(createdAt02)
		prescription02.creativeWork.datePublished = new DATE_PUBLISHED(createdAt02)
		prescription02.publiclyFundedDefrayerNo1 = "01234567"
		prescription02.publiclyFundedRecipientNo1 = ""
		prescription02.insuranceNo = "123456"
		prescription02.insuredCode = "123"
		prescription02.insuredNo = "4567"
		prescription02.dependent = "1"
		prescription02.formulaList = []
		DocFormula formula0201 = new DocFormula()
		formula0201.canGeneric = false
		formula0201.entry = "ダーゼン 10mg錠 3錠"
		prescription02.formulaList.add(formula0201)
		prescription02.formulaList.add(new DocFormula())
		prescription02.formulaList.add(new DocFormula())
		prescription02.formulaList.add(new DocFormula())
		DocMinion dscdt02 = new DocMinion("doc.doctor.prescription", "mnn", prescription02,
			docUsers.get("武田　拓夫"), docOffices.get("山中病院"), docTeams.get("新川　光彦さんのケアチーム"), null, null, null)
		docMinionDao.insert(dscdt02)

		return true
	}

}
