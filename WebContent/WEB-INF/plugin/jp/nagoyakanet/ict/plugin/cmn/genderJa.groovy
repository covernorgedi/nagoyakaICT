package jp.nagoyakanet.ict.plugin.cmn

import org.apache.commons.lang3.StringUtils

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.core.Clazz
import org.kyojo.schemaOrg.m3n3.core.DataType
import org.kyojo.schemaOrg.m3n3.core.Container.Gender

class GenderJa {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		Gender gender = My.deminion(args, Gender.class)
		if(gender == null) {
			// cache.addLine("\n")
			return null
		}

		List<Clazz.GenderType> genderTypeList = gender.getGenderTypeList()
		if(genderTypeList != null && genderTypeList.size() > 0) {
			Clazz.GenderType genderType = genderTypeList.get(0)
			// if(LangJa.lang.containsKey(genderType.getName().getNativeValue())) {
			//	cache.addLine(LangJa.lang.get(genderType.getName().getNativeValue()))
			//	// cache.addLine("\n")
			//	return null
			// }
			String str = te.parseTemplate("", "cmn/langJa", genderType.getName().getNativeValue(), "", false)
			cache.addLine(str)
		}

		// List<DataType.Text> textList = gender.getTextList()
		// if(textList != null && textList.size() > 0) {
		//	DataType.Text text = textList.get(0)
		//	cache.addLine(text.getNativeValue())
		//	// cache.addLine("\n")
		//	return null
		// }

		// cache.addLine("\n")
		return null
	}

}
