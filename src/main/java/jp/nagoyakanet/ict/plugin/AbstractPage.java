package jp.nagoyakanet.ict.plugin;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.core.CompleteThrowable;
import org.kyojo.core.GlobalData;
import org.kyojo.core.PluginException;
import org.kyojo.core.RedirectThrowable;
import org.kyojo.core.RequestData;
import org.kyojo.core.ResponseData;
import org.kyojo.core.SessionData;

public abstract class AbstractPage {

	private static final Log logger = LogFactory.getLog(AbstractPage.class);

	public transient String act;
	public transient String ext;
	public transient String path;
	public transient String sid;
	public String title;

	abstract public String getName(GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd);

	public Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		title = getName(gbd, ssd, rqd, rpd);
		return null;
	}

	private String encodeDownloadFileName(String orgFileName, HttpServletRequest request) throws UnsupportedEncodingException {
		String encFileName;

		if(request.getHeader("User-Agent").indexOf("Safari") > -1) {
			// Safariの場合
			// ToDo Mac版Safariで動作確認が必要
			encFileName = new String(orgFileName.getBytes("UTF-8"), "8859_1");
		} else if(request.getHeader("User-Agent").indexOf("MSIE") > -1
				|| request.getHeader("User-Agent").toLowerCase().indexOf("trident") > -1) {
			// IEの場合
			encFileName = URLEncoder.encode(orgFileName, "UTF-8");
		} else {
			// それ以外の場合（Firefox,Google Chromeで動作確認済み）
			encFileName = MimeUtility.encodeWord(orgFileName, "ISO-2022-JP", "B");
		}

		return encFileName;
	}

	protected boolean downloadFile(byte[] bytes, String fileName, String contentType,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean ret = true;
		final String encFileName = encodeDownloadFileName(fileName, request);

		InputStream is = new BufferedInputStream(new ByteArrayInputStream(bytes));
		OutputStream os = response.getOutputStream();
		try {
			// PDFファイルのダウンロード処理を行う
			response.setContentType(contentType);
			response.setHeader("Content-Disposition", "attachment;filename=\"" + encFileName + "\"");
			int data;
			byte[] b = new byte[1024];
			while((data = is.read(b, 0, 1024)) != -1) {
				os.write(b, 0, data);
			}
		} catch(final Exception ex) {
			logger.warn(ex.getMessage(), ex);
			ret = false;
		} finally {
			os.close();
		}

		return ret;
	}

}
