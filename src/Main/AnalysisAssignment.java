package Main;

import java.util.Arrays;

/**
 * 解を解析するクラス
 */
public class AnalysisAssignment {

	boolean disp_envy_flag = false;//envyを表示する場合はtrueとする
	boolean disp_claim_flag = false;//claimを表示する場合はtrueとする
	
	private Problem prob; // 問題のインスタンス
	private Assignment assign; // 割当
	private int NumOfStudents; // 学生数
	private int NumOfSchools; // 学校数
	private int Difference; // difference constraint
	
	/**
	 * 何番目に望ましい学校に配属されたか
	 *  welfare[i] = i番目に好む学校に割り当てられている学生数
	 */
	private int[] welfare;
	private int[] welfareCumulative; // welfareの累積分布

	private int NumOfClaimingStudents;
	private int NumOfClaims;
	private int NumOfStudentsWithEnvy;
	
	/* 定数宣言 */
	private final int NOTASSIGNED = -1;
	
	/**
	 * AnalysisAssignmentが呼び出されたときに実行
	 */
	public AnalysisAssignment(Problem pr, Assignment as) {
	
		// 初期設定
		this.prob = pr;
		this.assign = as;
		NumOfStudents = prob.getNumOfStudents();
		NumOfSchools = prob.getNumOfSchools();
		Difference = prob.getDif();
		
		// 初期化
		welfare = new int[NumOfSchools];
		for(int i=0; i<NumOfSchools; i++){
			welfare[i] = 0;
		}

		welfareCumulative = new int[NumOfSchools];
		for(int i=0; i<NumOfSchools; i++){
			welfareCumulative[i] = 0;
		}
		
		// 各データをセット
		this.setClaimingStudents();
		this.setStudentsWithEnvy();
		this.setWelfare();
		this.setWelfareCumulative();
	}

	/**
	 * envy の計算
	 */
	private void setStudentsWithEnvy() {
		//学生がenvyをもつかどうかのフラグ
		boolean[] envyFlag = new boolean[NumOfStudents];
		//初期化
		for (int i = 0; i < NumOfStudents; i++) {
			envyFlag[i] = false;
		}
		//すべての学生を調べる
		for (int i = 0; i < NumOfStudents; i++) {
			int student = i;
			int currentSchool = assign.getAssignedSchool(student);
			int currentRank = prob.getPreferenceStudentToSchool(student,
					currentSchool);
			//今割り当てられている学校より好む学校を調べる
			for (int j = 0; j < currentRank; j++) {
				int targetSchool = prob.getPreferenceOfStudents()[student][j];
				//より好む学校に割り当てられている学生全てを調べる
				for (int m = 0; m < assign.getNumOfAssignStudents(targetSchool); m++) {
					int targetStudent = assign.getAssignedStudnets(targetSchool,m);

					int studentRank = prob.getPreferenceSchoolToStudent(
							targetSchool, student);
					int targetStudentRank = prob
							.getPreferenceSchoolToStudent(targetSchool,
									targetStudent);
					// 「学生iがj <
					// currentRank番目に好きな学校targetSchool」の優先順序について，targetSchoolに割り当てられている学生targetStudentより，学生iが勝っている
					if (studentRank < targetStudentRank) {
							envyFlag[student] = true;
							if(disp_envy_flag) System.out.println("student "+student+" envies to student "+targetStudent);
					}
				}
			}
		}
		
		NumOfStudentsWithEnvy = 0;
		for (int i = 0; i < NumOfStudents; i++) {
			if (envyFlag[i]) {
				NumOfStudentsWithEnvy++;
			}
		}
	}

	/**
	 * claim の計算
	 */
	private void setClaimingStudents() {
		//学生がclaimをもつかどうかのフラグ
		boolean[] claimingStudentFlag = new boolean[NumOfStudents];
		//初期化
		NumOfClaims = 0;
		int order = 0;
		for (int i = 0; i < NumOfStudents; i++) {
			claimingStudentFlag[i] = false;
		}
		//すべての学生を調べる
		for (int i = 0; i < NumOfStudents; i++) {
			boolean flag = false;
			int student = i;
			int currentSchool = assign.getAssignedSchool(student);
			int currentRank = prob.getPreferenceStudentToSchool(student,
					currentSchool);
			for (int j = 0; j < currentRank; j++) {
				//今割り当てられている学校より好む学校を調べる
				int targetSchool = prob.getPreferenceOfStudents()[student][j];
				if (Difference == -1) {
					// Flexible uniform min/max constraintのとき
					if (!isNotSatisfyFlexibleForClaim(prob, assign,targetSchool,currentSchool)){
						claimingStudentFlag[student] = true;
						NumOfClaims += 1;
						if (!flag){
							assign.ClaimStudents[order] = student;
							order++;
							flag = true;
						}
						if(disp_claim_flag){
							System.out.println("student "+student+" claims to school "+targetSchool);
						}
					}					
				} else {
					// difference constraintのとき
					if (!isNotSatisfyDifForClaim(prob, assign,targetSchool,currentSchool)){
						claimingStudentFlag[student] = true;
						NumOfClaims += 1;
						if (!flag){
							assign.ClaimStudents[order] = student;
							order++;
							flag = true;
						}
						if(disp_claim_flag){
							System.out.println("student "+student+" claims to school "+targetSchool);
						}
					}
				}

			}
		}

		NumOfClaimingStudents = 0;
		for (int i = 0; i < NumOfStudents; i++) {
			if (claimingStudentFlag[i]) {
				NumOfClaimingStudents++;
			}
		}
	}

	/**
	 * 新たな割当がdifference constraintに違反するか(claimの判定に用いる)
	 */
	private boolean isNotSatisfyDifForClaim(Problem prob, Assignment assign, int targetschool, int currentschool) {
		boolean flag = false;
		int copy_assign[];
		copy_assign = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++){
			if (targetschool == i){
				copy_assign[i] = assign.getNumOfAssignStudents(i) + 1 ;
			}else if (currentschool == i){
				copy_assign[i] = assign.getNumOfAssignStudents(i) - 1 ;
			}else{
				copy_assign[i] = assign.getNumOfAssignStudents(i) ;
			}
		}
		// 割当数の配列を昇順ソート
		Arrays.sort(copy_assign);
		// 割当が違反するか
		if (copy_assign[NumOfSchools - 1] - copy_assign[0] > prob.getDif()){
			flag = true;//違反
		}
		return flag;			

	}
	
	/**
	 * 新たな割当がflexible constraintに違反するか(claimの判定に用いる)
	 */
	private boolean isNotSatisfyFlexibleForClaim(Problem prob, Assignment assign, int targetschool, int currentschool) {
		boolean flag = false;
		int copy_assign[];
		copy_assign = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++){
			if (targetschool == i){
				copy_assign[i] = assign.getNumOfAssignStudents(i) + 1 ;
			}else if (currentschool == i){
				copy_assign[i] = assign.getNumOfAssignStudents(i) - 1 ;
			}else{
				copy_assign[i] = assign.getNumOfAssignStudents(i) ;
			}
		}
		// 割当数の配列を昇順ソート
		Arrays.sort(copy_assign);
		// 割当が違反するか
		if ((copy_assign[0] < prob.getMinQuotas() || copy_assign[NumOfSchools - 1] > prob.getMaxQuotas()) && CalcManhattan(prob, assign) > prob.getManhattan()){
			flag = true;//違反
		}
		return flag;			

	}
	
	/**
	 * マンハッタン距離を計算
	 */
	private int CalcManhattan(Problem prob, Assignment assign) {
		int mostbalanced_border[];
		int highorlow[];
		int copy_assign[];
		int dist = 0;

		mostbalanced_border = new int[NumOfSchools];
		highorlow = new int[NumOfSchools];
		copy_assign = new int[NumOfSchools];
		
		for (int i = 0; i < NumOfSchools; i++){
//			if (i < NumOfSchools - (NumOfStudents % NumOfSchools)) {
//				mostbalanced[i] = (int)Math.floor((double)NumOfStudents/(double)NumOfSchools);
//			} else {
//				mostbalanced[i] = (int)Math.ceil((double)NumOfStudents/(double)NumOfSchools);
//			}
			mostbalanced_border[i] = (int)Math.floor((double)NumOfStudents/(double)NumOfSchools);
			copy_assign[i] = assign.getNumOfAssignStudents(i);
		}
		
		for (int i = 0; i < NumOfSchools; i++){
			dist += (int)Math.abs(mostbalanced_border[i] - copy_assign[i]);
		}
		
		for (int i = 0; i < NumOfSchools; i++){
			if (mostbalanced_border[i] >= assign.getNumOfAssignStudents(i)) {
				highorlow[i] = 1;
			} else {
				highorlow[i] = -1;
			}
		}
		
		Arrays.sort(highorlow);
		
		for (int i = 0; i < NumOfStudents % NumOfSchools; i++){
			dist += highorlow[i];
		}

		return dist;
	}

	/**
	 * 学生が何番目に望ましい学校に割り当てられたかの分布
	 */
	private void setWelfare() {
		int assignedSchool; // 学生が割り当てられている学校
		int preferNum; // 割り当てられている学校は選好で何番目か

		for (int i = 0; i < NumOfStudents; i++) {
			assignedSchool = assign.getAssignedSchool(i);
//			System.out.println("taegetstudent :" + i);
//			System.out.println("assignedschool :" + assign.getAssignedSchool(i));
//			System.out.println("preferNum :" + prob.getPreferenceStudentToSchool(i, assignedSchool));
			if (assignedSchool != NOTASSIGNED) {
				preferNum = prob.getPreferenceStudentToSchool(i, assignedSchool);
				welfare[preferNum]++;
			}
		}
	}

	/**
	 * welfareの累積分布． (welfareを参照して算出するので，setWelfareの後に呼ぶこと)
	 */
	private void setWelfareCumulative() {
		welfareCumulative[0] = welfare[0];
		for (int i = 1; i < prob.getNumOfSchools(); i++) {
			welfareCumulative[i] = welfare[i] + welfareCumulative[i - 1];
		}
	}

	/**
	 * claimを持つ学生の人数を返す
	 */
	public int getNumOfClaimingStudents() {
		return this.NumOfClaimingStudents;
	}

	/**
	 * claimの件数を返す
	 */
	public int getNumOfClaims() {
		return this.NumOfClaims;
	}
	
	/**
	 * envyを持つ学生の人数を返す
	 */
	public int getNumOfStudentsWithEnvy() {
		return this.NumOfStudentsWithEnvy;
	}
	
	/**
	 * welfareを返す
	 */
	public int[] getWelfare() {
		return this.welfare;
	}

	/**
	 * welfareの累積分布を返す
	 */
	public int[] getWelfareCumulative() {
		return this.welfareCumulative;
	}
	
	/**
	 * 学生のボルダスコアの平均を返す
	 * 
	 * 学生が0位の学校にいる：BordaScore = m
	 * 学生が1位の学校にいる：BordaScore = m-1
	 * 学生がm-1位の学校にいる：BordaScore = 1
	 * 学生がどの学校にも割り当てられていない：BordaScore = 0
	 */
	public double getAveBordaScoreOfStudent(){
		double sum = 0;
		int assignedSchool;
		for(int i=0; i<NumOfStudents; i++){
			assignedSchool = assign.getAssignedSchool(i);
			if(assignedSchool != NOTASSIGNED){
				sum += NumOfSchools - prob.getPreferenceStudentToSchool(i,assignedSchool);
			}
		}
		return sum/NumOfStudents;
	}
	
	/**
	 * Envyを持たない学生の割合を返す
	 */
	public double getRatioOfStudentsWithoutEnvy() {
		return 1.0 - (double)NumOfStudentsWithEnvy/NumOfStudents;
	}
	
	/**
	 * Claimを持たない学生の割合を返す
	 */
	public double getRatioOfStudentsWithoutClaim() {
		return 1.0 - (double)NumOfClaimingStudents/NumOfStudents;
	}
}
