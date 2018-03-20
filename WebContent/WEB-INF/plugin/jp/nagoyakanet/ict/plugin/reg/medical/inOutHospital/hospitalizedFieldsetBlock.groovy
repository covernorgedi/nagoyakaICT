package jp.nagoyakanet.ict.plugin.reg.medical.inOutHospital

import jp.nagoyakanet.ict.scm.DocHospitalized
import java.text.SimpleDateFormat
import java.util.List

import org.apache.commons.beanutils.BeanUtils
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
import org.kyojo.schemaOrg.m3n3.core.impl.END_TIME
import org.kyojo.schemaOrg.m3n3.core.impl.JOIN_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.LEAVE_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.START_TIME
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder

class HospitalizedFieldsetBlock {

	HospitalizedBlockConditions conds

	String initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		conds = new HospitalizedBlockConditions()
		conds.clear()

		// 現在時刻を初期値に
		conds.hospitalized.fromDate = conds.hospitalized.fromTime =
			conds.hospitalized.toDate = conds.hospitalized.toTime = new Date()

		return null
	}

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		te.changeExt("html")

		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd")
		SimpleDateFormat sdfHMS = new SimpleDateFormat("HH:mm:ss")
		if(conds.hospitalized != null) {
			if(SimpleJsonBuilder.toJson(conds.hospitalized).contentEquals("{}")) {
				// 新規行
				conds.hospitalized.fromDate = conds.hospitalized.fromTime =
					conds.hospitalized.toDate = conds.hospitalized.toTime = new Date()
			} else {
				if(conds.hospitalized.joinAction != null && conds.hospitalized.joinAction.startTime != null) {
					Date joinActionDate = conds.hospitalized.joinAction.startTime.getNativeValue()
					String ymdhmsStr = sdfYMDHMS.format(joinActionDate)
					conds.hospitalized.fromDate = sdfYMD.parse(ymdhmsStr.substring(0, 10))
					conds.hospitalized.fromTime = sdfHMS.parse(ymdhmsStr.substring(11, 19))
				}

				if(conds.hospitalized.leaveAction != null && conds.hospitalized.leaveAction.endTime != null) {
					Date leaveActionDate = conds.hospitalized.leaveAction.endTime.getNativeValue()
					String ymdhmsStr = sdfYMDHMS.format(leaveActionDate)
					conds.hospitalized.toDate = sdfYMD.parse(ymdhmsStr.substring(0, 10))
					conds.hospitalized.toTime = sdfHMS.parse(ymdhmsStr.substring(11, 19))
				}
			}
		}

		te.appendParsedTemplate(this, getClass(), cache,
			"reg/medical/inOutHospital/hospitalizedFieldsetBlock", My.hs(args), indent,
			isForced, Time14.OLD)

		return null
	}

}
