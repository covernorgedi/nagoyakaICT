package jp.nagoyakanet.ict.plugin.reg.basic

import static org.junit.Assert.*

import jp.nagoyakanet.ict.dao.DatabaseRule
import org.junit.Rule
import org.junit.Test

class UserTest {

	@Rule
	public final DatabaseRule databaseRule = DatabaseRule.getInstance();

	@Test
	void testGetName() {
		User user = new User()
		assertEquals("利用者", user.name)
	}

}
