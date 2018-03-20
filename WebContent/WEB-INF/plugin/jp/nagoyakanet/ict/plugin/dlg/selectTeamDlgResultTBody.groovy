package jp.nagoyakanet.ict.plugin.dlg

import jp.nagoyakanet.ict.dao.DocTeamDao
import jp.nagoyakanet.ict.plugin.reg.basic.Team
import jp.nagoyakanet.ict.scm.DocTeam
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

class selectTeamDlgResultTBody extends Team {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		te.changeExt("html")
		List<DocTeam> teamList = gbd.get(DocTeamDao.class).select()
		if(teamList.size() == 0) {
			// cache.addLine("0件")
		} else {
			cache.addLine("<tbody>")
			for(DocTeam team : teamList) {
				BeanUtils.copyProperties(this, team)
				te.appendParsedTemplate(this, getClass(), cache,
					"dlg/selectTeamDlgResultTBodyTr", My.hs(args + ":" + team.seq),
					" " + indent, isForced, Time14.OLD)
			}
			cache.addLine("</tbody>")
		}

		return null
	}

}
