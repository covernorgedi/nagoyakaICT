package jp.nagoyakanet.ict.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.rules.ExternalResource;
import org.kyojo.gbd.AppConfig;
import org.kyojo.minion.My;
import org.kyojo.schemaOrg.m3n3.SimpleJsonBuilder;
import org.kyojo.schemaOrg.m3n3.core.Clazz.PostalAddress;
import org.kyojo.schemaOrg.m3n3.core.Clazz.PropertyValue;
import org.kyojo.schemaOrg.m3n3.core.impl.ADDRESS_LOCALITY;
import org.kyojo.schemaOrg.m3n3.core.impl.ADDRESS_REGION;
import org.kyojo.schemaOrg.m3n3.core.impl.BIRTH_DATE;
import org.kyojo.schemaOrg.m3n3.core.impl.EMAIL;
import org.kyojo.schemaOrg.m3n3.core.impl.GENDER;
import org.kyojo.schemaOrg.m3n3.core.impl.GENDER_TYPE;
import org.kyojo.schemaOrg.m3n3.core.impl.IDENTIFIER;
import org.kyojo.schemaOrg.m3n3.core.impl.IMAGE;
import org.kyojo.schemaOrg.m3n3.core.impl.NAME;
import org.kyojo.schemaOrg.m3n3.core.impl.NAME_RUBY;
import org.kyojo.schemaOrg.m3n3.core.impl.POSTAL_ADDRESS;
import org.kyojo.schemaOrg.m3n3.core.impl.POSTAL_CODE;
import org.kyojo.schemaOrg.m3n3.core.impl.PROPERTY_ID;
import org.kyojo.schemaOrg.m3n3.core.impl.PROPERTY_VALUE;
import org.kyojo.schemaOrg.m3n3.core.impl.STREET_ADDRESS;
import org.kyojo.schemaOrg.m3n3.core.impl.TELEPHONE;
import org.kyojo.schemaOrg.m3n3.core.impl.TEXT;
import org.kyojo.schemaOrg.m3n3.core.impl.URL;
import org.kyojo.schemaOrg.m3n3.core.impl.VALUE;
import org.seasar.doma.jdbc.tx.TransactionManager;

import jp.nagoyakanet.ict.plugin.doc.careManager.HomeServicePlan1;
import jp.nagoyakanet.ict.plugin.doc.careManager.HomeServicePlan2;
import jp.nagoyakanet.ict.plugin.doc.careManager.WeeklyServicePlan;
import jp.nagoyakanet.ict.plugin.doc.caregiver.CareCard;
import jp.nagoyakanet.ict.plugin.doc.caregiver.VisitBathCareCard;
import jp.nagoyakanet.ict.plugin.doc.caregiver.VisitBathCarePlan;
import jp.nagoyakanet.ict.plugin.doc.caregiver.VisitBathCareReport;
import jp.nagoyakanet.ict.plugin.doc.caregiver.VisitCarePlan;
import jp.nagoyakanet.ict.plugin.doc.caregiver.VisitCareReport;
import jp.nagoyakanet.ict.plugin.doc.common.Diary;
import jp.nagoyakanet.ict.plugin.doc.common.MonitoringNote;
import jp.nagoyakanet.ict.plugin.doc.dietitian.NutritionCarePlan;
import jp.nagoyakanet.ict.plugin.doc.dietitian.NutritionScreening;
import jp.nagoyakanet.ict.plugin.doc.doctor.MedicalReferralLetter;
import jp.nagoyakanet.ict.plugin.doc.doctor.PatientSummary;
import jp.nagoyakanet.ict.plugin.doc.doctor.Prescription;
import jp.nagoyakanet.ict.plugin.doc.doctor.VisitNurseInstruction;
import jp.nagoyakanet.ict.plugin.doc.nurse.NurseContinueSummary;
import jp.nagoyakanet.ict.plugin.doc.nurse.VisitNursePlanAndReport;
import jp.nagoyakanet.ict.plugin.doc.nurse.VisitNurseRecord1;
import jp.nagoyakanet.ict.plugin.doc.nurse.VisitNurseRecord2;
import jp.nagoyakanet.ict.plugin.doc.pharmacist.HomeCareManagementGuidanceReport;
import jp.nagoyakanet.ict.plugin.doc.rehab.RehabContinueSummary;
import jp.nagoyakanet.ict.plugin.doc.rehab.VisitRehabRecord;
import jp.nagoyakanet.ict.plugin.portal.Schedule;
import jp.nagoyakanet.ict.plugin.reg.basic.Office;
import jp.nagoyakanet.ict.plugin.reg.basic.Team;
import jp.nagoyakanet.ict.plugin.reg.basic.User;
import jp.nagoyakanet.ict.plugin.reg.insurance.CareInsurance;
import jp.nagoyakanet.ict.plugin.reg.insurance.CarePublicExpense;
import jp.nagoyakanet.ict.plugin.reg.insurance.CertifiedCareLevel;
import jp.nagoyakanet.ict.plugin.reg.insurance.CertifiedDisability;
import jp.nagoyakanet.ict.plugin.reg.insurance.MedicalInsurance;
import jp.nagoyakanet.ict.plugin.reg.insurance.MedicalPublicExpense;
import jp.nagoyakanet.ict.plugin.reg.lifeCare.AdlAndIadl;
import jp.nagoyakanet.ict.plugin.reg.lifeCare.CareService;
import jp.nagoyakanet.ict.plugin.reg.lifeCare.Disabilities;
import jp.nagoyakanet.ict.plugin.reg.lifeCare.FamilyAndResidence;
import jp.nagoyakanet.ict.plugin.reg.lifeCare.PhyOralMwLife;
import jp.nagoyakanet.ict.plugin.reg.medical.BasicHealth;
import jp.nagoyakanet.ict.plugin.reg.medical.ClinicDentistPharmacy;
import jp.nagoyakanet.ict.plugin.reg.medical.DiagnosisMedicineAllergy;
import jp.nagoyakanet.ict.plugin.reg.medical.InOutHospital;
import jp.nagoyakanet.ict.plugin.reg.medical.SpecialMedicalTreatment;
import jp.nagoyakanet.ict.scm.DocMinion;
import jp.nagoyakanet.ict.scm.DocOccupation;
import jp.nagoyakanet.ict.scm.DocOffice;
import jp.nagoyakanet.ict.scm.DocTeam;
import jp.nagoyakanet.ict.scm.DocUser;

public class DatabaseRule extends ExternalResource {

	private static DatabaseRule instance = null;

	private DatabaseRule() {
	}

	// ToDo: 全体で1回だけ実行する正しい方法
	public static DatabaseRule getInstance() {
		if(instance == null) {
			instance = new DatabaseRule();
		}
		return instance;
	}

	private MstCodeAndLangDao mstCodeAndLangDao = new MstCodeAndLangDaoImpl();
	private DocMinionDao docMinionDao = new DocMinionDaoImpl();
	private DocUserDao docUserDao = new DocUserDaoImpl();
	private DocOfficeDao docOfficeDao = new DocOfficeDaoImpl();
	private DocTeamDao docTeamDao = new DocTeamDaoImpl();
	private DocMediaDao docMediaDao = new DocMediaDaoImpl();
	private SignInSessionDao signInSessionDao = new SignInSessionDaoImpl();
	private MessagingSessionDao messagingSessionDao = new MessagingSessionDaoImpl();
	private TlMessageDao tlMessageDao = new TlMessageDaoImpl();
	private FeedEntryDao feedEntryDao = new FeedEntryDaoImpl();
	private SchScheduleItemDao schScheduleItemDao = new SchScheduleItemDaoImpl();

	@Override
	protected void before() throws Throwable {
		TransactionManager tm = AppConfig.singleton().getTransactionManager();
		tm.required(() -> {
			mstCodeAndLangDao.drop();
			mstCodeAndLangDao.create();
			docMinionDao.drop();
			docMinionDao.create();
			docUserDao.drop();
			docUserDao.create();
			docOfficeDao.drop();
			docOfficeDao.create();
			docTeamDao.drop();
			docTeamDao.create();
			docMediaDao.drop();
			docMediaDao.create();
			signInSessionDao.drop();
			signInSessionDao.create();
			messagingSessionDao.drop();
			messagingSessionDao.create();
			tlMessageDao.drop();
			tlMessageDao.create();
			feedEntryDao.drop();
			feedEntryDao.create();
			schScheduleItemDao.drop();
			schScheduleItemDao.create();

			insertSamples();
		});
	}

	@Override
	protected void after() {
		TransactionManager tm = AppConfig.singleton().getTransactionManager();
		tm.required(() -> {
		});
	}

	private void insertSamples() {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			mstCodeAndLangDao.insertSamples();

			Map<String, User> users = new HashMap<>();
			putUser(101L, "880025", "管理者０１", "カンリシャ０１", true, users);
			putUser(102L, "330002", "武田　拓夫", "タケダ　タクオ", false, users);
			putUser(103L, "333025", "新川　光彦", "シンカワ　ミツヒコ", false, users);
			putUser(104L, "330010", "坪井　美希", "ツボイ　ミキ", false, users);
			putUser(105L, "330003", "久野　ひろ子", "クノ　ヒロコ", false, users);
			putUser(106L, "100001", "亀田　一郎", "カメダ　イチロウ", false, users);
			putUser(107L, "333026", "上田　岬", "ウエダ　ミサキ", false, users);
			putUser(108L, "330004", "川辺　祐介", "カワベ ユウスケ", false, users);
			putUser(109L, "330007", "堀内　菊治", "ホリウチ　キクハル", false, users);
			putUser(110L, "330015", "吉田　尚志", "ヨシダ　タカシ", false, users);
			putUser(111L, "330000", "村上　守", "ムラカミ　マモル", false, users);
			putUser(112L, "330001", "斉藤　隆也", "サイトウ　タカヤ", false, users);
			putUser(113L, "330011", "武藤　学", "ムトウ　マナブ", false, users);
			putUser(114L, "330020", "安田　恭子", "ヤスダ　キョウコ", false, users);
			putUser(115L, "330009", "藤崎　竜也", "フジサキ　タツヤ", false, users);
			putUser(116L, "330098", "伊吹　陽一郎", "イブキ　ヨウイチロウ", false, users);
			putUser(117L, "330099", "伊吹　マイケル", "イブキ　マイケル", false, users);
			Map<String, DocUser> docUsers = new HashMap<>();
			copyDocUsers(users, docUsers);

			Map<String, Office> offices = new HashMap<>();
			putOffice(201L, "1000000001", "本部", "ホンブ", offices);
			putOffice(202L, "1000000080", "山中病院", "ヤマナカビョウイン", offices);
			putOffice(203L, "1", "ゲスト", "ゲスト", offices);
			putOffice(204L, "1200000077", "寺島調剤薬局", "テラシマチョウザイヤッキョク", offices);
			putOffice(205L, "1000000081", "名古屋訪問看護ステーション", "ナゴヤホウモンカンゴステーション", offices);
			putOffice(206L, "1000000028", "愛田病院", "アイダビョウイン", offices);
			putOffice(207L, "1000000082", "東海介護センター", "トウカイカイゴセンター", offices);
			putOffice(208L, "1200000078", "ケアリエ東", "ケアリエヒガシ", offices);
			putOffice(209L, "1200000067", "イブキックス", "イブキックス", offices);
			putOffice(210L, "1200000069", "伊吹介護センター", "イブキカイゴセンター", offices);
			Map<String, DocOffice> docOffices = new HashMap<>();
			copyDocOffices(offices, docOffices);

			Map<String, Team> teams = new HashMap<>();
			putTeam(302L, "220", "新川　光彦さんのケアチーム", "シンカワ　ミツヒコサンノケアチーム", teams);
			putTeam(303L, "214", "上田　岬さんのケアチーム", "ウエダ　ミサキサンノケアチーム", teams);
			Map<String, DocTeam> docTeams = new HashMap<>();
			copyDocTeams(teams, docTeams);

			Map<String, DocOccupation> docOccupations = new HashMap<>();
			putDocOccupation("wkdt", "Q183888", "softwareDeveloper", docOccupations);
			putDocOccupation("wkdt", "Q39631", "physician", docOccupations);
			putDocOccupation("wkdt", "Q105186", "pharmacist", docOccupations);
			putDocOccupation("wkdt", "Q186360", "nurse", docOccupations);
			putDocOccupation("wkdt", "Q553079", "caregiver", docOccupations);
			putDocOccupation("wkdt", "Q11377480", "careManager", docOccupations);
			putDocOccupation("wkdt", "Q738142", "clerk", docOccupations);

			List<DocUser> docUserList1 = new ArrayList<>();
			docUserList1.add(docUsers.get("新川　光彦"));
			List<DocUser> docUserList2 = new ArrayList<>();
			docUserList2.add(docUsers.get("上田　岬"));
			teams.get("新川　光彦さんのケアチーム").getTeamTarget().setUserList(docUserList1);
			teams.get("上田　岬さんのケアチーム").getTeamTarget().setUserList(docUserList2);

			List<DocTeam> docTeamList1 = new ArrayList<>();
			docTeamList1.add(docTeams.get("新川　光彦さんのケアチーム"));
			List<DocTeam> docTeamList2 = new ArrayList<>();
			docTeamList2.add(docTeams.get("上田　岬さんのケアチーム"));
			List<DocTeam> docTeamList3 = new ArrayList<>();
			docTeamList3.add(docTeams.get("新川　光彦さんのケアチーム"));
			docTeamList3.add(docTeams.get("上田　岬さんのケアチーム"));

			users.get("武田　拓夫").getTeamMemberOf().setTeamList(docTeamList3);
			users.get("新川　光彦").getTeamMemberOf().setTeamList(docTeamList1);
			users.get("坪井　美希").getTeamMemberOf().setTeamList(docTeamList1);
			users.get("久野　ひろ子").getTeamMemberOf().setTeamList(docTeamList1);
			users.get("亀田　一郎").getTeamMemberOf().setTeamList(docTeamList1);
			users.get("上田　岬").getTeamMemberOf().setTeamList(docTeamList2);
			users.get("川辺　祐介").getTeamMemberOf().setTeamList(docTeamList1);
			users.get("堀内　菊治").getTeamMemberOf().setTeamList(docTeamList1);
			users.get("吉田　尚志").getTeamMemberOf().setTeamList(docTeamList1);
			users.get("斉藤　隆也").getTeamMemberOf().setTeamList(docTeamList3);
			users.get("伊吹　陽一郎").getTeamMemberOf().setTeamList(docTeamList1);
			users.get("伊吹　マイケル").getTeamMemberOf().setTeamList(docTeamList1);

			users.get("管理者０１").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("本部") }));
			users.get("武田　拓夫").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("山中病院") }));
			users.get("新川　光彦").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("ゲスト") }));
			users.get("坪井　美希").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("寺島調剤薬局") }));
			users.get("久野　ひろ子").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("名古屋訪問看護ステーション") }));
			users.get("亀田　一郎").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("愛田病院"), docOffices.get("イブキックス") }));
			users.get("上田　岬").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("ゲスト") }));
			users.get("川辺　祐介").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("東海介護センター") }));
			users.get("堀内　菊治").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("名古屋訪問看護ステーション") }));
			users.get("吉田　尚志").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("ケアリエ東") }));
			users.get("村上　守").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("山中病院") }));
			users.get("斉藤　隆也").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("山中病院") }));
			users.get("武藤　学").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("名古屋訪問看護ステーション") }));
			users.get("安田　恭子").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("愛田病院") }));
			users.get("藤崎　竜也").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("寺島調剤薬局") }));
			users.get("伊吹　陽一郎").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("伊吹介護センター") }));
			users.get("伊吹　マイケル").getOfficeMemberOf().setOfficeList(Arrays.asList(new DocOffice[] { docOffices.get("伊吹介護センター") }));

			users.get("管理者０１").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("softwareDeveloper") }));
			users.get("武田　拓夫").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("physician") }));
			users.get("坪井　美希").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("pharmacist") }));
			users.get("久野　ひろ子").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("nurse") }));
			users.get("亀田　一郎").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("pharmacist") }));
			users.get("川辺　祐介").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("caregiver") }));
			users.get("堀内　菊治").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("nurse") }));
			users.get("吉田　尚志").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("careManager") }));
			users.get("村上　守").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("physician") }));
			users.get("斉藤　隆也").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("clerk") }));
			users.get("武藤　学").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("clerk") }));
			users.get("安田　恭子").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("physician") }));
			users.get("藤崎　竜也").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("clerk") }));
			users.get("伊吹　陽一郎").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("caregiver") }));
			users.get("伊吹　マイケル").getHasOccupation().setOccupationList(Arrays.asList(new DocOccupation[] { docOccupations.get("clerk") }));

			users.get("新川　光彦").setBirthDate(new BIRTH_DATE(sdfYMDHMS.parse("1956-08-11 00:00:00")));
			users.get("新川　光彦").setGender(new GENDER(new GENDER_TYPE("male")));
			PostalAddress postalAddressShinkawa = new POSTAL_ADDRESS();
			postalAddressShinkawa.setPostalCode(new POSTAL_CODE("458-0001"));
			postalAddressShinkawa.setAddressRegion(new ADDRESS_REGION("愛知県"));
			postalAddressShinkawa.setAddressLocality(new ADDRESS_LOCALITY("名古屋市緑区"));
			postalAddressShinkawa.setStreetAddress(new STREET_ADDRESS("梅里"));
			users.get("新川　光彦").getAddress().setPostalAddressList(Arrays.asList(new PostalAddress[] { postalAddressShinkawa }));
			users.get("新川　光彦").setTelephone(new TELEPHONE("052-883-0000"));
			users.get("新川　光彦").setEmail(new EMAIL("shinkawa@abc.def"));

			users.get("武田　拓夫").setImage(new IMAGE(new URL("/img/sample/takeda.jpg")));
			users.get("新川　光彦").setImage(new IMAGE(new URL("/img/sample/shinkawa.jpg")));
			users.get("坪井　美希").setImage(new IMAGE(new URL("/img/sample/tsuboi.jpg")));
			users.get("久野　ひろ子").setImage(new IMAGE(new URL("/img/sample/kuno.jpg")));
			users.get("川辺　祐介").setImage(new IMAGE(new URL("/img/sample/kawabe.jpg")));
			users.get("堀内　菊治").setImage(new IMAGE(new URL("/img/sample/horiuchi.jpg")));
			users.get("吉田　尚志").setImage(new IMAGE(new URL("/img/sample/yoshida.jpg")));

			List<DocOffice> docOfficeList1 = new ArrayList<>();
			docOfficeList1.add(docOffices.get("ゲスト"));
			docOfficeList1.add(docOffices.get("山中病院"));
			docOfficeList1.add(docOffices.get("寺島調剤薬局"));
			docOfficeList1.add(docOffices.get("名古屋訪問看護ステーション"));
			docOfficeList1.add(docOffices.get("愛田病院"));
			docOfficeList1.add(docOffices.get("東海介護センター"));
			docOfficeList1.add(docOffices.get("ケアリエ東"));
			docOfficeList1.add(docOffices.get("伊吹介護センター"));
			teams.get("新川　光彦さんのケアチーム").getOfficeMemberOf().setOfficeList(docOfficeList1);
			List<DocOffice> docOfficeList2 = new ArrayList<>();
			docOfficeList2.add(docOffices.get("ゲスト"));
			docOfficeList2.add(docOffices.get("山中病院"));
			teams.get("上田　岬さんのケアチーム").getOfficeMemberOf().setOfficeList(docOfficeList2);

			PostalAddress postalAddressYamanaka = new POSTAL_ADDRESS();
			postalAddressYamanaka.setPostalCode(new POSTAL_CODE("466-8555"));
			postalAddressYamanaka.setAddressRegion(new ADDRESS_REGION("愛知県"));
			postalAddressYamanaka.setAddressLocality(new ADDRESS_LOCALITY("名古屋市昭和区"));
			postalAddressYamanaka.setStreetAddress(new STREET_ADDRESS("御器所町"));
			offices.get("山中病院").getAddress().setPostalAddressList(Arrays.asList(new PostalAddress[] { postalAddressYamanaka }));
			offices.get("山中病院").setTelephone(new TELEPHONE("052-732-0000"));
			offices.get("山中病院").setEmail(new EMAIL("yamanaka@hospital.jp"));

			copyDocUsers(users, docUsers);
			copyDocOffices(offices, docOffices);
			copyDocTeams(teams, docTeams);

			insertUsers(users);
			insertOffices(offices);
			insertTeams(teams);
			insertDocUsers(docUsers);
			insertDocOffices(docOffices);
			insertDocTeams(docTeams);

			copyDocUsers(users, docUsers);
			copyDocOffices(offices, docOffices);
			copyDocTeams(teams, docTeams);

			HomeServicePlan1.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			HomeServicePlan1.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			HomeServicePlan2.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			WeeklyServicePlan.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			CareCard.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			VisitBathCareCard.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			VisitBathCarePlan.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			VisitBathCareReport.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			VisitCarePlan.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			VisitCareReport.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			Diary.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			MonitoringNote.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			NutritionCarePlan.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			NutritionScreening.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			MedicalReferralLetter.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			PatientSummary.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			Prescription.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			VisitNurseInstruction.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			NurseContinueSummary.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			VisitNursePlanAndReport.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			VisitNurseRecord1.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			VisitNurseRecord2.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			HomeCareManagementGuidanceReport.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			RehabContinueSummary.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			VisitRehabRecord.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			CareInsurance.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			CarePublicExpense.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			CertifiedCareLevel.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			CertifiedDisability.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			MedicalInsurance.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			MedicalPublicExpense.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			AdlAndIadl.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			CareService.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			Disabilities.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			FamilyAndResidence.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			PhyOralMwLife.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			BasicHealth.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			ClinicDentistPharmacy.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			DiagnosisMedicineAllergy.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			InOutHospital.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			SpecialMedicalTreatment.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, docMinionDao);
			Schedule.insertSamples(users, offices, teams, docUsers, docOffices, docTeams, schScheduleItemDao);

			tlMessageDao.insertSamples();
		} catch(Exception ex) {
			ex.printStackTrace();
		}

	}

	private void putUser(Long seq, String code, String name, String nameRuby, boolean isAdmin, Map<String, User> users) {
		User user = new User();
		user.clear();
		user.setSeq(seq);
		user.setCode(code);
		if(isAdmin) {
			user.setPassword("1c2ab170eb5f14a9f91cd1f0b99fca31410ae1d0ef92f4d0ccfe65ad148104a3");
		} else {
			user.setPassword("e3cda416bd697443daa29c0e98e08dccf0b4a24161fc01f7de54a1ff7c5fd143");
		}
		user.setName(new NAME(name));
		user.setNameRuby(new NAME_RUBY(nameRuby));

		users.put(name, user);
	}

	private void putOffice(Long seq, String code, String name, String nameRuby, Map<String, Office> offices) {
		Office office = new Office();
		office.clear();
		office.setSeq(seq);
		office.setCode(code);
		office.setName(new NAME(name));
		office.setNameRuby(new NAME_RUBY(nameRuby));

		offices.put(name, office);
	}

	private void putTeam(Long seq, String code, String name, String nameRuby, Map<String, Team> teams) {
		Team team = new Team();
		team.clear();
		team.setSeq(seq);
		team.setCode(code);
		team.setName(new NAME(name));
		team.setNameRuby(new NAME_RUBY(nameRuby));

		teams.put(name, team);
	}

	private void putDocOccupation(String pid, String pval, String code, Map<String, DocOccupation> docOccupations) {
		DocOccupation docOccupation = new DocOccupation();
		PropertyValue propertyValue = new PROPERTY_VALUE();
		propertyValue.setPropertyID(new PROPERTY_ID(new TEXT(pid)));
		propertyValue.setValue(new VALUE(new TEXT(pval)));
		docOccupation.setIdentifier(new IDENTIFIER(propertyValue));
		docOccupation.setCode(code);
		docOccupation.setName(new NAME(code));

		docOccupations.put(code, docOccupation);
	}

	private void copyDocUsers(Map<String, User> users, Map<String, DocUser> docUsers) {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		for(Map.Entry<String, User> ent : users.entrySet()) {
			User user = ent.getValue();

			try {
				String mnn = SimpleJsonBuilder.toJson(user);
				DocUser docUser = My.deminion(mnn, DocUser.class);
				docUsers.put(ent.getKey(), docUser);

				docUser.refSeq = user.getSeq();
				docUser.refAcr = "";
				docUser.createdAt = sdfYMDHMS.parse("2017-09-01 11:00:00");
				docUser.createdBy = 0L;
				docUser.updatedAt = docUser.createdAt;
				docUser.updatedBy = docUser.createdBy;
				docUser.useFlg = "1";
			} catch(ParseException pe) {
				pe.printStackTrace();
			}
		}
	}

	private void copyDocOffices(Map<String, Office> offices, Map<String, DocOffice> docOffices) {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		for(Map.Entry<String, Office> ent : offices.entrySet()) {
			Office office = ent.getValue();

			try {
				String mnn = SimpleJsonBuilder.toJson(office);
				DocOffice docOffice = My.deminion(mnn, DocOffice.class);
				docOffices.put(ent.getKey(), docOffice);

				docOffice.refSeq = office.getSeq();
				docOffice.refAcr = "";
				docOffice.createdAt = sdfYMDHMS.parse("2017-09-02 11:00:00");
				docOffice.createdBy = 0L;
				docOffice.updatedAt = docOffice.createdAt;
				docOffice.updatedBy = docOffice.createdBy;
				docOffice.useFlg = "1";
			} catch(ParseException pe) {
				pe.printStackTrace();
			}
		}
	}

	private void copyDocTeams(Map<String, Team> teams, Map<String, DocTeam> docTeams) {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		for(Map.Entry<String, Team> ent : teams.entrySet()) {
			Team team = ent.getValue();

			try {
				String mnn = SimpleJsonBuilder.toJson(team);
				DocTeam docTeam = My.deminion(mnn, DocTeam.class);
				docTeams.put(ent.getKey(), docTeam);

				docTeam.refSeq = team.getSeq();
				docTeam.refAcr = "";
				docTeam.createdAt = sdfYMDHMS.parse("2017-09-01 10:00:00");
				docTeam.createdBy = 0L;
				docTeam.updatedAt = docTeam.createdAt;
				docTeam.updatedBy = docTeam.createdBy;
				docTeam.useFlg = "1";
			} catch(ParseException pe) {
				pe.printStackTrace();
			}
		}
	}

	private void insertUsers(Map<String, User> users) {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		users.forEach((key, val) -> {
			try {
				DocMinion dscdt = new DocMinion();
				dscdt.seq = val.getSeq();
				dscdt.minion = SimpleJsonBuilder.toJson(val);
				dscdt.sKey = "reg.basic.user";
				dscdt.dKey = My.hs(dscdt.minion);
				dscdt.eKey = "mnn";
				dscdt.comeAt = sdfYMDHMS.parse("2017-09-01 11:00:00");
				dscdt.comeByUserSeq = 0L;

				docMinionDao.insert(dscdt);
			} catch(ParseException pe) {
				pe.printStackTrace();
			}
		});
	}

	private void insertOffices(Map<String, Office> offices) {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		offices.forEach((key, val) -> {
			try {
				DocMinion dscdt = new DocMinion();
				dscdt.seq = val.getSeq();
				dscdt.minion = SimpleJsonBuilder.toJson(val);
				dscdt.sKey = "reg.basic.office";
				dscdt.dKey = My.hs(dscdt.minion);
				dscdt.eKey = "mnn";
				dscdt.comeAt = sdfYMDHMS.parse("2017-09-02 11:00:00");
				dscdt.comeByUserSeq = 0L;

				docMinionDao.insert(dscdt);
			} catch(ParseException pe) {
				pe.printStackTrace();
			}
		});
	}

	private void insertTeams(Map<String, Team> teams) {
		SimpleDateFormat sdfYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		teams.forEach((key, val) -> {
			try {
				DocMinion dscdt = new DocMinion();
				dscdt.seq = val.getSeq();
				dscdt.minion = SimpleJsonBuilder.toJson(val);
				dscdt.sKey = "reg.basic.team";
				dscdt.dKey = My.hs(dscdt.minion);
				dscdt.eKey = "mnn";
				dscdt.comeAt = sdfYMDHMS.parse("2017-09-01 10:00:00");
				dscdt.comeByUserSeq = 0L;

				docMinionDao.insert(dscdt);
			} catch(ParseException pe) {
				pe.printStackTrace();
			}
		});
	}

	private void insertDocUsers(Map<String, DocUser> docUsers) {
		docUsers.forEach((key, val) -> {
			docUserDao.insert(val);
		});
	}

	private void insertDocOffices(Map<String, DocOffice> docOffices) {
		docOffices.forEach((key, val) -> {
			docOfficeDao.insert(val);
		});
	}

	private void insertDocTeams(Map<String, DocTeam> docTeams) {
		docTeams.forEach((key, val) -> {
			docTeamDao.insert(val);
		});
	}

}
