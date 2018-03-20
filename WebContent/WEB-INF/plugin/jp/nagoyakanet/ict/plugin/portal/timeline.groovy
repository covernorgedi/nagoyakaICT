package jp.nagoyakanet.ict.plugin.portal

import jp.nagoyakanet.ict.dao.TlMessageDao
import jp.nagoyakanet.ict.plugin.portal.tl.Conditions
import jp.nagoyakanet.ict.scm.DocOffice
import jp.nagoyakanet.ict.scm.DocTeam
import jp.nagoyakanet.ict.scm.DocUser
import jp.nagoyakanet.ict.scm.TlMessage
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
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder
import org.seasar.doma.jdbc.SelectOptions

class Timeline {

	Conditions conds

	String initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		conds = new Conditions()
		conds.clear()

		return null
	}

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		cache.setExpires(Time14.OLD)
		DocUser signInUser = (DocUser)ssd.get("signInUser")
		DocOffice signInOffice = (DocOffice)ssd.get("signInOffice")
		DocTeam signInTeam = (DocTeam)ssd.get("signInTeam")

		SelectOptions options = SelectOptions.get().limit(conds.SEARCH_PER_PAGE)
		List<TlMessage> msgList = gbd.get(TlMessageDao.class).selectByFromSeq(conds.seq, signInUser.seq,
				signInOffice == null ? null : signInOffice.seq, signInTeam == null ? null : signInTeam.seq,
				true, conds.kinds, options)

		cache.addLine(SimpleJsonBuilder.toJson(msgList))

		return null
	}

}
