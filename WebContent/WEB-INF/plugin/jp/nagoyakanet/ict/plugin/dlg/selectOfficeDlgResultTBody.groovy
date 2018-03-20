package jp.nagoyakanet.ict.plugin.dlg

import jp.nagoyakanet.ict.dao.DocOfficeDao
import jp.nagoyakanet.ict.plugin.reg.basic.Office
import jp.nagoyakanet.ict.scm.DocOffice
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

class SelectOfficeDlgResultTBody extends Office {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		te.changeExt("html")
		List<DocOffice> officeList = gbd.get(DocOfficeDao.class).select()
		if(officeList.size() == 0) {
			// cache.addLine("0件")
		} else {
			cache.addLine("<tbody>")
			for(DocOffice office : officeList) {
				BeanUtils.copyProperties(this, office)
				te.appendParsedTemplate(this, getClass(), cache,
					"dlg/selectOfficeDlgResultTBodyTr", My.hs(args + ":" + office.seq),
					" " + indent, isForced, Time14.OLD)
			}
			cache.addLine("</tbody>")
		}

		return null
	}

}
