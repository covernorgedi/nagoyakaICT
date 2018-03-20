package jp.nagoyakanet.ict.dao;

import org.kyojo.gbd.AppConfig;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import jp.nagoyakanet.ict.scm.MessagingSession;

@Dao(config = AppConfig.class)
public interface MessagingSessionDao {

	@Script
	public void create();

	@Script
	public void drop();

	@Select
	public MessagingSession selectBySeq(Long seq, boolean excludeExpired);

	@Select
	public MessagingSession selectBySessionId(String sessionId, boolean excludeExpired);

	@Insert
	public int insert(MessagingSession messagingSession);

	@Update
	public int update(MessagingSession messagingSession);

}
