package Main;
import java.util.Arrays;
/**
 * 割当を格納するクラス
 */
public class Assignment {

	/* 変数宣言 */
	private int NumOfStudents; // 学生数
	private int NumOfSchools; // 学校数
	
	int[] ClaimStudents;
	int[] ACDAClaimer;
	int[] QRDAClaimer;

	/**
	 * 学校の割当を格納 
	 * assignment[i][j] = 学校iのj番目に割り当てられている学生
	 */
	private int[][] assignment;
	private int[][] ESDAassignment;

	/**
	 * 学生の割当を格納 
	 * assignmentOfStudents[i] = 学生iの割り当てられている学校
	 */
	private int[] assignmentOfStudents;
	private int[] ESDAassignmentOfStudents;

	/* 定数宣言 */
	private final int NOTASSIGNED = -1;

	/**
	 * コンストラクタ
	 */
	public Assignment(int students, int schools) {
		NumOfStudents = students;
		NumOfSchools = schools;

		// 割当の初期化
		assignment = new int[NumOfSchools][NumOfStudents];
		for (int i = 0; i < NumOfSchools; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				assignment[i][j] = NOTASSIGNED;//割当がないならNOTASSIGNED
			}
		}
		
		ESDAassignment = new int[NumOfSchools * 2][NumOfStudents];
		for (int i = 0; i < NumOfSchools * 2; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				ESDAassignment[i][j] = NOTASSIGNED;//割当がないならNOTASSIGNED
			}
		}

		//学生の割当を初期化
		assignmentOfStudents = new int[NumOfStudents];
		for (int i = 0; i < NumOfStudents; i++) {
			assignmentOfStudents[i] = NOTASSIGNED;//割当がないならNOTASSIGNED
		}
		
		ESDAassignmentOfStudents = new int[NumOfStudents];
		for (int i = 0; i < NumOfStudents; i++) {
			ESDAassignmentOfStudents[i] = NOTASSIGNED;//割当がないならNOTASSIGNED
		}
		
		ClaimStudents = new int[NumOfStudents];
		ACDAClaimer = new int[NumOfStudents];
		QRDAClaimer = new int[NumOfStudents];
		
		for (int i = 0; i < NumOfStudents; i++) {
			ClaimStudents[i] = 0;
		}
	}

	/**
	 * 学生を学校に割り当てる
	 * ※school = -1なら何もしない
	 */
	public void setStudent(int student, int school){
		if(school >= 0){
			for(int i=0; i<NumOfStudents; i++){
				if(assignment[school][i] < 0){
					assignment[school][i] = student;
					break;
				}
			}
		}
		assignmentOfStudents[student] = school;
	}
	
	/**
	 * 学生を学校に割り当てる (ESDA用)
	 * ※school = -1なら何もしない
	 */
	public void ESDAsetStudent(int student, int school){
		if(school >= 0){
			for(int i=0; i<NumOfStudents; i++){
				if(ESDAassignment[school][i] < 0){
					ESDAassignment[school][i] = student;
					break;
				}
			}
		}
		ESDAassignmentOfStudents[student] = school;
	}
	
	// ESDAで使った割当をstandard-schoolにまとめていつものassignmentに格納する
	public void ESDAconvert(){
		for (int i = 0; i < NumOfSchools; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				assignment[i][j] = ESDAassignment[i][j];//割当がないならNOTASSIGNED
			}
		}
		for (int i = 0; i < NumOfStudents; i++) {
			assignmentOfStudents[i] = ESDAassignmentOfStudents[i];
		}
	}
	
	public void debug() {
		System.out.println("assignment" + Arrays.deepToString(assignment));
		System.out.println("ESDAassignment " + Arrays.deepToString(ESDAassignment));
		System.out.println("assignmentOfStudents" + Arrays.toString(assignmentOfStudents));
		System.out.println("ESDAassignmentOfStudents " + Arrays.toString(ESDAassignmentOfStudents));
	}

	/**
	 * 学生を割当から外す
	 */
	public void removeStudent(int student, int school){
		boolean findStudentFlag = false;

		//学生の割当を外す
		assignmentOfStudents[student] = NOTASSIGNED;

		//学校の割当を外す
		for(int i=0; i<(NumOfStudents-1); i++){
			if(!findStudentFlag){
				if(assignment[school][i] == student){
					assignment[school][i] = assignment[school][i+1];
					findStudentFlag = true;
				}
			} else {
				assignment[school][i] = assignment[school][i+1];
			}
		}
		assignment[school][NumOfStudents-1] = NOTASSIGNED;
	}

	/**
	 * 割り当てられている学生数を取得
	 */
	public int getNumOfAssignStudents(int school){
		int counter =0;

		for(int i=0; i<NumOfStudents; i++){
			if(assignment[school][i] < 0){
				break;
			}
			counter++;
		}
		return counter;
	}
	
	/**
	 * 割り当てられている学生数を取得 (ESDA用)
	 */
	public int ESDAgetNumOfStudents(int school){
		int counter =0;

		for(int i=0; i<NumOfStudents; i++){
			if(ESDAassignment[school][i] < 0){
				break;
			}
			counter++;
		}
		return counter;
	}
	
	/**
	 * Extendedschool全体に割り当てられている学生数を取得
	 */
	public int getNumOfExtendedStudents(){
		int counter = 0;

		for(int i=NumOfSchools; i<NumOfSchools * 2; i++){
			counter += ESDAgetNumOfStudents(i);
		}
		return counter;
	}

	/**
	 * 全体で割り当てられている学生数を取得
	 */
	public int getNumOfAssignedStudentsAll(){
		int counter = 0;

		for(int i=0; i<NumOfSchools; i++){
			counter += getNumOfAssignStudents(i);
		}
		return counter;
	}

	/**
	 * schoolのn番目に割り当てられている学生を取得
	 */
	public int getAssignedStudnets(int school, int n) {
		return assignment[school][n];
	}

	/**
	 * 学生の割り当てられている学校を返す
	 */
	public int getAssignedSchool(int student) {
		return assignmentOfStudents[student];
	}
	
	/**
	 * 学生の割り当てられている学校を返す (ESDA用)
	 */
	public int ESDAgetAssignedSchool(int student) {
		return ESDAassignmentOfStudents[student];
	}

	/**
	 * 割当の初期化
	 */
	public void init() {
		// 割当の初期化
		for (int i = 0; i < NumOfSchools; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				assignment[i][j] = NOTASSIGNED;
			}
		}

		// 学生の割当を初期化
		for (int i = 0; i < NumOfStudents; i++) {
			assignmentOfStudents[i] = NOTASSIGNED;
		}
	}
	
	/**
	 * 割当の初期化 ESDA用
	 */
	public void ESDAinit() {
		// 割当の初期化
		for (int i = 0; i < NumOfSchools * 2; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				ESDAassignment[i][j] = NOTASSIGNED;
			}
		}

		// 学生の割当を初期化
		for (int i = 0; i < NumOfStudents; i++) {
			ESDAassignmentOfStudents[i] = NOTASSIGNED;
		}
	}

	/**
	 * 学校毎の割当の表示
	 */
	public void dispAssignment(){
		int NumOfAssignedStudents;

		for(int i=0; i<NumOfSchools; i++){
			System.out.print("school "+i+":");

			NumOfAssignedStudents = this.getNumOfAssignStudents(i);
			for(int j=0; j<NumOfAssignedStudents; j++){
				System.out.print(assignment[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	/**
	 * 学校毎の割当の表示 (ESDA用)
	 */
	public void ESDAdispAssignment(){
		int NumOfAssignedStudents;

		for(int i=0; i<NumOfSchools; i++){
			System.out.print("school "+i+":");

			NumOfAssignedStudents = this.ESDAgetNumOfStudents(i);
			for(int j=0; j<NumOfAssignedStudents; j++){
				System.out.print(ESDAassignment[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * Claimを持つ学生の表示
	 */
	public void dispClaimStudents(){
		for(int i = 0; i < NumOfStudents; i++){
			if (ClaimStudents[i] != 0) {
				System.out.print(ClaimStudents[i] + " ");
			}
		}
		System.out.println();
	}
	
	/**
	 * ACDAでClaimを持つ学生を保存
	 */
	public void saveACDAClaimStudents(){
		int order1 = 0;
		for(int i = 0; i < NumOfStudents; i++){
			if (ClaimStudents[i] != 0) {
				ACDAClaimer[order1] = ClaimStudents[i];
				order1++;
			}
		}
	}
	
	/**
	 * QRDAでClaimを持つ学生を保存
	 */
	public void saveQRDAClaimStudents(){
		int order2 = 0;
		for(int i = 0; i < NumOfStudents; i++){
			if (ClaimStudents[i] != 0) {
				QRDAClaimer[order2] = ClaimStudents[i];
				order2++;
			}
		}
	}
	
	/**
	 * QRDAでClaimを持ってACDAで持たない人がいるかチェック
	 */
	public void Claimcheck(){
		for(int i = 0; i < NumOfStudents; i++){
			if (QRDAClaimer[i] != 0 && Arrays.asList(ACDAClaimer).contains(QRDAClaimer[i])) {
				System.out.println("I found it!\n");
			}
		}
	}
	
	/**
	 * 学生毎の割当の表示
	 */
	public void dispAssignmentOfStudents(){
		for(int i=0; i<NumOfStudents; i++){
			System.out.println("student "+i+":"+assignmentOfStudents[i]+" ");
		}
	}
	
	/**
	 * この割当に別のAssignmentの学生を加える
	 * @param addAssign: 加える別の割当
	 */
	public void addAssignmentStudents(Assignment addAssign){
		int targetStudent;

		for(int i=0; i<NumOfSchools; i++){
			int school = i;
			for(int j=0;j<addAssign.getNumOfAssignStudents(school);j++){
				targetStudent = addAssign.getAssignedStudnets(school,j);
				this.setStudent(targetStudent, school);
			}
		}
	}
}
