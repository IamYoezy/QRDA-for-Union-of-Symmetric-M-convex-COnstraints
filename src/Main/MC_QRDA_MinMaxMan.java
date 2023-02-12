package Main;

import java.util.Arrays;

/**
 * QuotaReductionDAで解くクラス
 */
public class MC_QRDA_MinMaxMan {

	/* 変数宣言 */
	private int NumOfStudents;
	private int NumOfSchools;
	private int Dif;
	private int MaxQuotas;
	private int MinQuotas;
	private int Manhattan;
	private int[][] preferenceOfStudents; // 学生の選好を格納
	
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
	
	/* 定数宣言 */
	private final int NOTREJECTED = 0; // 拒否されていない
	private final int REJECTED = 1; // 拒否された

	private final int NOTSCHOOL = -1; //学生が有効な学校に申し込んでいないことを意味する
	private final int NOTSTUDENT = -1; //学校に申し込んでいる学生が存在しないことを意味する
	
	private final int NOTASSIGNED = 0; // 割り当てられていない
	private final int ASSIGNED = 1; // 割り当てられている
	
	/**
	 * QRDAを行う
	 */
	public void solve(Problem prob, Assignment assign) {

		/* 変数宣言 */
		int rejectedStudents; // 各ラウンドで拒否された学生数をカウント
		int targetSchool;
		int targetStudent;
		int stage;
		int round;
		int[] QuotaInEachStage; //各ステージで用いる上限値
		
		/* 初期設定 */
		NumOfStudents = prob.getNumOfStudents();
		NumOfSchools = prob.getNumOfSchools();
		Dif = prob.getDif();
		preferenceOfStudents = new int[NumOfStudents][NumOfSchools];
		proposeOfStudents = new int[NumOfStudents];
		proposeMatrix = new int[NumOfSchools][NumOfStudents];
		QuotaInEachStage = new int[NumOfSchools];
		stage = 0;
				
		// 選好を取得
		preferenceOfStudents = prob.getPreferenceOfStudents();

		while(true) {//ステージについてのループ
			
			if(disp_flag) System.out.println("---stage:"+stage+"---");
			
			//そのステージにおける上限を設定（学校のインデックス順になべて減らしていく）			
			for (int i = 0; i < NumOfSchools; i++) {
				QuotaInEachStage[i] = prob.getQRDACap(i) - (stage + NumOfSchools - i -1)/ NumOfSchools;
				if(disp_flag) System.out.println("quota of school "+i+" = "+QuotaInEachStage[i]); 
			}
			
			/* 以降，ArtificialCap以外ACDAと一致 */
			
			// 拒否された学生を初期化
			rejectedStudents = NumOfStudents;
			
			// 拒否された学校を初期化
			rejectedSchools = new int[NumOfStudents][NumOfSchools];
			for (int i = 0; i < NumOfStudents; i++) {
				for (int j = 0; j < NumOfSchools; j++) {
					rejectedSchools[i][j] = NOTREJECTED;
				}
			}
	
			/* 割当開始 */
	
			round = 0;
			// はじかれる学生がいなくなるまでループ
			while (rejectedStudents > 0) {
	
				if(disp_flag) System.out.println("---round:"+round+"---");
				
				rejectedStudents = 0;
	
				/*
				 * 割当はラウンドでリセット
				 */
				assign.init();
	
				// 学生の割当状況を初期化
				assignedStatus = new int[NumOfStudents];
				for (int i = 0; i < NumOfStudents; i++) {
					assignedStatus[i] = NOTASSIGNED;
				}
	
				// 学生のプロポーズ先を決定
				setProposeOfStudents();
				setProposeMatrix(prob);
	
				if(disp_flag){
					for (int i = 0; i < NumOfStudents; i++) {
						System.out.println("student "+i+" is proposing to school "+proposeOfStudents[i]);
					}
				}
				
				// 全学校に振られた学生の処理（今回の設定では起きない）
				for (int i = 0; i < NumOfStudents; i++) {
					targetStudent = i;
					targetSchool = proposeOfStudents[i];
					if (targetSchool == NOTSCHOOL) {
						assign.setStudent(targetStudent, targetSchool);
						assignedStatus[targetStudent] = ASSIGNED;
					}
				}
	
				//学校の番号順にみていく
				for(int j=0;j<NumOfSchools;j++){
					targetSchool = j;
	
					for(int i=0;i<NumOfStudents;i++){
						int rank = i;
						//rankの学生がプロポーズしているか
						if(proposeMatrix[targetSchool][rank] != NOTSTUDENT){
							targetStudent = proposeMatrix[targetSchool][rank];
	
							//上限に達していないなら割り当てる
							if(assign.getNumOfAssignStudents(targetSchool) < QuotaInEachStage[targetSchool]){
								assign.setStudent(targetStudent, targetSchool);
								assignedStatus[targetStudent] = ASSIGNED;
							}
							//上限に達しているならはじく
							else{
								rejectedSchools[targetStudent][targetSchool] = REJECTED;
								rejectedStudents ++;
							}
						}
					}
				}
				round++;
			}
			
			if(!isNotSatisfyFlexible(prob, assign)){//Flexible Constraintに違反しないなら，終了
				if (disp_flag) System.out.println("Last Quota : " + Arrays.toString(QuotaInEachStage));
				break;
			}
			stage++;
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
		for (int i = 0; i < NumOfSchools; i++) {
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
	
	/**
	 * 割当がFlexible Uniform Min-Max Constraintに違反するか (min + max + Manhattan)
	 */
	private boolean isNotSatisfyFlexible(Problem prob, Assignment assign) {
		boolean flag = false;
		int copy_assign[];
		copy_assign = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++){
			copy_assign[i] = assign.getNumOfAssignStudents(i);
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
	 * most balanced vectorとの"最小の"マンハッタン距離をとることに注意
	 * 単純にやると，階乗くらいの計算量がかかるので以下の手順で最小の距離を探索する
	 * (例) (3,3,3,4), (3,3,4,3), (3,4,3,3), (4,3,3,3) がmost balancedで(1,2,3,4)との最小値を探索
	 * まず(3,3,3,3)との距離の和を保存する (= 4) そして，ベクトルの要素が3以下であるか否かをjudgeしてyesなら1,noなら-1の配列を作る (= [1,1,1,-1])
	 * その配列をソートする (= [-1,1,1,1])
	 * most balancedで4が出てくる個数 (= 1) だけその配列の前から数字をgetして距離の和に加算する (= 4 + (-1))
	 * つまり最小のマンハッタン距離は3，以上の手法はmost balancedの要素が高々1差である事実より成り立っている
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

}
