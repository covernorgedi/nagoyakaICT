package jp.nagoyakanet.ict.plugin

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.Constants
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.minion.My

class HeaderInternal {

	private static final Log logger = LogFactory.getLog(HeaderInternal.class);

	String errorMessage

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		return recycle(args, gbd, ssd, rqd, rpd)
	}

	Object recycle(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		Object signInUser = ssd.get("signInUser")
		if(signInUser == null) {
			logger.info("no sign in data found.")
			return Constants.TIMEOUT_SKIN + "." + Constants.DEFAULT_EXT
		}

		return null
	}

}
