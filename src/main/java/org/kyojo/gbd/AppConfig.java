package org.kyojo.gbd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.doma.SingletonConfig;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource;
import org.seasar.doma.jdbc.tx.LocalTransactionManager;
import org.seasar.doma.jdbc.tx.TransactionManager;

@SingletonConfig
public class AppConfig implements Config {

	private static final Log logger = LogFactory.getLog(AppConfig.class);

	private static final AppConfig CONFIG = new AppConfig();

	private final Dialect dialect;

	private final LocalTransactionDataSource dataSource;

	private final TransactionManager transactionManager;

	private AppConfig() {
		DBCP dbcp = new DBCP();
		dialect = dbcp.getDialect();
		// dataSource = new LocalTransactionDataSource(dbcp.getDataSource());
		// transactionManager = new LocalTransactionManager(dataSource.getLocalTransaction(getJdbcLogger()));

		// String driver = "org.h2.Driver";
		String url = "jdbc:h2:mem:test";
		String user = "sa";
		String password = "";

		InputStream is = null;
		Properties props = null;
		try {
			is = AppConfig.class.getResourceAsStream("DataSources.properties");
			props = new Properties();
			props.load(is);
			for(Entry<Object, Object> entry : props.entrySet()){
				if(entry.getKey().toString().equals("MAIN_DRIVER")) {
					// driver = entry.getValue().toString();
				} else if(entry.getKey().toString().equals("MAIN_URL")) {
					url = entry.getValue().toString();
				} else if(entry.getKey().toString().equals("MAIN_USER")) {
					user = entry.getValue().toString();
				} else if(entry.getKey().toString().equals("MAIN_PASSWORD")) {
					password = entry.getValue().toString();
				}
			}
			is.close();
		} catch(IOException ioe) {
			logger.error(ioe.getMessage(), ioe);
		} finally {
			try {
				if(is != null) {
					is.close();
				}
			} catch(IOException ioe) {}
		}

		dataSource = new LocalTransactionDataSource(url, user, password);
		transactionManager = new LocalTransactionManager(dataSource.getLocalTransaction(getJdbcLogger()));
	}

	@Override
	public Dialect getDialect() {
		return dialect;
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public static AppConfig singleton() {
		return CONFIG;
	}

}
