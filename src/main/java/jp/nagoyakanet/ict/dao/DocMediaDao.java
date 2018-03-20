package jp.nagoyakanet.ict.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import jp.nagoyakanet.ict.scm.DocMedia;

import org.kyojo.gbd.AppConfig;

@Dao(config = AppConfig.class)
public interface DocMediaDao {

	@Script
	public void create();

	@Script
	public void drop();

	@Select
	public DocMedia selectBySeq(Long seq, boolean excludeGone);

	@Select
	public DocMedia selectByPath(String path, boolean excludeGone);

	@Select
	public DocMedia selectByPathOnlySmall(String path, boolean excludeGone);

	@Select
	public DocMedia selectByPathOnlyMiddle(String path, boolean excludeGone);

	@Select
	public DocMedia selectByPathOnlyLarge(String path, boolean excludeGone);

	@Insert
	public int insert(DocMedia docMinion);

	@Update
	public int update(DocMedia docMinion);

}
