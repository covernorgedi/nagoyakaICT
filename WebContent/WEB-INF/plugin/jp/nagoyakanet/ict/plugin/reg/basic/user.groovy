package jp.nagoyakanet.ict.plugin.reg.basic

import jp.nagoyakanet.ict.dao.DocMediaDao
import jp.nagoyakanet.ict.plugin.AbstractRegistryPage
import jp.nagoyakanet.ict.plugin.cmn.LangJa
import jp.nagoyakanet.ict.scm.DocHasOccupation
import jp.nagoyakanet.ict.scm.DocMedia
import jp.nagoyakanet.ict.scm.DocOccupation
import jp.nagoyakanet.ict.scm.DocOffice
import jp.nagoyakanet.ict.scm.DocOfficeMemberOf
import jp.nagoyakanet.ict.scm.DocTeam
import jp.nagoyakanet.ict.scm.DocTeamMemberOf
import jp.nagoyakanet.ict.scm.DocUser

import java.util.Date

import org.apache.commons.fileupload.FileItem
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.kyojo.core.Cache
import org.kyojo.core.Constants
import org.kyojo.core.GlobalData
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.gbd.AppConfig
import org.kyojo.schemaOrg.m3n3.core.Clazz.JoinAction
import org.kyojo.schemaOrg.m3n3.core.Clazz.LeaveAction
import org.kyojo.schemaOrg.m3n3.core.Container.Address
import org.kyojo.schemaOrg.m3n3.core.Container.BirthDate
import org.kyojo.schemaOrg.m3n3.core.Container.Email
import org.kyojo.schemaOrg.m3n3.core.Container.Gender
import org.kyojo.schemaOrg.m3n3.core.Container.Image
import org.kyojo.schemaOrg.m3n3.core.Container.Name
import org.kyojo.schemaOrg.m3n3.core.Container.NameRuby
import org.kyojo.schemaOrg.m3n3.core.Container.Telephone
import org.kyojo.schemaOrg.m3n3.core.impl.ADDRESS
import org.kyojo.schemaOrg.m3n3.core.impl.BIRTH_DATE
import org.kyojo.schemaOrg.m3n3.core.impl.EMAIL
import org.kyojo.schemaOrg.m3n3.core.impl.GENDER
import org.kyojo.schemaOrg.m3n3.core.impl.IMAGE
import org.kyojo.schemaOrg.m3n3.core.impl.JOIN_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.LEAVE_ACTION
import org.kyojo.schemaOrg.m3n3.core.impl.NAME
import org.kyojo.schemaOrg.m3n3.core.impl.NAME_RUBY
import org.kyojo.schemaOrg.m3n3.core.impl.TELEPHONE
import org.kyojo.schemaOrg.m3n3.core.impl.URL
import org.seasar.doma.jdbc.tx.TransactionManager

class User extends AbstractRegistryPage {

	private static final Log logger = LogFactory.getLog(User.class)

	String code
	String password
	Name name
	NameRuby nameRuby
	DocTeamMemberOf teamMemberOf
	DocOfficeMemberOf officeMemberOf
	DocHasOccupation hasOccupation
	Gender gender
	BirthDate birthDate
	Telephone telephone
	Email email
	Address address
	Image image
	String remarks
	JoinAction joinAction
	LeaveAction leaveAction
	String faceIconURI
	transient FileItem faceIconFile

	@Override
	String[] getMiddleMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String[] getMinorMenu(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
	}

	@Override
	String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		"利用者"
	}

	@Override
	void clear(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		super.clear(args, gbd, ssd, rqd, rpd)
		clear()
	}

	void clear() {
		seq = null
		code = ""
		password = ""
		name = new NAME()
		nameRuby = new NAME_RUBY()
		teamMemberOf = new DocTeamMemberOf()
		officeMemberOf = new DocOfficeMemberOf()
		hasOccupation = new DocHasOccupation()
		gender = new GENDER()
		birthDate = new BIRTH_DATE()
		telephone = new TELEPHONE()
		email = new EMAIL()
		address = new ADDRESS()
		image = new IMAGE()
		joinAction = new JOIN_ACTION()
		leaveAction = new LEAVE_ACTION()
	}

	@Override
	boolean load(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		boolean res = super.load(args, gbd, ssd, rqd, rpd)

		password = ""

		if(hasOccupation != null && hasOccupation.occupationList != null && hasOccupation.occupationList.size() > 0) {
			for(DocOccupation occupation : hasOccupation.occupationList) {
				if(LangJa.lang.containsKey(occupation.getCode())) {
					occupation.setName(new NAME(LangJa.lang.get(occupation.getCode())))
				}
			}
		}

		if(image != null && image.getURLList() != null
				&& image.getURLList().size() > 0
				&& image.getURLList().get(0) != null) {
			faceIconURI = gbd.get("BASE_URI") + image.getURLList().get(0).getNativeValue()
		} else {
			faceIconURI = gbd.get("BASE_URI") + "/img/sample/someone.png"
		}

		return res
	}

	@Override
	boolean confirm(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced) {
		if(faceIconFile != null && faceIconFile.getSize() > 0) {
			DocUser signInUser = (DocUser)ssd.get("signInUser")
			DocOffice signInOffice = (DocOffice)ssd.get("signInOffice")
			DocTeam signInTeam = (DocTeam)ssd.get("signInTeam")
			final DocMedia docMedia = new DocMedia(faceIconFile.get(), faceIconFile.getName(),
					signInUser, signInOffice, signInTeam, null, null, null);
			TransactionManager tm = AppConfig.singleton().getTransactionManager()
			tm.required {
				gbd.get(DocMediaDao.class).insert(docMedia)
			}

			String path = "/doc/media" + docMedia.path + "_m." + docMedia.ext
			image = new IMAGE(new URL(path))
			faceIconURI = gbd.get("BASE_URI") + path
		}

		boolean res = super.confirm(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
		return res
	}

}
