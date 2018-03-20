package jp.nagoyakanet.ict.task;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.core.GlobalData;
import org.kyojo.gbd.AppConfig;
import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder;
import org.seasar.doma.jdbc.tx.TransactionManager;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import jp.nagoyakanet.ict.dao.FeedEntryDao;
import jp.nagoyakanet.ict.dao.TlMessageDao;
import jp.nagoyakanet.ict.scm.DocAgent;
import jp.nagoyakanet.ict.scm.FeedEntry;
import jp.nagoyakanet.ict.scm.TlMessage;
import jp.nagoyakanet.ict.tl.TimeLineMessaging;

public class FetchFeedTask {

	private static final Log logger = LogFactory.getLog(FetchFeedTask.class);

	public static String[][] FEED_DEFS = new String[][] {
		new String[] { "厚生労働省 緊急情報", "http://www.mhlw.go.jp/stf/kinkyu.rdf" },
		new String[] { "厚生労働省 新着情報", "http://www.mhlw.go.jp/stf/news.rdf" },
		new String[] { "日本医師会 新着情報", "http://www.med.or.jp/people/atom.xml" },
		new String[] { "薬事日報ウェブサイト", "http://rss.rssad.jp/rss/yakuji/feed" },
		new String[] { "WAM NET ニュース", "http://www.wam.go.jp/content/wamnet/pcpub/top/wamnetlab/rss/wamnet_news_rss2.xml" },
		new String[] { "NAGOYAかいごネット", "http://www.kaigo-wel.city.nagoya.jp/view/kaigo/shinchaku/index.rss" }
	};

	public static void execute(String[] args) {
		GlobalData gbd = CronScheduler.getGlobalData();
		Date limDt = new Date();
		limDt.setTime(limDt.getTime() - 3 * 24 * 3600000); // 古いものは無視する

		// フィード取得
		Long feedSeq = 0L;
		List<FeedEntry> bcEntryList = new ArrayList<>();
		TransactionManager tm = AppConfig.singleton().getTransactionManager();
		int fetchedCnt = 0;
		int insertedCnt = 0;
		int ignoredCnt = 0;
		int errorCnt = 0;
		for(String[] feedDef : FEED_DEFS) {
			feedSeq++;

			try {
				URL feedUrl = new URL(feedDef[1]);
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed = input.build(new XmlReader(feedUrl));
				for(Object obj : feed.getEntries()) {
					fetchedCnt++;

					try {
						SyndEntry entry1 = (SyndEntry)obj;
						FeedEntry entry2 = new FeedEntry();
						boolean valid = true;
						entry2.feedSeq = feedSeq;
						entry2.feedUri = feedDef[1];
						entry2.feedTitle = feedDef[0];
						if(StringUtils.isBlank(entry1.getUri())) {
							if(StringUtils.isBlank(entry1.getLink())) {
								valid = false;
							} else {
								entry2.entryUri = entry1.getLink().trim();
							}
						} else {
							entry2.entryUri = entry1.getUri().trim();
						}
						if(StringUtils.isBlank(entry1.getTitle())) {
							valid = false;
						} else {
							entry2.entryTitle = entry1.getTitle().trim();
						}
						if(entry1.getDescription() != null) {
							entry2.entryDesc = entry1.getDescription().getValue();
						}
						if(entry1.getPublishedDate() == null) {
							valid = false;
						} else {
							entry2.publishedDate = entry1.getPublishedDate();
						}
						entry2.fetchedDate = new Date();

						if(valid) {
							if(entry2.publishedDate.compareTo(limDt) > 0) {
								Boolean res = tm.required(() -> {
									List<FeedEntry> feedEntryList = gbd.get(FeedEntryDao.class).selectByEntryUri(entry2.entryUri);
									if(feedEntryList.size() == 0) {
										gbd.get(FeedEntryDao.class).insert(entry2);
										return false;
									} else {
										return true;
									}
								});

								if(!res) {
									insertedCnt++;
									bcEntryList.add(entry2);
								}
							} else {
								ignoredCnt++;
							}
						} else {
							errorCnt++;
							logger.warn("not valid feed entry:\n" + entry1.toString());
						}
					} catch(Exception ex) {
						errorCnt++;
						logger.warn(ex.getMessage(), ex);
					}
				}
			} catch(Exception ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}
		logger.info("fetchedCnt=" + fetchedCnt + ", insertedCnt=" + insertedCnt
				+ ", ignoredCnt=" + ignoredCnt + ", errorCnt=" + errorCnt);

		// 送信
		SimpleDateFormat sdfYMDHMSZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		for(FeedEntry bcEntry : bcEntryList) {
			Date now = new Date();
			TlMessage tlMsg = new TlMessage();
			Map<String, String> outgoing = new HashMap<>();
			tlMsg.kind = TlMessage.Kind.FEED.getCode();
			String nowStr = sdfYMDHMSZ.format(now);
			tlMsg.createdAt = tlMsg.updatedAt = now;
			tlMsg.displayName = bcEntry.feedTitle;
			tlMsg.body = formatBody(bcEntry.entryTitle, bcEntry.entryUri, bcEntry.entryDesc);
			outgoing.put("kind", tlMsg.kind);
			outgoing.put("displayName", tlMsg.displayName);
			outgoing.put("body", tlMsg.body);
			outgoing.put("createdAt", nowStr);
			tlMsg.icon = "/img/sample/rss-feeds.png";
			outgoing.put("icon", tlMsg.icon);
			tlMsg.messageTo = new DocAgent();
			outgoing.put("messageTo", SimpleJsonBuilder.toJson(tlMsg.messageTo));
			tlMsg.emergency = TlMessage.Emergency.NORMAL.getCode();
			outgoing.put("emergency", tlMsg.emergency);
			tlMsg.emgSort = TlMessage.Emergency.getEmgSortByCode(tlMsg.emergency);
			outgoing.put("emgSort", "" + tlMsg.emgSort);
			tlMsg.createdBy = 0L;
			tlMsg.updatedByUserSeq = 0L;
			tm.required(() -> {
				gbd.get(TlMessageDao.class).insert(tlMsg);
			});

			TimeLineMessaging.broadcast(My.minion(outgoing));
		}
	}

	public static String formatBody(String title, String uri, String desc) {
		StringBuilder body = new StringBuilder();
		if(!StringUtils.isBlank(title)) {
			body.append("<p><a href=\"");
			body.append(uri);
			body.append("\" target=\"_blank\">");
			body.append(title.replaceAll("<[^>]+>", "").replaceAll("\\s+", " "));
			body.append("</a></p>");
		}

		if(!StringUtils.isBlank(desc)) {
			body.append("<p>");
			desc = desc.replaceAll("<[^>]+>", "").replaceAll("\\s+", " ");
			if(desc.length() >= 50) {
				desc = desc.substring(0, 49) + "...";
			}
			body.append(desc);
			body.append("</p>");
		}

		return body.toString();
	}

}
