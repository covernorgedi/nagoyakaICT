package org.kyojo.gbd;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.H2Dialect;

public class DBCP {

	private static final Log logger = LogFactory.getLog(DBCP.class);

	private final DataSource ds;

	private final Dialect dialect;

	public DBCP() {
		String driver = "org.h2.Driver";
		String url = "jdbc:h2:mem:test";
		String user = "sa";
		String password = "";

		InputStream is = null;
		Properties props = null;
		try {
			is = DBCP.class.getResourceAsStream("DataSources.properties");
			props = new Properties();
			props.load(is);
			for(Entry<Object, Object> entry : props.entrySet()){
				if(entry.getKey().toString().equals("MAIN_DRIVER")) {
					driver = entry.getValue().toString();
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

		DriverAdapterCPDS driverAdapterCPDS = new DriverAdapterCPDS();
		try {
			driverAdapterCPDS.setDriver(driver);
			driverAdapterCPDS.setUrl(url);
			driverAdapterCPDS.setUser(user);
			driverAdapterCPDS.setPassword(password);
		} catch(ClassNotFoundException cnfe) {
			logger.error(cnfe.getMessage(), cnfe);
		}

		SharedPoolDataSource sharedPoolDS = new SharedPoolDataSource();
		sharedPoolDS.setConnectionPoolDataSource(driverAdapterCPDS);
		// sharedPoolDS.setMaxActive(10);
		sharedPoolDS.setMaxActive(1);
		sharedPoolDS.setMaxWait(50);
		sharedPoolDS.setTestOnBorrow(true);
		sharedPoolDS.setValidationQuery("SELECT 1");
		sharedPoolDS.setTestWhileIdle(true);
		ds = sharedPoolDS;

		dialect = new H2Dialect();
	}

	public Dialect getDialect() {
		return dialect;
	}

	public DataSource getDataSource() {
		return ds;
	}

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		DataSource ds = getDataSource();

		return ds.getConnection();
	}

}
