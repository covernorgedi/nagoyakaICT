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
import org.kyojo.minion.My;

class NameAndBelong {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		List<String> al = My.deminion(args, List.class)
		StringBuilder sb = new StringBuilder()
		if(al != null && al.size() > 0) {
			sb.append(al[0])
			if(al.size() > 1 && StringUtils.isNotBlank(al[1])) {
				sb.append("（")
				sb.append(al[1])
				sb.append("）")
			}
			if(al.size() > 2 && StringUtils.isNotBlank(al[2])) {
				sb.append(al[2])
			}
		}
		cache.addLine(sb.toString())
		cache.addLine("\n")

		return null
	}

}
