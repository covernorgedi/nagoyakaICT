package jp.nagoyakanet.ict.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import jp.nagoyakanet.ict.scm.DocOffice;
import java.util.List;

import org.kyojo.gbd.AppConfig;

@Dao(config = AppConfig.class)
public interface DocOfficeDao {

	@Script
	public void create();

	@Script
	public void drop();

	@Select
	public List<DocOffice> select();

	@Select
	public DocOffice selectBySeq(Long seq);

	@Insert
	public int insert(DocOffice docOffice);

	@Update
	public int update(DocOffice docOffice);

}
