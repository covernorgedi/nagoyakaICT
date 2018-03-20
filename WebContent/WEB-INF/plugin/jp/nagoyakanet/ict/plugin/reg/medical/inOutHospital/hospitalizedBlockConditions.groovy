package jp.nagoyakanet.ict.plugin.reg.medical.inOutHospital

import jp.nagoyakanet.ict.plugin.AbstractConditions
import jp.nagoyakanet.ict.scm.DocHospitalized

class HospitalizedBlockConditions extends AbstractConditions {

	Integer allNum
	Integer allIdx
	Integer rowIdx
	Integer colIdx
	DocHospitalized hospitalized

	@Override
	void clear() {
		allNum = 0
		allIdx = 0
		rowIdx = 0
		colIdx = 0
		hospitalized = new DocHospitalized()
	}

}
