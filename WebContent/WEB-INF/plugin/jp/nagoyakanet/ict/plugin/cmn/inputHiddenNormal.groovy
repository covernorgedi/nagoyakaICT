package jp.nagoyakanet.ict.plugin.cmn

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.StringEscapeUtils
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

class InputHiddenNormal {

	Map<String, String> attrs

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(StringUtils.isBlank(args)) {
			cache.addLine("\n")
			return null
		}

		InputHiddenNormal obj = My.deminion(args, InputHiddenNormal.class)
		BeanUtils.copyProperties(this, obj)
		if(attrs.containsKey("id")) {
			if(!attrs.containsKey("name")) {
				attrs.name = attrs.id
			}
			if(!attrs.containsKey("value")) {
				String key = My.constantize(attrs.id)
				attrs.value = te.convMagicWord(key)
			}
		}
		if(!attrs.containsKey("type")) {
			attrs.type = "hidden"
		}

		if(attrs != null && attrs.size() > 0) {
			StringBuilder sb = new StringBuilder()
			sb.append("<input")
			for(Map.Entry<String, String> ent : attrs) {
				sb.append(" ")
				sb.append(StringEscapeUtils.escapeHtml4(ent.getKey()))
				sb.append("=\"")
				sb.append(StringEscapeUtils.escapeHtml4(ent.getValue()))
				sb.append("\"")
			}
			sb.append(" />\n")
			cache.addLine(sb.toString())
		}

		BeanUtils.copyProperties(this, new InputHiddenNormal())
		return null
	}

}
