package jp.nagoyakanet.ict.scm;

import java.io.Serializable;
import java.util.Date;

import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.jdbc.entity.NamingType;

@Entity(naming = NamingType.SNAKE_UPPER_CASE)
public class TlMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Kind {
		NOTICE("notice", "お知らせ"),
		SCHEDULE("schedule", "スケジュール"),
		MESSAGE("message", "メッセージ"),
		USER_STATUS("userStatus", "利用者状態"),
		USER_ACTVITY("userActvity", "利用者行動"),
		FEED("feed", "フィード"),
		WORKFLOW("workflow", "ワークフロー");

		private final String code;

		private final String name;

		private Kind(final String code, final String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static Kind getEnumByCode(final String code) {
			final Kind[] kinds = values();
			for(final Kind kind : kinds) {
				if(kind.code.equals(code)) {
					return kind;
				}
			}
			return null;
		}

		public static String getNameByCode(final String code) {
			Kind kind = getEnumByCode(code);
			if(kind != null) {
				return kind.name;
			}
			return null;
		}

	}

	public enum SubKind {
		ONLINE("online", "オンライン"),
		OFFLINE("offline", "オフライン");

		private final String code;

		private final String name;

		private SubKind(final String code, final String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static SubKind getEnumByCode(final String code) {
			final SubKind[] subKinds = values();
			for(final SubKind subKind : subKinds) {
				if(subKind.code.equals(code)) {
					return subKind;
				}
			}
			return null;
		}

		public static String getNameByCode(final String code) {
			SubKind subKind = getEnumByCode(code);
			if(subKind != null) {
				return subKind.name;
			}
			return null;
		}

	}

	public enum Emergency {
		EMERGENT("E", "緊急", 10),
		SOON("S", "至急", 1000/*50*/),
		REPORT("R", "報告", 1000/*500*/),
		INFORM("I", "連絡", 1000/*500*/),
		CONSULT("C", "相談", 1000/*500*/),
		NORMAL("N", "通常", 1000/*1000*/),
		WHENEVER("W", "不急", 1000/*5000*/);

		private final String code;

		private final String name;

		private final Integer emgSort;

		private Emergency(final String code, final String name, final Integer emgSort) {
			this.code = code;
			this.name = name;
			this.emgSort = emgSort;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public Integer getEmgSort() {
			return emgSort;
		}

		public static Emergency getEnumByCode(final String code) {
			final Emergency[] emergencies = values();
			for(final Emergency emergency : emergencies) {
				if(emergency.code.equals(code)) {
					return emergency;
				}
			}
			return null;
		}

		public static String getNameByCode(final String code) {
			final Emergency emergency = getEnumByCode(code);
			if(emergency != null) {
				return emergency.name;
			}
			return null;
		}

		public static Integer getEmgSortByCode(final String code) {
			final Emergency emergency = getEnumByCode(code);
			if(emergency != null) {
				return emergency.emgSort;
			}
			return null;
		}

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long seq;
	public String displayName;
	public String kind;
	public String subKind;
	public String body;
	public String icon;
	public DocAgent messageTo;
	public String emergency;
	public Integer emgSort;
	public Date createdAt;
	public Long createdBy;
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
	public Long shareWithUserSeq;
	public String shareWithUserCd;
	public String shareWithUserNm;
	public Long shareWithOfficeSeq;
	public String shareWithOfficeCd;
	public String shareWithOfficeNm;
	public Long shareWithTeamSeq;
	public String shareWithTeamCd;
	public String shareWithTeamNm;
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
	public String getSubKind() {
		return subKind;
	}
	public void setSubKind(String subKind) {
		this.subKind = subKind;
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
	public DocAgent getMessageTo() {
		return messageTo;
	}
	public void setMessageTo(DocAgent messageTo) {
		this.messageTo = messageTo;
	}
	public String getEmergency() {
		return emergency;
	}
	public void setEmergency(String emergency) {
		this.emergency = emergency;
	}
	public Integer getEmgSort() {
		return emgSort;
	}
	public void setEmgSort(Integer emgSort) {
		this.emgSort = emgSort;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
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
	public Long getShareWithUserSeq() {
		return shareWithUserSeq;
	}
	public void setShareWithUserSeq(Long shareWithUserSeq) {
		this.shareWithUserSeq = shareWithUserSeq;
	}
	public String getShareWithUserCd() {
		return shareWithUserCd;
	}
	public void setShareWithUserCd(String shareWithUserCd) {
		this.shareWithUserCd = shareWithUserCd;
	}
	public String getShareWithUserNm() {
		return shareWithUserNm;
	}
	public void setShareWithUserNm(String shareWithUserNm) {
		this.shareWithUserNm = shareWithUserNm;
	}
	public Long getShareWithOfficeSeq() {
		return shareWithOfficeSeq;
	}
	public void setShareWithOfficeSeq(Long shareWithOfficeSeq) {
		this.shareWithOfficeSeq = shareWithOfficeSeq;
	}
	public String getShareWithOfficeCd() {
		return shareWithOfficeCd;
	}
	public void setShareWithOfficeCd(String shareWithOfficeCd) {
		this.shareWithOfficeCd = shareWithOfficeCd;
	}
	public String getShareWithOfficeNm() {
		return shareWithOfficeNm;
	}
	public void setShareWithOfficeNm(String shareWithOfficeNm) {
		this.shareWithOfficeNm = shareWithOfficeNm;
	}
	public Long getShareWithTeamSeq() {
		return shareWithTeamSeq;
	}
	public void setShareWithTeamSeq(Long shareWithTeamSeq) {
		this.shareWithTeamSeq = shareWithTeamSeq;
	}
	public String getShareWithTeamCd() {
		return shareWithTeamCd;
	}
	public void setShareWithTeamCd(String shareWithTeamCd) {
		this.shareWithTeamCd = shareWithTeamCd;
	}
	public String getShareWithTeamNm() {
		return shareWithTeamNm;
	}
	public void setShareWithTeamNm(String shareWithTeamNm) {
		this.shareWithTeamNm = shareWithTeamNm;
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
