package jp.nagoyakanet.ict.plugin

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine

import jp.nagoyakanet.ict.plugin.AbstractInternalPage

abstract class AbstractRegistryPage extends AbstractDocumentPage {

	@Override
	String[] getMajorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		[ "台帳" ]
	}

	@Override
	String initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		super.initialize(args, gbd, ssd, rqd, rpd)

		navIconActivateDoc = ""
		navIconActivateReg = "navIconActivate"
		return null
	}

}
