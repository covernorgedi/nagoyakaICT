package jp.nagoyakanet.ict.scm;

import org.kyojo.schemaOrg.m3n3.core.Clazz.Thing;
import org.kyojo.schemaOrg.m3n3.core.Container.Identifier;
import org.kyojo.schemaOrg.m3n3.core.Container.Name;
import org.kyojo.schemaOrg.m3n3.core.impl.THING;
import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

@Entity(naming = NamingType.SNAKE_UPPER_CASE)
public class DocMedicalSpecialty extends THING implements Thing {

	private static final long serialVersionUID = 1L;

	public Long seq;
	public String code;
	public Long refSeq;
	public String refAcr;
	public java.util.Date createdAt;
	public Long createdBy;
	public java.util.Date updatedAt;
	public Long updatedBy;
	public java.util.Date expiredAt;
	public Long expiredBy;
	public Identifier identifier;
	public Name name;

	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public Identifier getIdentifier() {
		return identifier;
	}
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}
	public Name getName() {
		return name;
	}
	public void setName(Name name) {
		this.name = name;
	}

}
