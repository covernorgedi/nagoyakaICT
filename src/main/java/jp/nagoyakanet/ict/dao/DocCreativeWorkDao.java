package jp.nagoyakanet.ict.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Script;

import org.kyojo.gbd.AppConfig;

@Dao(config = AppConfig.class)
public interface DocCreativeWorkDao {

	@Script
	public void create();

}
