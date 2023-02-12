package Main;

/**
 * ���𐶐�����N���X
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Problem implements Cloneable {

	/* �ϐ��錾 */
	private int NumOfStudents; // �w����
	private int NumOfSchools; // �w�Z��
	private double Ratio; // Relative Constraint
	private int Dif; // Difference Constraint
	private int Manhattan; // Manhattan Constraint
	private int MaxQuotas; // ESDA
	private int MinQuotas; // ESDA
	
	//ArtificialCap�����߂邽�߂ɗp����l
	private int[] FeasibleQuotas;
	
	// ACDA���p����l�דI�ȏ��
	private int[] ArtificialCap;
	
	// QRDA�������l�Ƃ��ėp������
	private int[] QRDACap;
	
	// ADA���p������
	private int[] ADACap;

	// �w���̑I�D preferenceOfStudent[i][j] = �w��i��j�ԖڂɍD���Ȋw�Z�ƃ^�C�v�̒ʂ��ԍ�
	private int[][] preferenceOfStudents;
	
	// ESDA�p��preference
	private int[][] ESDApreferenceOfStudents;
	
	// ESDA�p��preference
	private int[][] ESDApreferenceOfSchools;
	
	//�w�Z�̑I�D  preferenceOfschools[i][j] = �w�Zi��j�ԖڂɍD���Ȋw���̔ԍ�
	private int[][] preferenceOfschools;

	//�w���̃}�X�^�[���X�gML(SD,ADA,Random�ŗp����) masterList[i] = �}�X�^�[���X�g��i�Ԗڂ̊w��
	private int[] masterList;
	
	// �w�Z�̃}�X�^�[���X�g schoolMasterList[i] = �}�X�^�[���X�g��i�Ԗڂ̊w�Z
	private int[] schoolMasterList; 
	
	/** �ǂݍ��ޖ�肪�i�[����Ă���t�H���_�p�X = �������������i�[����t�H���_�p�X */
	private static String DIR_NAME = "C:\\MatchingData\\MatchingProblemWithRelativeConstraintDATA\\";

	/* �����̏����� */
	Random rnd = new Random();

	/**
	 * ���쐬
	 */
	public void makeProblem(int students, int schools, int instanceNum, int makeMode,
			double studentCommonR, int maxquotas, int minquotas, int dif, int manhattan) {

		/* �w�����E�w�Z���̐ݒ�  Main����̃p�����[�^���󂯎��*/
		NumOfStudents = students;
		NumOfSchools = schools;
		MaxQuotas = maxquotas;
		MinQuotas = minquotas;
		Dif = dif;
		Manhattan = manhattan;
		
		
		/* ACDA���p����ArtificialCap�̐ݒ� */
		FeasibleQuotas = new int[NumOfSchools];
		SetFeasibleQuotas();
		ArtificialCap = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++) {
			ArtificialCap[i] = FeasibleQuotas[i];//ArtificialCap��FeasibleQuotas�Ɠ���������
		}
		
		/* QRDA���p����QRDACap�̐ݒ� */
		QRDACap = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++) {
			QRDACap[i] = NumOfStudents;//QRDA�̃X�^�[�g�ł���QRDACap�͊w�����Ƃ���
		}
		
		// �I�D�̐ݒ�
		preferenceOfStudents = new int[NumOfStudents][NumOfSchools];
		ESDApreferenceOfStudents = new int[NumOfStudents][NumOfSchools * 2];
		ESDApreferenceOfSchools = new int[NumOfSchools * 2][NumOfStudents];
		preferenceOfschools = new int[NumOfSchools][NumOfStudents];

		int[] commonComponent = new int[NumOfSchools];
		int[][] idiosyncraticComponent = new int[NumOfStudents][NumOfSchools];
		int[][] schoolIdiosyncraticComponent = new int[NumOfSchools][NumOfStudents];
		
		if (makeMode > 0) {
			// ����V���ɍ쐬

			/* �w����component��ݒ� */
			for (int i = 0; i < NumOfSchools; i++) {
				commonComponent[i] = rnd.nextInt(NumOfSchools);//0~NumOfSchools-1�̐����������_���ɐݒ�
			}

			for (int i = 0; i < NumOfStudents; i++) {
				for (int j = 0; j < NumOfSchools; j++) {
					idiosyncraticComponent[i][j] = rnd
							.nextInt(NumOfSchools);//0~NumOfSchools-1�̐����������_���ɐݒ�
				}
			}

			/* �w�Z��component��ݒ� */
			for (int i = 0; i < NumOfSchools; i++) {
				for (int j = 0; j < NumOfStudents; j++) {
					schoolIdiosyncraticComponent[i][j] = rnd
							.nextInt(NumOfStudents);//0~NumOfStudents-1�̐����������_���ɐݒ�
				}
			}

			try {
				String filename;

				// �w����component�̏o��
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

				// �w�Z��component�̏o��
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
			// �����t�@�C������ǂݍ���

			try {
				String filename;

				// �w����component�̓ǂݍ���
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

				// �w�Z��component�̓ǂݍ���
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

		// studentCommonR�ɏ]���Ċw����utility���v�Z
		double[][] utility = new double[NumOfStudents][NumOfSchools];
		for (int i = 0; i < NumOfStudents; i++) {
			for (int j = 0; j < NumOfSchools; j++) {
				utility[i][j] = studentCommonR * commonComponent[j]
						+ (1.0 - studentCommonR) * idiosyncraticComponent[i][j];
				// System.out.println(studentCommonR + ":"+utility[i][j]);
			}
		}

		// �w���̑I�D�����iutility�̑傫�Ȋw�Z���C�^�C�̓C���f�b�N�X���Ńu���[�N�j�𐶐�
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
		
		// ESDA�p��extendedschool���܂߂�preference���쐬
		for (int i = 0; i < NumOfStudents; i++) {
			int p = 0;
//			System.out.println(Arrays.toString(preferenceOfStudents[i]));
			for (int j = 0; j < NumOfSchools; j++) {
				ESDApreferenceOfStudents[i][p] = preferenceOfStudents[i][j];
				ESDApreferenceOfStudents[i][p + 1] = preferenceOfStudents[i][j] + NumOfSchools;
				p = p + 2;
			}	
		}


		/* �w�Z��utility���v�Z */
		double[][] schoolUtility = new double[NumOfSchools][NumOfStudents];
		for (int i = 0; i < NumOfSchools; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				schoolUtility[i][j] = schoolIdiosyncraticComponent[i][j];
				// System.out.println(schoolCommonR + ":"+schoolUtility[i][j]);
			}
		}
		/* �w�Z�̗D�揇���iutility�̑傫�Ȋw�Z���C�^�C�̓C���f�b�N�X���Ńu���[�N�j�𐶐� */
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
		
		/* �w�Z�̗D�揇���iutility�̑傫�Ȋw�Z���C�^�C�̓C���f�b�N�X���Ńu���[�N�j�𐶐�  (ESDA�p)*/
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
		

		/* ML�̐ݒ� */
		masterList = new int[NumOfStudents];
		for (int i = 0; i < NumOfStudents; i++) {
			masterList[i] = i;//�C���f�b�N�X��
		}
		
		schoolMasterList = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++) {
			schoolMasterList[i] = i;
		}
//		this.setSchoolMasterlist();
	}
	
	/**
	 * schoolMaster list��ݒ�
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
	 * �w���̐l�����擾
	 */
	public int getNumOfStudents() {
		return this.NumOfStudents;
	}

	/**
	 * �w�Z�̐����擾
	 */
	public int getNumOfSchools() {
		return this.NumOfSchools;
	}
	
	/**
	 * Ratio���擾
	 */
	public double getRatio(){
		return this.Ratio;
	}
	
	/**
	 * Difference Constraint�̒l���擾
	 */
	public int getDif(){
		return this.Dif;
	}
	
	/**
	 * MaxQuotas���擾
	 */
	public int getMaxQuotas(){
		return this.MaxQuotas;
	}
	
	/**
	 * MinQuotas���擾
	 */
	public int getMinQuotas(){
		return this.MinQuotas;
	}
	
	/**
	 * Manhattan Constraint�̒l���擾
	 */
	public int getManhattan(){
		return this.Manhattan;
	}


	/**
	 * �w���̑I�D��Ԃ�
	 */
	public int[][] getPreferenceOfStudents() {
		return this.preferenceOfStudents;
	}
	
	/**
	 * ESDA�̊w���̑I�D��Ԃ�
	 */
	public int[][] getESDAPreferenceOfStudents() {
		return this.ESDApreferenceOfStudents;
	}
	
	/**
	 * ESDA�̊w�Z�̑I�D��Ԃ�
	 */
	public int[][] getESDAPreferenceOfSchools() {
		return this.ESDApreferenceOfSchools;
	}


	/**
	 * �w�Z�̑I�D��Ԃ�
	 */
	public int[][] getPreferenceOfSchools() {
		return this.preferenceOfschools;
	}
	
	/**
	 * �w�Zschool��ArtificialCap��Ԃ�
	 */
	public int getArtificialCap(int school){
		return this.ArtificialCap[school];
	}
	
	/**
	 * �w�Zschool��QRDACap��Ԃ�
	 */
	public int getQRDACap(int school){
		return this.QRDACap[school];
	}
	
	/**
	 * �w�Zschool��ADACap��Ԃ�
	 */
	public int getADACap(int school){
		return this.ADACap[school];
	}

	/**
	 * �w�Zschool�̑I�D�ɂ����āC�w��student�͉��Ԗ�(0~NumOfStudents-1)�ɍD�ނ���Ԃ�
	 * �����������I�D��ɑ��݂��Ȃ�������-1��Ԃ��i����̐ݒ�ł͂���͋N����Ȃ��j
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
	 * �w�Zschool�̑I�D�ɂ����āC�w��student�͉��Ԗ�(0~NumOfStudents-1)�ɍD�ނ���Ԃ�
	 * �����������I�D��ɑ��݂��Ȃ�������-1��Ԃ��i����̐ݒ�ł͂���͋N����Ȃ��j
	 * (ESDA�p)
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
	 * �w��student�̑I�D�ɂ����āC�w�Zschool�͉��Ԗ�(0~NumOfSchools-1)�ɍD�ނ���Ԃ�
	 * �����������I�D��ɑ��݂��Ȃ�������-1��Ԃ��i����̐ݒ�ł͂���͋N����Ȃ��j
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
	 * ML��Ԃ�
	 */
	public int[] getMasterList() {
		return this.masterList;
	}
	
	/**
	 * �w�Z�̃}�X�^�[���X�g�i�^�C�u���C�N�j��Ԃ�
	 */
	public int[] getSchoolMasterList(){
		return this.schoolMasterList;
	}
	
	/**
	 * �w���̑I�D��\��
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
	 * �w��student�̑I�D��\��
	 */
	public void dispPreferenceOfStudent(int student) {
		System.out.print("student " + student + ":");
		for (int j = 0; j < NumOfSchools; j++) {
			System.out.print(preferenceOfStudents[student][j] + " ");
		}
		System.out.println("");

	}

	/**
	 * �w�Z�̑I�D��\��
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
	
	// most balanced��AC�Ƃ���
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
	