package jp.nagoyakanet.ict.plugin.cmn

import java.io.IOException

import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.gbd.AppConfig
import org.seasar.doma.jdbc.tx.TransactionManager

import jp.nagoyakanet.ict.dao.MstCodeAndLangDao
import jp.nagoyakanet.ict.scm.MstCodeAndLang

class LangJa {

	private static final Log logger = LogFactory.getLog(LangJa.class);

	static HashMap<String, String> lang = null

	void initialize(GlobalData gbd) {
		lang = new HashMap<>()
		try {
			TransactionManager tm = AppConfig.singleton().getTransactionManager();
			tm.required {
				List<MstCodeAndLang> mstCodeAndLangList = gbd.get(MstCodeAndLangDao.class).select(true)
				HashMap<String, String> withType = new HashMap<>()
				HashMap<String, String> withAct = new HashMap<>()
				for(MstCodeAndLang mstCodeAndLang : mstCodeAndLangList) {
					if(mstCodeAndLang.act == null && mstCodeAndLang.type == null) {
						lang.put(mstCodeAndLang.code, mstCodeAndLang.ja)
					} else if(mstCodeAndLang.act == null && mstCodeAndLang.type != null) {
						withType.put(mstCodeAndLang.code, mstCodeAndLang.ja)
					} else {
						withAct.put(mstCodeAndLang.code, mstCodeAndLang.ja)
					}
				}
				withType.each { key, val ->
					if(!lang.containsKey(key)) {
						lang.put(key, val)
					}
				}
				withAct.each { key, val ->
					if(!lang.containsKey(key)) {
						lang.put(key, val)
					}
				}
			}
		} catch(IOException ioe) {
			logger.fatal(ioe.getMessage(), ioe)
		}
	}

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(lang == null) {
			// staticが唯一にならない場合があるようなので逐次初期化
			initialize(gbd)
		}
		if(StringUtils.isNoneBlank(args)) {
			if(lang.containsKey(args)) {
				cache.addLine(lang.get(args))
			}
		}
		// cache.addLine("\n")

		return null
	}

}
