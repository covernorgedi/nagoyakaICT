package jp.nagoyakanet.ict.scm;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.kyojo.schemaOrg.m3n3.core.Clazz.JoinAction;
import org.kyojo.schemaOrg.m3n3.core.Clazz.LeaveAction;

public class DocHospitalized implements Serializable {

	private static final long serialVersionUID = 1L;

	public transient Date fromDate;
	public transient Date fromTime;
	public transient Date toDate;
	public transient Date toTime;
	public JoinAction joinAction;
	public LeaveAction leaveAction;
	public DocOffice office;
	public List<String> medicalSpecialtyList;
	public List<DocUser> userList;

}
