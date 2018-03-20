package jp.nagoyakanet.ict.plugin.reg.medical.inOutHospital

import jp.nagoyakanet.ict.plugin.AbstractConditions
import jp.nagoyakanet.ict.scm.DocHospitalized

class HospitalizedRowsConditions extends AbstractConditions {

	List<DocHospitalized> hospitalizedList // 入退院データすべて
	List<HospitalizedBlockConditions> hospitalizedBlockList // 行単位個別表示用

	@Override
	void clear() {
		hospitalizedList = []
		hospitalizedBlockList = []
	}

}
