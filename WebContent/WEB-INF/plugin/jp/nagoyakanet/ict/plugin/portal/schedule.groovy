package jp.nagoyakanet.ict.plugin.portal

import jp.nagoyakanet.ict.dao.DocMinionDao
import jp.nagoyakanet.ict.dao.DocOfficeDao
import jp.nagoyakanet.ict.dao.DocTeamDao
import jp.nagoyakanet.ict.dao.DocUserDao
import jp.nagoyakanet.ict.dao.SchScheduleItemDao
import jp.nagoyakanet.ict.plugin.SignIn
import jp.nagoyakanet.ict.plugin.reg.basic.Office
import jp.nagoyakanet.ict.plugin.reg.basic.Team
import jp.nagoyakanet.ict.plugin.reg.basic.User
import jp.nagoyakanet.ict.scm.DocAgent
import jp.nagoyakanet.ict.scm.DocOffice
import jp.nagoyakanet.ict.scm.DocTeam
import jp.nagoyakanet.ict.scm.DocUser
import jp.nagoyakanet.ict.scm.SchScheduleItem

import java.text.SimpleDateFormat
import java.util.Date
import java.util.List
import java.util.Map

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.core.Time14
import org.kyojo.minion.My
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder
import org.kyojo.schemaOrg.m3n3.core.Clazz
import org.kyojo.schemaOrg.m3n3.core.Clazz.Event
import org.kyojo.schemaOrg.m3n3.core.Container.About
import org.kyojo.schemaOrg.m3n3.core.Container.Actor
import org.kyojo.schemaOrg.m3n3.core.Container.AdditionalType
import org.kyojo.schemaOrg.m3n3.core.Container.AggregateRating
import org.kyojo.schemaOrg.m3n3.core.Container.AlternateName
import org.kyojo.schemaOrg.m3n3.core.Container.Attendee
import org.kyojo.schemaOrg.m3n3.core.Container.Audience
import org.kyojo.schemaOrg.m3n3.core.Container.Composer
import org.kyojo.schemaOrg.m3n3.core.Container.Contributor
import org.kyojo.schemaOrg.m3n3.core.Container.Description
import org.kyojo.schemaOrg.m3n3.core.Container.Director
import org.kyojo.schemaOrg.m3n3.core.Container.DisambiguatingDescription
import org.kyojo.schemaOrg.m3n3.core.Container.DoorTime
import org.kyojo.schemaOrg.m3n3.core.Container.Duration
import org.kyojo.schemaOrg.m3n3.core.Container.EndDate
import org.kyojo.schemaOrg.m3n3.core.Container.EventStatus
import org.kyojo.schemaOrg.m3n3.core.Container.Funder
import org.kyojo.schemaOrg.m3n3.core.Container.Identifier
import org.kyojo.schemaOrg.m3n3.core.Container.Image
import org.kyojo.schemaOrg.m3n3.core.Container.InLanguage
import org.kyojo.schemaOrg.m3n3.core.Container.IsAccessibleForFree
import org.kyojo.schemaOrg.m3n3.core.Container.Location
import org.kyojo.schemaOrg.m3n3.core.Container.MainEntityOfPage
import org.kyojo.schemaOrg.m3n3.core.Container.MaximumAttendeeCapacity
import org.kyojo.schemaOrg.m3n3.core.Container.Name
import org.kyojo.schemaOrg.m3n3.core.Container.NameRuby
import org.kyojo.schemaOrg.m3n3.core.Container.Offers
import org.kyojo.schemaOrg.m3n3.core.Container.Organizer
import org.kyojo.schemaOrg.m3n3.core.Container.Performer
import org.kyojo.schemaOrg.m3n3.core.Container.PotentialAction
import org.kyojo.schemaOrg.m3n3.core.Container.PreviousStartDate
import org.kyojo.schemaOrg.m3n3.core.Container.RecordedIn
import org.kyojo.schemaOrg.m3n3.core.Container.RemainingAttendeeCapacity
import org.kyojo.schemaOrg.m3n3.core.Container.Review
import org.kyojo.schemaOrg.m3n3.core.Container.SameAs
import org.kyojo.schemaOrg.m3n3.core.Container.Sponsor
import org.kyojo.schemaOrg.m3n3.core.Container.StartDate
import org.kyojo.schemaOrg.m3n3.core.Container.SubEvent
import org.kyojo.schemaOrg.m3n3.core.Container.SuperEvent
import org.kyojo.schemaOrg.m3n3.core.Container.Translator
import org.kyojo.schemaOrg.m3n3.core.Container.TypicalAgeRange
import org.kyojo.schemaOrg.m3n3.core.Container.WorkFeatured
import org.kyojo.schemaOrg.m3n3.core.Container.WorkPerformed
import org.kyojo.schemaOrg.m3n3.core.impl.DESCRIPTION
import org.kyojo.schemaOrg.m3n3.core.impl.END_DATE
import org.kyojo.schemaOrg.m3n3.core.impl.NAME
import org.kyojo.schemaOrg.m3n3.core.impl.START_DATE
import org.seasar.doma.Transient

class Schedule {

	private static final Log logger = LogFactory.getLog(Schedule.class)

	transient List<SchScheduleItem> newList

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		cache.setExpires(Time14.OLD)
		DocUser signInUser = (DocUser)ssd.get("signInUser")
		DocOffice signInOffice = (DocOffice)ssd.get("signInOffice")
		DocTeam signInTeam = (DocTeam)ssd.get("signInTeam")

		if(newList != null && newList.size() > 0) {
			for(SchScheduleItem newItem : newList) {
				if(newItem.actorUserSeq != 0 && newItem.actorUserSeq > 0) {
					DocUser actorUser = gbd.get(DocUserDao.class).selectBySeq(newItem.actorUserSeq);
					if(actorUser != null) {
						newItem.actorUserCd = actorUser.code;
						newItem.actorUserNm = actorUser.getName().getNativeValue();
					}
				}
				if(newItem.actorOfficeSeq != 0 && newItem.actorOfficeSeq > 0) {
					DocOffice actorOffice = gbd.get(DocOfficeDao.class).selectBySeq(newItem.actorOfficeSeq);
					if(actorOffice != null) {
						newItem.actorOfficeCd = actorOffice.code;
						newItem.actorOfficeNm = actorOffice.getName().getNativeValue();
					}
				}
				if(newItem.actorTeamSeq != 0 && newItem.actorTeamSeq > 0) {
					DocTeam actorTeam = gbd.get(DocTeamDao.class).selectBySeq(newItem.actorTeamSeq);
					if(actorTeam != null) {
						newItem.actorTeamCd = actorTeam.code;
						newItem.actorTeamNm = actorTeam.getName().getNativeValue();
					}
				}

				newItem.createdAt = new Date();
				newItem.createdBy = signInUser.seq;
				newItem.updatedAt = newItem.createdAt;
				newItem.updatedBy = newItem.createdBy;

				gbd.get(SchScheduleItemDao.class).insert(newItem)
			}
		}

		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfHM = new SimpleDateFormat("HH:mm");
		Date startDate = sdfYMDHMS.parse("2000-01-01 00:00:00")
		Date endDate = sdfYMDHMS.parse("2050-01-01 00:00:00")
		List<SchScheduleItem> siList = gbd.get(SchScheduleItemDao.class).selectByRange(startDate, endDate,
			signInUser.seq, signInOffice.seq, signInTeam.seq)
		Map<String, List<Map<String, String>>> monthlyMap = new HashMap<>()
		List<Map<String, String>> monthlyList = new ArrayList<>()
		monthlyMap.put("monthly", monthlyList)
		for(SchScheduleItem si : siList) {
			Map<String, String> map = new HashMap<>()
			map.put("id", si.seq)
			map.put("name", si.name.getNativeValue())
			map.put("startdate", si.startDate.getNativeValue() == null ? "" : sdfYMD.format(si.startDate.getNativeValue()))
			map.put("enddate", si.endDate.getNativeValue() == null ? "" : sdfYMD.format(si.endDate.getNativeValue()))
			map.put("starttime", si.startDate.getNativeValue() == null ? "" : sdfHM.format(si.startDate.getNativeValue()))
			map.put("endtime", si.endDate.getNativeValue() == null ? "" : sdfHM.format(si.endDate.getNativeValue()))
			map.put("color", "#FFB128")
			map.put("url", "")
			monthlyList.add(map)
		}

		// cache.addLine(SimpleJsonBuilder.toJson(monthlyMap))
		cache.addLine(My.minion(monthlyMap))

		return null
	}

	static boolean insertSamples(Map<String, User> users, Map<String, Office> offices, Map<String, Team> teams,
			Map<String, DocUser> docUsers, Map<String, DocOffice> docOffices, Map<String, DocTeam> docTeams,
			SchScheduleItemDao schScheduleItemDao) {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

		Date nowDt = new Date()
		Calendar nowCal = Calendar.getInstance()
		nowCal.setTime(nowDt)
		Calendar ystdyCal = Calendar.getInstance()
		ystdyCal.setTimeInMillis(nowDt.getTime() - 86400000)
		Calendar m15Cal = Calendar.getInstance()
		m15Cal.set(nowCal.get(Calendar.YEAR), nowCal.get(Calendar.MONTH), 15, 0, 0, 0)

		SchScheduleItem scheduleItem01 = new SchScheduleItem()
		Date createdAt01 = new Date(nowDt.getTime() - 7 * 86400000)
		scheduleItem01.createdAt = createdAt01
		scheduleItem01.createdBy = docUsers.get("武田　拓夫").seq
		scheduleItem01.updatedAt = createdAt01
		scheduleItem01.updatedBy = scheduleItem01.createdBy
		scheduleItem01.name = new NAME("新川様訪問")
		scheduleItem01.description = new DESCRIPTION("定期訪問")
		Calendar startCal01 = Calendar.getInstance()
		startCal01.set(ystdyCal.get(Calendar.YEAR), ystdyCal.get(Calendar.MONTH), ystdyCal.get(Calendar.DAY_OF_MONTH), 10, 0, 0)
		scheduleItem01.startDate = new START_DATE(startCal01.getTime())
		Calendar endCal01 = Calendar.getInstance()
		endCal01.set(ystdyCal.get(Calendar.YEAR), ystdyCal.get(Calendar.MONTH), ystdyCal.get(Calendar.DAY_OF_MONTH), 11, 0, 0)
		scheduleItem01.endDate = new END_DATE(endCal01.getTime())
		scheduleItem01.actorUserSeq = docUsers.get("武田　拓夫").seq
		scheduleItem01.actorUserCd = docUsers.get("武田　拓夫").code
		scheduleItem01.actorUserNm = docUsers.get("武田　拓夫").name.getNativeValue()
		schScheduleItemDao.insert(scheduleItem01)

		SchScheduleItem scheduleItem02 = new SchScheduleItem()
		Date createdAt02 = new Date(nowDt.getTime() - 7 * 86400000)
		scheduleItem02.createdAt = createdAt02
		scheduleItem02.createdBy = docUsers.get("武田　拓夫").seq
		scheduleItem02.updatedAt = createdAt02
		scheduleItem02.updatedBy = scheduleItem02.createdBy
		scheduleItem02.name = new NAME("診療時間")
		scheduleItem02.description = new DESCRIPTION("")
		Calendar startCal02 = Calendar.getInstance()
		startCal02.set(ystdyCal.get(Calendar.YEAR), ystdyCal.get(Calendar.MONTH), ystdyCal.get(Calendar.DAY_OF_MONTH), 13, 0, 0)
		scheduleItem02.startDate = new START_DATE(startCal02.getTime())
		Calendar endCal02 = Calendar.getInstance()
		endCal02.set(ystdyCal.get(Calendar.YEAR), ystdyCal.get(Calendar.MONTH), ystdyCal.get(Calendar.DAY_OF_MONTH), 17, 0, 0)
		scheduleItem02.endDate = new END_DATE(endCal02.getTime())
		scheduleItem02.actorOfficeSeq = docOffices.get("山中病院").seq
		scheduleItem02.actorOfficeCd = docOffices.get("山中病院").code
		scheduleItem02.actorOfficeNm = docOffices.get("山中病院").name.getNativeValue()
		schScheduleItemDao.insert(scheduleItem02)

		SchScheduleItem scheduleItem03 = new SchScheduleItem()
		Date createdAt03 = new Date(nowDt.getTime() - 7 * 86400000)
		scheduleItem03.createdAt = createdAt03
		scheduleItem03.createdBy = docUsers.get("武田　拓夫").seq
		scheduleItem03.updatedAt = createdAt03
		scheduleItem03.updatedBy = scheduleItem03.createdBy
		scheduleItem03.name = new NAME("サービス担当者会議")
		scheduleItem03.description = new DESCRIPTION("月定例")
		Calendar startCal03 = Calendar.getInstance()
		startCal03.set(m15Cal.get(Calendar.YEAR), m15Cal.get(Calendar.MONTH), m15Cal.get(Calendar.DAY_OF_MONTH), 16, 0, 0)
		scheduleItem03.startDate = new START_DATE(startCal03.getTime())
		Calendar endCal03 = Calendar.getInstance()
		endCal03.set(m15Cal.get(Calendar.YEAR), m15Cal.get(Calendar.MONTH), m15Cal.get(Calendar.DAY_OF_MONTH), 17, 0, 0)
		scheduleItem03.endDate = new END_DATE(endCal03.getTime())
		scheduleItem03.actorTeamSeq = docTeams.get("新川　光彦さんのケアチーム").seq
		scheduleItem03.actorTeamCd = docTeams.get("新川　光彦さんのケアチーム").code
		scheduleItem03.actorTeamNm = docTeams.get("新川　光彦さんのケアチーム").name.getNativeValue()
		schScheduleItemDao.insert(scheduleItem03)

		return true;
	}

}
