package jp.nagoyakanet.ict.plugin.reg.medical.diagnosisMedicineAllergy.lnm

import jp.nagoyakanet.ict.plugin.AbstractConditions
import jp.nagoyakanet.ict.scm.DocFormula

class Conditions extends AbstractConditions {

	Integer idx
	String rowID
	DocFormula medicine

	@Override
	void clear() {
		idx = 0
		rowID = ""
		medicine = new DocFormula()
	}

}
