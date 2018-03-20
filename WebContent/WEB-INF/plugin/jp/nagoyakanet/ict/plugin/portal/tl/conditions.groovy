package jp.nagoyakanet.ict.plugin.portal.tl

import jp.nagoyakanet.ict.plugin.AbstractConditions

class Conditions extends AbstractConditions {

	Long seq
	List<String> kinds

	@Override
	void clear() {
		seq = null
		kinds = []
	}

}
