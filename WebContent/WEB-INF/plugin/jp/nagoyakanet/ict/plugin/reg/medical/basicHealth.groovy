package jp.nagoyakanet.ict.plugin.reg.medical

import java.text.SimpleDateFormat
import java.util.Map

import jp.nagoyakanet.ict.dao.DocMinionDao
import jp.nagoyakanet.ict.plugin.AbstractDocumentPage
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

class BasicHealth extends AbstractRegistryPage {

	private static final Log logger = LogFactory.getLog(BasicHealth.class)

	CreateAction createAction
	ConsumeAction consumeAction
	CreativeWork creativeWork

	List<String> mediCareType // 医療介護区分
	List<String> mediCareStatus // 医療介護状態
	String remarks // 備考
	String height // 身長
	String weight // 体重
	String bloodAbo // 血液型ABO
	String bloodRh // 血液型Rh
	String bloodRemarks // 血液型備考
	String livingTogether // 同居区分
	String hasParking // 駐車場有無
	String hasUneven // 段差の有無
	String hasHandrail // 手摺の有無
	String hasHeating // 暖房の有無
	String hasCooling // 冷房の有無
	String sunlightCondition // 日当たり状況
	String houseType // 住居種別
	String floorNumber // 居室階数
	List<String> publicExpenseType // 公費情報
	String otherPublicExpense // 公費情報その他
	List<String> sourceIncomeType // 収入源
	String otherSourceIncome // 収入源その他
	String economicCondition // 経済状況
	String caretakerType // 介護者
	List<String> characterType // 性格
	String otherCharacter // 性格その他
	List<String> interestKind // 趣味
	String otherInterest // 趣味その他
	String smokingType // 喫煙
	String drinkType // 飲酒
	String glassesType // 眼鏡
	String contactLensType // コンタクト
	String hearingAid // 補聴器
	String walkingFrame // 歩行器
	String walkingStick // 杖

	@Override
	String[] getMiddleMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String[] getMinorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"基本個人情報"
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
		mediCareType = []
		mediCareStatus = []
		remarks = ""
		height = ""
		weight = ""
		bloodAbo = "U"
		bloodRh = "U"
		bloodRemarks = ""
		livingTogether = "U"
		hasParking = "U"
		hasUneven = "U"
		hasHandrail = "U"
		hasHeating = "U"
		hasCooling = "U"
		sunlightCondition = "U"
		houseType = "U"
		floorNumber = ""
		publicExpenseType = []
		otherPublicExpense = ""
		sourceIncomeType = []
		otherSourceIncome = ""
		economicCondition = "U"
		caretakerType = "U"
		characterType = []
		otherCharacter = ""
		interestKind = []
		otherInterest = ""
		smokingType = "U"
		drinkType = "U"
		glassesType = "U"
		contactLensType = "U"
		hearingAid = "U"
		walkingFrame = "U"
		walkingStick = "U"
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

		BasicHealth basicHealth01 = new BasicHealth()
		basicHealth01.clear(docUsers.get("武田　拓夫"), docOffices.get("山中病院"),
				docTeams.get("新川　光彦さんのケアチーム"), docUsers.get("新川　光彦"))
		Date createdAt01 = sdfYMDHMS.parse("2017-12-20 14:55:16")
		basicHealth01.remarks = "備考備考"
		basicHealth01.economicCondition = "B"
		basicHealth01.interestKind = [ "flowers", "karaoke" ]
		DocMinion dscdt01 = new DocMinion("reg.medical.basicHealth", "mnn", basicHealth01,
			docUsers.get("武田　拓夫"), docOffices.get("山中病院"), docTeams.get("新川　光彦さんのケアチーム"), null, null, null)
		docMinionDao.insert(dscdt01)

		return true;
	}

}
