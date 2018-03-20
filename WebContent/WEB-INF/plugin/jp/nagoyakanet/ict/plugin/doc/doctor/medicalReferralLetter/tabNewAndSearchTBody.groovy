package jp.nagoyakanet.ict.plugin.doc.doctor.medicalReferralLetter

import jp.nagoyakanet.ict.plugin.doc.doctor.MedicalReferralLetter

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine

class TabNewAndSearchTBody extends MedicalReferralLetter {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		search(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)

		return null
	}

}
