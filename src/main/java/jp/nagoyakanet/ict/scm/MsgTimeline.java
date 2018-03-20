package jp.nagoyakanet.ict.scm;

import java.io.Serializable;
import java.util.Date;

import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.jdbc.entity.NamingType;

@Entity(naming = NamingType.SNAKE_UPPER_CASE)
public class MsgTimeline implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long seq;
	public String displayName;
	public String kind;
	public String body;
	public String icon;
	public Date createdAt;
	public Long createdByUserSeq;
	public String createdByUserCd;
	public String createdByUserNm;
	public Long createdByOfficeSeq;
	public String createdByOfficeCd;
	public String createdByOfficeNm;
	public Long createdByTeamSeq;
	public String createdByTeamCd;
	public String createdByTeamNm;
	public Date updatedAt;
	public Long updatedByUserSeq;
	public String updatedByUserCd;
	public String updatedByUserNm;
	public Long updatedByOfficeSeq;
	public String updatedByOfficeCd;
	public String updatedByOfficeNm;
	public Long updatedByTeamSeq;
	public String updatedByTeamCd;
	public String updatedByTeamNm;
	public Date expiredAt;
	public Long expiredBy;

	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Long getCreatedByUserSeq() {
		return createdByUserSeq;
	}
	public void setCreatedByUserSeq(Long createdByUserSeq) {
		this.createdByUserSeq = createdByUserSeq;
	}
	public String getCreatedByUserCd() {
		return createdByUserCd;
	}
	public void setCreatedByUserCd(String createdByUserCd) {
		this.createdByUserCd = createdByUserCd;
	}
	public String getCreatedByUserNm() {
		return createdByUserNm;
	}
	public void setCreatedByUserNm(String createdByUserNm) {
		this.createdByUserNm = createdByUserNm;
	}
	public Long getCreatedByOfficeSeq() {
		return createdByOfficeSeq;
	}
	public void setCreatedByOfficeSeq(Long createdByOfficeSeq) {
		this.createdByOfficeSeq = createdByOfficeSeq;
	}
	public String getCreatedByOfficeCd() {
		return createdByOfficeCd;
	}
	public void setCreatedByOfficeCd(String createdByOfficeCd) {
		this.createdByOfficeCd = createdByOfficeCd;
	}
	public String getCreatedByOfficeNm() {
		return createdByOfficeNm;
	}
	public void setCreatedByOfficeNm(String createdByOfficeNm) {
		this.createdByOfficeNm = createdByOfficeNm;
	}
	public Long getCreatedByTeamSeq() {
		return createdByTeamSeq;
	}
	public void setCreatedByTeamSeq(Long createdByTeamSeq) {
		this.createdByTeamSeq = createdByTeamSeq;
	}
	public String getCreatedByTeamCd() {
		return createdByTeamCd;
	}
	public void setCreatedByTeamCd(String createdByTeamCd) {
		this.createdByTeamCd = createdByTeamCd;
	}
	public String getCreatedByTeamNm() {
		return createdByTeamNm;
	}
	public void setCreatedByTeamNm(String createdByTeamNm) {
		this.createdByTeamNm = createdByTeamNm;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Long getUpdatedByUserSeq() {
		return updatedByUserSeq;
	}
	public void setUpdatedByUserSeq(Long updatedByUserSeq) {
		this.updatedByUserSeq = updatedByUserSeq;
	}
	public String getUpdatedByUserCd() {
		return updatedByUserCd;
	}
	public void setUpdatedByUserCd(String updatedByUserCd) {
		this.updatedByUserCd = updatedByUserCd;
	}
	public String getUpdatedByUserNm() {
		return updatedByUserNm;
	}
	public void setUpdatedByUserNm(String updatedByUserNm) {
		this.updatedByUserNm = updatedByUserNm;
	}
	public Long getUpdatedByOfficeSeq() {
		return updatedByOfficeSeq;
	}
	public void setUpdatedByOfficeSeq(Long updatedByOfficeSeq) {
		this.updatedByOfficeSeq = updatedByOfficeSeq;
	}
	public String getUpdatedByOfficeCd() {
		return updatedByOfficeCd;
	}
	public void setUpdatedByOfficeCd(String updatedByOfficeCd) {
		this.updatedByOfficeCd = updatedByOfficeCd;
	}
	public String getUpdatedByOfficeNm() {
		return updatedByOfficeNm;
	}
	public void setUpdatedByOfficeNm(String updatedByOfficeNm) {
		this.updatedByOfficeNm = updatedByOfficeNm;
	}
	public Long getUpdatedByTeamSeq() {
		return updatedByTeamSeq;
	}
	public void setUpdatedByTeamSeq(Long updatedByTeamSeq) {
		this.updatedByTeamSeq = updatedByTeamSeq;
	}
	public String getUpdatedByTeamCd() {
		return updatedByTeamCd;
	}
	public void setUpdatedByTeamCd(String updatedByTeamCd) {
		this.updatedByTeamCd = updatedByTeamCd;
	}
	public String getUpdatedByTeamNm() {
		return updatedByTeamNm;
	}
	public void setUpdatedByTeamNm(String updatedByTeamNm) {
		this.updatedByTeamNm = updatedByTeamNm;
	}
	public Date getExpiredAt() {
		return expiredAt;
	}
	public void setExpiredAt(Date expiredAt) {
		this.expiredAt = expiredAt;
	}
	public Long getExpiredBy() {
		return expiredBy;
	}
	public void setExpiredBy(Long expiredBy) {
		this.expiredBy = expiredBy;
	}

}
