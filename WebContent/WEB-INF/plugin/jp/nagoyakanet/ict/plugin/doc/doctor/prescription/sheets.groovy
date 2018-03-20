package jp.nagoyakanet.ict.plugin.doc.doctor.prescription

import org.kyojo.core.Cache
import org.kyojo.core.GlobalData
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.core.Time14
import org.kyojo.minion.My

import jp.nagoyakanet.ict.plugin.doc.doctor.Prescription

class Sheets extends Prescription {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced) {
		te.appendParsedTemplate(this, getClass(), cache,
			"sheet", My.hs(args), indent, isForced, Time14.OLD)

		return null
	}

}
