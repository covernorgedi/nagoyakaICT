package jp.nagoyakanet.ict.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import jp.nagoyakanet.ict.scm.DocUser;

import java.util.List;

import org.kyojo.gbd.AppConfig;

@Dao(config = AppConfig.class)
public interface DocUserDao {

	@Script
	public void create();

	@Script
	public void drop();

	@Select
	public List<DocUser> select();

	@Select
	public DocUser selectBySeq(Long seq);

	@Select
	public DocUser selectByCode(String code);

	@Select
	public DocUser selectByCodeAndPassword(String code, String password);

	@Insert
	public int insert(DocUser docUser);

	@Update
	public int update(DocUser docUser);

}
