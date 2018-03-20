package jp.nagoyakanet.ict.plugin.cmn

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.beanutils.ConvertUtils
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

class InputRadioNormal {

	String label
	Map<String, String> attrs
	String checked

	/*
	 * 出力例
	 *
	 * <input name="dependent" type="radio" id="dependent.0" value="0" checked="checked" />
	 * <label for="dependent.0">なし</label>
	 */
	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(StringUtils.isBlank(args)) {
			cache.addLine("\n")
			return null
		}

		InputRadioNormal obj = My.deminion(args, InputRadioNormal.class)
		BeanUtils.copyProperties(this, obj)
		String iKey = null
		String nKey = null
		String vKey = null
		if(attrs.containsKey("value")) {
			String[] elms = attrs.value.split("\\.")
			StringBuilder sb = new StringBuilder(elms[0])
			for(int ei = 1; ei < elms.length - 1; ei++) {
				sb.append(".")
				sb.append(elms[ei])
			}
			vKey = sb.toString()
			attrs.value = elms[elms.length - 1]
		}
		if(attrs.containsKey("id")) {
			iKey = attrs.id
			String[] elms = iKey.split("\\.")
			StringBuilder sb = new StringBuilder(elms[0])
			for(int ei = 1; ei < elms.length - 1; ei++) {
				sb.append(".")
				sb.append(elms[ei])
			}
			nKey = sb.toString()
			if(attrs.containsKey("name")) {
				nKey = attrs.name
			} else {
				attrs.name = nKey
			}
			if(!attrs.containsKey("value")) {
				elms = iKey.split("\\.")
				if(elms.length > 1) {
					sb = new StringBuilder(elms[0])
					for(int ei = 1; ei < elms.length - 1; ei++) {
						sb.append(".")
						sb.append(elms[ei])
					}
					vKey = sb.toString()
					attrs.value = elms[elms.length - 1]
				} else {
					vKey = iKey
					attrs.value = "1"
				}
			}
		}
		if(attrs.containsKey("disabled")) {
			if(StringUtils.isNotBlank(attrs.disabled) && attrs.disabled != "disabled") {
				iKey = attrs.disabled
				String[] elms = attrs.get("disabled").split("\\.")
				StringBuilder sb = new StringBuilder(elms[0])
				for(int ei = 1; ei < elms.length - 1; ei++) {
					sb.append(".")
					sb.append(elms[ei])
				}
				nKey = sb.toString()
				if(attrs.containsKey("id")) {
					iKey = attrs.id
				} else {
					attrs.id = "_" + iKey
				}
				if(attrs.containsKey("name")) {
					nKey = attrs.name
				} else {
					attrs.name = "_" + nKey
				}
				if(!attrs.containsKey("value")) {
					elms = iKey.split("\\.")
					if(elms.length > 1) {
						sb = new StringBuilder(elms[0])
						for(int ei = 1; ei < elms.length - 1; ei++) {
							sb.append(".")
							sb.append(elms[ei])
						}
						vKey = sb.toString()
						attrs.value = elms[elms.length - 1]
					} else {
						vKey = iKey
						attrs.value = "1"
					}
				}
			}
			attrs.disabled = "disabled"
		}
		// if(attrs.containsKey("name") && StringUtils.isBlank(key)) {
		//	key = attrs.name
		// }
		if(!attrs.containsKey("type")) {
			attrs.type = "radio"
		}
		if(!attrs.containsKey("checked")) {
			String str = checked
			if(StringUtils.isBlank(str)) {
				String kc = My.constantize(vKey)
				str = te.convMagicWord(kc)
			}
			if(str.equals(attrs.value)) {
				attrs.checked = "checked"
			}
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
			if(attrs.containsKey("id")) {
				sb = new StringBuilder()
				sb.append("<label for=\"")
				sb.append(StringEscapeUtils.escapeHtml4(attrs.id))
				sb.append("\">")
				sb.append(StringEscapeUtils.escapeHtml4(label))
				sb.append("</label>\n")
				cache.addLine(sb.toString())
			}
		}

		BeanUtils.copyProperties(this, new InputRadioNormal())
		return null
	}

}
