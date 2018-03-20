package jp.nagoyakanet.ict.scm;

import java.util.List;

import org.kyojo.schemaOrg.m3n3.core.Clazz.Audience;
import org.kyojo.schemaOrg.m3n3.core.Clazz.ContactPoint;
import org.kyojo.schemaOrg.m3n3.core.Clazz.Organization;
import org.kyojo.schemaOrg.m3n3.core.Clazz.Person;
import org.kyojo.schemaOrg.m3n3.core.Container.Recipient;
import org.kyojo.schemaOrg.m3n3.core.impl.RECIPIENT;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;

public class DocTeamTarget extends RECIPIENT implements Recipient {

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
	public List<Audience> audienceList;
	public List<ContactPoint> contactPointList;
	public List<Organization> organizationList;
	public List<Person> personList;
	public List<DocUser> userList;

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
	public List<Audience> getAudienceList() {
		return audienceList;
	}
	public void setAudienceList(List<Audience> audienceList) {
		this.audienceList = audienceList;
	}
	public List<ContactPoint> getContactPointList() {
		return contactPointList;
	}
	public void setContactPointList(List<ContactPoint> contactPointList) {
		this.contactPointList = contactPointList;
	}
	public List<Organization> getOrganizationList() {
		return organizationList;
	}
	public void setOrganizationList(List<Organization> organizationList) {
		this.organizationList = organizationList;
	}
	public List<Person> getPersonList() {
		return personList;
	}
	public void setPersonList(List<Person> personList) {
		this.personList = personList;
	}
	public List<DocUser> getUserList() {
		return userList;
	}
	public void setUserList(List<DocUser> userList) {
		this.userList = userList;
	}

}
