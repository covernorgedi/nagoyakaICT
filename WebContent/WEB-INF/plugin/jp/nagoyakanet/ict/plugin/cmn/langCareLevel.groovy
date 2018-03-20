package jp.nagoyakanet.ict.plugin.cmn

import org.apache.commons.lang3.StringUtils

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine

class LangCareLevel {

	static final HashMap<String, String> lang;
	static {
		lang = new HashMap<>()
		lang.put("", "未回答")
		lang.put("s1", "要支援1")
		lang.put("s2", "要支援2")
		lang.put("c1", "要介護1")
		lang.put("c2", "要介護2")
		lang.put("c3", "要介護3")
		lang.put("c4", "要介護4")
		lang.put("c5", "要介護5")
	}

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(StringUtils.isNoneBlank(args)) {
			if(lang.containsKey(args)) {
				cache.addLine(lang.get(args))
			}
		}
		// cache.addLine("\n")

		return null
	}

}
