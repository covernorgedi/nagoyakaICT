package jp.nagoyakanet.ict.scm;

import java.util.List;

import org.kyojo.schemaOrg.m3n3.core.Container.MemberOf;
import org.kyojo.schemaOrg.m3n3.core.Container.Name;
import org.kyojo.schemaOrg.m3n3.core.Container.NameRuby;
import org.kyojo.schemaOrg.m3n3.core.impl.MEMBER_OF;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;

public class DocOfficeMemberOf extends MEMBER_OF implements MemberOf {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long seq;
	public Long refSeq;
	public String refAcr;
	public java.util.Date createdAt;
	public Long createdBy;
	public java.util.Date updatedAt;
	public Long updatedBy;
	public java.util.Date expiredAt;
	public Long expiredBy;
	public Name name;
	public NameRuby nameRuby;
	public List<DocOffice> officeList;

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
	public Name getName() {
		return name;
	}
	public void setName(Name name) {
		this.name = name;
	}
	public NameRuby getNameRuby() {
		return nameRuby;
	}
	public void setNameRuby(NameRuby nameRuby) {
		this.nameRuby = nameRuby;
	}
	public List<DocOffice> getOfficeList() {
		return officeList;
	}
	public void setOfficeList(List<DocOffice> officeList) {
		this.officeList = officeList;
	}

}
