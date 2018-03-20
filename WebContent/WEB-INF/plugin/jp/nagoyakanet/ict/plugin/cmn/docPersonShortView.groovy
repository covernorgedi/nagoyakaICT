package jp.nagoyakanet.ict.plugin.cmn

import jp.nagoyakanet.ict.scm.DocUser
import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.commons.lang3.StringUtils
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.minion.My
import org.kyojo.schemaOrg.m3n3.core.Clazz.Person

class DocPersonShortView {

	private static final Log logger = LogFactory.getLog(DocPersonShortView.class)

	String title
	Person person

	String initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(StringUtils.isBlank(args)) {
			return null
		}

		DocPersonShortView obj = My.deminion(args, DocPersonShortView.class)
		BeanUtils.copyProperties(this, obj)
		return null
	}

}
