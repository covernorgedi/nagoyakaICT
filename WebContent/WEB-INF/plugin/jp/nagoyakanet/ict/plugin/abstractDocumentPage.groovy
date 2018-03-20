package jp.nagoyakanet.ict.plugin

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
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
import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig

import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Matcher
import java.util.regex.Pattern
import jp.nagoyakanet.ict.dao.DocMinionDao
import jp.nagoyakanet.ict.plugin.AbstractInternalPage
import jp.nagoyakanet.ict.plugin.reg.basic.Office
import jp.nagoyakanet.ict.plugin.reg.basic.Team
import jp.nagoyakanet.ict.plugin.reg.basic.User
import jp.nagoyakanet.ict.scm.DocMinion
import jp.nagoyakanet.ict.scm.DocOffice
import jp.nagoyakanet.ict.scm.DocTeam
import jp.nagoyakanet.ict.scm.DocUser

abstract class AbstractDocumentPage extends AbstractInternalPage {

	private static final Log logger = LogFactory.getLog(AbstractDocumentPage.class)

	String tabSelected

	Long seq
	Date comeAt
	Long comeByUserSeq
	String comeByUserCd
	String comeByUserNm
	Long comeByOfficeSeq
	String comeByOfficeCd
	String comeByOfficeNm
	Long comeByTeamSeq
	String comeByTeamCd
	String comeByTeamNm
	Date goneAt
	Long goneByUserSeq
	String goneByUserCd
	String goneByUserNm
	Long goneByOfficeSeq
	String goneByOfficeCd
	String goneByOfficeNm
	Long goneByTeamSeq
	String goneByTeamCd
	String goneByTeamNm
	Long progenitor
	Long ancestor
	Long descendant
	Long lifetime
	Date cleanedAt

	@Override
	String[] getMajorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		[ "帳票" ]
	}

	String getSKey() {
		if(StringUtils.isBlank(act)) {
			return ""
		}
		return act.replaceAll("/", ".")
	}

	void clear(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		seq = null
		comeAt = null
		comeByUserSeq = null
		comeByUserCd = null
		comeByUserNm = null
		comeByOfficeSeq = null
		comeByOfficeCd = null
		comeByOfficeNm = null
		comeByTeamSeq = null
		comeByTeamCd = null
		comeByTeamNm = null
		goneAt = null
		goneByUserSeq = null
		goneByUserCd = null
		goneByUserNm = null
		goneByOfficeSeq = null
		goneByOfficeCd = null
		goneByOfficeNm = null
		goneByTeamSeq = null
		goneByTeamCd = null
		goneByTeamNm = null
		progenitor = null
		ancestor = null
		descendant = null
		lifetime = null
		cleanedAt = null
	}

	DocUser getSignInUser(GlobalData gbd, SessionData ssd) {
		DocUser signInUser = (DocUser)ssd.get("signInUser")
		if(signInUser == null) return null
		DocMinion mnnUser = gbd.get(DocMinionDao.class).selectBySeq(signInUser.refSeq, true)
		if(mnnUser != null) {
			User signInUserFull = My.deminion(mnnUser.minion, User.class)
			BeanUtils.copyProperties(signInUser, signInUserFull)
		}
		return signInUser
	}

	DocOffice getSignInOffice(GlobalData gbd, SessionData ssd) {
		DocOffice signInOffice = (DocOffice)ssd.get("signInOffice")
		if(signInUser == null) return null
		DocMinion mnnOffice = gbd.get(DocMinionDao.class).selectBySeq(signInOffice.refSeq, true)
		if(mnnOffice != null) {
			Office signInOfficeFull = My.deminion(mnnOffice.minion, Office.class)
			BeanUtils.copyProperties(signInOffice, signInOfficeFull)
		}
		return signInOffice
	}

	DocTeam getSignInTeam(GlobalData gbd, SessionData ssd) {
		DocTeam signInTeam = (DocTeam)ssd.get("signInTeam")
		if(signInUser == null) return null
		DocMinion mnnTeam = gbd.get(DocMinionDao.class).selectBySeq(signInTeam.refSeq, true)
		if(mnnTeam != null) {
			Team signInTeamFull = My.deminion(mnnTeam.minion, Team.class)
			BeanUtils.copyProperties(signInTeam, signInTeamFull)
		}
		return signInTeam
	}

	DocUser getTargetUser(GlobalData gbd, SessionData ssd, DocTeam signInTeam) {
		DocUser tgtUser = signInTeam.getTeamTarget().getUserList().get(0)
		if(signInUser == null) return null
		DocMinion mnnUser = gbd.get(DocMinionDao.class).selectBySeq(tgtUser.refSeq, true)
		if(mnnUser != null) {
			User tgtUserFull = My.deminion(mnnUser.minion, User.class)
			BeanUtils.copyProperties(tgtUser, tgtUserFull)
		}
		return tgtUser
	}

	String getPdfFileName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		Date now = new Date()
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyyMMdd_HHmm")
		return (getName(gbd, ssd, rqd, rpd) + "_" + sdfYMDHMS.format(now) + ".pdf").replaceAll("[ 　]", "_")
	}

	int search(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		List<DocMinion> mnnList = gbd.get(DocMinionDao.class).selectBySKey(getSKey(), true, false)
		if(mnnList.size() == 0) {
			// cache.addLine("0件")
		} else {
			for(DocMinion mnn : mnnList) {
				Object obj = My.deminion(mnn.minion, getClass())
				BeanUtils.copyProperties(this, obj)
				BeanUtils.copyProperties(this, mnn)
				te.appendParsedTemplate(this, getClass(), cache,
					"tabNewAndSearchTBodyTr", My.hs(args + ":" + mnn.seq), indent,
					isForced, Time14.OLD)
				clear(args, gbd, ssd, rqd, rpd)
			}
		}

		return mnnList.size()
	}

	private void path2Seq() {
		seq = 0;
		try {
			Pattern pt = Pattern.compile("/(\\d+)");
			Matcher mc = pt.matcher(path);
			if(mc.matches()) {
				seq = Long.parseLong(mc.group(1))
			}
		} catch(NumberFormatException nfe) {}
	}

	boolean load(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		clear(args, gbd, ssd, rqd, rpd)

		path2Seq()
		if(seq == 0) {
			return false
		}

		DocMinion mnnObj = gbd.get(DocMinionDao.class).selectBySeq(seq, true)
		if(mnnObj == null) {
			return false
		} else {
			System.out.println(getClass().getName())
			Object obj = My.deminion(mnnObj.minion, getClass())
			BeanUtils.copyProperties(this, obj)
			BeanUtils.copyProperties(this, mnnObj)
			return true
		}
	}

	boolean confirm(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced) {
		path2Seq()

		return true
	}

	boolean regist(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced) {
		path2Seq()

		// 新しい世代
		DocUser signInUser = (DocUser)ssd.get("signInUser")
		DocOffice signInOffice = (DocOffice)ssd.get("signInOffice")
		DocTeam signInTeam = (DocTeam)ssd.get("signInTeam")
		DocMinion dscdt = new DocMinion(getSKey(), "mnn", this,
			signInUser, signInOffice, signInTeam, seq, null, null)
		gbd.get(DocMinionDao.class).insert(dscdt)

		// 以前の世代
		DocMinion acstr = gbd.get(DocMinionDao.class).selectBySeq(seq, true)
		acstr.setGone(signInUser, signInOffice, signInTeam, dscdt.seq)
		gbd.get(DocMinionDao.class).update(acstr)

		return true
	}

	@Override
	String initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		super.initialize(args, gbd, ssd, rqd, rpd)
		clear(args, gbd, ssd, rqd, rpd)

		navIconActivateDoc = "navIconActivate"
		return null
	}

	Object doGoTabEditNewly(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		clear(args, gbd, ssd, rqd, rpd)
		DocUser signInUser = (DocUser)ssd.get("signInUser")
		DocOffice signInOffice = (DocOffice)ssd.get("signInOffice")
		DocTeam signInTeam = (DocTeam)ssd.get("signInTeam")
		DocMinion mnn = new DocMinion(getSKey(), "mnn", this,
			signInUser, signInOffice, signInTeam, null, null, null)
		gbd.get(DocMinionDao.class).insert(mnn)
		logger.info(String.format("new %s inserted to %s\n", mnn.sKey, mnn.seq))
		BeanUtils.copyProperties(this, mnn)

		tabSelected = "tabEdit"
		def args2 = ssd.getSessionID() + "," + act + "," + path
		ssd.takeOver(this, "tabEditBody", args2)
		ssd.takeOver(this, "tabViewBody", args2)
		ssd.takeOver(this, "sheets", args2)

		return act + "." + ext + "/" + mnn.seq + "?tabSelected=" + tabSelected // ToDo: getパラメータじゃなくしたい
	}

	Object doGoTabEdit(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		load(args, gbd, ssd, rqd, rpd)

		tabSelected = "tabEdit"
		def args2 = ssd.getSessionID() + "," + act + "," + path
		ssd.takeOver(this, "tabEditBody", args2)
		ssd.takeOver(this, "tabViewBody", args2)
		ssd.takeOver(this, "sheets", args2)

		return act + "." + ext + (StringUtils.isBlank(path) ? "" : path) + "?tabSelected=" + tabSelected
	}

	Object doGoTabView(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		load(args, gbd, ssd, rqd, rpd)

		tabSelected = "tabView"
		def args2 = ssd.getSessionID() + "," + act + "," + path
		ssd.takeOver(this, "tabEditBody", args2)
		ssd.takeOver(this, "tabViewBody", args2)
		ssd.takeOver(this, "sheets", args2)

		return act + "." + ext + (StringUtils.isBlank(path) ? "" : path) + "?tabSelected=" + tabSelected
	}

	Object doGoTabPrint(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		load(args, gbd, ssd, rqd, rpd)

		tabSelected = "tabPrint"
		def args2 = ssd.getSessionID() + "," + act + "," + path
		ssd.takeOver(this, "tabEditBody", args2)
		ssd.takeOver(this, "tabViewBody", args2)
		ssd.takeOver(this, "sheets", args2)

		return act + "." + ext + (StringUtils.isBlank(path) ? "" : path) + "?tabSelected=" + tabSelected
	}

	Object doGoTabWorkflow(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		load(args, gbd, ssd, rqd, rpd)

		tabSelected = "tabWorkflow"
		def args2 = ssd.getSessionID() + "," + act + "," + path
		ssd.takeOver(this, "tabEditBody", args2)
		ssd.takeOver(this, "tabViewBody", args2)
		ssd.takeOver(this, "sheets", args2)

		return act + "." + ext + (StringUtils.isBlank(path) ? "" : path) + "?tabSelected=" + tabSelected
	}

	Object doGoTabComment(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		load(args, gbd, ssd, rqd, rpd)

		tabSelected = "tabComment"
		def args2 = ssd.getSessionID() + "," + act + "," + path
		ssd.takeOver(this, "tabEditBody", args2)
		ssd.takeOver(this, "tabViewBody", args2)
		ssd.takeOver(this, "sheets", args2)

		return act + "." + ext + (StringUtils.isBlank(path) ? "" : path) + "?tabSelected=" + tabSelected
	}

	Object doGoTabEditWithCopy(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		load(args, gbd, ssd, rqd, rpd)

		tabSelected = "tabEdit"
		def args2 = ssd.getSessionID() + "," + act + "," + path
		ssd.takeOver(this, "tabEditBody", args2)
		ssd.takeOver(this, "tabViewBody", args2)
		ssd.takeOver(this, "sheets", args2)

		return act + "." + ext + (StringUtils.isBlank(path) ? "" : path) + "?tabSelected=" + tabSelected
	}

	Object doGoTabViewWithDelete(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		load(args, gbd, ssd, rqd, rpd)

		tabSelected = "tabView"
		def args2 = ssd.getSessionID() + "," + act + "," + path
		ssd.takeOver(this, "tabEditBody", args2)
		ssd.takeOver(this, "tabViewBody", args2)
		ssd.takeOver(this, "sheets", args2)

		return act + "." + ext + (StringUtils.isBlank(path) ? "" : path) + "?tabSelected=" + tabSelected
	}

	Object doGoTabNewAndSearch(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		tabSelected = "tabNewAndSearch"
		return act + "." + ext
	}

	Object doReset(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		load(args, gbd, ssd, rqd, rpd)

		def args2 = ssd.getSessionID() + "," + act + "," + path
		ssd.takeOver(this, "tabEditBody", args2)
		ssd.takeOver(this, "tabViewBody", args2)
		ssd.takeOver(this, "sheets", args2)

		return act + "." + ext + (StringUtils.isBlank(path) ? "" : path)
	}

	Object doWebPreview(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		confirm(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)

		tabSelected = "tabView"
		def args2 = ssd.getSessionID() + "," + act + "," + path // ToDo: argsの時点ではACTとPATHが置き換えられていない
		ssd.takeOver(this, "tabEditBody", args2)
		ssd.takeOver(this, "tabViewBody", args2)
		ssd.takeOver(this, "sheets", args2)

		return act + "." + ext + (StringUtils.isBlank(path) ? "" : path) + "?tabSelected=" + tabSelected
	}

	Object doPrintPreview(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		confirm(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)

		tabSelected = "tabPrint"
		def args2 = ssd.getSessionID() + "," + act + "," + path
		ssd.takeOver(this, "tabEditBody", args2)
		ssd.takeOver(this, "tabViewBody", args2)
		ssd.takeOver(this, "sheets", args2)

		return act + "." + ext + (StringUtils.isBlank(path) ? "" : path) + "?tabSelected=" + tabSelected
	}

	Object doRegist(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		confirm(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
		regist(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)

		tabSelected = "tabNewAndSearch"
		return act + "." + ext + "?tabSelected=" + tabSelected
	}

	Object doPrint(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		confirm(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)

		String html = te.parseTemplate("", "print", My.hs(args), "\n", true)
		String wkhtmltopdfPath = gbd.get("WKHTMLTOPDF_PATH").toString()
		WrapperConfig wrapperConfig = new WrapperConfig(wkhtmltopdfPath)
		Pdf pdf = new Pdf(wrapperConfig)
		pdf.addPageFromString(html)
		byte[] bytes = pdf.getPDF()
		downloadFile(bytes, getPdfFileName(gbd, ssd, rqd, rpd), "application/pdf", rqd.getRequest(), rpd.getResponse())

		return null
	}

	Object doDelete(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		return null
	}

}
