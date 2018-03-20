package jp.nagoyakanet.ict.scm;

import java.util.ArrayList;
import java.util.List;

import org.kyojo.schemaOrg.m3n3.core.DataType;
import org.kyojo.schemaOrg.m3n3.core.Container.Name;
import org.kyojo.schemaOrg.m3n3.core.Container.NameRuby;
import org.kyojo.schemaOrg.m3n3.core.impl.NAME;
import org.kyojo.schemaOrg.m3n3.core.impl.TEXT;

public class SIMPLE_ELEMENT implements SimpleElement {

	private static final long serialVersionUID = 1L;

	public Long seq;
	public String code;
	public Name name;
	public NameRuby nameRuby;
	public String displayName;
	public String opt;

	public SIMPLE_ELEMENT() {
	}

	public SIMPLE_ELEMENT(String string) {
		setName(new NAME(new TEXT(string)));
	}

	@Override
	public Long getSeq() {
		return seq;
	}

	@Override
	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Name getName() {
		return name;
	}

	@Override
	public void setName(Name name) {
		this.name = name;
	}

	@Override
	public NameRuby getNameRuby() {
		return nameRuby;
	}

	@Override
	public void setNameRuby(NameRuby nameRuby) {
		this.nameRuby = nameRuby;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getOpt() {
		return opt;
	}

	@Override
	public void setOpt(String opt) {
		this.opt = opt;
	}

	public String getString() {
		if(name == null) return null;

		List<DataType.Text> textList = name.getTextList();
		if(textList == null || textList.size() == 0) {
			return null;
		} else {
			return textList.get(0).getString();
		}
	}

	public void setString(String string) {
		if(name == null) name = new NAME();

		List<DataType.Text> textList = name.getTextList();
		if(textList == null) {
			textList = new ArrayList<DataType.Text>();
			name.setTextList(textList);
		}
		if(textList.size() == 0) {
			textList.add(new TEXT(string));
		} else {
			textList.set(0, new TEXT(string));
		}
	}

	@Override
	public String getNativeValue() {
		return getString();
	}

}
