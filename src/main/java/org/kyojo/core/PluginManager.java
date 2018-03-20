package org.kyojo.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.minion.My;

import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceConnector;
import groovy.util.ResourceException;

public class PluginManager implements ResourceConnector {

	private static final Log logger = LogFactory.getLog(PluginManager.class);

	private static PluginManager instance = null;

	private GlobalData gbd = null;

	private GroovyScriptEngine gse = null;

	private boolean isLogSearchTemplete = false;

	private PluginManager(GlobalData gbd) {
		this.gbd = gbd;
		gse = createGroovyScriptEngine();

		Object val = gbd.get("IS_LOG_SEARCH_TEMPLETE");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogSearchTemplete = true;
		}
	}

	private GroovyScriptEngine createGroovyScriptEngine() {
		return new GroovyScriptEngine(this);
	}

	public static PluginManager getInstance(GlobalData gbd) {
		if(instance == null) {
			instance = new PluginManager(gbd);
		}
		return instance;
	}

	public boolean hasPlugin(String act, String key) {
		File f = getPluginFile(act, key, null);
		return f != null;
	}

	protected class PluginFileInfo {
		File file;
		String mPath;
	}

	public File getPluginFile(String act, String key, Cache cache) {
		PluginFileInfo pfInfo = getPluginFile2(act, key, cache);
		return pfInfo.file;
	}

	protected PluginFileInfo getPluginFile2(String act, String key, Cache cache) {
		key = key.replaceAll(File.pathSeparator, "/").replaceAll("/+", "/").replaceAll("\\.+", ".");

		PluginFileInfo pfInfo = new PluginFileInfo();
		pfInfo.mPath = "";
		try {
			boolean withAct = false;
			if(StringUtils.isNotBlank(act) && key.indexOf("/") < 0) {
				act = act.replaceAll(File.pathSeparator, "/").replaceAll("/+", "/").replaceAll("\\.+", ".");
				String[] elms = act.split("/");
				for(int ei = elms.length - 1; ei >= 0; ei--) {
					// actの上位に遡って検索
					StringBuilder act2 = new StringBuilder();
					for(int ei2 = 0; ei2 <= ei; ei2++) {
						if(ei2 > 0) act2.append("/");
						act2.append(elms[ei2]);
					}
					pfInfo.mPath = act2.toString();
					pfInfo.file = new File("" + gbd.get("PLUGIN_DPATH") + gbd.get("PLUGIN_PKG") + pfInfo.mPath + File.separator + key + ".groovy");

					if(pfInfo.file.canRead()) {
						if(cache != null) {
							if(isLogSearchTemplete) {
								logger.debug(pfInfo.file.getCanonicalPath() + " is used.");
							}
							cache.addRefFile(pfInfo.file.getCanonicalPath());
						}
						withAct = true;
						break;
					} else {
						if(cache != null) {
							if(isLogSearchTemplete) {
								logger.debug(pfInfo.file.getCanonicalPath() + " is not found.");
							}
							cache.addMissFile(pfInfo.file.getCanonicalPath());
						}
					}
				}
			}

			if(!withAct) {
				pfInfo.mPath = "";
				pfInfo.file = new File("" + gbd.get("PLUGIN_DPATH") + gbd.get("PLUGIN_PKG") + key + ".groovy");
				if(pfInfo.file.canRead()) {
					if(cache != null) {
						if(isLogSearchTemplete) {
							logger.debug(pfInfo.file.getCanonicalPath() + " is used.");
						}
						cache.addRefFile(pfInfo.file.getCanonicalPath());
					}
				} else {
					if(cache != null) {
						if(isLogSearchTemplete) {
							logger.debug(pfInfo.file.getCanonicalPath() + " is not found.");
						}
						cache.addMissFile(pfInfo.file.getCanonicalPath());
					}
					pfInfo.file = null;
				}
			}
		} catch(Exception ex) {
			logger.error(ex);
		}

		return pfInfo;
	}

	public Class<?> loadPlugin(String act, String key) {
		PluginFileInfo pfInfo = getPluginFile2(act, key, null);
		ClassLoader loader = this.getClass().getClassLoader();
		try {
			if(pfInfo.file.canRead()) {
				// ToDo: PLUGIN_PKGは省略できるのでは
				String dlm = StringUtils.isEmpty(pfInfo.mPath) ? "" : ".";
				String className = (gbd.get("PLUGIN_PKG") + pfInfo.mPath + dlm).replaceAll("/", ".")
						+ My.pascalize(key);
				try {
					return loader.loadClass(className);
				} catch(ClassNotFoundException cnfe) {
					dlm = StringUtils.isEmpty(pfInfo.mPath) ? "" : "/";
					String scriptName = gbd.get("PLUGIN_PKG") + pfInfo.mPath + dlm + key + ".groovy";
					return gse.loadScriptByName(scriptName);
				}
			}
			if(isLogSearchTemplete) {
				logger.debug(pfInfo.file.getCanonicalPath() + " is not found.");
			}
		} catch(Exception ex) {
			logger.error(ex);
		}

		return null;
	}

	@Override
	public URLConnection getResourceConnection(String name) throws ResourceException {
		URLConnection groovyScriptConn = null;

		ResourceException se = null;
		URL scriptURL = null;
		try {
			File f = new File(gbd.get("PLUGIN_DPATH").toString());
			scriptURL = new URL(f.toURI().toURL(), name);
			groovyScriptConn = openConnection(scriptURL);
		} catch(MalformedURLException mue) {
			String message = "Malformed URL: " + gbd.get("PLUGIN_DPATH") + name;
			se = new ResourceException(message);
		} catch(IOException ioe) {
			String message = "Cannot open URL: " + gbd.get("PLUGIN_DPATH") + name;
			se = new ResourceException(message);
		}

		if (se == null) se = new ResourceException("No resource for " + name + " was found");

		if (groovyScriptConn == null) throw se;
		return groovyScriptConn;
	}

	private static URLConnection openConnection(URL scriptURL) throws IOException {
		URLConnection urlConnection = scriptURL.openConnection();
		verifyInputStream(urlConnection);

		return scriptURL.openConnection();
	}

	private static void verifyInputStream(URLConnection urlConnection) throws IOException {
		InputStream in = null;
		try {
			in = urlConnection.getInputStream();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

}
