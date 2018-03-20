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
import org.kyojo.schemaOrg.m3n3.core.Clazz.ListItem
import org.kyojo.schemaOrg.m3n3.core.Container.Breadcrumb

class Breadcrumb2Html {

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(StringUtils.isBlank(args)) {
			cache.addLine("\n")
			return null
		}
		// cache.addLine("\n")

		StringBuilder sb = new StringBuilder()
		Breadcrumb breadcrumb = My.deminion(args, Breadcrumb.class)
		if(breadcrumb.getBreadcrumbListList() != null
				&& breadcrumb.getBreadcrumbListList().size() > 0
				&& breadcrumb.getBreadcrumbListList().get(0) != null
				&& breadcrumb.getBreadcrumbListList().get(0).getItemListElement() != null
				&& breadcrumb.getBreadcrumbListList().get(0).getItemListElement().getListItemList() != null) {
			List<ListItem> listItemList = breadcrumb.getBreadcrumbListList().get(0).getItemListElement().getListItemList()
			for(ListItem listItem : listItemList) {
				sb.append(" > ");
				sb.append(listItem.getItem().getThingList().get(0).getName().getNativeValue());
			}
		}
		cache.addLine(sb.toString())
		cache.addLine("\n")

		return null
	}

}
