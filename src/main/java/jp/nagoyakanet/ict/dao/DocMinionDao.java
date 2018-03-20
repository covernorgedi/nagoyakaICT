package jp.nagoyakanet.ict.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import jp.nagoyakanet.ict.scm.DocMinion;

import java.util.List;

import org.kyojo.gbd.AppConfig;

@Dao(config = AppConfig.class)
public interface DocMinionDao {

	@Script
	public void create();

	@Script
	public void drop();

	@Select
	public DocMinion selectBySeq(Long seq, boolean excludeGone);

	@Select
	public List<DocMinion> selectBySKey(String sKey, boolean excludeGone, boolean sortOlder);

	@Insert
	public int insert(DocMinion docMinion);

	@Update
	public int update(DocMinion docMinion);

}
