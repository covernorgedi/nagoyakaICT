package jp.nagoyakanet.ict.scm;

import java.util.List;

import org.kyojo.schemaOrg.m3n3.core.Container;
import org.kyojo.schemaOrg.m3n3.core.impl.AGENT;

public class DocAgent extends AGENT implements Container.Agent {

	private static final long serialVersionUID = 1L;

	public enum Type {
		USER("U", "利用者"),
		OFFICE("O", "事業所"),
		TEAM("T", "チーム"),
		OCCUPATION("P", "職種");

		private final String code;

		private final String name;

		private Type(final String code, final String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static Type getEnumByCode(final String code) {
			final Type[] types = values();
			for(final Type type : types) {
				if(type.code.equals(code)) {
					return type;
				}
			}
			return null;
		}

		public static String getNameByCode(final String code) {
			Type type = getEnumByCode(code);
			if(type != null) {
				return type.name;
			}
			return null;
		}

	}

	public Long seq;
	public Long refSeq;
	public String refAcr;
	public java.util.Date createdAt;
	public Long createdBy;
	public java.util.Date updatedAt;
	public Long updatedBy;
	public java.util.Date expiredAt;
	public Long expiredBy;
	public List<DocUser> userList;
	public List<DocOffice> officeList;
	public List<DocTeam> teamList;
	public List<DocOccupation> occupationList;

	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	public Long getRefSeq() {
		return refSeq;
	}
	public void setRefSeq(Long refSeq) {
		this.refSeq = refSeq;
	}
	public String getRefAcr() {
		return refAcr;
	}
	public void setRefAcr(String refAcr) {
		this.refAcr = refAcr;
	}
	public java.util.Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(java.util.Date createdAt) {
		this.createdAt = createdAt;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public java.util.Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(java.util.Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Long getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}
	public java.util.Date getExpiredAt() {
		return expiredAt;
	}
	public void setExpiredAt(java.util.Date expiredAt) {
		this.expiredAt = expiredAt;
	}
	public Long getExpiredBy() {
		return expiredBy;
	}
	public void setExpiredBy(Long expiredBy) {
		this.expiredBy = expiredBy;
	}
	public List<DocUser> getUserList() {
		return userList;
	}
	public void setUserList(List<DocUser> userList) {
		this.userList = userList;
	}
	public List<DocOffice> getOfficeList() {
		return officeList;
	}
	public void setOfficeList(List<DocOffice> officeList) {
		this.officeList = officeList;
	}
	public List<DocTeam> getTeamList() {
		return teamList;
	}
	public void setTeamList(List<DocTeam> teamList) {
		this.teamList = teamList;
	}
	public List<DocOccupation> getOccupationList() {
		return occupationList;
	}
	public void setOccupationList(List<DocOccupation> occupationList) {
		this.occupationList = occupationList;
	}

}
