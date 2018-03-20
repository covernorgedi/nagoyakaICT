package jp.nagoyakanet.ict.plugin.cmn

import org.kyojo.core.Cache
import org.kyojo.minion.My

class EditListerSimple extends ThingLister {

	@Override
	String getName() {
		return My.camelize(getClass().getSimpleName())
	}

	@Override
	void addParentHtmlLines(Cache cache) {
		cache.addLine("<ul class=\"collection " + getName() + "\">\n")
		// 描画はJavaScript
		cache.addLine("</ul>\n")
	}

}
