package jp.nagoyakanet.ict.plugin

import jp.nagoyakanet.ict.dao.SignInSessionDao
import jp.nagoyakanet.ict.scm.DocUser
import jp.nagoyakanet.ict.scm.SignInSession
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData

class SignOut extends AbstractExternalPage {

	private static final Log logger = LogFactory.getLog(SignOut.class)

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"サインアウトページ"
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
		DocUser signInUser = ssd.get("signInUser")
		if(signInUser == null) {
			logger.info(String.format("already signed out."))
		} else {
			List<SignInSession> signInSessionList = gbd.get(SignInSessionDao.class).selectBySessionId(ssd.getSessionID(), true)
			if(signInSessionList.size() > 0) {
				for(SignInSession signInSession : signInSessionList) {
					signInSession.expiredAt = new Date()
					gbd.get(SignInSessionDao.class).update(signInSession)
				}
			}
			logger.info(String.format("signing out. signInId=\"%s\",sid=\"%s\",name=\"%s\"",
					signInUser.code, ssd.getSessionID(), signInUser.getName().getNativeValue()))
		}
		ssd.sessionRestart()
		return null
	}

}
