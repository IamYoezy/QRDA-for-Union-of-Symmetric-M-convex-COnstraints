package Main;

/**
 * 問題を生成するクラス
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Problem implements Cloneable {

	/* 変数宣言 */
	private int NumOfStudents; // 学生数
	private int NumOfSchools; // 学校数
	private double Ratio; // Relative Constraint
	private int Dif; // Difference Constraint
	private int Manhattan; // Manhattan Constraint
	private int MaxQuotas; // ESDA
	private int MinQuotas; // ESDA
	
	//ArtificialCapを求めるために用いる値
	private int[] FeasibleQuotas;
	
	// ACDAが用いる人為的な上限
	private int[] ArtificialCap;
	
	// QRDAが初期値として用いる上限
	private int[] QRDACap;
	
	// ADAが用いる上限
	private int[] ADACap;

	// 学生の選好 preferenceOfStudent[i][j] = 学生iがj番目に好きな学校とタイプの通し番号
	private int[][] preferenceOfStudents;
	
	// ESDA用のpreference
	private int[][] ESDApreferenceOfStudents;
	
	// ESDA用のpreference
	private int[][] ESDApreferenceOfSchools;
	
	//学校の選好  preferenceOfschools[i][j] = 学校iがj番目に好きな学生の番号
	private int[][] preferenceOfschools;

	//学生のマスターリストML(SD,ADA,Randomで用いる) masterList[i] = マスターリストでi番目の学生
	private int[] masterList;
	
	// 学校のマスターリスト schoolMasterList[i] = マスターリストでi番目の学校
	private int[] schoolMasterList; 
	
	/** 読み込む問題が格納されているフォルダパス = 生成した問題を格納するフォルダパス */
	private static String DIR_NAME = "C:\\MatchingData\\MatchingProblemWithRelativeConstraintDATA\\";

	/* 乱数の初期化 */
	Random rnd = new Random();

	/**
	 * 問題作成
	 */
	public void makeProblem(int students, int schools, int instanceNum, int makeMode,
			double studentCommonR, int maxquotas, int minquotas, int dif, int manhattan) {

		/* 学生数・学校数の設定  Mainからのパラメータを受け取る*/
		NumOfStudents = students;
		NumOfSchools = schools;
		MaxQuotas = maxquotas;
		MinQuotas = minquotas;
		Dif = dif;
		Manhattan = manhattan;
		
		
		/* ACDAが用いるArtificialCapの設定 */
		FeasibleQuotas = new int[NumOfSchools];
		SetFeasibleQuotas();
		ArtificialCap = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++) {
			ArtificialCap[i] = FeasibleQuotas[i];//ArtificialCapはFeasibleQuotasと等しくする
		}
		
		/* QRDAが用いるQRDACapの設定 */
		QRDACap = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++) {
			QRDACap[i] = NumOfStudents;//QRDAのスタートであるQRDACapは学生数とする
		}
		
		// 選好の設定
		preferenceOfStudents = new int[NumOfStudents][NumOfSchools];
		ESDApreferenceOfStudents = new int[NumOfStudents][NumOfSchools * 2];
		ESDApreferenceOfSchools = new int[NumOfSchools * 2][NumOfStudents];
		preferenceOfschools = new int[NumOfSchools][NumOfStudents];

		int[] commonComponent = new int[NumOfSchools];
		int[][] idiosyncraticComponent = new int[NumOfStudents][NumOfSchools];
		int[][] schoolIdiosyncraticComponent = new int[NumOfSchools][NumOfStudents];
		
		if (makeMode > 0) {
			// 問題を新たに作成

			/* 学生のcomponentを設定 */
			for (int i = 0; i < NumOfSchools; i++) {
				commonComponent[i] = rnd.nextInt(NumOfSchools);//0~NumOfSchools-1の整数をランダムに設定
			}

			for (int i = 0; i < NumOfStudents; i++) {
				for (int j = 0; j < NumOfSchools; j++) {
					idiosyncraticComponent[i][j] = rnd
							.nextInt(NumOfSchools);//0~NumOfSchools-1の整数をランダムに設定
				}
			}

			/* 学校のcomponentを設定 */
			for (int i = 0; i < NumOfSchools; i++) {
				for (int j = 0; j < NumOfStudents; j++) {
					schoolIdiosyncraticComponent[i][j] = rnd
							.nextInt(NumOfStudents);//0~NumOfStudents-1の整数をランダムに設定
				}
			}

			try {
				String filename;

				// 学生のcomponentの出力
				filename = DIR_NAME + "studentComponent_c"
						+ NumOfSchools + "_" + instanceNum + ".txt";

				File file = new File(filename);
				FileWriter filewriter = new FileWriter(file);

				for (int i = 0; i < NumOfStudents; i++) {
					filewriter.write("student " + i + ": ");
					for (int j = 0; j < NumOfSchools; j++) {
						filewriter.write(idiosyncraticComponent[i][j] + " ");
					}
					filewriter.write("\r\n");
				}
				filewriter.write("commonComponent : ");
				for (int j = 0; j < NumOfSchools; j++) {
					filewriter.write(commonComponent[j] + " ");
				}
				filewriter.write("\r\n");
				filewriter.close();

				// 学校のcomponentの出力
				filename = DIR_NAME + "schoolComponent_s"
						+ NumOfStudents + "_" + instanceNum + ".txt";

				file = new File(filename);
				filewriter = new FileWriter(file);

				for (int i = 0; i < NumOfSchools; i++) {
					filewriter.write("school " + i + ": ");
					for (int j = 0; j < NumOfStudents; j++) {
						filewriter.write(schoolIdiosyncraticComponent[i][j]
								+ " ");
					}
					filewriter.write("\r\n");
				}

				filewriter.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			// 問題をファイルから読み込み

			try {
				String filename;

				// 学生のcomponentの読み込み
				filename = DIR_NAME + "studentComponent_c"
						+ NumOfSchools + "_" + instanceNum + ".txt";
				FileReader in = new FileReader(filename);
				BufferedReader br = new BufferedReader(in);

				String line;
				int studentCount = 0;

				while (true) {
					if ((line = br.readLine()) == null) {
						if (studentCount <= NumOfStudents) {

						}
						break;
					}
					String[] splitLine = line.split(" ", 0);
					if (studentCount < NumOfStudents) {
						for (int i = 0; i < NumOfSchools; i++) {
							idiosyncraticComponent[studentCount][i] = Integer
									.parseInt(splitLine[i + 2]);
						}
					} else if (studentCount == NumOfStudents) {
						for (int i = 0; i < NumOfSchools; i++) {
							commonComponent[i] = Integer
									.parseInt(splitLine[i + 2]);
						}

					} else {
						break;
					}
					studentCount++;
				}
				br.close();
				in.close();

				// 学校のcomponentの読み込み
				filename = DIR_NAME + "schoolComponent_s"
						+ NumOfStudents + "_" + instanceNum + ".txt";
				in = new FileReader(filename);
				br = new BufferedReader(in);

				int schoolCount = 0;

				while (true) {
					if ((line = br.readLine()) == null) {
						if (schoolCount <= NumOfSchools) {

						}
						break;
					}
					String[] splitLine = line.split(" ", 0);

					if (schoolCount < NumOfSchools) {
						for (int i = 0; i < NumOfStudents; i++) {
							schoolIdiosyncraticComponent[schoolCount][i] = Integer
									.parseInt(splitLine[i + 2]);
						}
					} else {
						break;
					}
					schoolCount++;
				}
				br.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// studentCommonRに従って学生のutilityを計算
		double[][] utility = new double[NumOfStudents][NumOfSchools];
		for (int i = 0; i < NumOfStudents; i++) {
			for (int j = 0; j < NumOfSchools; j++) {
				utility[i][j] = studentCommonR * commonComponent[j]
						+ (1.0 - studentCommonR) * idiosyncraticComponent[i][j];
				// System.out.println(studentCommonR + ":"+utility[i][j]);
			}
		}

		// 学生の選好順序（utilityの大きな学校順，タイはインデックス順でブレーク）を生成
		for (int i = 0; i < NumOfStudents; i++) {
			preferenceOfStudents[i][0] = 0;
			ESDApreferenceOfStudents[i][0] = 0;
			for (int j = 1; j < NumOfSchools; j++) {
				for (int z = j; z > 0; z--) {
					if (utility[i][preferenceOfStudents[i][z - 1]] < utility[i][j]) {
						preferenceOfStudents[i][z] = preferenceOfStudents[i][z - 1];
						preferenceOfStudents[i][z - 1] = j;
					} else {
						preferenceOfStudents[i][z] = j;
						break;
					}
				}
			}
		}
		
		// ESDA用のextendedschoolを含めたpreferenceを作成
		for (int i = 0; i < NumOfStudents; i++) {
			int p = 0;
//			System.out.println(Arrays.toString(preferenceOfStudents[i]));
			for (int j = 0; j < NumOfSchools; j++) {
				ESDApreferenceOfStudents[i][p] = preferenceOfStudents[i][j];
				ESDApreferenceOfStudents[i][p + 1] = preferenceOfStudents[i][j] + NumOfSchools;
				p = p + 2;
			}	
		}


		/* 学校のutilityを計算 */
		double[][] schoolUtility = new double[NumOfSchools][NumOfStudents];
		for (int i = 0; i < NumOfSchools; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				schoolUtility[i][j] = schoolIdiosyncraticComponent[i][j];
				// System.out.println(schoolCommonR + ":"+schoolUtility[i][j]);
			}
		}
		/* 学校の優先順序（utilityの大きな学校順，タイはインデックス順でブレーク）を生成 */
		for (int i = 0; i < NumOfSchools; i++) {
			preferenceOfschools[i][0] = 0;
			for (int j = 1; j < NumOfStudents; j++) {
				for (int z = j; z > 0; z--) {
					if (schoolUtility[i][preferenceOfschools[i][z - 1]] < schoolUtility[i][j]) {
						preferenceOfschools[i][z] = preferenceOfschools[i][z - 1];
						preferenceOfschools[i][z - 1] = j;
					} else {
						preferenceOfschools[i][z] = j;
						break;
					}
				}
			}
		}
		
		/* 学校の優先順序（utilityの大きな学校順，タイはインデックス順でブレーク）を生成  (ESDA用)*/
		for (int i = 0; i < NumOfSchools; i++) {
			ESDApreferenceOfSchools[i][0] = 0;
			for (int j = 1; j < NumOfStudents; j++) {
				for (int z = j; z > 0; z--) {
					if (schoolUtility[i][preferenceOfschools[i][z - 1]] < schoolUtility[i][j]) {
						ESDApreferenceOfSchools[i][z] = ESDApreferenceOfSchools[i][z - 1];
						ESDApreferenceOfSchools[i][z - 1] = j;
					} else {
						ESDApreferenceOfSchools[i][z] = j;
						break;
					}
				}
			}
		}
		
		for (int i = 0; i < NumOfSchools; i++) {
			ESDApreferenceOfSchools[i + NumOfSchools] = ESDApreferenceOfSchools[i];
		}
		

		/* MLの設定 */
		masterList = new int[NumOfStudents];
		for (int i = 0; i < NumOfStudents; i++) {
			masterList[i] = i;//インデックス順
		}
		
		schoolMasterList = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++) {
			schoolMasterList[i] = i;
		}
//		this.setSchoolMasterlist();
	}
	
	/**
	 * schoolMaster listを設定
	 */
	public void setSchoolMasterlist(){
		int rank;
		int priorityListNum = 0;
		for(int i=0;i<NumOfStudents;i++){
			rank = i;
			for(int j=0;j < NumOfSchools;j++){
				int school = j;
				int student = this.getPreferenceOfSchools()[school][rank];
				schoolMasterList[priorityListNum] = school;

				priorityListNum++;
			}
		}
//		for(int j = 0 ; j < numberOfSchools ; j++){
//			for(int i = 0 ; i < numberOfStudents ; i++){
//				System.out.println("priorityMatrix["+j+"]["+i+"]="+priorityMatrix[j][i]);
//			}
//		}
	}
	
	/**
	 * 学生の人数を取得
	 */
	public int getNumOfStudents() {
		return this.NumOfStudents;
	}

	/**
	 * 学校の数を取得
	 */
	public int getNumOfSchools() {
		return this.NumOfSchools;
	}
	
	/**
	 * Ratioを取得
	 */
	public double getRatio(){
		return this.Ratio;
	}
	
	/**
	 * Difference Constraintの値を取得
	 */
	public int getDif(){
		return this.Dif;
	}
	
	/**
	 * MaxQuotasを取得
	 */
	public int getMaxQuotas(){
		return this.MaxQuotas;
	}
	
	/**
	 * MinQuotasを取得
	 */
	public int getMinQuotas(){
		return this.MinQuotas;
	}
	
	/**
	 * Manhattan Constraintの値を取得
	 */
	public int getManhattan(){
		return this.Manhattan;
	}


	/**
	 * 学生の選好を返す
	 */
	public int[][] getPreferenceOfStudents() {
		return this.preferenceOfStudents;
	}
	
	/**
	 * ESDAの学生の選好を返す
	 */
	public int[][] getESDAPreferenceOfStudents() {
		return this.ESDApreferenceOfStudents;
	}
	
	/**
	 * ESDAの学校の選好を返す
	 */
	public int[][] getESDAPreferenceOfSchools() {
		return this.ESDApreferenceOfSchools;
	}


	/**
	 * 学校の選好を返す
	 */
	public int[][] getPreferenceOfSchools() {
		return this.preferenceOfschools;
	}
	
	/**
	 * 学校schoolのArtificialCapを返す
	 */
	public int getArtificialCap(int school){
		return this.ArtificialCap[school];
	}
	
	/**
	 * 学校schoolのQRDACapを返す
	 */
	public int getQRDACap(int school){
		return this.QRDACap[school];
	}
	
	/**
	 * 学校schoolのADACapを返す
	 */
	public int getADACap(int school){
		return this.ADACap[school];
	}

	/**
	 * 学校schoolの選好において，学生studentは何番目(0~NumOfStudents-1)に好むかを返す
	 * ※そもそも選好上に存在しなかったら-1を返す（今回の設定ではこれは起こらない）
	 */
	public int getPreferenceSchoolToStudent(int school, int student) {
		int preferenceNum = -1;

		for (int i = 0; i < NumOfStudents; i++) {
			if (preferenceOfschools[school][i] == student) {
				preferenceNum = i;
				break;
			}
		}
		return preferenceNum;
	}
	
	/**
	 * 学校schoolの選好において，学生studentは何番目(0~NumOfStudents-1)に好むかを返す
	 * ※そもそも選好上に存在しなかったら-1を返す（今回の設定ではこれは起こらない）
	 * (ESDA用)
	 */
	public int getESDAPreferenceSchoolToStudent(int school, int student) {
		int preferenceNum = -1;

		for (int i = 0; i < NumOfStudents; i++) {
			if (ESDApreferenceOfSchools[school][i] == student) {
				preferenceNum = i;
				break;
			}
		}
		return preferenceNum;
	}

	/**
	 * 学生studentの選好において，学校schoolは何番目(0~NumOfSchools-1)に好むかを返す
	 * ※そもそも選好上に存在しなかったら-1を返す（今回の設定ではこれは起こらない）
	 */
	public int getPreferenceStudentToSchool(int student, int school) {
		int preferenceNum = -1;

		for (int i = 0; i < NumOfSchools; i++) {
			if (preferenceOfStudents[student][i] == school) {
				preferenceNum = i;
				break;
			}
		}
		return preferenceNum;
	}

	/**
	 * MLを返す
	 */
	public int[] getMasterList() {
		return this.masterList;
	}
	
	/**
	 * 学校のマスターリスト（タイブレイク）を返す
	 */
	public int[] getSchoolMasterList(){
		return this.schoolMasterList;
	}
	
	/**
	 * 学生の選好を表示
	 */
	public void dispPreferenceOfStudents() {
		for (int i = 0; i < NumOfStudents; i++) {
			System.out.print("student " + i + ":");
			for (int j = 0; j < NumOfSchools; j++) {
				System.out.print(preferenceOfStudents[i][j] + " ");
			}
			System.out.println("");
		}
	}

	/**
	 * 学生studentの選好を表示
	 */
	public void dispPreferenceOfStudent(int student) {
		System.out.print("student " + student + ":");
		for (int j = 0; j < NumOfSchools; j++) {
			System.out.print(preferenceOfStudents[student][j] + " ");
		}
		System.out.println("");

	}

	/**
	 * 学校の選好を表示
	 */
	public void dispPreferenceOfSchools() {
		for (int i = 0; i < NumOfSchools; i++) {
			System.out.print("school " + i + ":");
			for (int j = 0; j < NumOfStudents; j++) {
				System.out.print(preferenceOfschools[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	// most balancedをACとする
	public void SetFeasibleQuotas(){
		for (int i = 0; i < NumOfSchools; i++) {
			if (i < NumOfSchools - (NumOfStudents % NumOfSchools)) {
				FeasibleQuotas[i] = (int)Math.floor((double)NumOfStudents/(double)NumOfSchools);
			} else {
				FeasibleQuotas[i] = (int)Math.ceil((double)NumOfStudents/(double)NumOfSchools);
			}
		}
//		System.out.println(Arrays.toString(FeasibleQuotas));
	}
}
	