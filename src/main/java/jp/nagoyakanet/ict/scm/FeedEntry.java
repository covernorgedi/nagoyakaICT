package jp.nagoyakanet.ict.scm;

import java.io.Serializable;
import java.util.Date;

import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.jdbc.entity.NamingType;

@Entity(naming = NamingType.SNAKE_UPPER_CASE)
public class FeedEntry implements Serializable, Comparable<FeedEntry> {

	private static final long serialVersionUID = 1L;

	public Long feedSeq;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long entrySeq;
	public String feedUri;
	public String feedTitle;
	public String entryUri;
	public String entryTitle;
	public String entryDesc;
	public Date publishedDate;
	public Date fetchedDate;

	@Override
	public int compareTo(FeedEntry that) {
		return this.publishedDate.compareTo(that.publishedDate);
	}

}
