package org.kyojo.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder;

public final class SessionData implements Map<String, Object> {

	private static final Log logger = LogFactory.getLog(SessionData.class);

	public static final String SESSION_DATA_KEY = "org.kyojo.core.SessionData";

	private HttpServletRequest req = null;

	private HttpSession ssn = null;

	private GlobalData gbd = null;

	private boolean isLogSaveAndLoadResult = false;

	public SessionData(HttpServletRequest req, GlobalData gbd) {
		this.req = req;
		ssn = req.getSession(true);
		this.gbd = gbd;

		Object val = gbd.get("IS_LOG_SAVE_AND_LOAD_RESULT");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogSaveAndLoadResult = true;
		}

		synchronized(ssn) {
			if(ssn.getAttribute(SESSION_DATA_KEY) == null) {
				ssn.setAttribute(SESSION_DATA_KEY, new ConcurrentHashMap<String, Object>());
			}
		}
	}

	public void sessionRestart() {
		if(ssn != null) {
			ssn.invalidate();
		}
		ssn = req.getSession(true);
	}

	public String getSessionID() {
		if(ssn == null) {
			ssn = req.getSession(true);
		}

		String sid = ssn.getId();
		return StringUtils.isEmpty(sid) ? "" : My.hs(sid);
	}

	@SuppressWarnings("unchecked")
	private ConcurrentHashMap<String, Object> getSessionMap() {
		if(ssn == null) {
			ssn = req.getSession(true);
		}

		Object ssnObj;
		synchronized(ssn) {
			ssnObj = ssn.getAttribute(SESSION_DATA_KEY);
			if(ssnObj == null) {
				ssnObj = new ConcurrentHashMap<String, Object>();
				ssn.setAttribute(SESSION_DATA_KEY, ssnObj);
			}
		}

		return (ConcurrentHashMap<String, Object>)ssnObj;
	}

	@Override
	public int size() {
		ConcurrentHashMap<String, Object> ssnMap = getSessionMap();
		return ssnMap == null ? 0 : ssnMap.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		ConcurrentHashMap<String, Object> ssnMap = getSessionMap();
		return ssnMap == null ? false : ssnMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object val) {
		ConcurrentHashMap<String, Object> ssnMap = getSessionMap();
		return ssnMap == null ? false : ssnMap.containsValue(val);
	}

	@Override
	public Object get(Object key) {
		ConcurrentHashMap<String, Object> ssnMap = getSessionMap();
		return ssnMap == null ? null : ssnMap.get(key);
	}

	@Override
	public Object put(String key, Object val) {
		ConcurrentHashMap<String, Object> ssnMap = getSessionMap();
		return ssnMap == null ? null : ssnMap.put(key, val);
	}

	@Override
	public Object remove(Object key) {
		ConcurrentHashMap<String, Object> ssnMap = getSessionMap();
		return ssnMap == null ? null : ssnMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		ConcurrentHashMap<String, Object> ssnMap = getSessionMap();
		if(ssnMap != null) {
			ssnMap.putAll(map);
		}
	}

	@Override
	public void clear() {
		ConcurrentHashMap<String, Object> ssnMap = getSessionMap();
		ssnMap.clear();
	}

	@Override
	public Set<String> keySet() {
		ConcurrentHashMap<String, Object> ssnMap = getSessionMap();
		return ssnMap == null ? null : ssnMap.keySet();
	}

	@Override
	public Collection<Object> values() {
		ConcurrentHashMap<String, Object> ssnMap = getSessionMap();
		return ssnMap == null ? null : ssnMap.values();
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		ConcurrentHashMap<String, Object> ssnMap = getSessionMap();
		return ssnMap == null ? null : ssnMap.entrySet();
	}

	public HttpServletRequest getRequest() {
		return req;
	}

	public GlobalData getGlobalData() {
		return gbd;
	}

	public void takeOver(Object obj, Class<?> cls, String args) {
		takeOver(obj, cls.getSimpleName(), args);
	}

	public void takeOver(Object obj, String skin, String args) {
		// String mnn = My.minion(obj);
		String mnn = SimpleJsonBuilder.toJson(obj);
		String sKey = My.camelize(skin);
		Cache mnnCache = new Cache(sKey, My.hs(args), "mnn", new Time14());
		mnnCache.addLine(mnn);
		if(isLogSaveAndLoadResult) {
			logger.debug("args: \"" + args + "\"");
			logger.debug("ssd-store: " + mnnCache.getKey() + " -> " + mnn);
		}
		put(mnnCache.getKey(), mnnCache);
	}

}
