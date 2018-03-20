package jp.nagoyakanet.ict.tl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.StringEscapeUtils;
import org.kyojo.core.GlobalData;
import org.kyojo.gbd.AppConfig;
import org.kyojo.gson.reflect.TypeToken;
import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder;
import org.seasar.doma.jdbc.tx.TransactionManager;

import jp.nagoyakanet.ict.dao.DocMinionDao;
import jp.nagoyakanet.ict.dao.DocOfficeDao;
import jp.nagoyakanet.ict.dao.DocTeamDao;
import jp.nagoyakanet.ict.dao.DocUserDao;
import jp.nagoyakanet.ict.dao.MessagingSessionDao;
import jp.nagoyakanet.ict.dao.SignInSessionDao;
import jp.nagoyakanet.ict.dao.TlMessageDao;
import jp.nagoyakanet.ict.plugin.reg.basic.User;
import jp.nagoyakanet.ict.scm.DocAgent;
import jp.nagoyakanet.ict.scm.DocMinion;
import jp.nagoyakanet.ict.scm.DocOccupation;
import jp.nagoyakanet.ict.scm.DocOffice;
import jp.nagoyakanet.ict.scm.DocTeam;
import jp.nagoyakanet.ict.scm.DocUser;
import jp.nagoyakanet.ict.scm.MessagingSession;
import jp.nagoyakanet.ict.scm.SignInSession;
import jp.nagoyakanet.ict.scm.TlMessage;

@ServerEndpoint("/websocket/timeline")
public class TimeLineMessaging implements ServletContextListener {

	private static final Log logger = LogFactory.getLog(TimeLineMessaging.class);

	private static final Set<TimeLineMessaging> connections = new CopyOnWriteArraySet<>();

	private static GlobalData gbd = null;

	private Session session = null;

	private MessagingSession messagingSession = null;

	private DocUser docUser = null;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		try {
			gbd = GlobalData.getInstance(servletContext);
		} catch(IOException ioe) {
			logger.fatal(ioe.getMessage(), ioe);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		TransactionManager tm = AppConfig.singleton().getTransactionManager();
		tm.required(() -> {
			MessagingSession messagingSession = gbd.get(MessagingSessionDao.class).selectBySessionId(session.getId(), true);
			if(messagingSession == null) {
				messagingSession = new MessagingSession();
				messagingSession.sessionId = session.getId();
				messagingSession.createdAt = new Date();
				messagingSession.updatedAt = messagingSession.createdAt;
				gbd.get(MessagingSessionDao.class).insert(messagingSession);
				logger.info("MessagingSession sessionID=" + messagingSession.sessionId + " is inserted.");
				this.messagingSession = messagingSession;
				connections.add(this);
			} else {
				messagingSession.updatedAt = new Date();
				gbd.get(MessagingSessionDao.class).update(messagingSession);
				logger.info("MessagingSession sessionID=" + messagingSession.sessionId + " is updated.");
				this.messagingSession = messagingSession;
				if(!connections.contains(this)) {
					connections.add(this);
				}
			}
		});

		Map<String, String> outgoing = new HashMap<>();
		outgoing.put("kind", TlMessage.Kind.USER_STATUS.getCode());
		outgoing.put("subKind", TlMessage.SubKind.ONLINE.getCode());
		broadcast(My.minion(outgoing));
	}

	@OnClose
	public void onClose() {
		if(messagingSession == null) {
			logger.info("messagingSession is null.");
		} else {
			if(session != null && session.isOpen()) {
				Map<String, String> outgoing = new HashMap<>();
				outgoing.put("kind", TlMessage.Kind.USER_STATUS.getCode());
				outgoing.put("subKind", TlMessage.SubKind.OFFLINE.getCode());
				broadcast(My.minion(outgoing));
			}

			TransactionManager tm = AppConfig.singleton().getTransactionManager();
			tm.required(() -> {
				messagingSession.updatedAt = new Date();
				messagingSession.expiredAt = messagingSession.updatedAt;
				gbd.get(MessagingSessionDao.class).update(messagingSession);
				logger.info("MessagingSession sessionID=" + messagingSession.sessionId + " is expired.");
			});
		}
		connections.remove(this);
	}

	@OnMessage
	public void onMessage(String message) {
		logger.info("incoming meassage=\"" + message + "\"");
		Map<String, String> incoming = My.deminion(message, Map.class);
		TransactionManager tm = AppConfig.singleton().getTransactionManager();
		tm.required(() -> {
			if(incoming.containsKey("sessionId")) {
				List<SignInSession> signInSessionList = gbd.get(SignInSessionDao.class).selectBySessionId(incoming.get("sessionId"), true);
				if(signInSessionList.size() == 0) {
					logger.info("not found sid=" + incoming.get("sessionId"));
					manuallyClose(this);
					return;
				} else {
					for(SignInSession signInSession : signInSessionList) {
						if(docUser == null) {
							docUser = gbd.get(DocUserDao.class).selectByCode(signInSession.signInId);
							if(docUser == null) {
								logger.warn("not found signInId=" + signInSession.signInId);
							} else {
								if(messagingSession == null) {
									logger.warn("messagingSession is null.");
									manuallyClose(this);
								} else {
									messagingSession.signInId = signInSession.signInId;
									messagingSession.updatedAt = new Date();
									gbd.get(MessagingSessionDao.class).update(messagingSession);
									logger.info("messagingSession sessionId=" + incoming.get("sessionId") + " is bound to "
											+ docUser.code + "," + docUser.getName().getNativeValue());
								}
							}
						}
						if(docUser != null) {
							break;
						}
					}
					if(docUser == null) {
						logger.warn("docUser is null.");
					} else {
						Date now = new Date();
						DocOffice docOffice = null;
						if(docUser.officeMemberOf != null
								&& docUser.officeMemberOf.officeList != null
								&& docUser.officeMemberOf.officeList.size() > 0) {
							docOffice = docUser.officeMemberOf.officeList.get(0);
						}
						DocTeam docTeam = null;
						if(docUser.teamMemberOf.teamList.size() > 0
								&& docUser.teamMemberOf.teamList != null
								&& docUser.teamMemberOf.teamList.size() > 0) {
							docTeam = docUser.teamMemberOf.teamList.get(0);
						}
						DocMinion mnnUser = gbd.get(DocMinionDao.class).selectBySeq(docUser.refSeq, true);
						User userFull = null;
						if(mnnUser != null) {
							userFull = My.deminion(mnnUser.minion, User.class);
						}

						TlMessage tlMsg = new TlMessage();
						if(incoming.containsKey("kind")) {
							tlMsg.kind = incoming.get("kind");
							if(tlMsg.kind.equals(TlMessage.Kind.MESSAGE.getCode())) {
								SimpleDateFormat sdfYMDHMSZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
								String nowStr = sdfYMDHMSZ.format(now);
								tlMsg.createdAt = tlMsg.updatedAt = now;
								Map<String, Object> outgoing = new HashMap<>();
								StringBuilder sb = new StringBuilder(docUser.getName().getNativeValue());
								if(docUser.getOfficeMemberOf() != null
										&& docUser.getOfficeMemberOf().getOfficeList() != null
										&& docUser.getOfficeMemberOf().getOfficeList().size() > 0
										&& docUser.getOfficeMemberOf().getOfficeList().get(0) != null) {
									sb.append("（");
									sb.append(docUser.getOfficeMemberOf().getOfficeList().get(0).getName().getNativeValue());
									sb.append("）");
								}
								tlMsg.displayName = sb.toString();
								tlMsg.body = formatBody(incoming.get("body"));
								outgoing.put("kind", TlMessage.Kind.MESSAGE.getCode());
								outgoing.put("displayName", tlMsg.displayName);
								outgoing.put("body", tlMsg.body);
								outgoing.put("createdAt", nowStr);
								if(userFull != null && userFull.getImage() != null
										&& userFull.getImage().getURLList() != null
										&& userFull.getImage().getURLList().size() > 0
										&& userFull.getImage().getURLList().get(0) != null) {
									tlMsg.icon = userFull.getImage().getURLList().get(0).getNativeValue();
									outgoing.put("icon", tlMsg.icon);
								} else {
									outgoing.put("icon", "/img/sample/someone.png");
								}

								tlMsg.messageTo = new DocAgent();
								if(incoming.containsKey("messageTo")) {
									String messageToStr = incoming.get("messageTo");
									Type listType = TypeToken.getParameterized(ArrayList.class, String.class).getType();
									Type mapType = TypeToken.getParameterized(HashMap.class, String.class, listType).getType();
									Map<String, List<String>> messageToMap = My.deminion(messageToStr, mapType);
									if(messageToMap.containsKey("userList")) {
										List<String> userList = messageToMap.get("userList");
										tlMsg.messageTo.userList = new ArrayList<DocUser>();
										userList.forEach(seqStr -> {
											try {
												Long seq = Long.parseLong(seqStr);
												DocUser docUser = gbd.get(DocUserDao.class).selectBySeq(seq);
												if(docUser != null) {
													tlMsg.messageTo.userList.add(docUser);
												}
											} catch(NumberFormatException nfe) {}
										});
									}
									if(messageToMap.containsKey("occupationList")) {
										List<String> occupatioList = messageToMap.get("occupationList");
										tlMsg.messageTo.occupationList = new ArrayList<DocOccupation>();
										occupatioList.forEach(code -> {
											DocOccupation docOccupation = new DocOccupation();
											docOccupation.setCode(code);
											tlMsg.messageTo.occupationList.add(docOccupation);
										});
									}
								}
								outgoing.put("messageToJson", SimpleJsonBuilder.toJson(tlMsg.messageTo));
								// outgoing.put("messageTo", SimpleJsonBuilder.toJson(tlMsg.messageTo));
								outgoing.put("messageTo", tlMsg.messageTo);
								if(incoming.containsKey("emergency")) {
									tlMsg.emergency = incoming.get("emergency");
								} else {
									tlMsg.emergency = TlMessage.Emergency.NORMAL.getCode();
								}
								outgoing.put("emergency", tlMsg.emergency);
								tlMsg.emgSort = TlMessage.Emergency.getEmgSortByCode(tlMsg.emergency);
								outgoing.put("emgSort", "" + tlMsg.emgSort);
								tlMsg.createdBy = docUser.seq;
								tlMsg.updatedByUserSeq = docUser.seq;
								tlMsg.updatedByUserCd = docUser.code;
								tlMsg.updatedByUserNm = docUser.getName().getNativeValue();
								if(docOffice != null) {
									tlMsg.updatedByOfficeSeq = docOffice.seq;
									tlMsg.updatedByOfficeCd = docOffice.code;
									tlMsg.updatedByOfficeNm = docOffice.getName().getNativeValue();
								}
								if(docTeam != null) {
									tlMsg.updatedByTeamSeq = docTeam.seq;
									tlMsg.updatedByTeamCd = docTeam.code;
									tlMsg.updatedByTeamNm = docTeam.getName().getNativeValue();
								}
								if(incoming.containsKey("shareWithUser")) {
									try {
										Long seq = Long.parseLong(incoming.get("shareWithUser"));
										DocUser shareWithUser = gbd.get(DocUserDao.class).selectBySeq(seq);
										if(shareWithUser != null) {
											tlMsg.shareWithUserSeq = seq;
											tlMsg.shareWithUserCd = shareWithUser.code;
											tlMsg.shareWithUserNm = shareWithUser.getName().getNativeValue();
										}
									} catch(NumberFormatException nfe) {}
								}
								if(incoming.containsKey("shareWithOffice")) {
									try {
										Long seq = Long.parseLong(incoming.get("shareWithOffice"));
										DocOffice shareWithOffice = gbd.get(DocOfficeDao.class).selectBySeq(seq);
										if(shareWithOffice != null) {
											tlMsg.shareWithOfficeSeq = seq;
											tlMsg.shareWithOfficeCd = shareWithOffice.code;
											tlMsg.shareWithOfficeNm = shareWithOffice.getName().getNativeValue();
										}
									} catch(NumberFormatException nfe) {}
								}
								if(incoming.containsKey("shareWithTeam")) {
									try {
										Long seq = Long.parseLong(incoming.get("shareWithTeam"));
										DocTeam shareWithTeam = gbd.get(DocTeamDao.class).selectBySeq(seq);
										if(shareWithTeam != null) {
											tlMsg.shareWithTeamSeq = seq;
											tlMsg.shareWithTeamCd = shareWithTeam.code;
											tlMsg.shareWithTeamNm = shareWithTeam.getName().getNativeValue();
										}
									} catch(NumberFormatException nfe) {}
								}
								gbd.get(TlMessageDao.class).insert(tlMsg);

								// broadcast(My.minion(outgoing));
								// broadcast(SimpleJsonBuilder.toJson(outgoing));
								List<TlMessage> tlMsgList = new ArrayList<>();
								tlMsgList.add(tlMsg);
								broadcast(SimpleJsonBuilder.toJson(tlMsgList));
							}
						}
					}
				}
			}
		});
	}

	public static String formatBody(String orgBody) {
		if(StringUtils.isBlank(orgBody)) {
			return "";
		}

		String[] lines = orgBody.split("\\s*\\r\\n|\\s*\\r|\\s*\\n");
		StringBuilder sb = new StringBuilder();
		for(String line : lines) {
			sb.append("<p>");
			sb.append(StringEscapeUtils.escapeHtml4(line));
			sb.append("</p>");
		}

		return sb.toString();
	}

	@OnError
	public void onError(Throwable t) throws Throwable {
		logger.warn(t.getMessage(), t);
		manuallyClose(this);
	}

	private static void manuallyClose(TimeLineMessaging client) {
		// if(connections.contains(client)) {
		//	TransactionManager tm = AppConfig.singleton().getTransactionManager();
		//	tm.required(() -> {
		//		client.messagingSession.updatedAt = new Date();
		//		client.messagingSession.expiredAt = client.messagingSession.updatedAt;
		//		gbd.get(MessagingSessionDao.class).update(client.messagingSession);
		//		logger.info("session closing. sessionId=\"" + client.messagingSession.sessionId + "\"");
		//	});
		// }

		// connections.remove(client);
		try {
			client.session.close();
		} catch (IOException ioe) {}
	}

	public static void broadcast(String message) {
		for(TimeLineMessaging client : connections) {
			if(client.session == null) {
				logger.info("broadcast: session is null.");
			} else {
				logger.info("broadcast: sessionId=" + client.session.getId());
			}
			if(client.session != null && client.session.isOpen()) {
				try {
					synchronized(client) {
						if(client.docUser == null) {
							logger.warn("docUser is null.");
						} else if(client.messagingSession == null) {
							logger.warn("messagingSession is null.");
						} else {
							client.session.getBasicRemote().sendText(message);
							logger.info("outgoing meassage to sessionId=" + client.session.getId()
									+ ", signInId="+ client.docUser.code);
						}
					}
				} catch(IOException ioe) {
					logger.warn(ioe.getMessage(), ioe);
					manuallyClose(client);
				}
			} else {
				logger.info("not opend.");
			}
		}
	}

}
