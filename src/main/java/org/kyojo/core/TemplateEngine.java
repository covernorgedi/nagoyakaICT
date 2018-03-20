package org.kyojo.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.StringEscapeUtils;
import org.kyojo.gbd.AppConfig;
import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder;
import org.seasar.doma.jdbc.tx.TransactionManager;

public class TemplateEngine {

	private static final Log logger = LogFactory.getLog(TemplateEngine.class);

	public static String[] TP_PATTERNS = new String[] {
			"^([ \\t]*)([#?!])([\\w/]+)([\\(\\{\\[].*?[\\)\\}\\]]|)([\\r\\n]*)$",
			"^([ \\t]*)<script>([#?!])([\\w/]+)([\\(\\{\\[].*?[\\)\\}\\]]|)</script>([\\r\\n]*)$",
			"^([ \\t]*)<tr><td>([#?!])([\\w/]+)([\\(\\{\\[].*?[\\)\\}\\]]|)</td></tr>([\\r\\n]*)$",
			"^([ \\t]*)<thead><tr><td>([#?!])([\\w/]+)([\\(\\{\\[].*?[\\)\\}\\]]|)</td></tr></thead>([\\r\\n]*)$",
			"^([ \\t]*)<tbody><tr><td>([#?!])([\\w/]+)([\\(\\{\\[].*?[\\)\\}\\]]|)</td></tr></tbody>([\\r\\n]*)$"
	};

	private GlobalData gbd = null;

	private SessionData ssd = null;

	private RequestData rqd = null;

	private ResponseData rpd = null;

	private Skin skin = null;

	private PluginManager pm = null;

	private static boolean isLogDebugCache = false;

	private static boolean isLogAllMagicWords = false;

	private static boolean isLogMagicWordPattern = false;

	private static boolean isLogMagicWordReplace = false;

	private static boolean isLogParseTemplete = false;

	private static boolean isLogSaveAndLoadResult = false;

	private static boolean isLogMagicTower = false;

	public static Pattern[] tppts;
	static {
		tppts = new Pattern[TP_PATTERNS.length];
		for(int pidx = 0; pidx < TP_PATTERNS.length; pidx++) {
			tppts[pidx] = Pattern.compile(TP_PATTERNS[pidx]);
		}
	}

	private int depthLimit = 50;

	private LinkedList<Cache> cacheTower = new LinkedList<>();

	private String act;

	private String ext;

	private String path;

	private String sid;

	private String ct = "";

	private LinkedList<HashMap<String, ReferenceStructure>> magicTower = new LinkedList<>();

	private HashMap<String, ReferenceStructure> magicFloor = null;

	private LinkedList<Pattern> ptrnTower = new LinkedList<>();

	private Pattern magicPtrn = null;

	private LinkedList<Object> pluginTower = new LinkedList<>();

	public static String AP1_PATTERN = "^([\\w/]+)\\.(\\w+)(|#\\w+)$";

	public static String AP2_PATTERN = "^([\\w/]+)\\.(\\w+)(|#\\w+)\\?([\\w&;=%]+)$";

	public static String AP3_PATTERN = "^([\\w/]+)\\.(\\w+)(|#\\w+)(/[\\w/]+)$";

	public static String AP4_PATTERN = "^([\\w/]+)\\.(\\w+)(|#\\w+)(/[\\w/]+)\\?([\\w&;=%]+)$";

	public static Pattern ap1pt = Pattern.compile(AP1_PATTERN);

	public static Pattern ap2pt = Pattern.compile(AP2_PATTERN);

	public static Pattern ap3pt = Pattern.compile(AP3_PATTERN);

	public static Pattern ap4pt = Pattern.compile(AP4_PATTERN);

	public StringBuilder debugCacheTree = new StringBuilder("debugCacheTree\n");

	private boolean submitCalled = false; // ループ防止

	public TemplateEngine(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd) {
		this.gbd = gbd;
		this.ssd = ssd;
		this.rqd = rqd;
		this.rpd = rpd;

		Object val = gbd.get("IS_LOG_DEBUG_CACHE");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogDebugCache = true;
		}
		val = gbd.get("IS_LOG_ALL_MAGIC_WORDS");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogAllMagicWords = true;
		}
		val = gbd.get("IS_LOG_MAGIC_WORD_PATTERN");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogMagicWordPattern = true;
		}
		val = gbd.get("IS_LOG_MAGIC_WORD_REPLACE");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogMagicWordReplace = true;
		}
		val = gbd.get("IS_LOG_PARSE_TEMPLETE");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogParseTemplete = true;
		}
		val = gbd.get("IS_LOG_SAVE_AND_LOAD_RESULT");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogSaveAndLoadResult = true;
		}
		val = gbd.get("IS_LOG_MAGIC_TOWER");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogMagicTower = true;
		}

		act = rqd.getRequest().getParameter(Constants.ACT_KEY);
		if(act == null) act = "";
		ext = rqd.getRequest().getParameter(Constants.EXT_KEY);
		if(ext == null) ext = "";
		path = rqd.getRequest().getParameter(Constants.PATH_KEY);
		if(path == null) path = "";
		sid = ssd.getSessionID();
		logger.debug(String.format("%s=\"%s\", %s=\"%s\", %s=\"%s\"",
				Constants.ACT_KEY, act, Constants.EXT_KEY, ext, Constants.PATH_KEY, path));
		skin = new Skin(gbd, StringUtils.isNotBlank(ext) ? ext : Constants.DEFAULT_EXT);
		pm = PluginManager.getInstance(gbd);
	}

	public void printContent() throws IOException {
		Cache cache;
		try {
			cache = cacheParsedTemplate(Constants.TEMPLATE_ROOT, "", "", false, null);
		} catch(PluginException pe) {
			HashMap<String, String> err = new HashMap<>();
			err.put("errorMessage", pe.getMessage());
			ssd.takeOver(err, Constants.ERROR_SKIN, null);
			StringBuilder sb = new StringBuilder(rqd.getRequest().getContextPath());
			sb.append("/");
			sb.append(Constants.ERROR_SKIN);
			sb.append(".");
			sb.append(skin.getExt());
			rpd.getResponse().sendRedirect(sb.toString());
			return;
		} catch(RedirectThrowable rt) {
			Object rdctTo = rt.getRedirectTo();
			if(rdctTo instanceof Class) {
				rdctTo = My.camelize(((Class<?>)rdctTo).getSimpleName()) + "." + skin.getExt();
			}
			if(rdctTo instanceof String) {
				StringBuilder sb = new StringBuilder(rqd.getRequest().getContextPath());
				sb.append("/");
				sb.append((String)rdctTo);
				logger.debug("redirect to " + sb.toString());
				rpd.getResponse().sendRedirect(sb.toString());
			} else if (rdctTo instanceof URI) {
				logger.debug("redirect to " + rdctTo.toString());
				rpd.getResponse().sendRedirect(rdctTo.toString());
			}
			return;
		} catch(CompleteThrowable ct) {
			logger.debug("CompleteThrowable caught.");
			return;
		}
		// String act = request.getParameter(Constants.ACT_KEY").toString());
		// if(!skin.hasSkin(act)) {
		//	act = Constants.DEFAULT_ACT").toString();
		// }
		// Cache cache = cacheParsedTemplate(act, "", "", false);
		PrintWriter out = rpd.getResponse().getWriter();
		String full = new String(cache.toString().getBytes("UTF-8"), "ISO-8859-1");
		out.print(full);
		if(isLogDebugCache) {
			logger.debug(debugCacheTree.toString());
		}
	}

	public void changeExt(String ext) {
		skin = new Skin(gbd, ext);
	}


	private void getPluginMethods(Class<?> cls, Map<String, Method> plgMtdsMap) {
		for(Method mtd : cls.getMethods()) {
			if(mtd.getAnnotation(Deprecated.class) != null) {
				continue;
			}

			if(plgMtdsMap != null) {
				if(!plgMtdsMap.containsKey("inMtd") && mtd.getName().equals(Constants.PLG_INITIALIZE_MTD_NAME)
						&& isInitializeMethod(mtd)) {
					plgMtdsMap.put("inMtd", mtd);
				// } else if(!plgMtdsMap.containsKey("gdMtd") && mtd.getName().equals(Constants.PLG_GET_D_KEY_MTD_NAME)
				//		&& isInitializeMethod(mtd)) {
				//	plgMtdsMap.put("gdMtd", mtd);
				} else if(!plgMtdsMap.containsKey("bcMtd") && mtd.getName().equals(Constants.PLG_BUILD_CACHE_MTD_NAME)
						&& isBuildCacheMethod(mtd)) {
					plgMtdsMap.put("bcMtd", mtd);
				} else if(!plgMtdsMap.containsKey("rcMtd") && mtd.getName().equals(Constants.PLG_RECYCLE_MTD_NAME)
						&& isInitializeMethod(mtd)) {
					plgMtdsMap.put("rcMtd", mtd);
				} else {
					if(mtd.getName().startsWith(Constants.PLG_SUBMIT_MTD_PREFIX) && isBuildCacheMethod(mtd)
							&& rqd.containsKeyBfDlm(mtd.getName())) {
						plgMtdsMap.put("smMtd", mtd);
					}
				}
			}
		}
	}

	public static void getMethodsAndFields(Class<?> cls,
			Map<String, Method[]> gsMtdsMap, Map<String, Field> fldMap) {
		for(Method mtd : cls.getMethods()) {
			if(mtd.getAnnotation(Deprecated.class) != null) {
				continue;
			}
			int modifier = mtd.getModifiers();
			if(Modifier.isFinal(modifier)
					|| Modifier.isStatic(modifier) || Modifier.isVolatile(modifier)) {
				continue;
			}

			if(mtd.getName().length() > 3
					&& ((mtd.getName().startsWith("get") && mtd.getParameterTypes().length == 0)
							|| (mtd.getName().startsWith("set") && mtd.getParameterTypes().length == 1))) {
				// get/setメソッドを記憶
				String klc = mtd.getName().substring(3).toLowerCase();
				if(!klc.equals("class") && !klc.equals("nativevalue")
						&& !klc.equals("property") && !klc.equals("metaclass")) {
					if(!gsMtdsMap.containsKey(klc)) {
						gsMtdsMap.put(klc, new Method[2]);
					}
					Method[] gsMtds = gsMtdsMap.get(klc);
					gsMtds[mtd.getName().startsWith("get") ? 0 : 1] = mtd;
				}
			}
		}

		for(Field fld : cls.getFields()) {
			if(fld.getAnnotation(Deprecated.class) != null) {
				continue;
			}
			int modifier = fld.getModifiers();
			if(Modifier.isFinal(modifier)
					|| Modifier.isStatic(modifier) || Modifier.isVolatile(modifier)) {
				continue;
			}

			String klc = fld.getName().toLowerCase();
			if(!gsMtdsMap.containsKey(klc)) {
				// get/setメソッド優先
				if(!klc.equals("class") && !klc.equals("nativevalue")
						&& !klc.equals("property") && !klc.equals("metaclass")
						&& !klc.startsWith("_")) {
					// publicフィールドを記憶
					fldMap.put(klc, fld);
				}
			}
		}
	}

	public String parseTemplate(String indent, String sKey, String args,
			String br, boolean isForced)
			throws ServletException, IOException, PluginException, RedirectThrowable, CompleteThrowable {
		if(args == null) args = "";
		String dKey = "";
		if(StringUtils.isNotEmpty(args)) {
			dKey = My.hs(args);
		}
		Cache cache = new Cache(sKey, dKey, skin.getExt(), new Time14(), Time14.OLD);

		boolean ctRaised = false;
		boolean mtRaised = false;
		raiseCacheTower(cache);
		ctRaised = true;
		raiseMagicTower();
		mtRaised = true;
		if(isLogMagicWordPattern) {
			logger.debug("magicPtrn: " + magicPtrn.pattern());
		}
		try {
			String args2 = args;
			if(args.length() > 0 && magicPtrn != null) {
				// magic word展開
				Matcher mc = magicPtrn.matcher(args);
				StringBuffer sb = new StringBuffer();
				while(mc.find()) {
					String mwk = mc.group(1);
					ReferenceStructure ref = magicFloor.get(mwk);
					if(mwk.equals(My.constantize(Constants.MINION_SUFFIX))
							|| ReferenceConverter.convKey2Suf(mwk).equals(Constants.MINION_SUFFIX)) {
						if(ref == null) {
							if(isLogMagicWordReplace) {
								logger.debug("replace: " + mwk + "->(null)");
							}
							mc.appendReplacement(sb, "null");
						} else {
							String mwv = ref.getMnn();
							if(isLogMagicWordReplace) {
								logger.debug("replace: " + mwk + "->" + mwv);
							}
							mc.appendReplacement(sb, Matcher.quoteReplacement(mwv));
						}
					} else {
						if(ref == null) {
							if(isLogMagicWordReplace) {
								logger.debug("replace: " + mwk + "->(null)");
							}
							mc.appendReplacement(sb, "");
						} else {
							String mwv = ref.conv(String.class).getObj();
							if(isLogMagicWordReplace) {
								logger.debug("replace: " + mwk + "->" + mwv);
							}
							mc.appendReplacement(sb, Matcher.quoteReplacement(mwv));
						}
					}
				}
				mc.appendTail(sb);
				args2 = sb.toString();
			}

			parseTemplate(cache, indent, "#", sKey, args2, br, isForced);
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if(ctRaised) reduceCacheTower();
			if(mtRaised) reduceMagicTower();
		}

		Iterator<String> itr = cache.getLines();
		StringBuilder sb = new StringBuilder();
		while(itr.hasNext()) {
			sb.append(itr.next());
		}
		return sb.toString();
	}

	public void parseTemplate(Cache parent, String indent, String type, String sKey, String args,
			String br, boolean isForced)
			throws ServletException, IOException, PluginException, RedirectThrowable, CompleteThrowable {
		if(isLogParseTemplete) {
			logger.debug("parsing: " + indent + type + sKey + args);
		}
		if(cacheTower.size() > depthLimit) {
			logger.warn("depth exceeded.");
			return;
		}

		int len = args.length();
		String args0 = args;
		if(len > 0 && args.startsWith("(")) {
			args = args.substring(1, len - 1);
		}

		String dKey = "";
		if(StringUtils.isNotEmpty(args)) {
			dKey = My.hs(args);
		}
		String ssdKey = Cache.concatKeys(sKey, dKey, "mnn");
		if(type.equals("!")) {
			// TODO: error page?
		} else if(type.equals("?")) {
			parent.setExpires(Time14.OLD);
			if(act != null && act.equals(sKey)) {
				parent.setSkipAfter(true);
			} else {
				return;
			}
		}

		boolean hasPlugin = false;
		boolean pluginRan = false;
		Object obj = null;
		HashMap<String, Method> plgMtdsMap = new HashMap<>();
		HashMap<String, Method[]> gsMtdsMap = new HashMap<>();
		HashMap<String, Field> fldMap = new HashMap<>();
		boolean templateParsed = false;
		if(type.equals("#") || type.equals("?")) {
			try {
				if(pm.hasPlugin(act, sKey)) {
					hasPlugin = true;
					try {
						Class<?> cls = pm.loadPlugin(act, sKey);
						TransactionManager tm = AppConfig.singleton().getTransactionManager();

						// メソッドとフィールドの情報を収集
						getPluginMethods(cls, plgMtdsMap);
						getMethodsAndFields(cls, gsMtdsMap, fldMap);

						// submitの場合、あればnameからpathを取得
						if(!submitCalled) {
							if(plgMtdsMap.containsKey("smMtd")) {
								Method smMtd = plgMtdsMap.get("smMtd");
								if(rqd.containsKeyBfDlm(smMtd.getName())) {
									path = rqd.getKeyAfDlm(smMtd.getName());
								}
							}
							if(path == null) {
								path = "";
							}
							magicFloor.put(My.constantize(Constants.PATH_KEY), new ReferenceStructure(path));
							// rpd.put(Constants.PATH_KEY, path);
						}

						Cache mc = null;
						boolean needInit = false;
						// ssdに前回アクセス時のデータがあるか
						mc = (Cache)ssd.get(ssdKey);
						if(mc == null) {
							// ない
							needInit = true;
							if(isLogSaveAndLoadResult) {
								logger.debug("ssd-load: " + ssdKey + " -> (no data)");
							}
						} else {
							// プラグインファイルの更新時刻と比較
							File pf = pm.getPluginFile(act, sKey, null);
							long lm = pf.lastModified();
							if(lm == 0L) {
								needInit = true;
								if(isLogSaveAndLoadResult) {
									logger.debug("ssd-load: " + ssdKey + " -> (error?)");
								}
							} else {
								Time14 lmT14 = new Time14(new Date(lm));
								if(mc.getCreated().compareTo(lmT14) < 0) {
									needInit = true;
									if(isLogSaveAndLoadResult) {
										logger.debug("ssd-load: " + ssdKey + " -> (expired)");
									}
								}
							}
						}

						if(needInit) {
							// なければインスタンス生成
							obj = cls.newInstance();
						} else {
							pluginRan = true;
							Iterator<String> itr = mc.getLines();
							String mnn = null;
							if(itr.hasNext()) {
								mnn = itr.next();
							}

							if(isLogSaveAndLoadResult) {
								logger.debug("ssd-load: " + ssdKey + " -> " + mnn);
							}
							obj = My.deminion(mnn, cls);
						}
						pluginTower.add(obj);

						if(gsMtdsMap.containsKey(Constants.PATH_KEY.toLowerCase())) {
							Method[] gsMtds = gsMtdsMap.get(Constants.PATH_KEY.toLowerCase());
							Method sm = gsMtds[1];
							sm.invoke(obj, path);
						} else {
							if(fldMap.containsKey(Constants.PATH_KEY)) {
								Field fld = fldMap.get(Constants.PATH_KEY);
								fld.set(obj, path);
							}
						}

						if(needInit && plgMtdsMap.containsKey("inMtd")) {
							final Object obj2 = obj;
							final Method inMtd2 = plgMtdsMap.get("inMtd");
							final String args2 = args;
							Object res = tm.required(() -> {
								Object res2;
								try {
									res2 = inMtd2.invoke(obj2, args2, gbd, ssd, rqd, rpd);
									if(rpd.getResponse().isCommitted()) {
										return new CompleteThrowable();
									}
								} catch(IllegalAccessException iae) {
									logger.warn(iae.getMessage(), iae);
									return iae;
								} catch(IllegalArgumentException iae) {
									logger.warn(iae.getMessage(), iae);
									return iae;
								} catch(InvocationTargetException ite) {
									Throwable cause = ite.getCause();
									if(cause == null) {
										logger.warn(ite.getMessage(), ite);
										return ite;
									} else if(cause instanceof RedirectThrowable) {
										RedirectThrowable rt = (RedirectThrowable)cause;
										return rt.getRedirectTo();
									} else if(cause instanceof CompleteThrowable) {
										return cause;
									}
									logger.warn(ite.getMessage(), ite);
									return ite;
								}
								return res2;
							});
							if(res instanceof IllegalAccessException) {
								throw (IllegalAccessException)res;
							} else if(res instanceof IllegalArgumentException) {
								throw (IllegalArgumentException)res;
							} else if(res instanceof InvocationTargetException) {
								throw (InvocationTargetException)res;
							} else if(res instanceof CompleteThrowable) {
								throw (CompleteThrowable)res;
							}
							pluginRan = true;
							if(isRedirectable(res)) {
								throw new RedirectThrowable(res);
							} else {
								updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);
							}
						} else {
							Object res = null;
							if(plgMtdsMap.containsKey("rcMtd")) {
								final Object obj2 = obj;
								final Method rcMtd2 = plgMtdsMap.get("rcMtd");
								final String args2 = args;
								res = tm.required(() -> {
									Object res2;
									try {
										res2 = rcMtd2.invoke(obj2, args2, gbd, ssd, rqd, rpd);
										if(rpd.getResponse().isCommitted()) {
											return new CompleteThrowable();
										}
									} catch(IllegalAccessException iae) {
										logger.warn(iae.getMessage(), iae);
										return iae;
									} catch(IllegalArgumentException iae) {
										logger.warn(iae.getMessage(), iae);
										return iae;
									} catch(InvocationTargetException ite) {
										Throwable cause = ite.getCause();
										if(cause == null) {
											logger.warn(ite.getMessage(), ite);
											return ite;
										} else if(cause instanceof RedirectThrowable) {
											RedirectThrowable rt = (RedirectThrowable)cause;
											return rt.getRedirectTo();
										} else if(cause instanceof CompleteThrowable) {
											return cause;
										}
										logger.warn(ite.getMessage(), ite);
										return ite;
									}
									return res2;
								});
								if(res instanceof IllegalAccessException) {
									throw (IllegalAccessException)res;
								} else if(res instanceof IllegalArgumentException) {
									throw (IllegalArgumentException)res;
								} else if(res instanceof InvocationTargetException) {
									throw (InvocationTargetException)res;
								} else if(res instanceof CompleteThrowable) {
									throw (CompleteThrowable)res;
								}
							}
							pluginRan = true;
							if(plgMtdsMap.containsKey("rcMtd")) {
								if(isRedirectable(res)) {
									throw new RedirectThrowable(res);
								} else {
									updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);
								}
							}
						}

						// rpdかrqdが存在する場合はメソッドとフィールドを上書き
						for(Map.Entry<String, Method[]> ent : gsMtdsMap.entrySet()) {
							// メソッド
							Method[] gsMtds = gsMtdsMap.get(ent.getKey());
							if(gsMtds[0] != null && gsMtds[1] != null) {
								Method gm = gsMtds[0];
								String kpc = gm.getName().substring(3);
								if(rpd.containsKey(kpc)) {
									// rpdが存在すれば上書き
									Method sm = gsMtds[1];
									Class<?>[] prmClss = sm.getParameterTypes();
									if(prmClss.length == 1) {
										ReferenceStructure rs = rpd.get(kpc);
										if(List.class.isAssignableFrom(prmClss[0])) {
											ParameterizedType gType = (ParameterizedType)sm.getGenericParameterTypes()[0];
											Type[] aTypes = gType.getActualTypeArguments();
											ReferenceEntry<?> re = rs.conv((Class<?>)aTypes[0]);
											List<?> list = createListAndCopyFlx((Class<?>)aTypes[0], re.getList(), re.getCls());
											sm.invoke(obj, list);
											String kc = My.constantize(kpc);
											magicFloor.put(kc + re.getSuf(), rs);
										} else {
											ReferenceEntry<?> re = rs.conv(prmClss[0]);
											Object val = createObjectAndCopyFlx(prmClss[0], re.getObj(), re.getCls());
											sm.invoke(obj, val);
											String kc = My.constantize(kpc);
											magicFloor.put(kc + re.getSuf(), rs);
										}
									}
								} else if(rqd.containsKey(kpc)) {
									// rqdが存在すれば上書き
									Method sm = gsMtds[1];
									Class<?>[] prmClss = sm.getParameterTypes();
									if(prmClss.length == 1) {
										Object ro = rqd.get(kpc);
										if(ro instanceof ReferenceStructure) {
											ReferenceStructure rs = (ReferenceStructure)ro;
											if(List.class.isAssignableFrom(prmClss[0])) {
												ParameterizedType gType = (ParameterizedType)sm.getGenericParameterTypes()[0];
												Type[] aTypes = gType.getActualTypeArguments();
												ReferenceEntry<?> re = rs.conv((Class<?>)aTypes[0]);
												List<?> list = createListAndCopyFlx((Class<?>)aTypes[0], re.getList(), re.getCls());
												sm.invoke(obj, list);
											} else {
												ReferenceEntry<?> re = rs.conv(prmClss[0]);
												if(prmClss[0].isArray() && prmClss[0].getComponentType().equals(byte.class)) {
													sm.invoke(obj, re.getRaw());
												} else if(FileItem.class.isAssignableFrom(prmClss[0])) {
													sm.invoke(obj, re.getFileItem());
												} else {
													Object val = createObjectAndCopyFlx(prmClss[0], re.getObj(), re.getCls());
													sm.invoke(obj, val);
												}
											}
										} else if(ro instanceof Map) {
											if(List.class.isAssignableFrom(prmClss[0])) {
												ParameterizedType gType = (ParameterizedType)sm.getGenericParameterTypes()[0];
												Type[] aTypes = gType.getActualTypeArguments();
												List<Object> list = createListAndCopyFlx((Class<?>)aTypes[0], ro, Map.class);
												sm.invoke(obj, list);
											} else {
												Object val = createObjectAndCopyFlx(prmClss[0], ro, Map.class);
												sm.invoke(obj, val);
											}
										} else if(ro instanceof List) {
											if(List.class.isAssignableFrom(prmClss[0])) {
												ParameterizedType gType = (ParameterizedType)sm.getGenericParameterTypes()[0];
												Type[] aTypes = gType.getActualTypeArguments();
												List<?> list = createListAndCopyFlx((Class<?>)aTypes[0], ro, List.class);
												sm.invoke(obj, list);
											} else {
												List<?> list = createListAndCopyFlx(prmClss[0], ro, List.class);
												if(list.size() > 0) {
													sm.invoke(obj, list.get(0));
												}
											}
										}
									}
								}
							}
						}
						for(Map.Entry<String, Field> ent : fldMap.entrySet()) {
							if(gsMtdsMap.containsKey(ent.getKey())) {
								continue;
							}

							Field fld = fldMap.get(ent.getKey());
							String kcm = fld.getName();
							if(rpd.containsKey(kcm)) {
								// rpdが存在すれば上書き
								ReferenceStructure rs = rpd.get(kcm);
								if(List.class.isAssignableFrom(fld.getType())) {
									ParameterizedType gType = (ParameterizedType)fld.getGenericType();
									Type[] aTypes = gType.getActualTypeArguments();
									ReferenceEntry<?> re = rs.conv((Class<?>)aTypes[0]);
									List<?> list = createListAndCopyFlx((Class<?>)aTypes[0], re.getList(), re.getCls());
									fld.set(obj, list);
									String kc = My.constantize(kcm);
									magicFloor.put(kc + re.getSuf(), rs);
								} else {
									ReferenceEntry<?> re = rs.conv(fld.getType());
									Object val = createObjectAndCopyFlx(fld.getType(), re.getObj(), re.getCls());
									fld.set(obj, val);
									String kc = My.constantize(kcm);
									magicFloor.put(kc + re.getSuf(), rs);
								}
							} else if(rqd.containsKey(kcm)) {
								// rqdが存在すれば上書き
								Object ro = rqd.get(kcm);
								if(ro instanceof ReferenceStructure) {
									ReferenceStructure rs = (ReferenceStructure)ro;
									if(List.class.isAssignableFrom(fld.getType())) {
										ParameterizedType gType = (ParameterizedType)fld.getGenericType();
										Type[] aTypes = gType.getActualTypeArguments();
										ReferenceEntry<?> re = rs.conv((Class<?>)aTypes[0]);
										List<?> list = createListAndCopyFlx((Class<?>)aTypes[0], re.getList(), re.getCls());
										fld.set(obj, list);
									} else {
										ReferenceEntry<?> re = rs.conv(fld.getType());
										if(fld.getType().isArray() && fld.getType().getComponentType().equals(byte.class)) {
											fld.set(obj, re.getRaw());
										} else if(FileItem.class.isAssignableFrom(fld.getType())) {
											fld.set(obj, re.getFileItem());
										} else {
											Object val = createObjectAndCopyFlx(fld.getType(), re.getObj(), re.getCls());
											fld.set(obj, val);
										}
									}
								} else if(ro instanceof Map) {
									if(List.class.isAssignableFrom(fld.getType())) {
										ParameterizedType gType = (ParameterizedType)fld.getGenericType();
										Type[] aTypes = gType.getActualTypeArguments();
										List<Object> list = createListAndCopyFlx((Class<?>)aTypes[0], ro, Map.class);
										fld.set(obj, list);
									} else {
										Object val = createObjectAndCopyFlx(fld.getType(), ro, Map.class);
										fld.set(obj, val);
									}
								} else if(ro instanceof List) {
									if(List.class.isAssignableFrom(fld.getType())) {
										ParameterizedType gType = (ParameterizedType)fld.getGenericType();
										Type[] aTypes = gType.getActualTypeArguments();
										List<?> list = createListAndCopyFlx((Class<?>)aTypes[0], ro, List.class);
										fld.set(obj, list);
									} else {
										List<?> list = createListAndCopyFlx(fld.getType(), ro, List.class);
										if(list.size() > 0) {
											fld.set(obj, list.get(0));
										}
									}
								}
							}
						}

						updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);

						if(plgMtdsMap.containsKey("bcMtd") || (!submitCalled && plgMtdsMap.containsKey("smMtd"))) {
							Cache child = new Cache(sKey, "", skin.getExt(), Time14.OLD);
							pm.getPluginFile(act, sKey, child);
							if(!submitCalled && plgMtdsMap.containsKey("smMtd")) {
								final Object obj2 = obj;
								final Method smMtd2 = plgMtdsMap.get("smMtd");
								final String args2 = args;
								submitCalled = true;
								Object res = tm.required(() -> {
									Object res2;
									try {
										res2 = smMtd2.invoke(obj2, child, args2, gbd, ssd, rqd, rpd,
												this, indent, isForced);
										if(rpd.getResponse().isCommitted()) {
											return new CompleteThrowable();
										}
									} catch(IllegalAccessException iae) {
										logger.warn(iae.getMessage(), iae);
										return iae;
									} catch(IllegalArgumentException iae) {
										logger.warn(iae.getMessage(), iae);
										return iae;
									} catch(InvocationTargetException ite) {
										Throwable cause = ite.getCause();
										if(cause == null) {
											logger.warn(ite.getMessage(), ite);
											return ite;
										} else if(cause instanceof RedirectThrowable) {
											RedirectThrowable rt = (RedirectThrowable)cause;
											return rt.getRedirectTo();
										} else if(cause instanceof CompleteThrowable) {
											return cause;
										}
										logger.warn(ite.getMessage(), ite);
										return ite;
									}
									return res2;
								});
								if(res instanceof IllegalAccessException) {
									throw (IllegalAccessException)res;
								} else if(res instanceof IllegalArgumentException) {
									throw (IllegalArgumentException)res;
								} else if(res instanceof InvocationTargetException) {
									throw (InvocationTargetException)res;
								} else if(res instanceof CompleteThrowable) {
									throw (CompleteThrowable)res;
								}
								pluginRan = true;
								if(isRedirectable(res)) {
									throw new RedirectThrowable(res);
								} else {
									updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);
								}
							}
							if(plgMtdsMap.containsKey("bcMtd")) {
								final Object obj2 = obj;
								final Method bcMtd2 = plgMtdsMap.get("bcMtd");
								final String args2 = args;
								Object res = tm.required(() -> {
									Object res2;
									try {
										res2 = bcMtd2.invoke(obj2, child, args2, gbd, ssd, rqd, rpd,
												this, indent, isForced);
										if(rpd.getResponse().isCommitted()) {
											return new CompleteThrowable();
										}
									} catch(IllegalAccessException iae) {
										logger.warn(iae.getMessage(), iae);
										return iae;
									} catch(IllegalArgumentException iae) {
										logger.warn(iae.getMessage(), iae);
										return iae;
									} catch(InvocationTargetException ite) {
										Throwable cause = ite.getCause();
										if(cause == null) {
											logger.warn(ite.getMessage(), ite);
											return ite;
										} else if(cause instanceof RedirectThrowable) {
											RedirectThrowable rt = (RedirectThrowable)cause;
											return rt.getRedirectTo();
										} else if(cause instanceof CompleteThrowable) {
											return cause;
										}
										logger.warn(ite.getMessage(), ite);
										return ite;
									}
									return res2;
								});
								if(res instanceof IllegalAccessException) {
									throw (IllegalAccessException)res;
								} else if(res instanceof IllegalArgumentException) {
									throw (IllegalArgumentException)res;
								} else if(res instanceof InvocationTargetException) {
									throw (InvocationTargetException)res;
								} else if(res instanceof CompleteThrowable) {
									throw (CompleteThrowable)res;
								}
								pluginRan = true;
								if(isRedirectable(res)) {
									throw new RedirectThrowable(res);
								} else {
									updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);
								}
							}
							parent.addChildKey(child.getKey(), child.getExpires());
							Iterator<String> itr = child.getLines();
							while(itr.hasNext()) {
								String line = itr.next();
								if(line.length() > 0) {
									StringBuilder sb = new StringBuilder();
									sb.append(indent);
									sb.append(line);
									parent.addLine(sb.toString());
								}
							}
							if(plgMtdsMap.containsKey("bcMtd")) {
								return;
							}
						}
					} catch(IllegalArgumentException iae) {
						logger.warn(iae.getMessage(), iae);
						parent.setExpires(Time14.OLD);
						ssd.remove(ssdKey);
					} catch(InstantiationException ie) {
						logger.warn(ie.getMessage(), ie);
						parent.setExpires(Time14.OLD);
						ssd.remove(ssdKey);
					} catch(IllegalAccessException iae) {
						logger.warn(iae.getMessage(), iae);
						parent.setExpires(Time14.OLD);
						ssd.remove(ssdKey);
					} catch(InvocationTargetException ite) {
						logger.warn(ite.getMessage(), ite);
						parent.setExpires(Time14.OLD);
						ssd.remove(ssdKey);
					} catch(IllegalStateException ise) {
						logger.warn(ise.getMessage(), ise);
						parent.setExpires(Time14.OLD);
						ssd.remove(ssdKey);
					}
				}

				if(skin.hasSkin(act, sKey)) {
					Cache child;
					if(pluginRan) {
						child = cacheParsedTemplate(sKey, dKey, indent, isForced, Time14.OLD);
					} else {
						child = cacheParsedTemplate(sKey, dKey, indent, isForced, null);
					}
					if(!hasPlugin) {
						pm.getPluginFile(act, sKey, child);
					}
					parent.addChildKey(child.getKey(), child.getExpires());
					// Iterator<String> itr = child.getLinesAndSaveMemory();
					Iterator<String> itr = child.getLines();
					while(itr.hasNext()) {
						String line = itr.next();
						if(line.length() > 0) {
							StringBuilder sb = new StringBuilder();
							sb.append(indent);
							sb.append(line);
							parent.addLine(sb.toString());
						}
					}

					templateParsed = true;
				}
			} finally {
				if(obj != null) {
					// 最後に記録
					if(pluginRan || templateParsed) {
						// String mnn = My.minion(obj);
						String mnn = SimpleJsonBuilder.toJson(obj);
						Cache mnnCache = new Cache(sKey, My.hs(args), "mnn", new Time14());
						mnnCache.addLine(mnn);
						if(isLogSaveAndLoadResult) {
							logger.debug("ssd-save: " + mnnCache.getKey() + " -> " + mnn);
						}
						ssd.put(mnnCache.getKey(), mnnCache);
					}

					pluginTower.removeLast();
				}
			}

			if(obj != null || templateParsed) {
				return;
			}
		}
		// TODO: changelingってなんだっけ。textareaかなんかでタグを消すとかだったような。あれ、違うか。

		logger.warn("couldn't parse plugin " + type + sKey + args0 + ".");
		parent.setExpires(Time14.OLD);
	}

	private boolean isInitializeMethod(Method mtd) {
		Class<?>[] prmClss = mtd.getParameterTypes();
		if(prmClss.length != 5) return false;
		if(!prmClss[0].equals(String.class)) return false;
		if(!prmClss[1].equals(GlobalData.class)) return false;
		if(!prmClss[2].equals(SessionData.class)) return false;
		if(!prmClss[3].equals(RequestData.class)) return false;
		if(!prmClss[4].equals(ResponseData.class)) return false;
		return true;
	}

	private boolean isBuildCacheMethod(Method mtd) {
		Class<?>[] prmClss = mtd.getParameterTypes();
		if(prmClss.length != 9) return false;
		if(!prmClss[0].equals(Cache.class)) return false;
		if(!prmClss[1].equals(String.class)) return false;
		if(!prmClss[2].equals(GlobalData.class)) return false;
		if(!prmClss[3].equals(SessionData.class)) return false;
		if(!prmClss[4].equals(RequestData.class)) return false;
		if(!prmClss[5].equals(ResponseData.class)) return false;
		if(!prmClss[6].equals(TemplateEngine.class)) return false;
		if(!prmClss[7].equals(String.class)) return false;
		if(!prmClss[8].equals(boolean.class)) return false;
		return true;
	}

	// getMtdsAndFldsをまだ実行していない場合のupdatePluginResult
	public <T> void updatePluginResult(Object obj, Class<T> cls)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HashMap<String, Method> plgMtdsMap = new HashMap<>();
		HashMap<String, Method[]> gsMtdsMap = new HashMap<>();
		HashMap<String, Field> fldMap = new HashMap<>();
		getPluginMethods(cls, plgMtdsMap);
		getMethodsAndFields(cls, gsMtdsMap, fldMap);

		updatePluginResult(obj, cls, plgMtdsMap, gsMtdsMap, fldMap);
	}

	@SuppressWarnings("unchecked")
	private <T> void updatePluginResult(Object obj, Class<T> cls,
			Map<String, Method> plgMtdsMap, Map<String, Method[]> gsMtdsMap, Map<String, Field> fldMap)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(gsMtdsMap == null || fldMap == null) {
			gsMtdsMap = new HashMap<>();
			fldMap = new HashMap<>();
			getMethodsAndFields(cls, gsMtdsMap, fldMap); // ToDo: キャッシュできるのでは
		}

		// プラグイン自身の置換
		String key = My.constantize(Constants.MINION_SUFFIX);
		ReferenceStructure ref = new ReferenceStructure((T)obj, Constants.MINION_SUFFIX, cls);
		magicFloor.put(key, ref);

		// プラグインのルートのフィールド名でnullのとき一括で置換できるようにする
		for(Map.Entry<String, Method[]> ent : gsMtdsMap.entrySet()) {
			Method[] gsMtds = gsMtdsMap.get(ent.getKey());
			if(gsMtds[0] != null && gsMtds[1] != null) {
				Method gm = gsMtds[0];
				String kpc = gm.getName().substring(3);
				String kc = My.constantize(kpc);
				magicFloor.put(kc + "[", null);
			}
		}
		for(Map.Entry<String, Field> ent : fldMap.entrySet()) {
			if(gsMtdsMap.containsKey(ent.getKey())) {
				continue;
			}
			Field fld = fldMap.get(ent.getKey());
			String kcm = fld.getName();
			String kc = My.constantize(kcm);
			magicFloor.put(kc + "[", null);
		}

		buildMagicFloorRecursive(obj, gsMtdsMap, fldMap, "", magicFloor);
		magicPtrn = updateMagicPtrn();
		ptrnTower.removeLast();
		ptrnTower.add(magicPtrn);
	}

	public boolean isRedirectable(Object obj) {
		if(obj == null) return false;

		if(obj instanceof Class) {
			obj = My.camelize(((Class<?>)obj).getSimpleName()) + "." + skin.getExt();
		}

		if(obj instanceof String) {
			String ap = (String)obj;
			String act = null;
			String ext = null;
			Matcher ap1mt = ap1pt.matcher(ap);
			if(ap1mt.matches()) {
				act = ap1mt.group(1);
				ext = ap1mt.group(2);
			} else {
				Matcher ap2mt = ap2pt.matcher(ap);
				if(ap2mt.matches()) {
					act = ap2mt.group(1);
					ext = ap2mt.group(2);
				} else {
					Matcher ap3mt = ap3pt.matcher(ap);
					if(ap3mt.matches()) {
						act = ap3mt.group(1);
						ext = ap3mt.group(2);
					} else {
						Matcher ap4mt = ap4pt.matcher(ap);
						if(ap4mt.matches()) {
							act = ap4mt.group(1);
							ext = ap4mt.group(2);
						}
					}
				}
			}

			if(StringUtils.isNotEmpty(act) && StringUtils.isNotEmpty(ext)) {
				// if(skin.hasSkin(null, act) || pm.hasPlugin(act)) {
					return true;
				// }
			}
		} else if(obj instanceof URI) {
			return true;
		}

		logger.warn("can't redirect to " + obj.toString());
		return false;
	}

	public <T> Cache appendParsedTemplate(Object obj, Class<T> cls, Cache parent,
			String sKey, String dKey, String indent, boolean isForced, Time14 expires)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		boolean ctRaised = false;
		boolean mtRaised = false;
		raiseCacheTower(parent);
		ctRaised = true;
		raiseMagicTower();
		mtRaised = true;
		if(isLogMagicWordPattern) {
			logger.debug("magicPtrn: " + magicPtrn.pattern());
		}
		try {
			updatePluginResult(obj, cls);
			Cache child = cacheParsedTemplate(sKey, dKey, indent, isForced, expires);
			Iterator<String> itr = child.getLines();
			while(itr.hasNext()) {
				String line = itr.next();
				if(line.length() > 0) {
					StringBuilder sb = new StringBuilder();
					sb.append(indent);
					sb.append(line);
					parent.addLine(sb.toString());
				}
			}
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if(ctRaised) reduceCacheTower();
			if(mtRaised) reduceMagicTower();
		}

		return parent;
	}

	public Cache cacheParsedTemplate(String sKey, String dKey, String indent, boolean isForced, Time14 expires)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		Cache cache = null;
		if(!isForced) {
			cache = gbd.loadCacheIfPossible(sKey, dKey, skin.getExt(), true);
			if(cache != null) {
				addDebugCacheTree(cache, indent, "loaded");
				return cache;
			}
		}

		cache = new Cache(sKey, dKey, skin.getExt(), new Time14(), expires == null ? Time14.FUTURE : expires);

		// parse template
		JavaFGets jfg = null;
		InputStream is = null;
		File skinFile = skin.getSkinFile(act, sKey, cache);
		if(skinFile == null) {
			logger.warn("no template " + sKey + ".");
			cache.addLine("(no template.)");
			addDebugCacheTree(cache, indent, "no template");
			return cache;
		}

		boolean ctRaised = false;
		boolean mtRaised = false;
		raiseCacheTower(cache);
		ctRaised = true;
		raiseMagicTower();
		mtRaised = true;
		if(isLogMagicWordPattern) {
			logger.debug("magicPtrn: " + magicPtrn.pattern());
		}
		try {
			is = new FileInputStream(skinFile);
			jfg = new JavaFGets(is);
			String line = null;
			Matcher tpmt = null;
			while((line = jfg.readLine()) != null) {
				line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
				// line = new String(line.getBytes("UTF-8"));

				// テンプレート/プラグイン展開
				boolean isMatched = false;
				for(Pattern tppt : tppts) {
					tpmt = tppt.matcher(line);
					if(tpmt.matches()) {
						String args = tpmt.group(4);
						if(args.length() == 0 || (args.length() > 2
								&& (args.charAt(0) == '(' && args.charAt(args.length() - 1) == ')')
								|| (args.charAt(0) == '{' && args.charAt(args.length() - 1) == '}')
								|| (args.charAt(0) == '[' && args.charAt(args.length() - 1) == ']'))) {
							isMatched = true;
							break;
						} else {
							break;
						}
					}
				}
				if(isMatched) {
					String indent2 = tpmt.group(1);
					String type2 = tpmt.group(2);
					String sKey2 = tpmt.group(3);
					String args2 = tpmt.group(4);
					String br2 = tpmt.group(5);

					if(args2.length() > 0 && magicPtrn != null) {
						boolean isJsonArg = args2.startsWith("{") || args2.startsWith("[");
						List<Integer[]> strRngs = new ArrayList<>(); // JSONエスケープ用置換の範囲
						if(isJsonArg) {
							Matcher mc1 = Pattern.compile("\"\"[A-Z0-9_]+\"\"").matcher(args2);
							while(mc1.find()) {
								int start = mc1.start();
								int end = start + mc1.group(0).length();
								strRngs.add(new Integer[] { start, end });
							}
						}

						// magic word展開
						Matcher mc2 = magicPtrn.matcher(args2);
						StringBuffer sb2 = new StringBuffer();
						while(mc2.find()) {
							if(isJsonArg) {
								boolean inRng = false;
								for(Integer[] strRng : strRngs) {
									int start = mc2.start();
									int end = start + mc2.group(0).length();
									if((strRng[0] <= start && start < strRng[1]) || (strRng[0] < end && end <= strRng[1])) {
										mc2.appendReplacement(sb2, Matcher.quoteReplacement(mc2.group(0)));
										inRng = true;
										break;
									}
								}
								if(inRng) continue;
							}

							String mwk = mc2.group(1);
							ReferenceStructure ref = magicFloor.get(mwk);
							if(mwk.equals(My.constantize(Constants.MINION_SUFFIX))
									|| ReferenceConverter.convKey2Suf(mwk).equals(Constants.MINION_SUFFIX)) {
								if(ref == null) {
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->(null)");
									}
									mc2.appendReplacement(sb2, "null");
								} else {
									String mwv = ref.getMnn();
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->" + mwv);
									}
									mc2.appendReplacement(sb2, Matcher.quoteReplacement(mwv));
								}
							} else {
								if(ref == null) {
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->(null)");
									}
									mc2.appendReplacement(sb2, "");
								} else {
									String mwv = ref.conv(String.class).getObj();
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->" + mwv);
									}
									mc2.appendReplacement(sb2, Matcher.quoteReplacement(mwv));
								}
							}
						}
						mc2.appendTail(sb2);

						String tmpArg2 = sb2.toString();
						args2 = tmpArg2;
						if(isJsonArg) {
							String tmpArg3 = tmpArg2.replaceAll("\"\"([A-Z0-9_]+)\"\"", "__$1__");
							if(!tmpArg2.equals(tmpArg3)) {
								// JSONエスケープ用
								Matcher mc3 = magicPtrn.matcher(tmpArg3);
								StringBuffer sb3 = new StringBuffer();
								while(mc3.find()) {
									String mwk = mc3.group(1);
									ReferenceStructure ref = magicFloor.get(mwk);
									if(mwk.equals(My.constantize(Constants.MINION_SUFFIX))
											|| ReferenceConverter.convKey2Suf(mwk).equals(Constants.MINION_SUFFIX)) {
										if(ref == null) {
											if(isLogMagicWordReplace) {
												logger.debug("replace: " + mwk + "->(null)");
											}
											mc3.appendReplacement(sb3, "\"\"");
										} else {
											String mwv = ref.getMnn();
											String mwv2 = SimpleJsonBuilder.escapeJson(mwv);
											String mwv3 = "\"" + mwv2 + "\"";
											if(isLogMagicWordReplace) {
												logger.debug("replace: " + mwk + "->" + mwv3);
											}
											mc3.appendReplacement(sb3, Matcher.quoteReplacement(mwv3));
										}
									} else {
										if(ref == null) {
											if(isLogMagicWordReplace) {
												logger.debug("replace: " + mwk + "->(null)");
											}
											mc3.appendReplacement(sb3, "\"\"");
										} else {
											String mwv = ref.conv(String.class).getObj();
											String mwv2 = SimpleJsonBuilder.escapeJson(mwv);
											String mwv3 = "\"" + mwv2 + "\"";
											if(isLogMagicWordReplace) {
												logger.debug("replace: " + mwk + "->" + mwv3);
											}
											mc3.appendReplacement(sb3, Matcher.quoteReplacement(mwv3));
										}
									}
								}
								mc3.appendTail(sb3);

								args2 = sb3.toString();
							}
						}
					}

					parseTemplate(cache, indent2, type2, sKey2, args2, br2, isForced);

					if(cache.isSkipAfter()) {
						break;
					}
					if(cache.isChRoot()) {
						break;
					}
					continue;
				} else {
					if(magicPtrn != null) {
						// magic word展開
						Matcher mc = magicPtrn.matcher(line);
						StringBuffer sb = new StringBuffer();
						while(mc.find()) {
							String mwk = mc.group(1);
							ReferenceStructure ref = magicFloor.get(mwk);
							if(mwk.equals(My.constantize(Constants.MINION_SUFFIX))
									|| ReferenceConverter.convKey2Suf(mwk).equals(Constants.MINION_SUFFIX)) {
								if(ref == null) {
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->(null)");
									}
									mc.appendReplacement(sb, "null");
								} else {
									String mwv = ref.getMnn();
									String mwv2 = StringEscapeUtils.escapeHtml4(mwv);
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->" + mwv2);
									}
									mc.appendReplacement(sb, Matcher.quoteReplacement(mwv2));
								}
							} else {
								if(ref == null) {
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->(null)");
									}
									mc.appendReplacement(sb, "");
								} else {
									String mwv = ref.conv(String.class).getObj();
									String mwv2 = StringEscapeUtils.escapeHtml4(mwv);
									if(isLogMagicWordReplace) {
										logger.debug("replace: " + mwk + "->" + mwv2);
									}
									mc.appendReplacement(sb, Matcher.quoteReplacement(mwv2));
								}
							}
						}
						mc.appendTail(sb);
						line = sb.toString();
					}
				}

				cache.addLine(line);
			}
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if(ctRaised) reduceCacheTower();
			if(mtRaised) reduceMagicTower();
			try {
				if(is != null) {
					is.close();
				}
				if(jfg != null) {
					jfg.close();
				}
			} catch(Exception ex) {}
		}

		if(!isForced) {
			gbd.completeCache(cache);
		}

		addDebugCacheTree(cache, indent, "created");
		return cache;
	}

	private void raiseMagicTower() {
		if(magicTower.size() == 0) {
			magicFloor = new HashMap<>();
			for(Map.Entry<String, Object> ent : gbd.entrySet()) {
				Object val = ent.getValue();
				if(val == null) {
					val = "";
				}
				if(String.class.isAssignableFrom(val.getClass())) { // ToDo: もっと適当な条件があるか
					String key = My.constantize(ent.getKey());
					magicFloor.put(key, new ReferenceStructure(val.toString()));
				}
			}
			// ToDo: 今のところ役に立つ場面がない？
			// for(Map.Entry<String, Object> ent : ssd.entrySet()) {
			//	String key = My.constantize(ent.getKey());
			//	magicFloor.put(key, ent.getValue().toString());
			// }
			magicFloor.put(My.constantize(Constants.ACT_KEY), new ReferenceStructure(act));
			magicFloor.put(My.constantize(Constants.EXT_KEY), new ReferenceStructure(ext));
			magicFloor.put(My.constantize(Constants.PATH_KEY), new ReferenceStructure(path));
			magicFloor.put(My.constantize(Constants.SESSION_ID_KEY), new ReferenceStructure(sid));
		} else {
			magicFloor = new HashMap<>(magicFloor);
		}

		StringBuilder sb = new StringBuilder();
		boolean isRootCache = true;
		for(Cache tc : cacheTower) {
			if(isRootCache) {
				isRootCache = false;
				continue;
			}
			sb.append(tc.getSKey());
			sb.append(".");
		}
		ct = sb.toString();
		String ctKey = Constants.CACHE_TOWER_KEY;
		magicFloor.put(My.constantize(ctKey), new ReferenceStructure(ct));
		if(isLogMagicTower) {
			logger.info("magicTower raised: " + ct);
		}

		magicTower.add(magicFloor);
		magicPtrn = updateMagicPtrn();
		ptrnTower.add(magicPtrn);
	}

	public static void buildMagicFloorRecursive(Object obj,
			Map<String, Method[]> gsMtdsMap, Map<String, Field> fldMap, String ns,
			HashMap<String, ReferenceStructure> magicFloor)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(obj instanceof List) {
			List<?> list = (List<?>)obj;
			Class<?> cls = null;
			String suf = null;
			for(int idx = 0; idx < list.size(); idx++) {
				int no = idx + 1;
				Object val = list.get(idx);

				String kec = ns + no;
				String kc;
				if(val == null) {
					// kc = My.constantize(kec);
					// magicFloor.put(kc + "[", null);
					// if(isLogMagicWord) {
					//	logger.debug("magic word: \"" + kc + "\"=(null)");
					// }
				} else {
					if(cls == null) {
						cls = val.getClass();
						suf = ReferenceConverter.convCls2Suf(cls);
					}
					ReferenceStructure ref;
					if(StringUtils.isEmpty(suf)) {
						kc = My.constantize(kec);
						ref = ReferenceConverter.genStructureAssortedType(val, null, cls);
					} else {
						kc = My.constantize(kec + "." + suf);
						if(List.class.isAssignableFrom(cls)) {
							List<?> list2 = (List<?>)val;
							if(list2.size() == 0 || list2.get(0) == null) {
								ref = ReferenceConverter.genStructureAssortedType(list2, suf, String.class);
							} else {
								Object val2 = list2.get(0);
								ref = ReferenceConverter.genStructureAssortedType(list2, suf, val2.getClass());
							}
						} else {
							ref = ReferenceConverter.genStructureAssortedType(val, suf, cls);
						}
						buildMagicFloorRecursive(val, null, null, kec + ".", magicFloor);
					}
					magicFloor.put(kc, ref);
					if(isLogAllMagicWords) {
						logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
					}
				}
			}
			// if(list.size() == 0) {
			//	String kc = My.constantize(ns);
			//	magicFloor.put(kc + "[", null);
			//	if(isLogAllMagicWords) {
			//		logger.debug("magic word: \"" + kc + "\"=(null)");
			//	}
			// }
		} else if(obj instanceof Map) {
			Map<?, ?> map = (Map<?, ?>)obj;
			for(Map.Entry<?, ?> ent : map.entrySet()) {
				String key = ent.getKey().toString();
				Object val = ent.getValue();

				String kec = ns + key;
				String kc;
				if(val == null) {
					// kc = My.constantize(kec);
					// magicFloor.put(kc + "[", null);
					// if(isLogAllMagicWords) {
					//	logger.debug("magic word: \"" + kc + "\"=(null)");
					// }
				} else {
					Class<?> cls = val.getClass();
					String suf = ReferenceConverter.convCls2Suf(cls);
					ReferenceStructure ref;
					if(StringUtils.isEmpty(suf)) {
						kc = My.constantize(kec);
						ref = ReferenceConverter.genStructureAssortedType(val, null, cls);
					} else {
						kc = My.constantize(kec + "." + suf);
						if(List.class.isAssignableFrom(cls)) {
							List<?> list2 = (List<?>)val;
							if(list2.size() == 0 || list2.get(0) == null) {
								ref = ReferenceConverter.genStructureAssortedType(list2, suf, String.class);
							} else {
								Object val2 = list2.get(0);
								ref = ReferenceConverter.genStructureAssortedType(list2, suf, val2.getClass());
							}
						} else {
							ref = ReferenceConverter.genStructureAssortedType(val, suf, cls);
						}
						buildMagicFloorRecursive(val, null, null, kec + ".", magicFloor);
					}
					magicFloor.put(kc, ref);
					if(isLogAllMagicWords) {
						logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
					}
				}
			}
		} else {
			if(gsMtdsMap == null || fldMap == null) {
				gsMtdsMap = new HashMap<>();
				fldMap = new HashMap<>();
				getMethodsAndFields(obj.getClass(), gsMtdsMap, fldMap); // ToDo: キャッシュできるのでは
			}
			if(gsMtdsMap.size() == 0 && fldMap.size() == 0) {
				// if(ns.length() > 0) {
				//	String kpp = ns.substring(0, ns.length() - 1);
				//	String kcn = My.constantize(kpp);
				//	magicFloor.put(kcn + "[", null);
				//	if(isLogAllMagicWords) {
				//		logger.debug("magic word: \"" + kcn + "[A-Z0-9_]*\"=(null)");
				//	}
				// }
				return;
			}

			for(Map.Entry<String, Method[]> ent : gsMtdsMap.entrySet()) {
				// メソッド
				Method[] gsMtds = gsMtdsMap.get(ent.getKey());
				if(gsMtds[0] != null && gsMtds[1] != null) {
					Method gm = gsMtds[0];
					String kpc = ns + gm.getName().substring(3);
					Object val = gm.invoke(obj);
					String kc;
					if(val == null) {
						// kc = My.constantize(kpc);
						// magicFloor.put(kc + "[", null);
						// if(isLogAllMagicWords) {
						//	logger.debug("magic word: \"" + kc + "\"=(null)");
						// }
					} else {
						Class<?> cls = val.getClass();
						String suf = ReferenceConverter.convCls2Suf(cls);
						ReferenceStructure ref;
						if(StringUtils.isEmpty(suf)) {
							kc = My.constantize(kpc);
							ref = ReferenceConverter.genStructureAssortedType(val, null, cls);
						} else {
							kc = My.constantize(kpc + "." + suf);
							if(List.class.isAssignableFrom(cls)) {
								List<?> list2 = (List<?>)val;
								if(list2.size() == 0 || list2.get(0) == null) {
									ref = ReferenceConverter.genStructureAssortedType(list2, suf, String.class);
								} else {
									Object val2 = list2.get(0);
									ref = ReferenceConverter.genStructureAssortedType(list2, suf, val2.getClass());
								}
							} else {
								ref = ReferenceConverter.genStructureAssortedType(val, suf, cls);
							}
							buildMagicFloorRecursive(val, null, null, kpc + ".", magicFloor);
						}
						magicFloor.put(kc, ref);
						if(isLogAllMagicWords) {
							logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
						}
					}
				}
			}
			for(Map.Entry<String, Field> ent : fldMap.entrySet()) {
				// フィールド
				if(gsMtdsMap.containsKey(ent.getKey())) {
					continue;
				}
				Field fld = fldMap.get(ent.getKey());
				String kcm = ns + fld.getName();
				Object val = fld.get(obj);
				String kc;
				if(val == null) {
					// kc = My.constantize(kcm);
					// magicFloor.put(kc + "[", null);
					// if(isLogAllMagicWords) {
					//	logger.debug("magic word: \"" + kc + "\"=(null)");
					// }
				} else {
					Class<?> cls = val.getClass();
					String suf = ReferenceConverter.convCls2Suf(cls);
					ReferenceStructure ref;
					if(StringUtils.isEmpty(suf)) {
						kc = My.constantize(kcm);
						ref = ReferenceConverter.genStructureAssortedType(val, null, cls);
					} else {
						kc = My.constantize(kcm + "." + suf);
						if(List.class.isAssignableFrom(cls)) {
							List<?> list2 = (List<?>)val;
							if(list2.size() == 0 || list2.get(0) == null) {
								ref = ReferenceConverter.genStructureAssortedType(list2, suf, String.class);
							} else {
								Object val2 = list2.get(0);
								ref = ReferenceConverter.genStructureAssortedType(list2, suf, val2.getClass());
							}
						} else {
							ref = ReferenceConverter.genStructureAssortedType(val, suf, cls);
						}
						buildMagicFloorRecursive(val, null, null, kcm + ".", magicFloor);
					}
					magicFloor.put(kc, ref);
					if(isLogAllMagicWords) {
						logger.debug("magic word: \"" + kc + "\"=" + ref.getMnn());
					}
				}
			}
		}
	}

	private Pattern updateMagicPtrn() {
		if(magicFloor.size() == 0) {
			return null;
		}

		List<String> kl1 = new LinkedList<String>();
		for(String key : magicFloor.keySet()) {
			kl1.add(key);
		}
		kl1.sort((a, b) -> {
			ReferenceStructure av = magicFloor.get(a);
			ReferenceStructure bv = magicFloor.get(b);
			if(av != null && bv == null) {
				return -1;
			} else if(av == null && bv != null) {
				return 1;
			} else {
				return b.length() - a.length();
			}
		});
		List<String> kl2 = new LinkedList<String>();
		for(String key : kl1) {
			if(magicFloor.get(key) == null) {
				kl2.add(key + "A-Z0-9_]*");
			} else {
				kl2.add(key);
			}
		}

		String magicExpr = StringUtils.join(kl2, "|");
		if(isLogMagicTower) {
			logger.debug("magicExpr=" + magicExpr);
		}
		return Pattern.compile("__(" + magicExpr + ")__");
	}

	private void reduceMagicTower() {
		magicTower.removeLast();
		if(magicTower.size() > 0) {
			magicFloor = magicTower.getLast();
		} else {
			magicFloor = null;
		}
		ptrnTower.removeLast();
		if(ptrnTower.size() > 0) {
			magicPtrn = ptrnTower.getLast();
		} else {
			magicPtrn = null;
		}

		String tmpCT = ct;
		StringBuilder sb = new StringBuilder();
		boolean isRootCache = true;
		for(Cache tc : cacheTower) {
			if(isRootCache) {
				isRootCache = false;
				continue;
			}
			sb.append(tc.getSKey());
			sb.append(".");
		}
		ct = sb.toString();
		String ctKey = Constants.CACHE_TOWER_KEY;
		if(magicFloor != null) {
			magicFloor.put(My.constantize(ctKey), new ReferenceStructure(ct));
		}
		if(isLogMagicTower) {
			logger.info("magicTower reduced: " + tmpCT);
		}
	}

	private void raiseCacheTower(Cache cache) {
		cacheTower.add(cache);
	}

	private void reduceCacheTower() {
		cacheTower.removeLast();
	}

	public String convMagicWord(String key) {
		if(magicFloor.containsKey(key)) {
			ReferenceStructure ref = magicFloor.get(key);
			if(ref == null) {
				return "";
			} else if(key.equals(My.constantize(Constants.MINION_SUFFIX))
					|| ReferenceConverter.convKey2Suf(key).equals(Constants.MINION_SUFFIX)) {
				return ref.getMnn();
			} else {
				return ref.conv(String.class).getObj();
			}
		} else {
			return "";
		}
	}

	// ToDo: ネストしたList
	@SuppressWarnings("unchecked")
	private <T> T createObjectAndCopyFlx(Class<T> clsDst, Object objSrc, Class<?> clsSrc)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		if(objSrc == null) {
			return null;
		} else if(ReferenceStructure.class.isAssignableFrom(clsSrc)) {
			ReferenceStructure ref = (ReferenceStructure)objSrc;
			return ref.conv(clsDst).getObj();
		} else if(List.class.isAssignableFrom(clsSrc)) {
			List<Object> lst = (List<Object>)objSrc;
			if(lst.size() == 0) {
				return null;
			} else {
				objSrc = lst.get(0);
				if(objSrc == null) {
					return null;
				}
				clsSrc = objSrc.getClass();
			}
		}

		if(clsDst.isAssignableFrom(clsSrc)) {
			return (T)objSrc;
		} else if((String.class.isAssignableFrom(clsSrc) || clsSrc.isPrimitive()
					|| Number.class.isAssignableFrom(clsSrc) || Boolean.class.isAssignableFrom(clsSrc)
					|| Date.class.isAssignableFrom(clsSrc) || Calendar.class.isAssignableFrom(clsSrc)
					|| OffsetDateTime.class.isAssignableFrom(clsSrc) || LocalDateTime.class.isAssignableFrom(clsSrc)
					|| LocalDate.class.isAssignableFrom(clsSrc) || LocalTime.class.isAssignableFrom(clsSrc))
				&& (String.class.isAssignableFrom(clsDst) || clsDst.isPrimitive()
					|| Number.class.isAssignableFrom(clsDst) || Boolean.class.isAssignableFrom(clsDst)
					|| Date.class.isAssignableFrom(clsDst) || Calendar.class.isAssignableFrom(clsDst)
					|| OffsetDateTime.class.isAssignableFrom(clsDst) || LocalDateTime.class.isAssignableFrom(clsDst)
					|| LocalDate.class.isAssignableFrom(clsDst) || LocalTime.class.isAssignableFrom(clsDst))) {
			try {
				return (T)ReferenceConverter.getConvertUtils().convert(objSrc, clsDst);
			} catch(ConversionException ce) {
				logger.warn(ce.getMessage());
				return null;
			}
		}

		// System.out.println("clsSrc: " + clsSrc.toString());
		// System.out.println("clsDst: " + clsDst.toString());
		// T objDst = clsDst.newInstance();
		T objDst = My.deminion("{}", clsDst);
		copyObjectFlx(objDst, clsDst, objSrc, clsSrc);
		return objDst;
	}

	// ToDo: copyMapFlxを作るべきでは

	private <T> void copyObjectFlx(T objDst, Class<T> clsDst, Object objSrc, Class<?> clsSrc)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Map<String, Method[]> gsMtdsDstMap = new HashMap<>();
		Map<String, Field> fldDstMap = new HashMap<>();
		getMethodsAndFields(clsDst, gsMtdsDstMap, fldDstMap);

		if(objSrc instanceof Map) {
			Map<?, ?> mapSrc = (Map<?, ?>)objSrc;
			for(Map.Entry<?, ?> ent : mapSrc.entrySet()) {
				String keySrc = ent.getKey().toString().toLowerCase();
				if(gsMtdsDstMap.containsKey(keySrc)) {
					Method[] gsMtdsDst = gsMtdsDstMap.get(keySrc);
					Method smDst = gsMtdsDst[1];
					if(gsMtdsDst[0] != null && gsMtdsDst[1] != null) {
						Class<?>[] prmClssDst = smDst.getParameterTypes();
						if(prmClssDst.length == 1) {
							// マップ→メソッドのコピーが可能

							Object valSrc = ent.getValue();
							if(valSrc != null) {
								if(List.class.isAssignableFrom(prmClssDst[0])) {
									ParameterizedType gType = (ParameterizedType)smDst.getGenericParameterTypes()[0];
									Type[] aTypes = gType.getActualTypeArguments();
									List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass());
									smDst.invoke(objDst, lstDst);
								} else {
									Object valDst = createObjectAndCopyFlx(prmClssDst[0], valSrc, valSrc.getClass());
									smDst.invoke(objDst, valDst);
								}
							}
						}
					}
				} else if(fldDstMap.containsKey(keySrc)) {
					Field fldDst = fldDstMap.get(keySrc);
					// マップ→フィールドのコピーが可能

					Object valSrc = ent.getValue();
					if(valSrc != null) {
						if(List.class.isAssignableFrom(fldDst.getType())) {
							ParameterizedType gType = (ParameterizedType)fldDst.getGenericType();
							Type[] aTypes = gType.getActualTypeArguments();
							List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass());
							fldDst.set(objDst, lstDst);
						} else {
							Object valDst = createObjectAndCopyFlx(fldDst.getType(), valSrc, valSrc.getClass());
							fldDst.set(objDst, valDst);
						}
					}
				}
			}
		} else {
			Map<String, Method[]> gsMtdsSrcMap = new HashMap<>();
			Map<String, Field> fldSrcMap = new HashMap<>();
			getMethodsAndFields(clsSrc, gsMtdsSrcMap, fldSrcMap);

			for(Map.Entry<String, Method[]> ent : gsMtdsSrcMap.entrySet()) {
				// メソッド
				Method[] gsMtdsSrc = gsMtdsSrcMap.get(ent.getKey());
				if(gsMtdsSrc[0] != null && gsMtdsSrc[1] != null) {
					Method smSrc = gsMtdsSrc[1];
					Class<?>[] prmClssSrc = smSrc.getParameterTypes();
					if(prmClssSrc.length == 1) {
						Method gmSrc = gsMtdsSrc[0];
						if(gsMtdsDstMap.containsKey(ent.getKey())) {
							Method[] gsMtdsDst = gsMtdsDstMap.get(ent.getKey());
							Method smDst = gsMtdsDst[1];
							if(gsMtdsDst[0] != null && gsMtdsDst[1] != null) {
								Class<?>[] prmClssDst = smDst.getParameterTypes();
								if(prmClssDst.length == 1) {
									// メソッド→メソッドのコピーが可能

									Object valSrc = gmSrc.invoke(objSrc);
									if(valSrc != null) {
										if(List.class.isAssignableFrom(prmClssDst[0])) {
											ParameterizedType gType = (ParameterizedType)smDst.getGenericParameterTypes()[0];
											Type[] aTypes = gType.getActualTypeArguments();
											List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass());
											smDst.invoke(objDst, lstDst);
										} else {
											Object valDst = createObjectAndCopyFlx(prmClssDst[0], valSrc, valSrc.getClass());
											smDst.invoke(objDst, valDst);
										}
									}
								}
							}
						} else if(fldDstMap.containsKey(ent.getKey())) {
							Field fldDst = fldDstMap.get(ent.getKey());
							// メソッド→フィールドのコピーが可能

							Object valSrc = gmSrc.invoke(objSrc);
							if(valSrc != null) {
								if(List.class.isAssignableFrom(fldDst.getType())) {
									ParameterizedType gType = (ParameterizedType)fldDst.getGenericType();
									Type[] aTypes = gType.getActualTypeArguments();
									List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass());
									fldDst.set(objDst, lstDst);
								} else {
									Object valDst = createObjectAndCopyFlx(fldDst.getType(), valSrc, valSrc.getClass());
									fldDst.set(objDst, valDst);
								}
							}
						}
					}
				}
			}
			for(Map.Entry<String, Field> ent : fldSrcMap.entrySet()) {
				// フィールド
				if(gsMtdsSrcMap.containsKey(ent.getKey())) {
					continue;
				}

				Field fldSrc = fldSrcMap.get(ent.getKey());
				if(gsMtdsDstMap.containsKey(ent.getKey())) {
					Method[] gsMtdsDst = gsMtdsDstMap.get(ent.getKey());
					Method smDst = gsMtdsDst[1];
					if(gsMtdsDst[0] != null && gsMtdsDst[1] != null) {
						Class<?>[] prmClssDst = smDst.getParameterTypes();
						if(prmClssDst.length == 1) {
							// フィールド→メソッドのコピーが可能

							Object valSrc = fldSrc.get(objSrc);
							if(valSrc != null) {
								if(List.class.isAssignableFrom(prmClssDst[0])) {
									ParameterizedType gType = (ParameterizedType)smDst.getGenericParameterTypes()[0];
									Type[] aTypes = gType.getActualTypeArguments();
									List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass());
									smDst.invoke(objDst, lstDst);
								} else {
									Object valDst = createObjectAndCopyFlx(prmClssDst[0], valSrc, valSrc.getClass());
									smDst.invoke(objDst, valDst);
								}
							}
						}
					}
				} else if(fldDstMap.containsKey(ent.getKey())) {
					Field fldDst = fldDstMap.get(ent.getKey());
					// フィールド→フィールドのコピーが可能

					Object valSrc = fldSrc.get(objSrc);
					if(valSrc != null) {
						if(List.class.isAssignableFrom(fldDst.getType())) {
							ParameterizedType gType = (ParameterizedType)fldDst.getGenericType();
							Type[] aTypes = gType.getActualTypeArguments();
							List<?> lstDst = createListAndCopyFlx((Class<?>)aTypes[0], valSrc, valSrc.getClass());
							fldDst.set(objDst, lstDst);
						} else {
							Object valDst = createObjectAndCopyFlx(fldDst.getType(), valSrc, valSrc.getClass());
							fldDst.set(objDst, valDst);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> createListAndCopyFlx(Type typeDst, Object objSrc, Class<?> clsSrc)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		if(objSrc == null) {
			return null;
		}

		Class<T> clsDst;
		if(typeDst instanceof Class) {
			clsDst = (Class<T>)typeDst;
		} else {
			ParameterizedType gType = (ParameterizedType)typeDst;
			// Type[] aTypes = gType.getActualTypeArguments();
			clsDst = (Class<T>)gType.getRawType();
			// ToDo: ネストしたジェネリクスの考慮
		}

		List<Object> lstSrc = null;
		List<T> lstDst = new ArrayList<>();
		if(ReferenceStructure.class.isAssignableFrom(clsSrc)) {
			ReferenceStructure ref = (ReferenceStructure)objSrc;
			lstSrc = (List<Object>)ref.conv(clsDst).getList();
			if(lstSrc == null) {
				return null;
			} else if(lstSrc.size() == 0) {
				return lstDst;
			} else {
				objSrc = null;
				for(Object lo : lstSrc) {
					if(lo != null) {
						objSrc = lo;
						break;
					}
				}
				if(objSrc == null) {
					for(int li = 0; li < lstSrc.size(); li++) {
						lstDst.add(null);
					}
					return lstDst;
				}
				clsSrc = objSrc.getClass();
			}
			if(clsDst.isAssignableFrom(clsSrc)) {
				return (List<T>)lstSrc;
			}
		} else if(List.class.isAssignableFrom(clsSrc)) {
			lstSrc = (List<Object>)objSrc;
			if(lstSrc.size() == 0) {
				return lstDst;
			} else {
				int idx = 0;
				while(idx < lstSrc.size()) {
					objSrc = lstSrc.get(idx);
					if(objSrc != null) {
						break;
					}
					idx++;
				}
				if(idx == lstSrc.size()) {
					// 要素が全てnull
					return (List<T>)lstSrc;
				}
				clsSrc = objSrc.getClass();
				objSrc = lstSrc.get(0);
			}
			if(clsDst.isAssignableFrom(clsSrc)) {
				return (List<T>)lstSrc;
			}
		} else if(Map.class.isAssignableFrom(clsSrc)) {
			Map<?, ?> mapSrc = (Map<?, ?>)objSrc;
			for(Map.Entry<?, ?> ent : mapSrc.entrySet()) {
				objSrc = ent.getValue();
				clsSrc = objSrc.getClass();
				T valDst = createObjectAndCopyFlx(clsDst, objSrc, clsSrc);
				lstDst.add(valDst);
			}
			return lstDst;
		} else if(clsDst.isAssignableFrom(clsSrc)) {
			// ToDo: clsSrcにListが渡されてくるパターンってある？ 整理されてないような
			// lstDst.add((T)objSrc);
			lstDst.addAll((List<T>)objSrc);
			return lstDst;
		} else {
			T valDst = createObjectAndCopyFlx(clsDst, objSrc, clsSrc);
			lstDst.add(valDst);
			return lstDst;
		}

		copyListFlx(lstDst, clsDst, lstSrc, clsSrc);
		return lstDst;
	}

	private <T> void copyListFlx(List<T> lstDst, Class<T> clsDst, List<Object> lstSrc, Class<?> clsSrc)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		for(int idx = 0; idx < lstSrc.size(); idx++) {
			Object elmSrc = lstSrc.get(idx);
			if(elmSrc == null) {
				lstDst.add(null);
			} else {
				T elmDst = createObjectAndCopyFlx(clsDst, elmSrc, clsSrc);
				lstDst.add(elmDst);
			}
		}
	}

	private void addDebugCacheTree(Cache cache, String indent, String msg) {
		if(!isLogDebugCache) return;
		// debugCacheTree.append(indent);
		debugCacheTree.append(StringUtils.repeat(" ", cacheTower.size()));
		debugCacheTree.append(cache.getKey());
		debugCacheTree.append(",");
		debugCacheTree.append(cache.getCreated().toString());
		debugCacheTree.append(",");
		debugCacheTree.append(cache.getExpires().toString());
		debugCacheTree.append(",");
		debugCacheTree.append(msg);
		debugCacheTree.append("\n");
	}

}
