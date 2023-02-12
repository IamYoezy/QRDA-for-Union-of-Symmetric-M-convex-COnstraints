package Main;

import java.util.Arrays;
/**
 * AdaptiveDAで解くクラス
 */
public class MC_ADA {

	/* 変数宣言 */
	private int NumOfStudents;
	private int NumOfSchools;
	private double Ratio;
	private int[][] preferenceOfStudents; // 学生の選好を格納
	
	boolean disp_flag = false;//動作内容を表示する場合はtrueとする
	
	
	/**
	 * 学生の通常枠へのプロポーズ
	 * proposeOfStudents[i] = 学生iがプロポーズしている学校
	 */
	private int[] proposeOfStudents;

	/**
	 * 拒否された学校 
	 * rejectedSchools[i][j] = 学生iの学校jに対する状態
	 *  NotRejected:0, Rejected:1
	 */
	private int[][] rejectedSchools;

	/**
	 * 学生の割当状態を格納
	 *  assignedStatus[i] = 学生iの割当状態 
	 *  NotAssigned:0, Assigned:1, Unparticipant:-1
	 */
	private int[] assignedStatus;

	/**
	 * 学生のプロポーズを行列で管理． 
	 * proposeMatrix[i][j] = 学校iがj番目に好む学生のプロポーズ
	 */
	private int[][] proposeMatrix;
	
	/**
	 * 割当を禁止する学校でを管理． 
	 * forbiddenSchool[i] = 学校iへの割当は禁止
	 */
	private boolean[] forbiddenSchool;
	
	/* 定数宣言 */
	private final int NOTREJECTED = 0; // 拒否されていない
	private final int REJECTED = 1; // 拒否された

	private final int NOTSCHOOL = -1; //学生が有効な学校に申し込んでいないことを意味する
	private final int NOTSTUDENT = -1; //学校に申し込んでいる学生が存在しないことを意味する
	
	private final int NOTASSIGNED = 0; // 割り当てられていない
	private final int ASSIGNED = 1; // 割り当てられている
	private final int UNPARTICIPAIT = -1; // 参加していない

	
	/**
	 * ADAを行う
	 */
	public void solve(Problem prob, Assignment assign) {

		/* 変数宣言 */
		int rejectedStudents; // そのラウンドで拒否された学生数をカウント
		int targetSchool;
		int targetStudent;
		int restStudents; // 割当残り学生数
		
		/* 初期設定 */
		NumOfStudents = prob.getNumOfStudents();
		NumOfSchools = prob.getNumOfSchools();
		Ratio = prob.getRatio();

		rejectedStudents = NumOfStudents;
		preferenceOfStudents = new int[NumOfStudents][NumOfSchools];
		proposeOfStudents = new int[NumOfStudents];
		proposeMatrix = new int[NumOfSchools][NumOfStudents];
		forbiddenSchool = new boolean[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++) {
			forbiddenSchool[i] = false;
		}


		// 選好を取得
		preferenceOfStudents = prob.getPreferenceOfStudents();

		// 拒否された学校を初期化
		rejectedSchools = new int[NumOfStudents][NumOfSchools];
		for (int j = 0; j < NumOfStudents; j++) {
			for (int k = 0; k < NumOfSchools; k++) {
				rejectedSchools[j][k] = NOTREJECTED;
			}
		}

		int NumOfAssignedStudents = 0;

		/* 割当開始 */

		// 割当の初期化
		assign.init();

		// ステージにおける割当
		Assignment assignmentOnStage = new Assignment(NumOfStudents,NumOfSchools);
		
		int stage = 0;
		int round = 0;
		int sub_round;
		restStudents = NumOfStudents;

		// ステージ
		for (int k = 0; k < NumOfStudents; k++) {
			stage = k;
			if(disp_flag) System.out.println("---stage:"+stage+"---");

			// このステージにおける割当てを初期化
			assignmentOnStage.init();

			// 全員割り当てたら終了
			if (NumOfAssignedStudents >= NumOfStudents) {
				if(disp_flag) System.out.println("Finish ADA.");
				break;// ADAを終了
			}

			// ラウンド（マスターリストで上位の学生を一人ずつ増やしていきDA）
			for (int t = 0; t < NumOfStudents - NumOfAssignedStudents + 1; t++) {
				round = t;

				int newStudent = NumOfAssignedStudents + round;

				/* ステージの終了判定 */
				
				// 全員割り当てたとき(i)
				if (newStudent == NumOfStudents) {
					NumOfAssignedStudents += round;
					if(disp_flag) System.out.println("Fix the result of DA since all students are assigned.");
					break;// DAを終了
				}

				boolean forbidden_flag = false;
				restStudents = NumOfStudents - newStudent -1; 
				for (int i = 0; i < NumOfSchools; i++) {
					if(!forbiddenSchool[i] && isForbiddenSchool(prob,assign,assignmentOnStage,i,restStudents)){
						if(disp_flag) System.out.println("school "+i+" becomes forbidden! ");
						forbiddenSchool[i] = true;
						forbidden_flag = true;
					}
				}
				// forbidden schoolがあるとき(iii)
				if(forbidden_flag){
					// 割り当てられた学生数を追加
					NumOfAssignedStudents += round;
					if(disp_flag) System.out.println("Fix the result of DA since new forbidden school appeared.");
					break;// DAを終了
				}

				//以降，forbidden schoolがないとき(ii)
				assignmentOnStage.init();

				rejectedStudents = 1;
				sub_round = 0;
				// はじかれる学生がいなくなるまでループ(DA)
				while (rejectedStudents > 0) {

					if(disp_flag) System.out.println("---round:"+round+"-"+sub_round+"---");

					
					rejectedStudents = 0;

					/*
					 * 割り当ては毎期リセット 
					 * 割り当てられていた学生は同じschoolに再度プロポーズ
					 */
					assignmentOnStage.init();
					// 学生の割当状況を初期化
					// このラウンドで参加しない人は UNPARTICIPAIT
					assignedStatus = new int[NumOfStudents];
					for (int j = 0; j < NumOfStudents; j++) {

						if (j < NumOfAssignedStudents) {
							// 前のステージまでで割り当てられた人は参加しない
							assignedStatus[prob.getMasterList()[j]] = UNPARTICIPAIT;
						} else if (j < NumOfAssignedStudents + round + 1) {
							assignedStatus[prob.getMasterList()[j]] = NOTASSIGNED;
						} else {
							assignedStatus[prob.getMasterList()[j]] = UNPARTICIPAIT;
						}
					}

					// 学生のプロポーズ先を決定
					setProposeOfStudents();
					setProposeMatrix(prob);
					
					if(disp_flag){
						for (int i = 0; i < NumOfStudents; i++) {
							if(assignedStatus[i]!=UNPARTICIPAIT){
								System.out.println("student "+i+" is proposing to school "+proposeOfStudents[i]);
							}
						}
					}

					// 学校の番号順にみていく
					for (int j = 0; j < NumOfSchools; j++) {
						targetSchool = j;
						
						for (int m = 0; m < NumOfStudents; m++) {
							int rank = m;
							// rankの学生がプロポーズしているか
							if (proposeMatrix[targetSchool][rank] != NOTSTUDENT) {
								targetStudent = proposeMatrix[targetSchool][rank];
								// 参加しているか
								if (assignedStatus[targetStudent] != UNPARTICIPAIT) {
									// 上限に達していないかつforbiddenでないなら割り当てる
									if (assign
											.getNumOfAssignStudents(targetSchool)
											+ assignmentOnStage
													.getNumOfAssignStudents(targetSchool) < prob
												.getADACap(targetSchool)
											&& !forbiddenSchool[targetSchool]) {
										assignmentOnStage.setStudent(
												targetStudent, targetSchool);
										assignedStatus[targetStudent] = ASSIGNED;
									}
									// 上限に達しているならはじく
									else {
										rejectedSchools[targetStudent][targetSchool] = REJECTED;
										rejectedStudents++;
									}
								}
							}
						}
					}
					sub_round++;
				}
			}

			/* ステージの割当を追加 */
			assign.addAssignmentStudents(assignmentOnStage);
		}
	}

	/**
	 * schoolがforbiddenかどうか
	 */
	private boolean isForbiddenSchool(Problem prob, Assignment assign,
			Assignment assignmentOnStage, int school,int restStudents) {
		boolean flag = false;
		//schoolがfullでない
		if(assignmentOnStage.getNumOfAssignStudents(school) + assign.getNumOfAssignStudents(school) < prob.getADACap(school)){
			int copy_assign[];
			copy_assign = new int[NumOfSchools];
			for (int i = 0; i < NumOfSchools; i++){
				if (school == i){
					copy_assign[i] = assignmentOnStage.getNumOfAssignStudents(i) + assign.getNumOfAssignStudents(i) + 1 ;
				}else{
					copy_assign[i] = assignmentOnStage.getNumOfAssignStudents(i) + assign.getNumOfAssignStudents(i) ;
				}
			}
			
			/*
			 * restStudentsの学生一人ひとりについて，
			 * 割当数の配列を昇順ソートし，割当数が最小の学校にその学生を割り当てる場合を考える
			 * （すなわち，割当数の分散が最も抑えられるケースを考える）
			 */
			for(int i = 0; i < restStudents; i++){
				Arrays.sort(copy_assign);
				copy_assign[0] += 1;
			}
			// 割当数の分散が最も抑えられるケースで，ratioに違反するか
			Arrays.sort(copy_assign);
			if ((double) copy_assign[0]/copy_assign[NumOfSchools-1] < Ratio){
				flag = true;//ratioに違反
			}
		}
		return flag;
	}

	/**
	 * 学生のプロポーズを行列で設定
	 */
	private void setProposeMatrix(Problem prob) {
		int targetStudent;
		int targetSchool;
		int preferenceNum;

		// プロポーズ行列を初期化
		for (int i = 0; i < NumOfSchools; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				proposeMatrix[i][j] = NOTSTUDENT;
			}
		}

		for (int i = 0; i < NumOfStudents; i++) {
			targetStudent = i;
			targetSchool = proposeOfStudents[targetStudent];

			// 学生がNOTSCHOOLにプロポーズしている場合（全ての学校に振られた場合）スキップ
			if (targetSchool == NOTSCHOOL) {
				continue;
			}

			preferenceNum = prob.getPreferenceSchoolToStudent(targetSchool,
					targetStudent);
			proposeMatrix[targetSchool][preferenceNum] = targetStudent;
		}
	}

	/**
	 * 学生がプロポーズする学校を設定
	 */
	private void setProposeOfStudents() {
		int targetSchool;
		for (int i = 0; i < NumOfStudents; i++) {
			proposeOfStudents[i] = NOTSCHOOL;//初期値はNOTSCHOOL
		}

		// すべての学生を設定
		for (int i = 0; i < NumOfStudents; i++) {
			// 断られていない学校を探す
			for (int j = 0; j < NumOfSchools; j++) {

				targetSchool = preferenceOfStudents[i][j];

				// 断られていないならプロポーズ
				if (rejectedSchools[i][targetSchool] != REJECTED) {
					// すでに割り当てられていても同じschoolにプロポーズし続ける
					proposeOfStudents[i] = targetSchool;
					break;
				}
			}
		}
	}
}
