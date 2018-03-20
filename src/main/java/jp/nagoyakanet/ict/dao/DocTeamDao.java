package jp.nagoyakanet.ict.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import jp.nagoyakanet.ict.scm.DocTeam;
import java.util.List;

import org.kyojo.gbd.AppConfig;

@Dao(config = AppConfig.class)
public interface DocTeamDao {

	@Script
	public void create();

	@Script
	public void drop();

	@Select
	public DocTeam selectBySeq(Long seq);

	@Select
	public List<DocTeam> select();

	@Insert
	public int insert(DocTeam docTeam);

	@Update
	public int update(DocTeam docTeam);

}
