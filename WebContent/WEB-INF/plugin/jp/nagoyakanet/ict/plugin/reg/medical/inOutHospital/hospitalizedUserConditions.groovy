package jp.nagoyakanet.ict.plugin.reg.medical.inOutHospital

import jp.nagoyakanet.ict.plugin.AbstractConditions
import jp.nagoyakanet.ict.scm.DocUser

class HospitalizedUserConditions extends AbstractConditions {

	Integer idx
	String rowID
	DocUser user

	@Override
	void clear() {
		idx = 0
		rowID = ""
		user = new DocUser()
	}

}
