package Main;

import java.util.Arrays;

/**
 * メインクラス
 */

import java.math.BigDecimal;

public class Main {
	
	// 収集データの設定
	private static int NumOfData = 3;
	private static final int CLAIMING_STUDENTS = 0;
	private static final int STUDENT_WITH_ENVY = 1;
	private static final int NUMBER_OF_CLAIM = 2;
	private static String[] DATA_NAME = {"claimingStudents","studentsWithEnvy","numOfClaims"};
	
	// データを集計するかどうか
	private static boolean[] dataCheckFlag = new boolean[NumOfData];

	// 各インスタンスごとの実験結果
	private static int[][][] evaluationResult;
	// 全インスタンスの平均
	private static double[][] average;
	
	// 学生，学校のwelfare
	private static int[][][] studentWelfare;
	private static double[][] studentWelfareAve;
	private static String DATA_NAME_STUDENTWELFARE = "studentWelfare";

	// 散布図に用いる各インスタンス毎の結果	
	private static double[][] AveBordaScoreOfStudent; 
	private static double[][] RatioOfAveBordaScoreOfStudent;
	private static double[][] RatioOfStudentsWithoutClaim;
	private static double[][] RatioOfStudentsWithoutEnvy;
	private static String DATA_NAME_ScatterDiagram = "Borda_StudentsWithoutEnvy";
	
	// 2つのメカニズムを比較し，厳密に良い割り当て結果となった学生数を記録する
	private static int[][] WhichLike;
	private static double[] AveWhichLike;
	private static String DATA_NAME_WhichLike = "WhichLike";
	
	// 各種宣言
	private static int instance;

	/**
	 * メイン関数
	 */
	public static void main(String[] args) {
		evaluation();
	}
	
	public static void evaluation() {
		// ファイルの出力
		FileOutput fio = new FileOutput();
	
		// 入力情報
		int NumOfParameters = 7;
		final int STUDENTS = 0;
		final int SCHOOLS = 1;
		final int STUDENT_CR = 2;
		final int MAXQUOTAS = 3;
		final int MINQUOTAS = 4;
		final int DIFFERENCE = 5;
		final int MANHATTAN = 6;

		String[] parameterName = { "s", "c", "alpha", "maxquota", "minquota", "dif", "Manhattan"};

		double[] parameter = new double[NumOfParameters];
		// 変数（初期値は0）
		for (int i = 0; i < NumOfParameters; i++) {
			parameter[i] = 0;
		}

		//---------- 変数設定（基本的にはココをいじる）-------------------- 
		parameter[STUDENTS] = 800;//学生数n
		parameter[SCHOOLS] = 20;//学校数m
		parameter[DIFFERENCE] = 70;//Difference Constraint (使用しないときは-1)
		parameter[MAXQUOTAS] = 106;//個別上限 (全部の学校同じ) (使用しないときは-1)
		parameter[MINQUOTAS] = 36;//個別下限 (全部の学校同じ) (使用しないときは-1)
		parameter[MANHATTAN] = -1;//Manhattan Constraint (使用しないときは-1)
		//------------------------------------------------------------
		
		//---------- make mode の設定（基本的にはココをいじる）-------------------- 
		//makeMode = 0:読み込み（問題を読み込み，解く）
		//makeMode = 1:書き出し（問題を書き出すのみ）
		//実験の流れは [makeMode = 1で実行] -> [makeMode = 0で実行]
		int makeMode = 0;
		//---------------------------------------------------------------
		
		//---------- 問題数の設定（基本的にはココをいじる）-------------------- 
		instance = 100;
		//------------------------------------------------------------
		
		//---------- 表示の設定（基本的にはココをいじる）-------------------- 
		boolean disp_flag_pref = true;//選好を表示する場合はtrueとする
		boolean disp_flag_mech = true;//メカニズムの結果を表示する場合はtrueとする
		//------------------------------------------------------------
		
//		コンソールに表示されるのは，普段見慣れた"選好"
//		Problemとして生成されたり，手打ちするのは学校も学生も"効用値"
		
		parameter[STUDENT_CR] = 0.0;//学生の選好の相関alpha
		
		
		// グラフの横軸とする変数の設定
		boolean[] graphParameterFlag = new boolean[NumOfParameters];
		for (int i = 0; i < NumOfParameters; i++) {
			graphParameterFlag[i] = false;
		}
		graphParameterFlag[STUDENT_CR] = true;

		// PLTを生成するかどうか
		boolean makePltFlag = true;
		
		// メカニズム数
		int NumOfAlgorithm = 2;

		// メカニズムの設定（全てstrategy-proofかつfeasibleなメカニズム）
		Mechanisms[] mechanisms = new Mechanisms[NumOfAlgorithm];
		for (int i = 0; i < NumOfAlgorithm; i++) {
			mechanisms[i] = new Mechanisms();
		}
		String[] ALG_NAME = new String[NumOfAlgorithm];

		ALG_NAME[0] = "QRDA";
		ALG_NAME[1] = "ESDA";
//		ALG_NAME[2] = "ESDA";
//		ALG_NAME[2] = "SD";
//		ALG_NAME[3] = "ADA";
//		ALG_NAME[4] = "Random";

		// 調べるデータの設定
		for (int i = 0; i < NumOfData; i++) {
			dataCheckFlag[i] = false;
		}
		dataCheckFlag[CLAIMING_STUDENTS] = true;
		dataCheckFlag[STUDENT_WITH_ENVY] = true;
		dataCheckFlag[NUMBER_OF_CLAIM] = true;
		
		//welfareを出力するかどうか（学生の相関ごとに出る）
		boolean Welfareflag = true;
		
		// 散布図（縦軸：RatioOfStudentsWithoutEnvy，横軸：RatioOfAveBordaScoreOfStudent）を出力するかどうか（学生の相関ごとに出る）
		boolean Scatterflag = false;
		
		//which likeを出力するかどうか
		boolean WhichLikeflag = true;
		
		//Bordaやclaimを表示するか
		boolean resultinformation = true; 
		
		//比較したいメカニズムの番号を記録
		int CompAlgNum_1, CompAlgNum_2;
		CompAlgNum_1 = 0;
		CompAlgNum_2 = 1;

		// 各種データの初期化
		evaluationResult = new int[NumOfData][NumOfAlgorithm][instance];
		average = new double[NumOfData][NumOfAlgorithm];

		studentWelfare = new int[NumOfAlgorithm][instance][(int) parameter[SCHOOLS]];
		studentWelfareAve = new double[NumOfAlgorithm][(int) parameter[SCHOOLS]];	
		
		AveBordaScoreOfStudent = new double[NumOfAlgorithm][instance];
		RatioOfAveBordaScoreOfStudent = new double[NumOfAlgorithm][instance];
		RatioOfStudentsWithoutEnvy = new double[NumOfAlgorithm][instance];
		RatioOfStudentsWithoutClaim = new double[NumOfAlgorithm][instance];
		
		WhichLike = new int[instance][NumOfAlgorithm+1];//WhichLike[i][numOfAlgorithm]には，問題iにおいて，どちらのメカニズムにおいても割当が変わらなかった人数を記録
		AveWhichLike = new double[NumOfAlgorithm+1];
		
		double BordaAve = 0;
		double ClaimerAve = 0;
		double ACDABordaAve = 0;
		double QRDABordaAve = 0;
		double ACDAClaimerAve = 0;
		double QRDAClaimerAve = 0;
		double ESDAClaimerAve = 0;
		
		BigDecimal bd;

		// 学生の相関についてループ
		for (int j = 0; j <= 0; j++) {//0～10の整数
			bd = new BigDecimal(j * 0.1);
			bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP); // 小数第２位;
			parameter[STUDENT_CR] = bd.doubleValue();

			// 問題数についてループ
			int instanceNum;
			for (int i = 0; i < instance; i++) {
				instanceNum = i;
				// System.out.println(i);
				if (i % 10 == 0) {
					System.out.print(".");
				}
				if (i == instance - 1) {
					System.out.println();
				}

				Problem prob;
				prob = new Problem();
				Assignment[] answer = new Assignment[NumOfAlgorithm];
				AnalysisAssignment[] analy = new AnalysisAssignment[NumOfAlgorithm];

				prob.makeProblem(
						(int) parameter[STUDENTS],
						(int) parameter[SCHOOLS], 
						instanceNum,
						makeMode,
						parameter[STUDENT_CR],
						(int) parameter[MAXQUOTAS],
						(int) parameter[MINQUOTAS],
						(int) parameter[DIFFERENCE],
						(int) parameter[MANHATTAN]
						);
				
				if(disp_flag_pref){
					System.out.println("instance " + i + ":");//問題番号を表示
					prob.dispPreferenceOfStudents();//学生の選好を表示
					prob.dispPreferenceOfSchools();//学校の選好を表示
				}
				
				if(makeMode != 1){
				
					// 各アルゴリズムでとく
					for (int a = 0; a < NumOfAlgorithm; a++) {
						answer[a] = new Assignment((int) parameter[STUDENTS],
								(int) parameter[SCHOOLS]);
	
						/* 解く */
						mechanisms[a].init(answer[a], prob);
						if (a == 0) {
							mechanisms[a].solveWithQRDA_Dif();
							if(disp_flag_mech){
								System.out.println("<result of QRDA>");
								answer[a].dispAssignment();//割当結果を表示
							}							
						} else if (a == 1) {
							mechanisms[a].solveWithESDA();
							if(disp_flag_mech){
								System.out.println("<result of ESDA>");
								answer[a].dispAssignment();//割当結果を表示
							}
						} else if (a == 2) {
							mechanisms[a].solveWithACDA();
							if(disp_flag_mech){
								System.out.println("<result of ESDA>");
								answer[a].ESDAdispAssignment();//割当結果を表示
							}
						} else if (a == 3) {
							mechanisms[a].solveWithSD();
							if(disp_flag_mech){
								System.out.println("<result of SD>");
								answer[a].dispAssignment();//割当結果を表示
							}
						} else if (a == 4) {
							mechanisms[a].solveWithADA();
							if(disp_flag_mech){
								System.out.println("<result of ADA>");
								answer[a].dispAssignment();//割当結果を表示
							}
						} else if (a == 5) {
							mechanisms[a].solveWithRandom();
							if(disp_flag_mech){
								System.out.println("<result of Random>");
								answer[a].dispAssignment();//割当結果を表示
							}
						}
						// 解析データ格納
						analy[a] = new AnalysisAssignment(prob, answer[a]);
						

					}
					
					// claimのTheoremの反例探し					
//					int ACDAclaimer = analy[0].getNumOfClaimingStudents();
//					int QRDAclaimer = analy[1].getNumOfClaimingStudents();
//					int ESDAclaimer = analy[2].getNumOfClaimingStudents();
//					int NumofACDAclaimer = analy[0].getNumOfClaims();
//					int NumofQRDAclaimer = analy[1].getNumOfClaims();
//					int NumofESDAclaimer = analy[2].getNumOfClaims();
//					if (QRDAclaimer > ESDAclaimer) {
//						System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//						return;
//					}
//					if (NumofQRDAclaimer > NumofACDAclaimer && QRDAclaimer > ACDAclaimer) {
//						System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//						return;
//					}

					// Bordaスコアのプロット用
					if (resultinformation) {
//						double ACDABorda = analy[0].getAveBordaScoreOfStudent();
//						double QRDABorda = analy[1].getAveBordaScoreOfStudent();
//						double ESDABorda = analy[2].getAveBordaScoreOfStudent();
						
						System.out.println("instance: " + i);
//						System.out.println("QRDABorda : " + QRDABorda);
//						System.out.println("ACDABorda : " + ACDABorda);
//						System.out.println("ESDABorda : " + ESDABorda);
//						System.out.println("QRDABorda - ACDABorda : " + (QRDABorda - ACDABorda));
//						QRDABordaAve += QRDABorda;
//						ACDABordaAve += ACDABorda;
//						ESDABordaAve += ESDABorda;
//						BordaAve += QRDABorda - ACDABorda;
					}

					// claimingのプロット用
					if (resultinformation) {
						double QRDAClaimer = analy[0].getNumOfClaimingStudents();
						double ESDAClaimer = analy[1].getNumOfClaimingStudents();
//						double ESDAClaimer = analy[2].getNumOfClaimingStudents();
//						System.out.println("ACDAClaimer : " + ACDAClaimer);
						System.out.println("QRDAClaimer : " + QRDAClaimer);
						System.out.println("ESDAClaimer : " + ESDAClaimer);
						System.out.println("ESDAClaimer - QRDAClaimer : " + (ESDAClaimer - QRDAClaimer));
//						ACDAClaimerAve += ACDAClaimer;
						QRDAClaimerAve += QRDAClaimer;
						ESDAClaimerAve += ESDAClaimer;
						ClaimerAve += ESDAClaimer - QRDAClaimer;
					}

					
					// QRDAを厳密に好む学生の割合のプロット用
//					System.out.println("The number of students who strictly prefer QRDA to ACDA : " + getWhichLike(parameter[STUDENTS],prob,answer,NumOfAlgorithm,0, 1)[1]);
					System.out.println("The number of students who strictly prefer QRDA to ESDA : " + getWhichLike(parameter[STUDENTS],prob,answer,NumOfAlgorithm,0, 1)[0]);
//					System.out.println("instance :" + i);
//					System.out.println("The number of students who strictly prefer ESDA : " + getWhichLike(parameter[STUDENTS],prob,answer,NumOfAlgorithm,CompAlgNum_1,CompAlgNum_2)[2]);
//					if (getWhichLike(parameter[STUDENTS],prob,answer,NumOfAlgorithm,1, 2)[2] != 0) {
//						return;
//					}
//					if (getWhichLike(parameter[STUDENTS],prob,answer,NumOfAlgorithm,1, 2)[1] != 0) {
//						return;
//					}
//					System.out.println(Arrays.toString(getWhichLike(parameter[STUDENTS],prob,answer,NumOfAlgorithm,CompAlgNum_1,CompAlgNum_2)));
					

					// 解析データ格納
					for (int a = 0; a < NumOfAlgorithm; a++) {
						for (int d = 0; d < NumOfData; d++) {
							if (d == CLAIMING_STUDENTS) {
								evaluationResult[d][a][i] = analy[a]
										.getNumOfClaimingStudents();//claimを持つ学生数を記録
							}
							if (d == STUDENT_WITH_ENVY) {
								evaluationResult[d][a][i] = analy[a]
										.getNumOfStudentsWithEnvy();//envyを持つ学生数を記録
							}
							if (d == NUMBER_OF_CLAIM){
								evaluationResult[d][a][i] = analy[a]
										.getNumOfClaims();//claimの件数を記録
							}
						}
						studentWelfare[a][i] = analy[a].getWelfareCumulative();//welfareの累積分布を記録
						
						//[学生のボルダスコアの平均/m]を記録
						RatioOfAveBordaScoreOfStudent[a][i] = analy[a].getAveBordaScoreOfStudent()/(int)parameter[SCHOOLS];
						//学生のボルダスコアの平均を記録
						AveBordaScoreOfStudent[a][i] = analy[a].getAveBordaScoreOfStudent();
						//envyを持つ学生数の割合を記録
						RatioOfStudentsWithoutEnvy[a][i] = analy[a].getRatioOfStudentsWithoutEnvy();
						//claimを持つ学生数の割合を記録
						RatioOfStudentsWithoutClaim[a][i] = analy[a].getRatioOfStudentsWithoutClaim();
					
					}
					
					if(WhichLikeflag){
						//もう一つのメカニズムと比較し，厳密に良い学校に割り当てられた学生数を記録
						WhichLike[i] = getWhichLike(parameter[STUDENTS],prob,answer,NumOfAlgorithm,CompAlgNum_1,CompAlgNum_2);
					}
				}
			}
			
			if(makeMode != 1){
				// 全問題の結果を集計して平均を計算
				for (int a = 0; a < NumOfAlgorithm; a++) {
					for (int d = 0; d < NumOfData; d++) {
						if (dataCheckFlag[d]) {
							average[d][a] = getAve(evaluationResult[d][a]);
						}
					}
					studentWelfareAve[a] = getWelfareAve(studentWelfare[a],
							(int) parameter[SCHOOLS], (int) parameter[STUDENTS],
							instance);

				}
				
				// dataCheckFlag[d]=trueとなっている項目の結果を出力
				for (int d = 0; d < NumOfData; d++) {
					if (dataCheckFlag[d]) {
						if(d == CLAIMING_STUDENTS || d == STUDENT_WITH_ENVY){
						//学生の割合(平均)を出力
						fio.outputRatioOfStudents(average[d], DATA_NAME[d],
								parameter, parameterName, graphParameterFlag,
								ALG_NAME, parameter[STUDENTS], makePltFlag);
						}else if(d == NUMBER_OF_CLAIM){
						//そのままのデータを出力
							fio.output(average[d], DATA_NAME[d],
									parameter, parameterName, graphParameterFlag,
									ALG_NAME, makePltFlag);
						}
					}
				}
				// welfareを出力
				if(Welfareflag){
					fio.outputCDF(studentWelfareAve, DATA_NAME_STUDENTWELFARE,
							parameter, parameterName, graphParameterFlag, ALG_NAME,
							makePltFlag);
				}
				
//				//横x[AveBordaScoreOfStudent] - 縦y[RatioOfStudentsWithoutEnvy]の散布図データを出力
//				if(Scatterflag){
//					fio.outputScatter(AveBordaScoreOfStudent,
//							RatioOfStudentsWithoutEnvy,
//							DATA_NAME_ScatterDiagram,
//							parameter, parameterName, graphParameterFlag,
//							ALG_NAME, makePltFlag);
//				}
				
				//横x[AveBordaScoreOfStudent/m] - 縦y[RatioOfStudentsWithoutEnvy]の散布図データを出力
				if(Scatterflag){
					fio.outputScatter(RatioOfAveBordaScoreOfStudent,
							RatioOfStudentsWithoutEnvy,
							DATA_NAME_ScatterDiagram,
							parameter, parameterName, graphParameterFlag,
							ALG_NAME, makePltFlag);
				}
				
				//横x[AveBordaScoreOfStudent/m] - 縦y[RatioOfStudentsWithoutEnvy]の散布図データを出力
//				if(Scatterflag){
//					fio.outputScatter(RatioOfStudentsWithoutClaim,
//							RatioOfStudentsWithoutEnvy,
//							DATA_NAME_ScatterDiagram,
//							parameter, parameterName, graphParameterFlag,
//							ALG_NAME, makePltFlag);
//				}
				// whichLikeを出力
				if(WhichLikeflag){
					AveWhichLike[CompAlgNum_1] = getAveWhichLike(WhichLike,instance,CompAlgNum_1,parameter[STUDENTS]);
					AveWhichLike[CompAlgNum_2] = getAveWhichLike(WhichLike,instance,CompAlgNum_2,parameter[STUDENTS]);
					AveWhichLike[NumOfAlgorithm] = getAveWhichLike(WhichLike,instance,NumOfAlgorithm,parameter[STUDENTS]);
					fio.outputWhichLike(AveWhichLike,CompAlgNum_1,CompAlgNum_2,DATA_NAME_WhichLike,
							parameter, parameterName,graphParameterFlag,ALG_NAME, makePltFlag);
				}
				
				if (resultinformation) {
//					System.out.println("\n\n***ACDABordaAve*** : " + parameter[STUDENTS]*ACDABordaAve/instance);
//					System.out.println("\n\n***QRDABordaAve*** : " + parameter[STUDENTS]*QRDABordaAve/instance);
//					System.out.println("\n\n***ESDABordaAve*** : " + parameter[STUDENTS]*ESDABordaAve/instance);
//					System.out.println("\n\n***BordaAve (QRDA - ACDA)*** : " + parameter[STUDENTS]*BordaAve/instance);
//					System.out.println("\n\n***ACDAClaimerAve*** : " + ACDAClaimerAve/instance);
					System.out.println("\n\n***QRDAClaimerAve*** : " + QRDAClaimerAve/instance);
					System.out.println("\n\n***ESDAClaimerAve*** : " + ESDAClaimerAve/instance);
					System.out.println("\n\n***ClaimerAve (ESDA - QRDA)*** : " + ClaimerAve/instance);
					System.out.println("\n\n***WhichlikeAve (prefer QRDA to ESDA)*** : "+ AveWhichLike[CompAlgNum_1]);
				}
			}
			if(makeMode == 1) break;
		}
	}

	/**
	 * 平均値を取得
	 */
	static public double getAve(int[] data) {
		double average = 0;
		int dataSize = data.length;

		for (int i = 0; i < dataSize; i++) {
			average += data[i];
		}
		average /= instance;

		return average;
	}

	/**
	 * 指定されたwelfareの平均値を取得
	 */
	private static double[] getWelfareAve(int[][] welfare, int schools,
			int students, int instance) {
		double[] average = new double[schools];

		for (int i = 0; i < schools; i++) {
			for (int j = 0; j < instance; j++) {
				average[i] += welfare[j][i];
			}
			average[i] /= instance; // インスタンスの平均値
			average[i] = (average[i] / students); // 学生数で割る
		}

		return average;
	}
	
	/**
	 * 厳密に良い学校に割り当てられた学生数を記録した配列を返す
	 * メカニズム数+1の配列を用意し，勝ったところに+1．結果がつかないときは配列の最後にカウント
	 */
	private static int[] getWhichLike(double parameter, Problem prob, Assignment[] answer, int numOfAlgorithm, int A_1, int A_2){
		int[] NumOfWinner = new int[numOfAlgorithm + 1];
		NumOfWinner[A_1] = 0;
		NumOfWinner[A_2] = 0;

		for(int i = 0; i < parameter; i++){//全学生についてみる
			int RankOfA_1 = prob.getPreferenceStudentToSchool(i, answer[A_1].getAssignedSchool(i));
			int RankOfA_2 = prob.getPreferenceStudentToSchool(i, answer[A_2].getAssignedSchool(i));
			if(RankOfA_1 < RankOfA_2){
				NumOfWinner[A_1] += 1;
//				System.out.println("student "+i+" prefer A_1 to A_2");
			}else if(RankOfA_1 > RankOfA_2){
				NumOfWinner[A_2] += 1;
//				System.out.println("student "+i+" prefer A_2 to A_1");
			}else{
				NumOfWinner[numOfAlgorithm] += 1;
			}
		}
//		System.out.println(Arrays.toString(NumOfWinner));
		
		return NumOfWinner;
	}
	
	/**
	 * WhichLikeの割合を出し，その平均を返す
	 */
	private static double getAveWhichLike(int[][] WhichLike,int instance,int Alg, double students){
		double average = 0;
		for (int i = 0; i < instance; i++) {
			average += WhichLike[i][Alg] / students;//WhichLikeに関する人数の割合を出す
													//(WhichLike[i][Alg] ... 問題iにおいて，メカニズムAlgの方がより良い学校に割り当てられた学生の数)
		}
		average /= instance;
		return average;
	}
}
