package jp.nagoyakanet.ict.plugin

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
import org.kyojo.schemaOrg.m3n3.core.Clazz.MusicPlaylist

class PlayLand extends AbstractExternalPage {

	private static final Log logger = LogFactory.getLog(PlayLand.class)

	String a
	List<String> b
	MusicPlaylist musicPlaylist

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"何か"
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
		return null
	}

}
