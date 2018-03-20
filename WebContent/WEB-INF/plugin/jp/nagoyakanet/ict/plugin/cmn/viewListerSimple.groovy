package jp.nagoyakanet.ict.plugin.cmn

import org.kyojo.core.Cache
import org.kyojo.minion.My

class ViewListerSimple extends ThingLister {

	@Override
	public String getName() {
		return My.camelize(getClass().getSimpleName())
	}

	@Override
	void addParentHtmlLines(Cache cache) {
		cache.addLine("<table class=\"collection " + getName() + "\">\n")
		// 描画はJavaScript
		cache.addLine("</table>\n")
	}

}
