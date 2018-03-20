package jp.nagoyakanet.ict.plugin

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

class Portal extends AbstractPortalPage {

	Date scheduleItemStartDate
	Date scheduleItemEndDate

	@Override
	String[] getMajorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String[] getMiddleMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String[] getMinorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"マイポータル"
	}

	@Override
	String initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		super.initialize(args, gbd, ssd, rqd, rpd)

		scheduleItemStartDate = scheduleItemEndDate = new Date()
	}

}
