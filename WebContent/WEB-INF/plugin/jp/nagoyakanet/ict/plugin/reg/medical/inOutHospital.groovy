package jp.nagoyakanet.ict.plugin.reg.medical

import java.text.SimpleDateFormat
import java.util.List
import java.util.Map

import jp.nagoyakanet.ict.dao.DocMinionDao
import jp.nagoyakanet.ict.plugin.AbstractRegistryPage
import jp.nagoyakanet.ict.plugin.reg.basic.Office
import jp.nagoyakanet.ict.plugin.reg.basic.Team
import jp.nagoyakanet.ict.plugin.reg.basic.User
import jp.nagoyakanet.ict.scm.DocHospitalized
import jp.nagoyakanet.ict.scm.DocMinion
import jp.nagoyakanet.ict.scm.DocOffice
import jp.nagoyakanet.ict.scm.DocTeam
import jp.nagoyakanet.ict.scm.DocUser
import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.lang3.StringUtils
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
import org.kyojo.schemaOrg.m3n3.core.Clazz.JoinAction
import org.kyojo.schemaOrg.m3n3.core.Clazz.LeaveAction
import org.kyojo.schemaOrg.m3n3.core.impl.AGENT
import org.kyojo.schemaOrg.m3n3.core.impl.CONSUME_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.CREATE_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.CREATIVE_WORK
import org.kyojo.schemaOrg.m3n3.core.impl.CREATOR
import org.kyojo.schemaOrg.m3n3.core.impl.DATE_CREATED
import org.kyojo.schemaOrg.m3n3.core.impl.DATE_MODIFIED
import org.kyojo.schemaOrg.m3n3.core.impl.DATE_PUBLISHED
import org.kyojo.schemaOrg.m3n3.core.impl.END_TIME
import org.kyojo.schemaOrg.m3n3.core.impl.JOIN_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.LEAVE_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.START_TIME

class InOutHospital extends AbstractRegistryPage {

	private static final Log logger = LogFactory.getLog(InOutHospital.class)

	CreateAction createAction
	ConsumeAction consumeAction
	CreativeWork creativeWork

	List<DocHospitalized> hospitalizedList

	String remarks

	@Override
	String[] getMiddleMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String[] getMinorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"入退院"
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

		hospitalizedList = []
	}

	@Override
	boolean load(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		boolean res = super.load(args, gbd, ssd, rqd, rpd)

		if(hospitalizedList != null) {
			SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd")
			SimpleDateFormat sdfHMS = new SimpleDateFormat("HH:mm:ss")

			for(DocHospitalized hospitalized : hospitalizedList) {
				if(hospitalized != null) {
					if(hospitalized.joinAction != null && hospitalized.joinAction.startTime != null) {
						Date joinActionDate = hospitalized.joinAction.startTime.getNativeValue()
						String ymdhmsStr = sdfYMDHMS.format(joinActionDate)
						hospitalized.fromDate = sdfYMD.parse(ymdhmsStr.substring(0, 10))
						hospitalized.fromTime = sdfHMS.parse(ymdhmsStr.substring(11, 19))
					}

					if(hospitalized.leaveAction != null && hospitalized.leaveAction.endTime != null) {
						Date leaveActionDate = hospitalized.leaveAction.endTime.getNativeValue()
						String ymdhmsStr = sdfYMDHMS.format(leaveActionDate)
						hospitalized.toDate = sdfYMD.parse(ymdhmsStr.substring(0, 10))
						hospitalized.toTime = sdfHMS.parse(ymdhmsStr.substring(11, 19))
					}
				}
			}
		}

		return res
	}

	@Override
	boolean confirm(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced) {
		boolean res = super.confirm(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
		Date now = new Date()
		creativeWork.dateModified = new DATE_MODIFIED(now)

		if(hospitalizedList != null) {
			SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd")
			SimpleDateFormat sdfHMS = new SimpleDateFormat("HH:mm:ss")

			for(DocHospitalized hospitalized : hospitalizedList) {
				if(hospitalized != null) {
					if(hospitalized.fromDate != null || hospitalized.fromTime != null) {
						hospitalized.joinAction = new JOIN_ACTION()
						String ymdStr = sdfYMD.format(hospitalized.fromDate ?: now)
						String hmsStr = sdfHMS.format(hospitalized.fromTime ?: now)
						Date joinActionDate = sdfYMDHMS.parse(ymdStr + " " + hmsStr)
						hospitalized.joinAction.startTime = new START_TIME(joinActionDate)
						hospitalized.joinAction.endTime = new END_TIME(joinActionDate)
					}

					if(hospitalized.toDate != null || hospitalized.toTime != null) {
						hospitalized.leaveAction = new LEAVE_ACTION()
						String ymdStr = sdfYMD.format(hospitalized.toDate ?: now)
						String hmsStr = sdfHMS.format(hospitalized.toTime ?: now)
						Date leaveActionDate = sdfYMDHMS.parse(ymdStr + " " + hmsStr)
						hospitalized.leaveAction.startTime = new START_TIME(leaveActionDate)
						hospitalized.leaveAction.endTime = new END_TIME(leaveActionDate)
					}
				}
			}
		}

		return res
	}

	static boolean insertSamples(Map<String, User> users, Map<String, Office> offices, Map<String, Team> teams,
			Map<String, DocUser> docUsers, Map<String, DocOffice> docOffices, Map<String, DocTeam> docTeams,
			DocMinionDao docMinionDao) {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

		InOutHospital inOutHospital01 = new InOutHospital()
		inOutHospital01.clear(docUsers.get("武田　拓夫"), docOffices.get("山中病院"),
				docTeams.get("新川　光彦さんのケアチーム"), docUsers.get("新川　光彦"))
		Date createdAt01 = sdfYMDHMS.parse("2017-04-24 21:56:29")
		DocHospitalized hospitalized0101 = new DocHospitalized()
		hospitalized0101.joinAction = new JOIN_ACTION()
		Date joinActionDate0101 = sdfYMDHMS.parse("2015-01-10 12:00:00")
		hospitalized0101.joinAction.startTime = new START_TIME(joinActionDate0101)
		hospitalized0101.joinAction.endTime = new END_TIME(joinActionDate0101)
		hospitalized0101.leaveAction = new LEAVE_ACTION()
		Date leaveActionDate0101 = sdfYMDHMS.parse("2015-01-18 12:00:00")
		hospitalized0101.leaveAction.startTime = new START_TIME(leaveActionDate0101)
		hospitalized0101.leaveAction.endTime = new END_TIME(leaveActionDate0101)
		hospitalized0101.office = docOffices.get("愛田病院")
		hospitalized0101.medicalSpecialtyList = [ "geriatrics" ]
		hospitalized0101.userList = [ docUsers.get("安田　恭子") ]
		DocHospitalized hospitalized0102 = new DocHospitalized()
		hospitalized0102.joinAction = new JOIN_ACTION()
		Date joinActionDate0102 = sdfYMDHMS.parse("2016-07-05 12:00:00")
		hospitalized0102.joinAction.startTime = new START_TIME(joinActionDate0102)
		hospitalized0102.joinAction.endTime = new END_TIME(joinActionDate0102)
		hospitalized0102.leaveAction = new LEAVE_ACTION()
		Date leaveActionDate0102 = sdfYMDHMS.parse("2016-08-20 12:00:00")
		hospitalized0102.leaveAction.startTime = new START_TIME(leaveActionDate0102)
		hospitalized0102.leaveAction.endTime = new END_TIME(leaveActionDate0102)
		hospitalized0102.office = hospitalized0101.office
		hospitalized0102.medicalSpecialtyList = hospitalized0101.medicalSpecialtyList
		hospitalized0102.userList = hospitalized0101.userList
		DocHospitalized hospitalized0103 = new DocHospitalized()
		hospitalized0103.joinAction = new JOIN_ACTION()
		Date joinActionDate0103 = sdfYMDHMS.parse("2017-09-01 12:00:00")
		hospitalized0103.joinAction.startTime = new START_TIME(joinActionDate0103)
		hospitalized0103.joinAction.endTime = new END_TIME(joinActionDate0103)
		hospitalized0103.leaveAction = new LEAVE_ACTION()
		Date leaveActionDate0103 = sdfYMDHMS.parse("2016-08-20 12:00:00")
		hospitalized0103.leaveAction.startTime = new START_TIME(leaveActionDate0103)
		hospitalized0103.leaveAction.endTime = new END_TIME(leaveActionDate0103)
		hospitalized0103.office = hospitalized0101.office
		hospitalized0103.medicalSpecialtyList = hospitalized0101.medicalSpecialtyList
		hospitalized0103.userList = hospitalized0101.userList
		inOutHospital01.hospitalizedList = [ hospitalized0101, hospitalized0102, hospitalized0103 ]

		DocMinion dscdt01 = new DocMinion("reg.medical.inOutHospital", "mnn", inOutHospital01,
			docUsers.get("武田　拓夫"), docOffices.get("山中病院"), docTeams.get("新川　光彦さんのケアチーム"), null, null, null)
		docMinionDao.insert(dscdt01)

		return true;
	}

}
