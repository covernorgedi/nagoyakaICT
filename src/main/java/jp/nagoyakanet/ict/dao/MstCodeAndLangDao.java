package jp.nagoyakanet.ict.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import jp.nagoyakanet.ict.scm.MstCodeAndLang;

import java.util.List;

import org.kyojo.gbd.AppConfig;

@Dao(config = AppConfig.class)
public interface MstCodeAndLangDao {

	@Script
	public void create();

	@Script
	public void drop();

	@Select
	public List<MstCodeAndLang> select(boolean excludeExpired);

	@Select
	public MstCodeAndLang selectBySeq(Long seq);

	@Select
	public List<MstCodeAndLang> selectByAct(String act, String type, boolean excludeExpired);

	@Select
	public List<MstCodeAndLang> selectByType(String type, boolean excludeExpired);

	@Select
	public MstCodeAndLang selectByActAndCode(String act, String type, String code);

	@Select
	public MstCodeAndLang selectByTypeAndCode(String type, String code);

	@Select
	public MstCodeAndLang selectByCode(String code);

	@Insert
	public int insert(MstCodeAndLang mstCodeAndLang);

	@Update
	public int update(MstCodeAndLang mstCodeAndLang);

	@Script
	public void insertSamples();

}
