package jp.nagoyakanet.ict.plugin.reg.medical.clinicDentistPharmacy

import jp.nagoyakanet.ict.plugin.reg.medical.clinicDentistPharmacy.pcuf.Conditions

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
import org.kyojo.schemaOrg.m3n3.core.impl.NAME

class PrimaryCareUserFieldset {

	Conditions conds

	String initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		conds = new Conditions()
		conds.clear()

		return null
	}

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		te.changeExt("html")
		te.appendParsedTemplate(this, getClass(), cache,
			"reg/medical/clinicDentistPharmacy/primaryCareUserFieldset", My.hs(args), indent,
			isForced, Time14.OLD)

		return null
	}

}
