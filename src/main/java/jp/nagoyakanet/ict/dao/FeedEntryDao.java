package jp.nagoyakanet.ict.dao;

import java.util.List;

import org.kyojo.gbd.AppConfig;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import jp.nagoyakanet.ict.scm.FeedEntry;

@Dao(config = AppConfig.class)
public interface FeedEntryDao {

	@Script
	public void create();

	@Script
	public void drop();

	@Select
	public List<FeedEntry> selectByEntryUri(String entryUri);

	@Insert
	public int insert(FeedEntry feedEntry);

	@Update
	public int update(FeedEntry feedEntry);

}
