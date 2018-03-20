package jp.nagoyakanet.ict.mc;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.random.MersenneTwister;
import org.kyojo.core.GlobalData;
import org.kyojo.gbd.AppConfig;
import org.seasar.doma.jdbc.tx.TransactionManager;

import jp.nagoyakanet.ict.dao.DocMediaDao;
import jp.nagoyakanet.ict.scm.DocMedia;

public class MediaServlet extends HttpServlet {

	private static final Log logger = LogFactory.getLog(MediaServlet.class);

	private static final long serialVersionUID = 1L;

	private static Pattern pt1 = Pattern.compile("(/[\\w/]+)_([smlo])\\.(\\w+)");
	private static Pattern pt2 = Pattern.compile("(/[\\w/]+)\\.(\\w+)");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		GlobalData gbd = GlobalData.getInstance(getServletContext());
		String kind = request.getParameter("kind");
		String path0 = request.getParameter("path");
		String path = null;
		String sz = null;
		String ext = null;
		Matcher mc = pt1.matcher(path0);
		if(mc.matches()) {
			ext = mc.group(3);
			// path = mc.group(1) + "." + ext;
			path = mc.group(1);
			sz = mc.group(2);
		} else {
			mc = pt2.matcher(path0);
			if(mc.matches()) {
				ext = mc.group(2);
				path = mc.group(1);
				// path = mc.group(1) + "." + ext;
			}
		}
		if(path == null) {
			// PrintWriter out = response.getWriter();
			// out.print("Not found.");
			response.setContentType("text/html; charset=UTF-8");
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not found.");
			return;
		}

		byte[] bin = null;
		if(kind.equals("doc")) {
			TransactionManager tm = AppConfig.singleton().getTransactionManager();
			final String path2 = path;
			if(sz == null || sz.equals("o")) {
				DocMedia docMedia = tm.required(() -> {
					return gbd.get(DocMediaDao.class).selectByPath(path2, true);
				});
				if(docMedia != null) {
					bin = docMedia.origBin;
				}
			} else if(sz.equals("s")) {
				DocMedia docMedia = tm.required(() -> {
					return gbd.get(DocMediaDao.class).selectByPathOnlySmall(path2, true);
				});
				if(docMedia != null) {
					bin = docMedia.smallBin;
				}
			} else if(sz.equals("m")) {
				DocMedia docMedia = tm.required(() -> {
					return gbd.get(DocMediaDao.class).selectByPathOnlyMiddle(path2, true);
				});
				if(docMedia != null) {
					bin = docMedia.middleBin;
				}
			} else if(sz.equals("l")) {
				DocMedia docMedia = tm.required(() -> {
					return gbd.get(DocMediaDao.class).selectByPathOnlyLarge(path2, true);
				});
				if(docMedia != null) {
					bin = docMedia.largeBin;
				}
			}
		}
		if(bin == null) {
			// PrintWriter out = response.getWriter();
			// out.print("Not found.");
			response.setContentType("text/html; charset=UTF-8");
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not found.");
			return;
		}

		MediaType mt = MediaType.getEnumByExt(ext);
		response.setContentType(mt.getMime());
		OutputStream out = response.getOutputStream();
		out.write(bin);
		out.close();
	}

	public static final int SMALL_SIZE = 100;

	public static final int MIDDLE_SIZE = 400;

	public static final int LARGE_SIZE = 1000;

	public static BufferedImage shrinkImage(BufferedImage biSrc, int wmax, int hmax) {
		int width = biSrc.getWidth();
		int height = biSrc.getHeight();

		int wtgt = wmax;
		int htgt = hmax;
		if(width >= height) {
			htgt = height * wmax / width;
		} else {
			wtgt = width * hmax / height;
		}

		// 画像を滑らかに縮小
		BufferedImage biDst = null;
		Graphics2D g2d;
		int wtmp = width;
		int htmp = height;
		do {
			wtmp /= 2;
			if(wtmp < wtgt) {
				wtmp = wtgt;
			}
			htmp /= 2;
			if(htmp < htgt) {
				htmp = htgt;
			}

			biDst = new BufferedImage(wtmp, htmp, biSrc.getType());
			g2d = biDst.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			g2d.drawImage(biSrc, 0, 0, wtmp, htmp, null);

			g2d.dispose();
			biSrc = biDst;
		} while(wtmp > wtgt || htmp > htgt);

		return biDst;
	}

	public static final String KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	public static String genKeyToken() {
		StringBuilder keyToken = new StringBuilder();
		MersenneTwister mt = new MersenneTwister();
		byte[] keySrc = new byte[100];
		mt.nextBytes(keySrc);
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch(NoSuchAlgorithmException nsae) {
			logger.error(nsae.getMessage());
			return null;
		}
		md.update(keySrc);

		int bidx = 0;
		for(byte b : md.digest()) {
			keyToken.append(KEY_CHARS.charAt((Math.abs(mt.nextInt() >> 1) + b) % KEY_CHARS.length()));
			bidx++;
			if (bidx == 20) {
				break;
			}
		}

		return keyToken.toString();
	}

	public enum MediaGroup {
		MG_IMAGE;
	}

	public static final String IC_EMPTY = "/img/gnome/100x100/mimetypes/generic-empty.png";

	public static final String IC_IMAGE = "/img/tango/100x100/mimetypes/image-x-generic.png";

	public static final String IC_WORD = "/img/sample/word.png";

	public static final String IC_EXCEL = "/img/sample/excel.png";

	public static final String IC_PPT = "/img/sample/ppt.png";

	public static final String IC_PDF = "/img/sample/pdf.png";

	public enum MediaType {
		MT_AAC("aac", "audio/aac", null, IC_EMPTY, "AAC 音声ファイル"),
		MT_AVI("avi", "video/x-msvideo", null, IC_EMPTY, "AVI: Audio Video Interleave"),
		MT_AZW("azw", "application/vnd.amazon.ebook", null, IC_EMPTY, "Amazon Kindle eBook 形式"),
		MT_BIN("bin", "application/octet-stream", null, IC_EMPTY, "任意の種類のバイナリーデータ"),
		MT_BZ("bz", "application/x-bzip", null, IC_EMPTY, "BZip アーカイブ"),
		MT_BZ2("bz2", "application/x-bzip2", null, IC_EMPTY, "BZip2 アーカイブ"),
		MT_CSH("csh", "application/x-csh", null, IC_EMPTY, "C-Shell スクリプト"),
		MT_CSS("css", "text/css", null, IC_EMPTY, "Cascading Style Sheets (CSS)"),
		MT_CSV("csv", "text/csv", null, IC_EMPTY, "Comma-separated values (CSV)"),
		MT_DOC("doc", "application/msword", null, IC_WORD, "Microsoft Word"),
		MT_DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", null, IC_WORD, "Microsoft Office Word 2007 文書"),
		MT_EPUB("epub", "application/epub+zip", null, IC_EMPTY, "Electronic publication (EPUB)"),
		MT_GIF("gif", "image/gif", MediaGroup.MG_IMAGE, IC_IMAGE, "Graphics Interchange Format (GIF)"),
		MT_HTM("htm", "text/html", null, IC_EMPTY, "HyperText Markup Language (HTML)"),
		MT_HTML("html", "text/html", null, IC_EMPTY, "HyperText Markup Language (HTML)"),
		MT_ICO("ico", "image/x-icon", null, IC_EMPTY, "アイコン形式"),
		MT_ICS("ics", "text/calendar", null, IC_EMPTY, "iCalendar 形式"),
		MT_JAR("jar", "application/java-archive", null, IC_EMPTY, "Java Archive (JAR)"),
		MT_JPEG("jpeg", "image/jpeg", MediaGroup.MG_IMAGE, IC_IMAGE, "JPEG 画像"),
		MT_JPG("jpg", "image/jpeg", MediaGroup.MG_IMAGE, IC_IMAGE, "JPEG 画像"),
		MT_JS("js", "application/js", null, IC_EMPTY, "JavaScript (ECMAScript)"),
		MT_JSON("json", "application/json", null, IC_EMPTY, "JSON 形式"),
		MT_MID("mid", "audio/midi", null, IC_EMPTY, "Musical Instrument Digital Interface (MIDI)"),
		MT_MIDI("midi", "audio/midi", null, IC_EMPTY, "Musical Instrument Digital Interface (MIDI)"),
		MT_MPEG("mpeg", "video/mpeg", null, IC_EMPTY, "MPEG 動画"),
		MT_MPKG("mpkg", "application/vnd.apple.installer+xml", null, IC_EMPTY, "Apple Installer Package"),
		MT_ODP("odp", "application/vnd.oasis.opendocument.presentation", null, IC_EMPTY, "OpenDocuemnt プレゼンテーション文書"),
		MT_ODS("ods", "application/vnd.oasis.opendocument.spreadsheet", null, IC_EMPTY, "OpenDocuemnt 表計算文書"),
		MT_ODT("odt", "application/vnd.oasis.opendocument.text", null, IC_EMPTY, "OpenDocument テキスト文書"),
		MT_OGA("oga", "audio/ogg", null, IC_EMPTY, "OGG 音声"),
		MT_OGV("ogv", "video/ogg", null, IC_EMPTY, "OGG 動画"),
		MT_OGX("ogx", "application/ogg", null, IC_EMPTY, "OGG"),
		MT_PDF("pdf", "application/pdf", null, IC_PDF, "Adobe Portable Document Format (PDF)"),
		MT_PNG("png", "image/png", MediaGroup.MG_IMAGE, IC_IMAGE, "Portable Network Graphics (PNG)"),
		MT_PPT("ppt", "application/vnd.ms-powerpoint", null, IC_PPT, "Microsoft PowerPoint"),
		MT_PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation", null, IC_PPT, "Microsoft Office PowerPoint 2007 プレゼンテーション"),
		MT_RAR("rar", "application/x-rar-compressed", null, IC_EMPTY, "RAR アーカイブ"),
		MT_RTF("rtf", "application/rtf", null, IC_EMPTY, "リッチテキスト形式 (RTF)"),
		MT_SH("sh", "application/x-sh", null, IC_EMPTY, "Bourne shell スクリプト"),
		MT_SVG("svg", "image/svg+xml", null, IC_EMPTY, "Scalable Vector Graphics (SVG)"),
		MT_SWF("swf", "application/x-shockwave-flash", null, IC_EMPTY, "Small web format (SWF) または Adobe Flash 文書"),
		MT_TAR("tar", "application/x-tar", null, IC_EMPTY, "Tape Archive (TAR)"),
		MT_TIF("tif", "image/tiff", null, IC_EMPTY, "Tagged Image File Format (TIFF)"),
		MT_TIFF("tiff", "image/tiff", null, IC_EMPTY, "Tagged Image File Format (TIFF)"),
		MT_TTF("ttf", "application/x-font-ttf", null, IC_EMPTY, "TrueType フォント"),
		MT_VSD("vsd", "application/vnd.visio", null, IC_EMPTY, "Microsft Visio"),
		MT_WAV("wav", "audio/x-wav", null, IC_EMPTY, "Waveform 音声形式"),
		MT_WEBA("weba", "audio/webm", null, IC_EMPTY, "WEBM 音声"),
		MT_WEBM("webm", "video/webm", null, IC_EMPTY, "WEBM 動画"),
		MT_WEBP("webp", "image/webp", null, IC_EMPTY, "WEBP 画像"),
		MT_WOFF("woff", "application/x-font-woff", null, IC_EMPTY, "Web Open Font Format (WOFF)"),
		MT_XHTML("xhtml", "application/xhtml+xml", null, IC_EMPTY, "XHTML"),
		MT_XLS("xls", "application/vnd.ms-excel", null, IC_EXCEL, "Microsoft Excel"),
		MT_XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", null, IC_EXCEL, "Microsoft Office Excel 2007 ブック"),
		MT_XML("xml", "application/xml", null, IC_EMPTY, "XML"),
		MT_XUL("xul", "application/vnd.mozilla.xul+xml", null, IC_EMPTY, "XUL"),
		MT_ZIP("zip", "application/zip", null, IC_EMPTY, "ZIP アーカイブ"),
		MT_3GP("3gp", "video/3gpp", null, IC_EMPTY, "3GPP 音声/動画コンテナー"),
		MT_7Z("7z", "application/x-7z-compressed", null, IC_EMPTY, "7-zip アーカイブ"),
		MT_UNKNOWN("", "application/octet-stream", null, IC_EMPTY, "未登録の形式");

		private MediaType(String ext, String mime, MediaGroup group, String icon, String name) {
			this.ext = ext;
			this.mime = mime;
			this.group = group;
			this.icon = icon;
			this.name = name;
		}

		private static final Map<String, MediaType> EXT_MAP = new HashMap<>();
		static {
			final MediaType[] values = values();
			for(final MediaType value : values) {
				EXT_MAP.put(value.ext, value);
			}
		}

		public static MediaType getEnumByExt(final String ext) {
			if(EXT_MAP.containsKey(ext)) {
				return EXT_MAP.get(ext);
			}
			return MT_UNKNOWN;
		}

		private String ext;

		private String mime;

		private MediaGroup group;

		private String icon;

		private String name;

		public String getExt() {
			return ext;
		}

		public String getMime() {
			return mime;
		}

		public MediaGroup getGroup() {
			return group;
		}

		public String getIcon() {
			return icon;
		}

		public String getName() {
			return name;
		}

	}

}
