package jp.nagoyakanet.ict.scm;

import java.io.Serializable;

import org.kyojo.schemaOrg.m3n3.core.Container.Name;
import org.kyojo.schemaOrg.m3n3.core.Container.NameRuby;

public interface SimpleElement extends Serializable {

	public Long getSeq();
	public void setSeq(Long seq);
	public String getCode();
	public void setCode(String code);
	public Name getName();
	public void setName(Name name);
	public NameRuby getNameRuby();
	public void setNameRuby(NameRuby name);
	public String getDisplayName();
	public void setDisplayName(String displayName);
	public String getOpt();
	public void setOpt(String opt);

	public String getNativeValue();

}
