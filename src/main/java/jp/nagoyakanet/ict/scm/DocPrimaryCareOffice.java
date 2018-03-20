package jp.nagoyakanet.ict.scm;

import java.io.Serializable;
import java.util.List;

public class DocPrimaryCareOffice implements Serializable {

	private static final long serialVersionUID = 1L;

	public DocOffice office;
	public String primaryCare;
	public List<String> medicalSpecialtyList;
	public List<DocUser> userList;

}
