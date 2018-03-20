package jp.nagoyakanet.ict.plugin.cmn

import org.apache.commons.beanutils.BeanUtils
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
import org.kyojo.core.Time14
import org.kyojo.minion.My;

class InputTextTimePicker {

	String label
	Map<String, String> attrs

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(StringUtils.isBlank(args)) {
			cache.addLine("\n")
			return null
		}

		InputTextTimePicker obj = My.deminion(args, InputTextTimePicker.class)
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
		if(!attrs.containsKey("class")) {
			attrs.put("class", "timepicker")
		}

		if(attrs.value && attrs.value.length() >= 19) {
			// 2015-01-10T00:00:00+09:00 の形式は 00:00:00 にする
			attrs.value = attrs.value[11..18]
		} else if(!attrs.value) {
			attrs.value = ""
		}

		if(attrs != null && attrs.size() > 0) {
			StringBuilder sb = null
			if(attrs.containsKey("id")) {
				sb = new StringBuilder()
				sb.append("<label for=\"")
				sb.append("_")
				sb.append(attrs.id)
				sb.append("\">")
				sb.append(label)
				sb.append("</label>\n")
				cache.addLine(sb.toString())
			}
			sb = new StringBuilder()
			sb.append("<input")
			for(Map.Entry<String, String> ent : attrs) {
				sb.append(" ")
				sb.append(ent.getKey())
				sb.append("=\"")
				if(attrs.containsKey("id") && (ent.getKey().equals("id") || ent.getKey().equals("name"))) {
					sb.append(ent.getValue())
				} else {
					sb.append(ent.getValue())
				}
				sb.append("\"")
			}
			sb.append(" />\n")
			cache.addLine(sb.toString())
		}

		return null
	}

}
