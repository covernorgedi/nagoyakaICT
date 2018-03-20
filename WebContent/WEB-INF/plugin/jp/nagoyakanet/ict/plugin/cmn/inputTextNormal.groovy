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

class InputTextNormal {

	String label
	Map<String, String> attrs
	String desc

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(StringUtils.isBlank(args)) {
			cache.addLine("\n")
			return null
		}

		InputTextNormal obj = My.deminion(args, InputTextNormal.class)
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
			attrs.type = "text"
		}

		if(attrs != null && attrs.size() > 0) {
			cache.addLine("<div class=\"input-field\">\n")
			StringBuilder sb = new StringBuilder()
			sb.append(" <input")
			for(Map.Entry<String, String> ent : attrs) {
				sb.append(" ")
				sb.append(StringEscapeUtils.escapeHtml4(ent.getKey()))
				sb.append("=\"")
				sb.append(StringEscapeUtils.escapeHtml4(ent.getValue()))
				sb.append("\"")
			}
			sb.append(" />\n")
			cache.addLine(sb.toString())
			if(attrs.containsKey("id")) {
				sb = new StringBuilder()
				sb.append(" <label for=\"")
				sb.append(StringEscapeUtils.escapeHtml4(attrs.id))
				sb.append("\">")
				sb.append(StringEscapeUtils.escapeHtml4(label))
				sb.append("</label>\n")
				cache.addLine(sb.toString())
			}
			if(StringUtils.isNotBlank(desc)) {
				sb.append(" <div class=\"inputDesc\">")
				sb.append(desc)
				sb.append("</div>\n")
				cache.addLine(sb.toString())
			}
			cache.addLine("</div>\n")
		}

		BeanUtils.copyProperties(this, new InputTextNormal())
		return null
	}

}
