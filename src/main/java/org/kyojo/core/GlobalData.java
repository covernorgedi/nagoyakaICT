package org.kyojo.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.core.annotation.NoLoadGlobalData;

import jp.nagoyakanet.ict.dao.DocMediaDao;
import jp.nagoyakanet.ict.dao.DocMediaDaoImpl;
import jp.nagoyakanet.ict.dao.DocMinionDao;
import jp.nagoyakanet.ict.dao.DocMinionDaoImpl;
import jp.nagoyakanet.ict.dao.DocOfficeDao;
import jp.nagoyakanet.ict.dao.DocOfficeDaoImpl;
import jp.nagoyakanet.ict.dao.DocTeamDao;
import jp.nagoyakanet.ict.dao.DocTeamDaoImpl;
import jp.nagoyakanet.ict.dao.DocUserDao;
import jp.nagoyakanet.ict.dao.DocUserDaoImpl;
import jp.nagoyakanet.ict.dao.FeedEntryDao;
import jp.nagoyakanet.ict.dao.FeedEntryDaoImpl;
import jp.nagoyakanet.ict.dao.MessagingSessionDao;
import jp.nagoyakanet.ict.dao.MessagingSessionDaoImpl;
import jp.nagoyakanet.ict.dao.MstCodeAndLangDao;
import jp.nagoyakanet.ict.dao.MstCodeAndLangDaoImpl;
import jp.nagoyakanet.ict.dao.SchScheduleItemDao;
import jp.nagoyakanet.ict.dao.SchScheduleItemDaoImpl;
import jp.nagoyakanet.ict.dao.SignInSessionDao;
import jp.nagoyakanet.ict.dao.SignInSessionDaoImpl;
import jp.nagoyakanet.ict.dao.TlMessageDao;
import jp.nagoyakanet.ict.dao.TlMessageDaoImpl;

public final class GlobalData extends ConcurrentHashMap<String, Object> {

	private static final Log logger = LogFactory.getLog(GlobalData.class);

	private static final long serialVersionUID = -6928081982874321939L;

	private static GlobalData instance = null;

	private ServletContext context = null;

	public ServletContext getContext() {
		return context;
	}

	private Pattern tbpt = null;

	public <T> T get(Class<T> cls) {
		return cls.cast(super.get(cls.getName()));
	}

	private boolean isLogSaveAndLoadResult = false;

	private GlobalData(ServletContext context) {
		this.context = context;
		tbpt = Pattern.compile("\\t");
		loadConstants();

		Object val = get("IS_LOG_SAVE_AND_LOAD_RESULT");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogSaveAndLoadResult = true;
		}
	}

	public static GlobalData getInstance(ServletContext context) throws IOException {
		if(instance == null) {
			instance = new GlobalData(context);
		}
		return instance;
	}

	private void loadConstants() {
		try {
			for(Field field : Constants.class.getDeclaredFields()) {
				int mdfrs = field.getModifiers();
				if(Modifier.isPublic(mdfrs) && Modifier.isStatic(mdfrs) && Modifier.isFinal(mdfrs)
						&& field.getAnnotation(NoLoadGlobalData.class) == null) {
					put(field.getName(), field.get(null));
				}
			}
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		InputStream is = null;
		Properties props = null;
		try {
			is = GlobalData.class.getResourceAsStream("Constants.properties");
			props = new Properties();
			props.load(is);
			for(Entry<Object, Object> entry : props.entrySet()){
				put(entry.getKey().toString(), entry.getValue());
			}
			is.close();
			is = null;

			is = GlobalData.class.getResourceAsStream("Messages.properties");
			props = new Properties();
			props.load(is);
			for(Entry<Object, Object> entry : props.entrySet()) {
				put(entry.getKey().toString(), entry.getValue());
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

		// ToDo: クラス外に定義
		put(MstCodeAndLangDao.class.getName(), new MstCodeAndLangDaoImpl());
		put(DocMinionDao.class.getName(), new DocMinionDaoImpl());
		put(DocUserDao.class.getName(), new DocUserDaoImpl());
		put(DocOfficeDao.class.getName(), new DocOfficeDaoImpl());
		put(DocTeamDao.class.getName(), new DocTeamDaoImpl());
		put(DocMediaDao.class.getName(), new DocMediaDaoImpl());
		put(SchScheduleItemDao.class.getName(), new SchScheduleItemDaoImpl());
		put(SignInSessionDao.class.getName(), new SignInSessionDaoImpl());
		put(MessagingSessionDao.class.getName(), new MessagingSessionDaoImpl());
		put(TlMessageDao.class.getName(), new TlMessageDaoImpl());
		put(FeedEntryDao.class.getName(), new FeedEntryDaoImpl());
		put(FeedEntryDao.class.getName(), new FeedEntryDaoImpl());
	}

	private HashMap<String, ReentrantReadWriteLock> fileSystemCacheLockTable = new HashMap<>();

	private Lock lockFileSystemCache(String path, boolean isRead) {
		Lock lock;
		synchronized(fileSystemCacheLockTable) {
			ReentrantReadWriteLock readWriteLock;
			if(fileSystemCacheLockTable.containsKey(path)) {
				readWriteLock = fileSystemCacheLockTable.get(path);
			} else {
				readWriteLock = new ReentrantReadWriteLock();
				fileSystemCacheLockTable.put(path, readWriteLock);
			}

			lock = isRead ? readWriteLock.readLock() : readWriteLock.writeLock();
			lock.lock();
		}

		return lock;
	}

	private void unlockFileSystemCache(String path, Lock lock) {
		lock.unlock();
		synchronized(fileSystemCacheLockTable) {
			// int idx = 0;
			// for(Map.Entry<String, ReentrantReadWriteLock> ent : fileSystemCacheLockTable.entrySet()) {
			//	idx++;
			//	logger.debug("fileSystemCacheLockTable" + idx + ": " + ent.getKey() + " => " + (ent.getValue() == null ? "null" : ent.getValue().toString()));
			// }
			ReentrantReadWriteLock readWriteLock = fileSystemCacheLockTable.get(path);
			try {
				if(!readWriteLock.hasQueuedThreads()) {
					fileSystemCacheLockTable.remove(path);
				}
			} catch(NullPointerException npe) {
				logger.debug("unlock: " + path); // ToDo: readWriteLockがnullになることがあるので調査中
				// logger.debug(npe.getMessage());
			}
		}
	}

	public Cache loadCacheIfPossible(String sKey, String dKey, String eKey, boolean isRealLoad) {
		Cache cache = null;
		String key = Cache.concatKeys(sKey, dKey, eKey);
		String inFPath = get("CACHE_DPATH") + key;
		String expireFPath = get("EXPIRE_DPATH") + key;
		File rf = null, tf = null;
		JavaFGets jfg = null;
		Time14 now = new Time14();
		Time14 created = null;
		Time14 expires = null;
		String tmpStr;
		Lock lock = null;

		try {
			lock = lockFileSystemCache(inFPath, true);
			rf = new File(inFPath);
			jfg = new JavaFGets(rf);

			// cache file opened normally
			// all cache files have >=6 lines
			tmpStr = jfg.readLine();
			tmpStr = StringUtils.trim(tmpStr);
			created = new Time14(tmpStr);
			tmpStr = jfg.readLine();
			tmpStr = StringUtils.trim(tmpStr);
			tf = new File(expireFPath);
			if(tf.canRead() && tf.length() > 0) {
				expires = created;
			} else {
				expires = new Time14(tmpStr);
			}

			String line;
			cache = new Cache(sKey, dKey, eKey, created);
			cache.setExpires(expires);
			line = jfg.readLine();
			line = StringUtils.trim(line);
			if(line.length() > 0) {
				String[] missFiles = tbpt.split(line);
				for(String fpath: missFiles) {
					cache.addMissFile(fpath);
				}
			}
			line = jfg.readLine();
			line = StringUtils.trim(line);
			if(line.length() > 0) {
				String[] refFiles = tbpt.split(line);
				for(String fpath: refFiles) {
					cache.addRefFile(fpath);
				}
			}
			line = jfg.readLine();
			line = StringUtils.trim(line);
			if(line.length() > 0) {
				String[] childKeys = tbpt.split(line);
				for(String childKey: childKeys) {
					cache.addChildKey(childKey, Time14.OLD);
				}
			}
			jfg.readLine();
			if(isRealLoad) {
				while((line = jfg.readLine()) != null) {
					// cache.addLine(line);
					cache.addLine(new String(line.getBytes("ISO-8859-1"), "UTF-8"));
					// cache.addLine(new String(line.getBytes("ISO-8859-1")));
					// cache.addLine(new String(line.getBytes("UTF-8")));
				}
			}
		} catch(FileNotFoundException fnfe) {
			if(isLogSaveAndLoadResult) {
				logger.info("no cache \"" + key + "\"");
			}
			// logger.error(fnfe.getMessage(), fnfe);
		} catch(IOException ioe) {
			logger.error(ioe.getMessage(), ioe);
		} finally {
			try {
				if(jfg != null) {
					jfg.close();
				}
				if(lock != null) {
					unlockFileSystemCache(inFPath, lock);
				}
			} catch(IOException ioe) {}
		}

		if(cache == null) {
			return null;
		}

		if(expires.compareTo(now) < 0) {
			rf.delete(); // cacheのdeleteはロックとか関係なく試行すればいい？
			return null;
		}

		// missed files created?
		Iterator<String> it = cache.getMissFiles();
		while(it.hasNext()) {
			tf = new File(it.next());
			if(tf.canRead()) {
				expires = created;
				break;
			}
		}

		if(expires.compareTo(now) < 0) {
			rf.delete();
			return null;
		}

		// reference files updated?
		it = cache.getRefFiles();
		long lm;
		Time14 lmT14;
		while(it.hasNext()) {
			tf = new File(it.next());
			lm = tf.lastModified();
			if(lm == 0L) {
				expires = created;
				break;
			} else {
				lmT14 = new Time14(new Date(lm));
				if(cache.getCreated().compareTo(lmT14) < 0) {
					expires = created;
					break;
				}
			}
		}

		if(expires.compareTo(now) < 0) {
			rf.delete();
			return null;
		}

		// child caches expired?
		it = cache.getChildKeys();
		String[] keyAry;
		Cache child;
		HashSet<Cache> tmpChildren = new HashSet<Cache>();
		while(it.hasNext()) {
			String tmpKey = it.next();
			keyAry = Cache.splitKey(tmpKey);
			child = loadCacheIfPossible(keyAry[0], keyAry[1], keyAry[2], false);
			if(child == null || cache.getCreated().compareTo(child.getCreated()) < 0) {
				expires = created;
				break;
			} else {
				tmpChildren.add(child);
			}
		}
		for(Cache tmpChild: tmpChildren) {
			cache.addChildKey(tmpChild.getKey(), tmpChild.getExpires());
		}

		if(expires.compareTo(now) < 0) {
			rf.delete();
			return null;
		}

		return cache;
	}

	public void completeCache(Cache cache) {
		String nowT14 = (new Time14()).toString();
		if(cache.getExpires().compareTo(nowT14) <= 0) {
			return;
		}

		String outFPath = get("CACHE_DPATH") + cache.getKey();
		File wf = null, wd = null;
		FileOutputStream wfos = null;
		BufferedOutputStream wbos = null;
		Lock lock = null;

		try {
			lock = lockFileSystemCache(outFPath, false);
			wf = new File(outFPath);
			wd = wf.getParentFile();
			if(!wd.exists()) {
				wd.mkdirs();
			}

			// final String ENCODING = "ISO-8859-1";
			final String ENCODING = "UTF-8";
			wfos = new FileOutputStream(wf);
			wbos = new BufferedOutputStream(wfos);
			wbos.write(cache.getCreated().toString().getBytes(ENCODING));
			wbos.write('\n');
			wbos.write(cache.getExpires().toString().getBytes(ENCODING));
			wbos.write('\n');

			String sep = "";
			Iterator<String> it = cache.getMissFiles();
			while(it.hasNext()) {
				wbos.write((sep + it.next()).getBytes(ENCODING));
				sep = "\t";
			}
			wbos.write('\n');

			sep = "";
			it = cache.getRefFiles();
			while(it.hasNext()) {
				wbos.write((sep + it.next()).getBytes(ENCODING));
				sep = "\t";
			}
			wbos.write('\n');

			sep = "";
			it = cache.getChildKeys();
			while(it.hasNext()) {
				wbos.write((sep + it.next()).getBytes(ENCODING));
				sep = "\t";
			}
			wbos.write('\n');
			wbos.write('\n');

			// Iterator<String> itr = cache.getLinesAndSaveMemory();
			Iterator<String> itr = cache.getLines();
			while(itr.hasNext()) {
				String line = itr.next();
				wbos.write(line.getBytes(ENCODING));
			}
		} catch(IOException ioe) {
			logger.error(ioe.getMessage(), ioe);
		} finally {
			try {
				if(wbos != null) {
					wbos.close();
				}
				if(wfos != null) {
					wfos.close();
				}
				if(lock != null) {
					unlockFileSystemCache(outFPath, lock);
				}
			} catch(IOException ioe) {}
		}
	}

}
