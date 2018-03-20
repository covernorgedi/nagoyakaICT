package jp.nagoyakanet.ict.plugin.doc.doctor

import java.util.Arrays

import org.kyojo.schemaOrg.m3n3.core.Clazz.PostalAddress
import org.kyojo.schemaOrg.m3n3.core.impl.ADDRESS
import org.kyojo.schemaOrg.m3n3.core.impl.ADDRESS_LOCALITY
import org.kyojo.schemaOrg.m3n3.core.impl.ADDRESS_REGION
import org.kyojo.schemaOrg.m3n3.core.impl.POSTAL_ADDRESS
import org.kyojo.schemaOrg.m3n3.core.impl.POSTAL_CODE
import org.kyojo.schemaOrg.m3n3.core.impl.STREET_ADDRESS

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
import java.text.SimpleDateFormat
import java.util.Map

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
import org.kyojo.schemaOrg.m3n3.core.Clazz.Organization
import org.kyojo.schemaOrg.m3n3.core.Clazz.Person
import org.kyojo.schemaOrg.m3n3.core.Clazz.ReceiveAction
import org.kyojo.schemaOrg.m3n3.core.Clazz.ReviewAction
import org.kyojo.schemaOrg.m3n3.core.Clazz.SendAction
import org.kyojo.schemaOrg.m3n3.core.Container.Address
import org.kyojo.schemaOrg.m3n3.core.Container.Agent
import org.kyojo.schemaOrg.m3n3.core.impl.AGENT
import org.kyojo.schemaOrg.m3n3.core.impl.CONSUME_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.CREATE_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.CREATIVE_WORK
import org.kyojo.schemaOrg.m3n3.core.impl.CREATOR
import org.kyojo.schemaOrg.m3n3.core.impl.DATE_CREATED
import org.kyojo.schemaOrg.m3n3.core.impl.DATE_MODIFIED
import org.kyojo.schemaOrg.m3n3.core.impl.NAME
import org.kyojo.schemaOrg.m3n3.core.impl.ORGANIZATION
import org.kyojo.schemaOrg.m3n3.core.impl.PERSON
import org.kyojo.schemaOrg.m3n3.core.impl.RECEIVE_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.RECIPIENT
import org.kyojo.schemaOrg.m3n3.core.impl.REVIEW_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.SENDER
import org.kyojo.schemaOrg.m3n3.core.impl.SEND_ACTION

class MedicalReferralLetter extends AbstractDocumentPage {

	private static final Log logger = LogFactory.getLog(MedicalReferralLetter.class)

	CreateAction createAction
	SendAction sendAction
	ReceiveAction receiveAction
	ConsumeAction consumeAction
	CreativeWork creativeWork

	String diseaseName // 病名
	String introductionAim // 紹介目的
	String pastMedicalHistory // 既往歴及び家族歴
	String inspectionResult // 症状経過及び検査結果
	String medicalTreatmentHistory // 治療履歴
	String currentPrescription // 現在の処方
	String remarks // 備考

	@Override
	String[] getMiddleMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String[] getMinorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"診療情報提供書"
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
		sendAction = new SEND_ACTION()
		sendAction.agent = new AGENT()
		sendAction.agent.organizationList = [ signInOffice ]
		sendAction.agent.personList = [ signInUser ]
		receiveAction = new RECEIVE_ACTION()
		receiveAction.agent = new AGENT()
		receiveAction.agent.organizationList = [ new ORGANIZATION() ]
		receiveAction.agent.personList = [ new PERSON() ]
		sendAction.recipient = new RECIPIENT()
		sendAction.recipient.organizationList = receiveAction.agent.organizationList
		sendAction.recipient.personList = receiveAction.agent.personList
		receiveAction.sender = new SENDER()
		receiveAction.sender.organizationList = sendAction.agent.organizationList
		receiveAction.sender.personList = sendAction.agent.personList
		consumeAction = new CONSUME_ACTION()
		consumeAction.agent = new AGENT()
		consumeAction.agent.personList = [ tgtUser ]
		diseaseName = ""
		introductionAim = ""
		pastMedicalHistory = ""
		inspectionResult = ""
		medicalTreatmentHistory = ""
		currentPrescription = ""
		remarks = ""
	}

	boolean confirm(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced) {
		boolean res = super.confirm(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
		Date now = new Date()
		creativeWork.dateModified = new DATE_MODIFIED(now)
		sendAction.recipient.organizationList = receiveAction.agent.organizationList
		sendAction.recipient.personList = receiveAction.agent.personList
		receiveAction.sender.organizationList = sendAction.agent.organizationList
		receiveAction.sender.personList = sendAction.agent.personList

		return res
	}

	static boolean insertSamples(Map<String, User> users, Map<String, Office> offices, Map<String, Team> teams,
			Map<String, DocUser> docUsers, Map<String, DocOffice> docOffices, Map<String, DocTeam> docTeams,
			DocMinionDao docMinionDao) {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

		MedicalReferralLetter medicalReferralLetter01 = new MedicalReferralLetter()
		medicalReferralLetter01.clear(docUsers.get("武田　拓夫"), docOffices.get("山中病院"),
			docTeams.get("新川　光彦さんのケアチーム"), docUsers.get("新川　光彦"))
		Date createdAt01 = sdfYMDHMS.parse("2017-08-14 14:23:41")
		medicalReferralLetter01.createAction.createdAt = createdAt01
		medicalReferralLetter01.creativeWork.createdAt = createdAt01
		medicalReferralLetter01.creativeWork.dateCreated = new DATE_CREATED(createdAt01)
		medicalReferralLetter01.creativeWork.dateModified = new DATE_MODIFIED(createdAt01)
		medicalReferralLetter01.sendAction.agent.organizationList = [ docOffices.get("山中病院") ]
		medicalReferralLetter01.sendAction.agent.personList = [ docUsers.get("武田　拓夫") ]
		Agent receiveAgent01 = new AGENT()
		Organization receiveOrganization01 = new ORGANIZATION()
		receiveOrganization01.setName(new NAME("スマイル病院"))
		PostalAddress receivePostalAddress01 = new POSTAL_ADDRESS()
		receivePostalAddress01.setPostalCode(new POSTAL_CODE("466-8555"))
		receivePostalAddress01.setAddressRegion(new ADDRESS_REGION("愛知県"))
		receivePostalAddress01.setAddressLocality(new ADDRESS_LOCALITY("名古屋市Ａ区"))
		receivePostalAddress01.setStreetAddress(new STREET_ADDRESS("Ｂ町"))
		// Address receiveAddress01 = new ADDRESS()
		// receiveAddress01.setPostalAddressList([ receivePostalAddress01 ])
		Address receiveAddress01 = new ADDRESS("名古屋市Ａ区Ｂ町")
		receiveAddress01.setPostalAddressList([ receivePostalAddress01 ])
		receiveOrganization01.setAddress(receiveAddress01)
		receiveAgent01.organizationList = [ receiveOrganization01 ]
		Person receivePerson01 = new PERSON()
		receivePerson01.setName(new NAME("山田　武"))
		receiveAgent01.personList = [ receivePerson01 ]
		medicalReferralLetter01.receiveAction.agent = receiveAgent01
		medicalReferralLetter01.sendAction.recipient.organizationList = medicalReferralLetter01.receiveAction.agent.organizationList
		medicalReferralLetter01.sendAction.recipient.personList = medicalReferralLetter01.receiveAction.agent.personList
		medicalReferralLetter01.receiveAction.sender.organizationList = medicalReferralLetter01.sendAction.agent.organizationList
		medicalReferralLetter01.receiveAction.sender.personList = medicalReferralLetter01.sendAction.agent.personList
		medicalReferralLetter01.diseaseName = "肺炎"
		DocMinion dscdt01 = new DocMinion("doc.doctor.medicalReferralLetter", "mnn", medicalReferralLetter01,
			docUsers.get("武田　拓夫"), docOffices.get("山中病院"), docTeams.get("新川　光彦さんのケアチーム"), null, null, null)
		docMinionDao.insert(dscdt01)

		return true;
	}

}
