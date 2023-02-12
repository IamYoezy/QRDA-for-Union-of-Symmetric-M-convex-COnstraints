package Main;

import java.util.Arrays;

/**
 * ���C���N���X
 */

import java.math.BigDecimal;

public class Main {
	
	// ���W�f�[�^�̐ݒ�
	private static int NumOfData = 3;
	private static final int CLAIMING_STUDENTS = 0;
	private static final int STUDENT_WITH_ENVY = 1;
	private static final int NUMBER_OF_CLAIM = 2;
	private static String[] DATA_NAME = {"claimingStudents","studentsWithEnvy","numOfClaims"};
	
	// �f�[�^���W�v���邩�ǂ���
	private static boolean[] dataCheckFlag = new boolean[NumOfData];

	// �e�C���X�^���X���Ƃ̎�������
	private static int[][][] evaluationResult;
	// �S�C���X�^���X�̕���
	private static double[][] average;
	
	// �w���C�w�Z��welfare
	private static int[][][] studentWelfare;
	private static double[][] studentWelfareAve;
	private static String DATA_NAME_STUDENTWELFARE = "studentWelfare";

	// �U�z�}�ɗp����e�C���X�^���X���̌���	
	private static double[][] AveBordaScoreOfStudent; 
	private static double[][] RatioOfAveBordaScoreOfStudent;
	private static double[][] RatioOfStudentsWithoutClaim;
	private static double[][] RatioOfStudentsWithoutEnvy;
	private static String DATA_NAME_ScatterDiagram = "Borda_StudentsWithoutEnvy";
	
	// 2�̃��J�j�Y�����r���C�����ɗǂ����蓖�Č��ʂƂȂ����w�������L�^����
	private static int[][] WhichLike;
	private static double[] AveWhichLike;
	private static String DATA_NAME_WhichLike = "WhichLike";
	
	// �e��錾
	private static int instance;

	/**
	 * ���C���֐�
	 */
	public static void main(String[] args) {
		evaluation();
	}
	
	public static void evaluation() {
		// �t�@�C���̏o��
		FileOutput fio = new FileOutput();
	
		// ���͏��
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
		// �ϐ��i�����l��0�j
		for (int i = 0; i < NumOfParameters; i++) {
			parameter[i] = 0;
		}

		//---------- �ϐ��ݒ�i��{�I�ɂ̓R�R��������j-------------------- 
		parameter[STUDENTS] = 800;//�w����n
		parameter[SCHOOLS] = 20;//�w�Z��m
		parameter[DIFFERENCE] = 70;//Difference Constraint (�g�p���Ȃ��Ƃ���-1)
		parameter[MAXQUOTAS] = 106;//�ʏ�� (�S���̊w�Z����) (�g�p���Ȃ��Ƃ���-1)
		parameter[MINQUOTAS] = 36;//�ʉ��� (�S���̊w�Z����) (�g�p���Ȃ��Ƃ���-1)
		parameter[MANHATTAN] = -1;//Manhattan Constraint (�g�p���Ȃ��Ƃ���-1)
		//------------------------------------------------------------
		
		//---------- make mode �̐ݒ�i��{�I�ɂ̓R�R��������j-------------------- 
		//makeMode = 0:�ǂݍ��݁i����ǂݍ��݁C�����j
		//makeMode = 1:�����o���i���������o���̂݁j
		//�����̗���� [makeMode = 1�Ŏ��s] -> [makeMode = 0�Ŏ��s]
		int makeMode = 0;
		//---------------------------------------------------------------
		
		//---------- ��萔�̐ݒ�i��{�I�ɂ̓R�R��������j-------------------- 
		instance = 100;
		//------------------------------------------------------------
		
		//---------- �\���̐ݒ�i��{�I�ɂ̓R�R��������j-------------------- 
		boolean disp_flag_pref = true;//�I�D��\������ꍇ��true�Ƃ���
		boolean disp_flag_mech = true;//���J�j�Y���̌��ʂ�\������ꍇ��true�Ƃ���
		//------------------------------------------------------------
		
//		�R���\�[���ɕ\�������̂́C���i�����ꂽ"�I�D"
//		Problem�Ƃ��Đ������ꂽ��C��ł�����̂͊w�Z���w����"���p�l"
		
		parameter[STUDENT_CR] = 0.0;//�w���̑I�D�̑���alpha
		
		
		// �O���t�̉����Ƃ���ϐ��̐ݒ�
		boolean[] graphParameterFlag = new boolean[NumOfParameters];
		for (int i = 0; i < NumOfParameters; i++) {
			graphParameterFlag[i] = false;
		}
		graphParameterFlag[STUDENT_CR] = true;

		// PLT�𐶐����邩�ǂ���
		boolean makePltFlag = true;
		
		// ���J�j�Y����
		int NumOfAlgorithm = 2;

		// ���J�j�Y���̐ݒ�i�S��strategy-proof����feasible�ȃ��J�j�Y���j
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

		// ���ׂ�f�[�^�̐ݒ�
		for (int i = 0; i < NumOfData; i++) {
			dataCheckFlag[i] = false;
		}
		dataCheckFlag[CLAIMING_STUDENTS] = true;
		dataCheckFlag[STUDENT_WITH_ENVY] = true;
		dataCheckFlag[NUMBER_OF_CLAIM] = true;
		
		//welfare���o�͂��邩�ǂ����i�w���̑��ւ��Ƃɏo��j
		boolean Welfareflag = true;
		
		// �U�z�}�i�c���FRatioOfStudentsWithoutEnvy�C�����FRatioOfAveBordaScoreOfStudent�j���o�͂��邩�ǂ����i�w���̑��ւ��Ƃɏo��j
		boolean Scatterflag = false;
		
		//which like���o�͂��邩�ǂ���
		boolean WhichLikeflag = true;
		
		//Borda��claim��\�����邩
		boolean resultinformation = true; 
		
		//��r���������J�j�Y���̔ԍ����L�^
		int CompAlgNum_1, CompAlgNum_2;
		CompAlgNum_1 = 0;
		CompAlgNum_2 = 1;

		// �e��f�[�^�̏�����
		evaluationResult = new int[NumOfData][NumOfAlgorithm][instance];
		average = new double[NumOfData][NumOfAlgorithm];

		studentWelfare = new int[NumOfAlgorithm][instance][(int) parameter[SCHOOLS]];
		studentWelfareAve = new double[NumOfAlgorithm][(int) parameter[SCHOOLS]];	
		
		AveBordaScoreOfStudent = new double[NumOfAlgorithm][instance];
		RatioOfAveBordaScoreOfStudent = new double[NumOfAlgorithm][instance];
		RatioOfStudentsWithoutEnvy = new double[NumOfAlgorithm][instance];
		RatioOfStudentsWithoutClaim = new double[NumOfAlgorithm][instance];
		
		WhichLike = new int[instance][NumOfAlgorithm+1];//WhichLike[i][numOfAlgorithm]�ɂ́C���i�ɂ����āC�ǂ���̃��J�j�Y���ɂ����Ă��������ς��Ȃ������l�����L�^
		AveWhichLike = new double[NumOfAlgorithm+1];
		
		double BordaAve = 0;
		double ClaimerAve = 0;
		double ACDABordaAve = 0;
		double QRDABordaAve = 0;
		double ACDAClaimerAve = 0;
		double QRDAClaimerAve = 0;
		double ESDAClaimerAve = 0;
		
		BigDecimal bd;

		// �w���̑��ւɂ��ă��[�v
		for (int j = 0; j <= 0; j++) {//0�`10�̐���
			bd = new BigDecimal(j * 0.1);
			bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP); // ������Q��;
			parameter[STUDENT_CR] = bd.doubleValue();

			// ��萔�ɂ��ă��[�v
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
					System.out.println("instance " + i + ":");//���ԍ���\��
					prob.dispPreferenceOfStudents();//�w���̑I�D��\��
					prob.dispPreferenceOfSchools();//�w�Z�̑I�D��\��
				}
				
				if(makeMode != 1){
				
					// �e�A���S���Y���łƂ�
					for (int a = 0; a < NumOfAlgorithm; a++) {
						answer[a] = new Assignment((int) parameter[STUDENTS],
								(int) parameter[SCHOOLS]);
	
						/* ���� */
						mechanisms[a].init(answer[a], prob);
						if (a == 0) {
							mechanisms[a].solveWithQRDA_Dif();
							if(disp_flag_mech){
								System.out.println("<result of QRDA>");
								answer[a].dispAssignment();//�������ʂ�\��
							}							
						} else if (a == 1) {
							mechanisms[a].solveWithESDA();
							if(disp_flag_mech){
								System.out.println("<result of ESDA>");
								answer[a].dispAssignment();//�������ʂ�\��
							}
						} else if (a == 2) {
							mechanisms[a].solveWithACDA();
							if(disp_flag_mech){
								System.out.println("<result of ESDA>");
								answer[a].ESDAdispAssignment();//�������ʂ�\��
							}
						} else if (a == 3) {
							mechanisms[a].solveWithSD();
							if(disp_flag_mech){
								System.out.println("<result of SD>");
								answer[a].dispAssignment();//�������ʂ�\��
							}
						} else if (a == 4) {
							mechanisms[a].solveWithADA();
							if(disp_flag_mech){
								System.out.println("<result of ADA>");
								answer[a].dispAssignment();//�������ʂ�\��
							}
						} else if (a == 5) {
							mechanisms[a].solveWithRandom();
							if(disp_flag_mech){
								System.out.println("<result of Random>");
								answer[a].dispAssignment();//�������ʂ�\��
							}
						}
						// ��̓f�[�^�i�[
						analy[a] = new AnalysisAssignment(prob, answer[a]);
						

					}
					
					// claim��Theorem�̔���T��					
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

					// Borda�X�R�A�̃v���b�g�p
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

					// claiming�̃v���b�g�p
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

					
					// QRDA�������ɍD�ފw���̊����̃v���b�g�p
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
					

					// ��̓f�[�^�i�[
					for (int a = 0; a < NumOfAlgorithm; a++) {
						for (int d = 0; d < NumOfData; d++) {
							if (d == CLAIMING_STUDENTS) {
								evaluationResult[d][a][i] = analy[a]
										.getNumOfClaimingStudents();//claim�����w�������L�^
							}
							if (d == STUDENT_WITH_ENVY) {
								evaluationResult[d][a][i] = analy[a]
										.getNumOfStudentsWithEnvy();//envy�����w�������L�^
							}
							if (d == NUMBER_OF_CLAIM){
								evaluationResult[d][a][i] = analy[a]
										.getNumOfClaims();//claim�̌������L�^
							}
						}
						studentWelfare[a][i] = analy[a].getWelfareCumulative();//welfare�̗ݐϕ��z���L�^
						
						//[�w���̃{���_�X�R�A�̕���/m]���L�^
						RatioOfAveBordaScoreOfStudent[a][i] = analy[a].getAveBordaScoreOfStudent()/(int)parameter[SCHOOLS];
						//�w���̃{���_�X�R�A�̕��ς��L�^
						AveBordaScoreOfStudent[a][i] = analy[a].getAveBordaScoreOfStudent();
						//envy�����w�����̊������L�^
						RatioOfStudentsWithoutEnvy[a][i] = analy[a].getRatioOfStudentsWithoutEnvy();
						//claim�����w�����̊������L�^
						RatioOfStudentsWithoutClaim[a][i] = analy[a].getRatioOfStudentsWithoutClaim();
					
					}
					
					if(WhichLikeflag){
						//������̃��J�j�Y���Ɣ�r���C�����ɗǂ��w�Z�Ɋ��蓖�Ă�ꂽ�w�������L�^
						WhichLike[i] = getWhichLike(parameter[STUDENTS],prob,answer,NumOfAlgorithm,CompAlgNum_1,CompAlgNum_2);
					}
				}
			}
			
			if(makeMode != 1){
				// �S���̌��ʂ��W�v���ĕ��ς��v�Z
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
				
				// dataCheckFlag[d]=true�ƂȂ��Ă��鍀�ڂ̌��ʂ��o��
				for (int d = 0; d < NumOfData; d++) {
					if (dataCheckFlag[d]) {
						if(d == CLAIMING_STUDENTS || d == STUDENT_WITH_ENVY){
						//�w���̊���(����)���o��
						fio.outputRatioOfStudents(average[d], DATA_NAME[d],
								parameter, parameterName, graphParameterFlag,
								ALG_NAME, parameter[STUDENTS], makePltFlag);
						}else if(d == NUMBER_OF_CLAIM){
						//���̂܂܂̃f�[�^���o��
							fio.output(average[d], DATA_NAME[d],
									parameter, parameterName, graphParameterFlag,
									ALG_NAME, makePltFlag);
						}
					}
				}
				// welfare���o��
				if(Welfareflag){
					fio.outputCDF(studentWelfareAve, DATA_NAME_STUDENTWELFARE,
							parameter, parameterName, graphParameterFlag, ALG_NAME,
							makePltFlag);
				}
				
//				//��x[AveBordaScoreOfStudent] - �cy[RatioOfStudentsWithoutEnvy]�̎U�z�}�f�[�^���o��
//				if(Scatterflag){
//					fio.outputScatter(AveBordaScoreOfStudent,
//							RatioOfStudentsWithoutEnvy,
//							DATA_NAME_ScatterDiagram,
//							parameter, parameterName, graphParameterFlag,
//							ALG_NAME, makePltFlag);
//				}
				
				//��x[AveBordaScoreOfStudent/m] - �cy[RatioOfStudentsWithoutEnvy]�̎U�z�}�f�[�^���o��
				if(Scatterflag){
					fio.outputScatter(RatioOfAveBordaScoreOfStudent,
							RatioOfStudentsWithoutEnvy,
							DATA_NAME_ScatterDiagram,
							parameter, parameterName, graphParameterFlag,
							ALG_NAME, makePltFlag);
				}
				
				//��x[AveBordaScoreOfStudent/m] - �cy[RatioOfStudentsWithoutEnvy]�̎U�z�}�f�[�^���o��
//				if(Scatterflag){
//					fio.outputScatter(RatioOfStudentsWithoutClaim,
//							RatioOfStudentsWithoutEnvy,
//							DATA_NAME_ScatterDiagram,
//							parameter, parameterName, graphParameterFlag,
//							ALG_NAME, makePltFlag);
//				}
				// whichLike���o��
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
	 * ���ϒl���擾
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
	 * �w�肳�ꂽwelfare�̕��ϒl���擾
	 */
	private static double[] getWelfareAve(int[][] welfare, int schools,
			int students, int instance) {
		double[] average = new double[schools];

		for (int i = 0; i < schools; i++) {
			for (int j = 0; j < instance; j++) {
				average[i] += welfare[j][i];
			}
			average[i] /= instance; // �C���X�^���X�̕��ϒl
			average[i] = (average[i] / students); // �w�����Ŋ���
		}

		return average;
	}
	
	/**
	 * �����ɗǂ��w�Z�Ɋ��蓖�Ă�ꂽ�w�������L�^�����z���Ԃ�
	 * ���J�j�Y����+1�̔z���p�ӂ��C�������Ƃ����+1�D���ʂ����Ȃ��Ƃ��͔z��̍Ō�ɃJ�E���g
	 */
	private static int[] getWhichLike(double parameter, Problem prob, Assignment[] answer, int numOfAlgorithm, int A_1, int A_2){
		int[] NumOfWinner = new int[numOfAlgorithm + 1];
		NumOfWinner[A_1] = 0;
		NumOfWinner[A_2] = 0;

		for(int i = 0; i < parameter; i++){//�S�w���ɂ��Ă݂�
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
	 * WhichLike�̊������o���C���̕��ς�Ԃ�
	 */
	private static double getAveWhichLike(int[][] WhichLike,int instance,int Alg, double students){
		double average = 0;
		for (int i = 0; i < instance; i++) {
			average += WhichLike[i][Alg] / students;//WhichLike�Ɋւ���l���̊������o��
													//(WhichLike[i][Alg] ... ���i�ɂ����āC���J�j�Y��Alg�̕������ǂ��w�Z�Ɋ��蓖�Ă�ꂽ�w���̐�)
		}
		average /= instance;
		return average;
	}
}
