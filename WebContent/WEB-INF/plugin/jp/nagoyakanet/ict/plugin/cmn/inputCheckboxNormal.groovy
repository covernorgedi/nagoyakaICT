package jp.nagoyakanet.ict.plugin.cmn

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.beanutils.ConvertUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.StringEscapeUtils
import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.Constants
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.minion.My;

class InputCheckboxNormal {

	String label
	Map<String, String> attrs
	String checked

	/*
	 * 出力例
	 *
	 * <input id="formulaList.1.canGeneric" type="checkbox" class="filled-in" name="formulaList.1.canGeneric" value="1" checked="checked" />
	 * <label for="formulaList.1.canGeneric">変更不可</label>
	 */
	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(StringUtils.isBlank(args)) {
			cache.addLine("\n")
			return null
		}

		InputCheckboxNormal obj = My.deminion(args, InputCheckboxNormal.class)
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
		if(!attrs.containsKey("type")) {
			attrs.type = "checkbox"
		}
		if(!attrs.containsKey("class")) {
			attrs.put("class", "filled-in")
		}
		if(!attrs.containsKey("checked")) {
			String str = checked
			if(StringUtils.isBlank(str)) {
				String kc = My.constantize(vKey)
				str = te.convMagicWord(kc)
			}
			if(StringUtils.isBlank(str)) {
				String kc2 = My.constantize(vKey + "." + Constants.MINION_SUFFIX)
				str = te.convMagicWord(kc2)
				if(str != null && str.startsWith("[")) {
					List<?> tmpLst = My.deminion(str, List.class)
					for(Object tmpObj : tmpLst) {
						if(attrs.value == tmpObj.toString()) {
							attrs.checked = "checked"
							break;
						}
					}
				}
			} else {
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

		BeanUtils.copyProperties(this, new InputCheckboxNormal())
		return null
	}

}
