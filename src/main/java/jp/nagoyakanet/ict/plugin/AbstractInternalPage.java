package jp.nagoyakanet.ict.plugin;

import java.util.ArrayList;
import java.util.List;

import org.kyojo.core.Cache;
import org.kyojo.core.CompleteThrowable;
import org.kyojo.core.GlobalData;
import org.kyojo.core.PluginException;
import org.kyojo.core.RedirectThrowable;
import org.kyojo.core.RequestData;
import org.kyojo.core.ResponseData;
import org.kyojo.core.SessionData;
import org.kyojo.core.TemplateEngine;
import org.kyojo.schemaOrg.m3n3.core.Clazz.BreadcrumbList;
import org.kyojo.schemaOrg.m3n3.core.Clazz.ListItem;
import org.kyojo.schemaOrg.m3n3.core.Clazz.Thing;
import org.kyojo.schemaOrg.m3n3.core.Container.Breadcrumb;
import org.kyojo.schemaOrg.m3n3.core.Container.ItemListElement;
import org.kyojo.schemaOrg.m3n3.core.impl.BREADCRUMB;
import org.kyojo.schemaOrg.m3n3.core.impl.BREADCRUMB_LIST;
import org.kyojo.schemaOrg.m3n3.core.impl.INTEGER;
import org.kyojo.schemaOrg.m3n3.core.impl.ITEM;
import org.kyojo.schemaOrg.m3n3.core.impl.ITEM_LIST_ELEMENT;
import org.kyojo.schemaOrg.m3n3.core.impl.LIST_ITEM;
import org.kyojo.schemaOrg.m3n3.core.impl.POSITION;
import org.kyojo.schemaOrg.m3n3.core.impl.THING;
import jp.nagoyakanet.ict.scm.DocUser;

public abstract class AbstractInternalPage extends AbstractPage {

	public String navIconActivatePortal;
	public String navIconActivateSched;
	public String navIconActivateDoc;
	public String navIconActivateReg;
	public String navIconActivateConf;
	public Breadcrumb breadcrumb;
	public DocUser signInUser;

	abstract public String[] getMajorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd);

	abstract public String[] getMiddleMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd);

	abstract public String[] getMinorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd);

	public Breadcrumb getBreadcrumb(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		List<ListItem> listItemList = new ArrayList<>();

		Long pos = 0L;
		String[] mjrMenu = getMajorMenu(gbd, ssd, rqd, rpd);
		if(mjrMenu != null && mjrMenu.length > 0) {
			pos++;
			ListItem listItem = new LIST_ITEM();
			listItem.setPosition(new POSITION(new INTEGER(pos)));
			Thing thing = new THING(mjrMenu[0]);
			listItem.setItem(new ITEM(thing));
			listItemList.add(listItem);
		}
		String[] mdlMenu = getMiddleMenu(gbd, ssd, rqd, rpd);
		if(mdlMenu != null && mdlMenu.length > 0) {
			pos++;
			ListItem listItem = new LIST_ITEM();
			listItem.setPosition(new POSITION(new INTEGER(pos)));
			Thing thing = new THING(mdlMenu[0]);
			listItem.setItem(new ITEM(thing));
			listItemList.add(listItem);
		}
		String[] mnrMenu = getMinorMenu(gbd, ssd, rqd, rpd);
		if(mnrMenu != null && mnrMenu.length > 0) {
			pos++;
			ListItem listItem = new LIST_ITEM();
			listItem.setPosition(new POSITION(new INTEGER(pos)));
			Thing thing = new THING(mnrMenu[0]);
			listItem.setItem(new ITEM(thing));
			listItemList.add(listItem);
		}

		pos++;
		ListItem listItem = new LIST_ITEM();
		listItem.setPosition(new POSITION(new INTEGER(pos)));
		Thing thing = new THING(getName(gbd, ssd, rqd, rpd));
		listItem.setItem(new ITEM(thing));
		listItemList.add(listItem);

		ItemListElement itemListElement = new ITEM_LIST_ELEMENT();
		itemListElement.setListItemList(listItemList);
		BreadcrumbList breadcrumbList = new BREADCRUMB_LIST(itemListElement);
		Breadcrumb breadcrumb = new BREADCRUMB(breadcrumbList);

		return breadcrumb;
	}

	public Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		Object res = super.initialize(args, gbd, ssd, rqd, rpd);
		if(res != null) {
			return res;
		}

		navIconActivatePortal = "";
		navIconActivateSched = "";
		navIconActivateDoc = "";
		navIconActivateReg = "";
		navIconActivateConf = "";
		breadcrumb = getBreadcrumb(gbd, ssd, rqd, rpd);
		signInUser = (DocUser)ssd.get("signInUser");

		return null;
	}

	public Object doSignOut(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		return "signOut.html";
	}

}
