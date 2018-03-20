package jp.nagoyakanet.ict.task;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.core.GlobalData;

import it.sauronsoftware.cron4j.Scheduler;

public class CronScheduler implements ServletContextListener {

	private static final Log logger = LogFactory.getLog(CronScheduler.class);

	private Scheduler scheduler = null;

	private static GlobalData gbd = null;

	public static GlobalData getGlobalData() {
		return gbd;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		try {
			gbd = GlobalData.getInstance(servletContext);
		} catch(IOException ioe) {
			logger.fatal(ioe.getMessage(), ioe);
		}

		URL crontabURL = CronScheduler.class.getResource("crontab.txt");
		scheduler = new Scheduler();
		try {
			scheduler.scheduleFile(new File(crontabURL.toURI()));
			scheduler.start();
			logger.info("CronScheduler started.");
		} catch(URISyntaxException use) {
			logger.error(use.getMessage(), use);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		if(scheduler != null) {
			scheduler.stop();
			logger.info("CronScheduler stopped.");
		}
	}

}
