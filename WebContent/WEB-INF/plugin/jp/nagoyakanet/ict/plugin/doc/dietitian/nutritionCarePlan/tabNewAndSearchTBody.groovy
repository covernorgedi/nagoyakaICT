package jp.nagoyakanet.ict.plugin.doc.dietitian.nutritionCarePlan

import jp.nagoyakanet.ict.plugin.doc.dietitian.NutritionCarePlan
import org.kyojo.core.Cache
import org.kyojo.core.GlobalData
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine

class TabNewAndSearchTBody extends NutritionCarePlan {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced) {
		search(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)

		return null
	}

}
