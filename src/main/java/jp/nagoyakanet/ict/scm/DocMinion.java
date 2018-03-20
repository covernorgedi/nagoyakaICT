package jp.nagoyakanet.ict.scm;

import java.io.Serializable;
import java.util.Date;

import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.jdbc.entity.NamingType;

@Entity(naming = NamingType.SNAKE_UPPER_CASE)
public class DocMinion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long seq;
	public String sKey;
	public String dKey;
	public String eKey;
	public String minion;
	public Date comeAt;
	public Long comeByUserSeq;
	public String comeByUserCd;
	public String comeByUserNm;
	public Long comeByOfficeSeq;
	public String comeByOfficeCd;
	public String comeByOfficeNm;
	public Long comeByTeamSeq;
	public String comeByTeamCd;
	public String comeByTeamNm;
	public Date goneAt;
	public Long goneByUserSeq;
	public String goneByUserCd;
	public String goneByUserNm;
	public Long goneByOfficeSeq;
	public String goneByOfficeCd;
	public String goneByOfficeNm;
	public Long goneByTeamSeq;
	public String goneByTeamCd;
	public String goneByTeamNm;
	public Long progenitor;
	public Long ancestor;
	public Long descendant;
	public Long lifetime;
	public Date cleanedAt;

	public DocMinion() {}

	public DocMinion(String sKey, String eKey, Object obj,
			DocUser signInUser, DocOffice signInOffice, DocTeam signInTeam,
			Long ancestor, Long lifetime, Date cleanedAt) {
		minion = SimpleJsonBuilder.toJson(obj);
		this.sKey = sKey;
		this.dKey = My.hs(minion);
		this.eKey = eKey;

		setCome(signInUser, signInOffice, signInTeam, ancestor);
		this.lifetime = lifetime;
		this.cleanedAt = cleanedAt;
	}

	public void setCome(DocUser signInUser, DocOffice signInOffice, DocTeam signInTeam, Long ancestor) {
		comeAt = new Date();
		comeByUserSeq = signInUser.getSeq();
		comeByUserCd = signInUser.getCode();
		comeByUserNm = signInUser.getName().getNativeValue();
		if(signInOffice != null) {
			comeByOfficeSeq = signInOffice.getSeq();
			comeByOfficeCd = signInOffice.getCode();
			comeByOfficeNm = signInOffice.getName().getNativeValue();
		}
		if(signInTeam != null) {
			comeByTeamSeq = signInTeam.getSeq();
			comeByTeamCd = signInTeam.getCode();
			comeByTeamNm = signInTeam.getName().getNativeValue();
		}
		this.ancestor = ancestor;
	}

	public void setGone(DocUser signInUser, DocOffice signInOffice, DocTeam signInTeam, Long descendant) {
		goneAt = new Date();
		goneByUserSeq = signInUser.getSeq();
		goneByUserCd = signInUser.getCode();
		goneByUserNm = signInUser.getName().getNativeValue();
		if(signInOffice != null) {
			goneByOfficeSeq = signInOffice.getSeq();
			goneByOfficeCd = signInOffice.getCode();
			goneByOfficeNm = signInOffice.getName().getNativeValue();
		}
		if(signInTeam != null) {
			goneByTeamSeq = signInTeam.getSeq();
			goneByTeamCd = signInTeam.getCode();
			goneByTeamNm = signInTeam.getName().getNativeValue();
		}
		this.descendant = descendant;
	}

	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	public String getsKey() {
		return sKey;
	}
	public void setsKey(String sKey) {
		this.sKey = sKey;
	}
	public String getdKey() {
		return dKey;
	}
	public void setdKey(String dKey) {
		this.dKey = dKey;
	}
	public String geteKey() {
		return eKey;
	}
	public void seteKey(String eKey) {
		this.eKey = eKey;
	}
	public String getMinion() {
		return minion;
	}
	public void setMinion(String minion) {
		this.minion = minion;
	}
	public Date getComeAt() {
		return comeAt;
	}
	public void setComeAt(Date comeAt) {
		this.comeAt = comeAt;
	}
	public Long getComeByUserSeq() {
		return comeByUserSeq;
	}
	public void setComeByUserSeq(Long comeByUserSeq) {
		this.comeByUserSeq = comeByUserSeq;
	}
	public String getComeByUserCd() {
		return comeByUserCd;
	}
	public void setComeByUserCd(String comeByUserCd) {
		this.comeByUserCd = comeByUserCd;
	}
	public String getComeByUserNm() {
		return comeByUserNm;
	}
	public void setComeByUserNm(String comeByUserNm) {
		this.comeByUserNm = comeByUserNm;
	}
	public Long getComeByOfficeSeq() {
		return comeByOfficeSeq;
	}
	public void setComeByOfficeSeq(Long comeByOfficeSeq) {
		this.comeByOfficeSeq = comeByOfficeSeq;
	}
	public String getComeByOfficeCd() {
		return comeByOfficeCd;
	}
	public void setComeByOfficeCd(String comeByOfficeCd) {
		this.comeByOfficeCd = comeByOfficeCd;
	}
	public String getComeByOfficeNm() {
		return comeByOfficeNm;
	}
	public void setComeByOfficeNm(String comeByOfficeNm) {
		this.comeByOfficeNm = comeByOfficeNm;
	}
	public Long getComeByTeamSeq() {
		return comeByTeamSeq;
	}
	public void setComeByTeamSeq(Long comeByTeamSeq) {
		this.comeByTeamSeq = comeByTeamSeq;
	}
	public String getComeByTeamCd() {
		return comeByTeamCd;
	}
	public void setComeByTeamCd(String comeByTeamCd) {
		this.comeByTeamCd = comeByTeamCd;
	}
	public String getComeByTeamNm() {
		return comeByTeamNm;
	}
	public void setComeByTeamNm(String comeByTeamNm) {
		this.comeByTeamNm = comeByTeamNm;
	}
	public Date getGoneAt() {
		return goneAt;
	}
	public void setGoneAt(Date goneAt) {
		this.goneAt = goneAt;
	}
	public Long getGoneByUserSeq() {
		return goneByUserSeq;
	}
	public void setGoneByUserSeq(Long goneByUserSeq) {
		this.goneByUserSeq = goneByUserSeq;
	}
	public String getGoneByUserCd() {
		return goneByUserCd;
	}
	public void setGoneByUserCd(String goneByUserCd) {
		this.goneByUserCd = goneByUserCd;
	}
	public String getGoneByUserNm() {
		return goneByUserNm;
	}
	public void setGoneByUserNm(String goneByUserNm) {
		this.goneByUserNm = goneByUserNm;
	}
	public Long getGoneByOfficeSeq() {
		return goneByOfficeSeq;
	}
	public void setGoneByOfficeSeq(Long goneByOfficeSeq) {
		this.goneByOfficeSeq = goneByOfficeSeq;
	}
	public String getGoneByOfficeCd() {
		return goneByOfficeCd;
	}
	public void setGoneByOfficeCd(String goneByOfficeCd) {
		this.goneByOfficeCd = goneByOfficeCd;
	}
	public String getGoneByOfficeNm() {
		return goneByOfficeNm;
	}
	public void setGoneByOfficeNm(String goneByOfficeNm) {
		this.goneByOfficeNm = goneByOfficeNm;
	}
	public Long getGoneByTeamSeq() {
		return goneByTeamSeq;
	}
	public void setGoneByTeamSeq(Long goneByTeamSeq) {
		this.goneByTeamSeq = goneByTeamSeq;
	}
	public String getGoneByTeamCd() {
		return goneByTeamCd;
	}
	public void setGoneByTeamCd(String goneByTeamCd) {
		this.goneByTeamCd = goneByTeamCd;
	}
	public String getGoneByTeamNm() {
		return goneByTeamNm;
	}
	public void setGoneByTeamNm(String goneByTeamNm) {
		this.goneByTeamNm = goneByTeamNm;
	}
	public Long getProgenitor() {
		return progenitor;
	}
	public void setProgenitor(Long progenitor) {
		this.progenitor = progenitor;
	}
	public Long getAncestor() {
		return ancestor;
	}
	public void setAncestor(Long ancestor) {
		this.ancestor = ancestor;
	}
	public Long getDescendant() {
		return descendant;
	}
	public void setDescendant(Long descendant) {
		this.descendant = descendant;
	}
	public Long getLifetime() {
		return lifetime;
	}
	public void setLifetime(Long lifetime) {
		this.lifetime = lifetime;
	}
	public Date getCleanedAt() {
		return cleanedAt;
	}
	public void setCleanedAt(Date cleanedAt) {
		this.cleanedAt = cleanedAt;
	}

}
