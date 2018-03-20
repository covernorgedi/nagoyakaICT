package jp.nagoyakanet.ict.plugin

import java.util.Date

import jp.nagoyakanet.ict.dao.DocMinionDao
import jp.nagoyakanet.ict.dao.DocUserDao
import jp.nagoyakanet.ict.dao.MessagingSessionDao
import jp.nagoyakanet.ict.dao.SignInSessionDao
import jp.nagoyakanet.ict.plugin.reg.basic.User
import jp.nagoyakanet.ict.scm.DocMinion
import jp.nagoyakanet.ict.scm.DocOffice
import jp.nagoyakanet.ict.scm.DocTeam
import jp.nagoyakanet.ict.scm.DocUser
import jp.nagoyakanet.ict.scm.MessagingSession
import jp.nagoyakanet.ict.scm.SignInSession
import org.apache.commons.beanutils.BeanUtils
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
import org.kyojo.minion.My

class SignIn extends AbstractExternalPage {

	private static final Log logger = LogFactory.getLog(SignIn.class)

	String signInId
	transient String signInPwd
	String trStyleMessage
	String signInMessage
	Integer failCnt

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"サインインページ"
	}

	// String getDKey(String args,
	//	GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	//	return My.hs(failCnt + "_" + signInId);
	// }

	@Override
	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		super.initialize(args, gbd, ssd, rqd, rpd)

		failCnt = 0
		trStyleMessage = "display:none;"
		return null
	}

	Object doSignIn2(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		DocUser signInUser = gbd.get(DocUserDao.class).selectByCodeAndPassword(signInId, signInPwd)
		if(signInUser != null) {
			signInUser.password = ""
			ssd.put("signInId", signInId)
			ssd.put("signInUser", signInUser)
			DocOffice signInOffice = null
			if(signInUser.officeMemberOf.officeList.size() > 0) {
				signInOffice = signInUser.officeMemberOf.officeList[0]
			}
			ssd.put("signInOffice", signInOffice)
			DocTeam signInTeam = null
			if(signInUser.teamMemberOf.teamList.size() > 0) {
				signInTeam = signInUser.teamMemberOf.teamList[0]
			}
			ssd.put("signInTeam", signInTeam)

			DocMinion mnnUser = gbd.get(DocMinionDao.class).selectBySeq(signInUser.refSeq, true)
			if(mnnUser != null) {
				User userFull = My.deminion(mnnUser.minion, User.class)
				ssd.put("signInUserFull", userFull)
				BeanUtils.copyProperties(signInUser, userFull)
			}

			SignInSession signInSession = new SignInSession()
			signInSession.signInId = signInId
			signInSession.sessionId = ssd.getSessionID()
			signInSession.createdAt = new Date()
			signInSession.updatedAt = signInSession.createdAt
			gbd.get(SignInSessionDao.class).insert(signInSession)
			logger.info(String.format("sign in succeeded. signInId=\"%s\",sid=\"%s\",name=\"%s\",office=\"%s\"",
				signInId, signInSession.sessionId, signInUser.getName().getNativeValue(),
				signInOffice == null ? "" : signInOffice.getName().getNativeValue()))

			return Portal.class
		}

		failCnt++;
		trStyleMessage = ""
		signInMessage = "該当するアカウントが存在しません。もう一度ご確認の上、入力してください。"
		logger.info(String.format("sign in failed. signInId=\"%s\",sid=\"%s\"", signInId, ssd.getSessionID()))
		return null
	}

}
