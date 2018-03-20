package jp.nagoyakanet.ict.plugin.reg.medical.inOutHospital

import jp.nagoyakanet.ict.scm.DocHospitalized

import java.util.List

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

class HospitalizedTablesRows {

	static final int COL_NUM = 2

	HospitalizedRowsConditions conds

	String initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		conds = new HospitalizedRowsConditions()
		conds.clear()

		return null
	}

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		te.changeExt("html")
		int anum = conds.hospitalizedList.size();
		int rnum = (anum + 1) / COL_NUM
		for(int ridx = 0; ridx < rnum; ridx++) {
			conds.hospitalizedBlockList = []
			for(int cidx = 0; cidx < COL_NUM; cidx++) {
				int aidx = ridx * COL_NUM + cidx
				HospitalizedBlockConditions hospitalizedBlock = new HospitalizedBlockConditions()
				hospitalizedBlock.allNum = anum
				hospitalizedBlock.allIdx = aidx + 1
				hospitalizedBlock.rowIdx = ridx + 1
				hospitalizedBlock.colIdx = cidx + 1
				if(aidx < anum) {
					hospitalizedBlock.hospitalized = conds.hospitalizedList[aidx]
				} else {
					hospitalizedBlock.hospitalized = null
				}
				conds.hospitalizedBlockList.add(hospitalizedBlock)
			}

			te.appendParsedTemplate(this, getClass(), cache,
				"reg/medical/inOutHospital/hospitalizedTableCols", My.hs(args), indent,
				isForced, Time14.OLD)
		}

		return null
	}

}
