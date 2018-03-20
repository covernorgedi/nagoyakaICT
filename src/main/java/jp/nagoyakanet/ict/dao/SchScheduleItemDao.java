package jp.nagoyakanet.ict.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import jp.nagoyakanet.ict.scm.SchScheduleItem;

import java.util.Date;
import java.util.List;

import org.kyojo.gbd.AppConfig;

@Dao(config = AppConfig.class)
public interface SchScheduleItemDao {

	@Script
	public void create();

	@Script
	public void drop();

	@Select
	public SchScheduleItem selectBySeq(Long seq);

	@Select
	public List<SchScheduleItem> selectByRange(Date startDate, Date endDate, Long userSeq, Long officeSeq, Long teamSeq);

	@Insert
	public int insert(SchScheduleItem schScheduleItem);

	@Update
	public int update(SchScheduleItem schScheduleItem);

}
