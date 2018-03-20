package jp.nagoyakanet.ict.scm;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.nagoyakanet.ict.mc.MediaServlet;
import jp.nagoyakanet.ict.mc.MediaServlet.MediaGroup;
import jp.nagoyakanet.ict.mc.MediaServlet.MediaType;

@Entity(naming = NamingType.SNAKE_UPPER_CASE)
public class DocMedia implements Serializable {

	private static final Log logger = LogFactory.getLog(DocMedia.class);

	private static final long serialVersionUID = 1L;

	// ToDo: byte[]でなくInputStreamで扱う方法もいずれ必要？

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long seq;
	public String path;
	public String ext;
	public byte[] origBin;
	public Long origLen;
	public String origPath;
	public byte[] smallBin;
	public Long smallLen;
	public byte[] middleBin;
	public Long middleLen;
	public byte[] largeBin;
	public Long largeLen;
	public String remarks;
	public Date comeAt;
	public Long comeByUserSeq;
	public String comeByUserCd;
	public String comeByUserNm;
	public Long comeByOfficeSeq;
	public String comeByOfficeCd;
	public String comeByOfficeNm;
	public Long comeByTeamSeq;
	public String comeByTeamCd;
	public String comeByTeamNm;
	public Date goneAt;
	public Long goneByUserSeq;
	public String goneByUserCd;
	public String goneByUserNm;
	public Long goneByOfficeSeq;
	public String goneByOfficeCd;
	public String goneByOfficeNm;
	public Long goneByTeamSeq;
	public String goneByTeamCd;
	public String goneByTeamNm;
	public Long progenitor;
	public Long ancestor;
	public Long descendant;
	public Long lifetime;
	public Date cleanedAt;

	public DocMedia() {}

	public DocMedia(byte[] origBin, String origPath,
			DocUser signInUser, DocOffice signInOffice, DocTeam signInTeam,
			Long ancestor, Long lifetime, Date cleanedAt) {
		this.origBin = origBin;
		this.origLen = (long)origBin.length;
		this.origPath = origPath;
		String[] pes = origPath.split("\\.");
		ext = pes[pes.length - 1].toLowerCase();

		SimpleDateFormat sdfYM = new SimpleDateFormat("/yyyy/MM/");
		Date now = new Date();
		StringBuilder sb = new StringBuilder(sdfYM.format(now));
		sb.append(MediaServlet.genKeyToken());
		// sb.append(".");
		// sb.append(ext);
		path = sb.toString();

		MediaType mt = MediaType.getEnumByExt(ext);
		if(MediaGroup.MG_IMAGE.equals(mt.getGroup())) {
			try {
				BufferedImage biOrig = ImageIO.read(new ByteArrayInputStream(origBin));
				BufferedImage biMiddle = MediaServlet.shrinkImage(biOrig, MediaServlet.MIDDLE_SIZE, MediaServlet.MIDDLE_SIZE);
				BufferedImage biSmall = MediaServlet.shrinkImage(biMiddle, MediaServlet.SMALL_SIZE, MediaServlet.SMALL_SIZE);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(baos);
				biMiddle.flush();
				biSmall.flush();
				ImageIO.write(biMiddle, ext, bos);
				middleBin = baos.toByteArray();
				middleLen = (long)middleBin.length;
				baos.reset();
				ImageIO.write(biSmall, ext, bos);
				smallBin = baos.toByteArray();
				smallLen = (long)smallBin.length;
			} catch(IOException ioe) {
				logger.error(ioe.getMessage(), ioe);
			}
		}

		setCome(signInUser, signInOffice, signInTeam, ancestor);
		this.lifetime = lifetime;
		this.cleanedAt = cleanedAt;
	}

	public void setCome(DocUser signInUser, DocOffice signInOffice, DocTeam signInTeam, Long ancestor) {
		comeAt = new Date();
		comeByUserSeq = signInUser.getSeq();
		comeByUserCd = signInUser.getCode();
		comeByUserNm = signInUser.getName().getNativeValue();
		if(signInOffice != null) {
			comeByOfficeSeq = signInOffice.getSeq();
			comeByOfficeCd = signInOffice.getCode();
			comeByOfficeNm = signInOffice.getName().getNativeValue();
		}
		if(signInTeam != null) {
			comeByTeamSeq = signInTeam.getSeq();
			comeByTeamCd = signInTeam.getCode();
			comeByTeamNm = signInTeam.getName().getNativeValue();
		}
		this.ancestor = ancestor;
	}

	public void setGone(DocUser signInUser, DocOffice signInOffice, DocTeam signInTeam, Long descendant) {
		goneAt = new Date();
		goneByUserSeq = signInUser.getSeq();
		goneByUserCd = signInUser.getCode();
		goneByUserNm = signInUser.getName().getNativeValue();
		if(signInOffice != null) {
			goneByOfficeSeq = signInOffice.getSeq();
			goneByOfficeCd = signInOffice.getCode();
			goneByOfficeNm = signInOffice.getName().getNativeValue();
		}
		if(signInTeam != null) {
			goneByTeamSeq = signInTeam.getSeq();
			goneByTeamCd = signInTeam.getCode();
			goneByTeamNm = signInTeam.getName().getNativeValue();
		}
		this.descendant = descendant;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public byte[] getOrigBin() {
		return origBin;
	}

	public void setOrigBin(byte[] origBin) {
		this.origBin = origBin;
	}

	public Long getOrigLen() {
		return origLen;
	}

	public void setOrigLen(Long origLen) {
		this.origLen = origLen;
	}

	public String getOrigPath() {
		return origPath;
	}

	public void setOrigPath(String origPath) {
		this.origPath = origPath;
	}

	public byte[] getSmallBin() {
		return smallBin;
	}

	public void setSmallBin(byte[] smallBin) {
		this.smallBin = smallBin;
	}

	public Long getSmallLen() {
		return smallLen;
	}

	public void setSmallLen(Long smallLen) {
		this.smallLen = smallLen;
	}

	public byte[] getMiddleBin() {
		return middleBin;
	}

	public void setMiddleBin(byte[] middleBin) {
		this.middleBin = middleBin;
	}

	public Long getMiddleLen() {
		return middleLen;
	}

	public void setMiddleLen(Long middleLen) {
		this.middleLen = middleLen;
	}

	public byte[] getLargeBin() {
		return largeBin;
	}

	public void setLargeBin(byte[] largeBin) {
		this.largeBin = largeBin;
	}

	public Long getLargeLen() {
		return largeLen;
	}

	public void setLargeLen(Long largeLen) {
		this.largeLen = largeLen;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getComeAt() {
		return comeAt;
	}

	public void setComeAt(Date comeAt) {
		this.comeAt = comeAt;
	}

	public Long getComeByUserSeq() {
		return comeByUserSeq;
	}

	public void setComeByUserSeq(Long comeByUserSeq) {
		this.comeByUserSeq = comeByUserSeq;
	}

	public String getComeByUserCd() {
		return comeByUserCd;
	}

	public void setComeByUserCd(String comeByUserCd) {
		this.comeByUserCd = comeByUserCd;
	}

	public String getComeByUserNm() {
		return comeByUserNm;
	}

	public void setComeByUserNm(String comeByUserNm) {
		this.comeByUserNm = comeByUserNm;
	}

	public Long getComeByOfficeSeq() {
		return comeByOfficeSeq;
	}

	public void setComeByOfficeSeq(Long comeByOfficeSeq) {
		this.comeByOfficeSeq = comeByOfficeSeq;
	}

	public String getComeByOfficeCd() {
		return comeByOfficeCd;
	}

	public void setComeByOfficeCd(String comeByOfficeCd) {
		this.comeByOfficeCd = comeByOfficeCd;
	}

	public String getComeByOfficeNm() {
		return comeByOfficeNm;
	}

	public void setComeByOfficeNm(String comeByOfficeNm) {
		this.comeByOfficeNm = comeByOfficeNm;
	}

	public Long getComeByTeamSeq() {
		return comeByTeamSeq;
	}

	public void setComeByTeamSeq(Long comeByTeamSeq) {
		this.comeByTeamSeq = comeByTeamSeq;
	}

	public String getComeByTeamCd() {
		return comeByTeamCd;
	}

	public void setComeByTeamCd(String comeByTeamCd) {
		this.comeByTeamCd = comeByTeamCd;
	}

	public String getComeByTeamNm() {
		return comeByTeamNm;
	}

	public void setComeByTeamNm(String comeByTeamNm) {
		this.comeByTeamNm = comeByTeamNm;
	}

	public Date getGoneAt() {
		return goneAt;
	}

	public void setGoneAt(Date goneAt) {
		this.goneAt = goneAt;
	}

	public Long getGoneByUserSeq() {
		return goneByUserSeq;
	}

	public void setGoneByUserSeq(Long goneByUserSeq) {
		this.goneByUserSeq = goneByUserSeq;
	}

	public String getGoneByUserCd() {
		return goneByUserCd;
	}

	public void setGoneByUserCd(String goneByUserCd) {
		this.goneByUserCd = goneByUserCd;
	}

	public String getGoneByUserNm() {
		return goneByUserNm;
	}

	public void setGoneByUserNm(String goneByUserNm) {
		this.goneByUserNm = goneByUserNm;
	}

	public Long getGoneByOfficeSeq() {
		return goneByOfficeSeq;
	}

	public void setGoneByOfficeSeq(Long goneByOfficeSeq) {
		this.goneByOfficeSeq = goneByOfficeSeq;
	}

	public String getGoneByOfficeCd() {
		return goneByOfficeCd;
	}

	public void setGoneByOfficeCd(String goneByOfficeCd) {
		this.goneByOfficeCd = goneByOfficeCd;
	}

	public String getGoneByOfficeNm() {
		return goneByOfficeNm;
	}

	public void setGoneByOfficeNm(String goneByOfficeNm) {
		this.goneByOfficeNm = goneByOfficeNm;
	}

	public Long getGoneByTeamSeq() {
		return goneByTeamSeq;
	}

	public void setGoneByTeamSeq(Long goneByTeamSeq) {
		this.goneByTeamSeq = goneByTeamSeq;
	}

	public String getGoneByTeamCd() {
		return goneByTeamCd;
	}

	public void setGoneByTeamCd(String goneByTeamCd) {
		this.goneByTeamCd = goneByTeamCd;
	}

	public String getGoneByTeamNm() {
		return goneByTeamNm;
	}

	public void setGoneByTeamNm(String goneByTeamNm) {
		this.goneByTeamNm = goneByTeamNm;
	}

	public Long getProgenitor() {
		return progenitor;
	}

	public void setProgenitor(Long progenitor) {
		this.progenitor = progenitor;
	}

	public Long getAncestor() {
		return ancestor;
	}

	public void setAncestor(Long ancestor) {
		this.ancestor = ancestor;
	}

	public Long getDescendant() {
		return descendant;
	}

	public void setDescendant(Long descendant) {
		this.descendant = descendant;
	}

	public Long getLifetime() {
		return lifetime;
	}

	public void setLifetime(Long lifetime) {
		this.lifetime = lifetime;
	}

	public Date getCleanedAt() {
		return cleanedAt;
	}

	public void setCleanedAt(Date cleanedAt) {
		this.cleanedAt = cleanedAt;
	}

}
