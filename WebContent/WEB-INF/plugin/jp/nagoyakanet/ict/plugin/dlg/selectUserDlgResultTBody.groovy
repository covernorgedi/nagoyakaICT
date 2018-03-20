package jp.nagoyakanet.ict.plugin.dlg

import jp.nagoyakanet.ict.dao.DocUserDao
import jp.nagoyakanet.ict.plugin.reg.basic.User
import jp.nagoyakanet.ict.scm.DocUser
import org.apache.commons.beanutils.BeanUtils
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

class selectUserDlgResultTBody extends User {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		te.changeExt("html")
		List<DocUser> userList = gbd.get(DocUserDao.class).select()
		if(userList.size() == 0) {
			// cache.addLine("0件")
		} else {
			cache.addLine("<tbody>")
			for(DocUser user : userList) {
				BeanUtils.copyProperties(this, user)
				te.appendParsedTemplate(this, getClass(), cache,
					"dlg/selectUserDlgResultTBodyTr", My.hs(args + ":" + user.seq),
					" " + indent, isForced, Time14.OLD)
			}
			cache.addLine("</tbody>")
		}

		return null
	}

}
