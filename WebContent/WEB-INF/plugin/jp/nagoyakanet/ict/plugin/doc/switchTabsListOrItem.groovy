package jp.nagoyakanet.ict.plugin.doc

import java.util.Iterator
import java.util.regex.Matcher
import java.util.regex.Pattern

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
import org.kyojo.minion.My

class SwitchTabsListOrItem {

	transient String path

	static String P2T_PATTERN1 = "^/(\\d{1,20})/(\\d{1,20})\$"

	static String P2T_PATTERN2 = "^/(\\d{1,20})\$"

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		String sKey = "tabsList"
		Pattern pt = Pattern.compile(P2T_PATTERN1)
		Matcher mc = pt.matcher(path)
		if(mc.matches()) {
			sKey = "tabsItem"
		} else {
			pt = Pattern.compile(P2T_PATTERN2)
			mc = pt.matcher(path)
			if(mc.matches()) {
				sKey = "tabsItem"
			}
		}

		te.appendParsedTemplate(this, getClass(), cache,
			sKey, My.hs(args), indent, isForced, Time14.OLD)

		return null
	}

}
