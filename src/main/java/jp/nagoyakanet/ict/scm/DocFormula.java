package jp.nagoyakanet.ict.scm;

import java.io.Serializable;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

@Entity(naming = NamingType.SNAKE_UPPER_CASE)
// public class DocFormula extends THING implements Thing {
public class DocFormula implements Serializable {

	private static final long serialVersionUID = 1L;

	public Boolean canGeneric; // ジェネリック化可能
	public String entry; // 自由記入
	public String name; // 薬品名
	public String mainEffect; // 効能
	public String administrationRoute; // 使用方法
	public String doseSchedule; // 使用頻度
	public String adverseEffect; // 副作用
	public String appearance; // 剤形
	public String warning; // 注意

}
