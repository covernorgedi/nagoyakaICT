package jp.nagoyakanet.ict.plugin

import jp.nagoyakanet.ict.scm.DocOffice
import jp.nagoyakanet.ict.scm.DocTeam
import jp.nagoyakanet.ict.scm.DocUser
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

import jp.nagoyakanet.ict.plugin.AbstractInternalPage

class FileUpload {

	private static final Log logger = LogFactory.getLog(FileUpload.class)

	List<String> files

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		cache.setExpires(Time14.OLD)
		DocUser signInUser = (DocUser)ssd.get("signInUser")
		DocOffice signInOffice = (DocOffice)ssd.get("signInOffice")
		DocTeam signInTeam = (DocTeam)ssd.get("signInTeam")

		return null
	}

}
