package Main;

import java.util.Arrays;

/**
 * ExtendedSeatDAで解くクラス
 * Extended-schoolの学生もstandard-schoolにまとめる
 */
public class MC_ESDA2 {

	/* 変数宣言 */
	private int NumOfStudents;
	private int NumOfSchools;
	private int MaxQuotas;
	private int MinQuotas;
	private int DifQuotas;
	private int Regionale;
	private int[][] preferenceOfStudents; // 学生の選好を格納
	private int[][] preferenceOfSchools; // デバッグ用	
	boolean disp_flag = false; //動作内容を表示する場合はtrueとする
	
	/**
	 * 学生の学校へのプロポーズ 
	 * proposeOfStudents[i] = 学生iがプロポーズしている学校
	 */
	private int[] proposeOfStudents;

	/**
	 * 拒否された学校 
	 * rejectedSchools[i][j] = 学生iの学校jに対する状態 
	 * NotRejected:0, Rejected:1
	 */
	private int[][] rejectedSchools;

	/**
	 * 学生の割当状態を格納 
	 * assignedStatus[i] = 学生iの割当状態
	 * NotAssigned:0, Assigned:1
	 */
	private int[] assignedStatus;

	/**
	 * 学生のプロポーズを行列で管理．
	 *  proposeMatrix[i][j] = 学校iがj番目に好む学生のプロポーズ
	 */
	private int[][] proposeMatrix;
	
	// extended-schoolに割り当てられた学生を保存
	private int[] extendedstudents;
	
	/* 定数宣言 */
	private final int NOTREJECTED = 0; // 拒否されていない
	private final int REJECTED = 1; // 拒否された

	private final int NOTSCHOOL = -1; //学生が有効な学校に申し込んでいないことを意味する
	private final int NOTSTUDENT = -1; //学校に申し込んでいる学生が存在しないことを意味する
	
	private final int NOTASSIGNED = 0; // 割り当てられていない
	private final int ASSIGNED = 1; // 割り当てられている
	
	/**
	 * ESDAを行う
	 */
	public void solve(Problem prob, Assignment assign) {

		/* 変数宣言 */
		int rejectedStudents; // 各ラウンドで拒否された学生数をカウント
		int targetSchool;
		int targetStudent;
		int stage;
		
		/* 初期設定 */
		NumOfStudents = prob.getNumOfStudents();
		NumOfSchools = prob.getNumOfSchools();
		MaxQuotas = prob.getMaxQuotas();
		MinQuotas = prob.getMinQuotas();
		DifQuotas = MaxQuotas - MinQuotas;
		Regionale = NumOfStudents - (NumOfSchools * MinQuotas);
		
		preferenceOfStudents = new int[NumOfStudents][NumOfSchools * 2];
		proposeOfStudents = new int[NumOfStudents];
		proposeMatrix = new int[NumOfSchools * 2][NumOfStudents];
		extendedstudents = new int[NumOfStudents];
		
		// 初期化
		for (int i = 0; i < NumOfStudents; i++) {
			extendedstudents[i] = -1;
		}
				
		// 選好を取得
		preferenceOfStudents = prob.getESDAPreferenceOfStudents();
		preferenceOfSchools = prob.getESDAPreferenceOfSchools();
		
//		System.out.println("p" + MinQuotas);
//		System.out.println("q" + MaxQuotas);
		
		while(true) {//ステージについてのループ
		
			// 拒否された学生を初期化
			rejectedStudents = NumOfStudents;
			
			// 拒否された学校を初期化
			rejectedSchools = new int[NumOfStudents][NumOfSchools * 2];
			for (int i = 0; i < NumOfStudents; i++) {
				for (int j = 0; j < NumOfSchools * 2; j++) {
					rejectedSchools[i][j] = NOTREJECTED;
				}
			}
	
			/* 割当開始 */
	
			stage = 0;
			// はじかれる学生がいなくなるまでループ
			while (rejectedStudents > 0) {
	
				if(disp_flag) System.out.println("---stage:"+stage+"---");
				
				rejectedStudents = 0;
	
				/*
				 * 割当はラウンドでリセット
				 */
				assign.ESDAinit();
	
				// 学生の割当状況を初期化
				assignedStatus = new int[NumOfStudents];
				for (int i = 0; i < NumOfStudents; i++) {
					assignedStatus[i] = NOTASSIGNED;
				}
	
				// 学生のプロポーズ先を決定
				setProposeOfStudents();
				setProposeMatrix(prob);
//				System.out.println("学校のpropose情報 " + Arrays.deepToString(proposeMatrix));
	
				if(disp_flag){
					for (int i = 0; i < NumOfStudents; i++) {
						System.out.println("student "+i+" is proposing to school "+proposeOfStudents[i]);
					}
				}
				
				// extended-schoolにapplyする予定(accept or rejectはわからない)の人数
				int NumofExtendedApplyStudents = 0;
				for (int i = NumOfSchools; i < NumOfSchools * 2; i++) {
					for (int j = 0; j < NumOfStudents; j++) {
						if(proposeMatrix[i][j] != NOTSTUDENT) {
							NumofExtendedApplyStudents++;
						}
					}
				}
//				System.out.println("学校のpropose情報 " + Arrays.deepToString(proposeMatrix));
	
				// standard-school
				//学校の番号順にみていく
				for(int j=0; j < NumOfSchools; j++){
					targetSchool = j;
	
					for(int i=0;i<NumOfStudents;i++){
						int rank = i;
						//rankの学生がプロポーズしているか
						if(proposeMatrix[targetSchool][rank] != NOTSTUDENT){
							targetStudent = proposeMatrix[targetSchool][rank];
	
							//上限に達していないなら割り当てる
							if(assign.ESDAgetNumOfStudents(targetSchool) < MinQuotas) {
								assign.ESDAsetStudent(targetStudent, targetSchool);
								assignedStatus[targetStudent] = ASSIGNED;	
							}
							//上限に達しているならはじく
							else{
								rejectedSchools[targetStudent][targetSchool] = REJECTED;
								rejectedStudents ++;
								if (disp_flag) System.out.println("student "+ targetStudent + " is rejected by "+ targetSchool);
							}
						}
					}
				}
				
				// extended-school
				//round-robinにみていく (今回の設定では逆のround-robinにしている)
				boolean studentcheckflag = false;
				int rrcounter = NumOfSchools * 2 - 1; // 今見ている学校
				int studentcheck = 0; // その学校の何番目の学生を見ている
				int extendedcheckedstudents = 0; // extendedにいる中でチェックが終わった学生数
				int counter = 0; // extendedに割り当てられた学生数 (結果ではなくて動作とともに増えていく)
				
				// その学校で一人見つけてチェックして次の学校に行く
				while (NumofExtendedApplyStudents != extendedcheckedstudents) {
					while (!studentcheckflag) {
						if (proposeMatrix[rrcounter][studentcheck] != NOTSTUDENT) {
							targetStudent = proposeMatrix[rrcounter][studentcheck];
							proposeMatrix[rrcounter][studentcheck] = NOTSTUDENT;
							studentcheckflag = true;
							extendedcheckedstudents++;
							// 上限とeに違反しないなら割り当てる
							if (assign.ESDAgetNumOfStudents(rrcounter) < DifQuotas && assign.getNumOfExtendedStudents() < Regionale) {
								assign.ESDAsetStudent(targetStudent, rrcounter);
								assignedStatus[targetStudent] = ASSIGNED;
								extendedstudents[counter] = targetStudent;
								counter++;
							} else {
								// そうでなければreject
								rejectedSchools[targetStudent][rrcounter] = REJECTED;
								rejectedStudents ++;
								if (disp_flag) System.out.println("student "+ targetStudent + " is rejected by "+ rrcounter);
							}						
						} else if (studentcheck == NumOfStudents - 1){
							studentcheckflag = true;
						} else {
							studentcheck++;
						}
					}
					if (rrcounter == NumOfSchools) {
						rrcounter = NumOfSchools * 2 - 1;
					} else {
						rrcounter--;
					}
					studentcheckflag = false;
					studentcheck = 0;
				}

				stage++;
			}
			
			// extended-schoolの学生をstandard-schoolに移動させる
			int w = 0;
			while (w < assign.getNumOfExtendedStudents()) {
				targetStudent = extendedstudents[w];
//				assign.debug();
//				System.out.println("w " + w +" targetstudent " + targetStudent + " idousaki " + (assign.ESDAgetAssignedSchool(targetStudent) - NumOfSchools));
				assign.ESDAsetStudent(targetStudent, assign.ESDAgetAssignedSchool(targetStudent) - NumOfSchools);
				w++;
			}
				
			assign.ESDAconvert();
			System.out.println(); // インスタンス間の見た目用
			return;
		}
	}

	/**
	 * 学生のプロポーズを行列で設定
	 */
	private void setProposeMatrix(Problem prob) {
		int targetStudent;
		int targetSchool;
		int preferenceNum;

		// プロポーズ行列を初期化
		for (int i = 0; i < NumOfSchools * 2; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				proposeMatrix[i][j] = NOTSTUDENT;//初期値はNOTSTUDENT
			}
		}

		for (int i = 0; i < NumOfStudents; i++) {
			targetStudent = i;
			targetSchool = proposeOfStudents[targetStudent];

			// 学生がNOTSCHOOLにプロポーズしている場合（全ての学校に振られた場合）スキップ
			if (targetSchool == NOTSCHOOL) {
				continue;
			}
			preferenceNum = prob.getESDAPreferenceSchoolToStudent(targetSchool, targetStudent);
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
			for (int j = 0; j < NumOfSchools * 2; j++) {
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
