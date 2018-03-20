package jp.nagoyakanet.ict.scm;

import org.kyojo.schemaOrg.m3n3.core.Clazz.Organization;
import org.kyojo.schemaOrg.m3n3.core.Container.Address;
import org.kyojo.schemaOrg.m3n3.core.Container.Email;
import org.kyojo.schemaOrg.m3n3.core.Container.FaxNumber;
import org.kyojo.schemaOrg.m3n3.core.Container.Name;
import org.kyojo.schemaOrg.m3n3.core.Container.NameRuby;
import org.kyojo.schemaOrg.m3n3.core.Container.Telephone;
import org.kyojo.schemaOrg.m3n3.core.impl.ORGANIZATION;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.jdbc.entity.NamingType;

@Entity(naming = NamingType.SNAKE_UPPER_CASE)
public class DocOffice extends ORGANIZATION implements Organization {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	public Name name;
	public NameRuby nameRuby;
	public Address address; // ToDo: postalcodeはconverter未対応
	public Telephone telephone;
	public FaxNumber faxNumber;
	public Email email;
	public DocOfficeMemberOf officeMemberOf;
	public String useFlg;

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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Telephone getTelephone() {
		return telephone;
	}

	public void setTelephone(Telephone telephone) {
		this.telephone = telephone;
	}

	public FaxNumber getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(FaxNumber faxNumber) {
		this.faxNumber = faxNumber;
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	public DocOfficeMemberOf getOfficeMemberOf() {
		return officeMemberOf;
	}

	public void setOfficeMemberOf(DocOfficeMemberOf officeMemberOf) {
		this.officeMemberOf = officeMemberOf;
	}

	public String getUseFlg() {
		return useFlg;
	}

	public void setUseFlg(String useFlg) {
		this.useFlg = useFlg;
	}

}
