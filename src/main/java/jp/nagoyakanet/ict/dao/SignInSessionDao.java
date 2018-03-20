package jp.nagoyakanet.ict.dao;

import java.util.List;

import org.kyojo.gbd.AppConfig;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import jp.nagoyakanet.ict.scm.SignInSession;

@Dao(config = AppConfig.class)
public interface SignInSessionDao {

	@Script
	public void create();

	@Script
	public void drop();

	@Select
	public SignInSession selectBySeq(Long seq, boolean excludeExpired);

	@Select
	public List<SignInSession> selectBySessionId(String sessionId, boolean excludeExpired);

	@Insert
	int insert(SignInSession signInSession);

	@Update
	int update(SignInSession signInSession);

}
