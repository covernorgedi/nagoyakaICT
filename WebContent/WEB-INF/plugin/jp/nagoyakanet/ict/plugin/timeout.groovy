package jp.nagoyakanet.ict.plugin

import jp.nagoyakanet.ict.scm.DocUser
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData

class Timeout extends AbstractExternalPage {

	private static final Log logger = LogFactory.getLog(Timeout.class)

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"タイムアウトページ"
	}

	@Override
	String initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		super.initialize(args, gbd, ssd, rqd, rpd)
		recycle(args, gbd, ssd, rqd, rpd)
		return null
	}

	Object recycle(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		logger.info(String.format("timed out."))
		ssd.sessionRestart()
		return null
	}

}
