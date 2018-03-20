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

class LangCertifiedStatus {

	static final HashMap<String, String> lang;
	static {
		lang = new HashMap<>()
		lang.put("", "未回答")
		lang.put("1", "認定")
		lang.put("2", "申請中")
		lang.put("3", "非該当")
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
