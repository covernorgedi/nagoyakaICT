package jp.nagoyakanet.ict.plugin.dlg

import jp.nagoyakanet.ict.plugin.auth.master.CodeAndLang
import jp.nagoyakanet.ict.plugin.cmn.LangJa
import jp.nagoyakanet.ict.dao.MstCodeAndLangDao
import jp.nagoyakanet.ict.scm.MstCodeAndLang
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
import org.kyojo.schemaOrg.m3n3.core.impl.NAME

class selectOccupationDlgResultTBody extends CodeAndLang {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		te.changeExt("html")
		List<MstCodeAndLang> occupationList = gbd.get(MstCodeAndLangDao.class).selectByType("occupation", true)
		if(occupationList.size() == 0) {
			// cache.addLine("0件")
		} else {
			cache.addLine("<tbody>")
			for(MstCodeAndLang occupation : occupationList) {
				BeanUtils.copyProperties(this, occupation)
				name = new NAME(occupation.ja)
				te.appendParsedTemplate(this, getClass(), cache,
					"dlg/selectOccupationDlgResultTBodyTr", My.hs(args + ":" + occupation.seq),
					" " + indent, isForced, Time14.OLD)
			}
			cache.addLine("</tbody>")
		}

		return null
	}

}
