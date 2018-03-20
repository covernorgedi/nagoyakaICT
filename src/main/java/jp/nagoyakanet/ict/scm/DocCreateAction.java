package jp.nagoyakanet.ict.scm;

import org.kyojo.schemaOrg.m3n3.core.Clazz.CreateAction;
import org.kyojo.schemaOrg.m3n3.core.Container.Name;
import org.kyojo.schemaOrg.m3n3.core.Container.NameRuby;
import org.kyojo.schemaOrg.m3n3.core.impl.CREATE_ACTION;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.jdbc.entity.NamingType;

@Entity(naming = NamingType.SNAKE_UPPER_CASE)
public class DocCreateAction extends CREATE_ACTION implements CreateAction {

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

}
