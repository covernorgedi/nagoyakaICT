package jp.nagoyakanet.ict.dao;

import java.util.List;

import org.kyojo.gbd.AppConfig;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.jdbc.SelectOptions;

import jp.nagoyakanet.ict.scm.TlMessage;

@Dao(config = AppConfig.class)
public interface TlMessageDao {

	@Script
	public void create();

	@Script
	public void drop();

	@Select
	public List<TlMessage> selectByFromSeq(Long seq, Long userSeq, Long officeSeq, Long teamSeq, Boolean emgSort,
			List<String> kinds, SelectOptions options);

	@Insert
	public int insert(TlMessage tlMessage);

	@Update
	public int update(TlMessage tlMessage);

	@Script
	public void insertSamples();

}
