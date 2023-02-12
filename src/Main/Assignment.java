package Main;
import java.util.Arrays;
/**
 * �������i�[����N���X
 */
public class Assignment {

	/* �ϐ��錾 */
	private int NumOfStudents; // �w����
	private int NumOfSchools; // �w�Z��
	
	int[] ClaimStudents;
	int[] ACDAClaimer;
	int[] QRDAClaimer;

	/**
	 * �w�Z�̊������i�[ 
	 * assignment[i][j] = �w�Zi��j�ԖڂɊ��蓖�Ă��Ă���w��
	 */
	private int[][] assignment;
	private int[][] ESDAassignment;

	/**
	 * �w���̊������i�[ 
	 * assignmentOfStudents[i] = �w��i�̊��蓖�Ă��Ă���w�Z
	 */
	private int[] assignmentOfStudents;
	private int[] ESDAassignmentOfStudents;

	/* �萔�錾 */
	private final int NOTASSIGNED = -1;

	/**
	 * �R���X�g���N�^
	 */
	public Assignment(int students, int schools) {
		NumOfStudents = students;
		NumOfSchools = schools;

		// �����̏�����
		assignment = new int[NumOfSchools][NumOfStudents];
		for (int i = 0; i < NumOfSchools; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				assignment[i][j] = NOTASSIGNED;//�������Ȃ��Ȃ�NOTASSIGNED
			}
		}
		
		ESDAassignment = new int[NumOfSchools * 2][NumOfStudents];
		for (int i = 0; i < NumOfSchools * 2; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				ESDAassignment[i][j] = NOTASSIGNED;//�������Ȃ��Ȃ�NOTASSIGNED
			}
		}

		//�w���̊�����������
		assignmentOfStudents = new int[NumOfStudents];
		for (int i = 0; i < NumOfStudents; i++) {
			assignmentOfStudents[i] = NOTASSIGNED;//�������Ȃ��Ȃ�NOTASSIGNED
		}
		
		ESDAassignmentOfStudents = new int[NumOfStudents];
		for (int i = 0; i < NumOfStudents; i++) {
			ESDAassignmentOfStudents[i] = NOTASSIGNED;//�������Ȃ��Ȃ�NOTASSIGNED
		}
		
		ClaimStudents = new int[NumOfStudents];
		ACDAClaimer = new int[NumOfStudents];
		QRDAClaimer = new int[NumOfStudents];
		
		for (int i = 0; i < NumOfStudents; i++) {
			ClaimStudents[i] = 0;
		}
	}

	/**
	 * �w�����w�Z�Ɋ��蓖�Ă�
	 * ��school = -1�Ȃ牽�����Ȃ�
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
	 * �w�����w�Z�Ɋ��蓖�Ă� (ESDA�p)
	 * ��school = -1�Ȃ牽�����Ȃ�
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
	
	// ESDA�Ŏg����������standard-school�ɂ܂Ƃ߂Ă�����assignment�Ɋi�[����
	public void ESDAconvert(){
		for (int i = 0; i < NumOfSchools; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				assignment[i][j] = ESDAassignment[i][j];//�������Ȃ��Ȃ�NOTASSIGNED
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
	 * �w������������O��
	 */
	public void removeStudent(int student, int school){
		boolean findStudentFlag = false;

		//�w���̊������O��
		assignmentOfStudents[student] = NOTASSIGNED;

		//�w�Z�̊������O��
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
	 * ���蓖�Ă��Ă���w�������擾
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
	 * ���蓖�Ă��Ă���w�������擾 (ESDA�p)
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
	 * Extendedschool�S�̂Ɋ��蓖�Ă��Ă���w�������擾
	 */
	public int getNumOfExtendedStudents(){
		int counter = 0;

		for(int i=NumOfSchools; i<NumOfSchools * 2; i++){
			counter += ESDAgetNumOfStudents(i);
		}
		return counter;
	}

	/**
	 * �S�̂Ŋ��蓖�Ă��Ă���w�������擾
	 */
	public int getNumOfAssignedStudentsAll(){
		int counter = 0;

		for(int i=0; i<NumOfSchools; i++){
			counter += getNumOfAssignStudents(i);
		}
		return counter;
	}

	/**
	 * school��n�ԖڂɊ��蓖�Ă��Ă���w�����擾
	 */
	public int getAssignedStudnets(int school, int n) {
		return assignment[school][n];
	}

	/**
	 * �w���̊��蓖�Ă��Ă���w�Z��Ԃ�
	 */
	public int getAssignedSchool(int student) {
		return assignmentOfStudents[student];
	}
	
	/**
	 * �w���̊��蓖�Ă��Ă���w�Z��Ԃ� (ESDA�p)
	 */
	public int ESDAgetAssignedSchool(int student) {
		return ESDAassignmentOfStudents[student];
	}

	/**
	 * �����̏�����
	 */
	public void init() {
		// �����̏�����
		for (int i = 0; i < NumOfSchools; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				assignment[i][j] = NOTASSIGNED;
			}
		}

		// �w���̊�����������
		for (int i = 0; i < NumOfStudents; i++) {
			assignmentOfStudents[i] = NOTASSIGNED;
		}
	}
	
	/**
	 * �����̏����� ESDA�p
	 */
	public void ESDAinit() {
		// �����̏�����
		for (int i = 0; i < NumOfSchools * 2; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				ESDAassignment[i][j] = NOTASSIGNED;
			}
		}

		// �w���̊�����������
		for (int i = 0; i < NumOfStudents; i++) {
			ESDAassignmentOfStudents[i] = NOTASSIGNED;
		}
	}

	/**
	 * �w�Z���̊����̕\��
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
	 * �w�Z���̊����̕\�� (ESDA�p)
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
	 * Claim�����w���̕\��
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
	 * ACDA��Claim�����w����ۑ�
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
	 * QRDA��Claim�����w����ۑ�
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
	 * QRDA��Claim��������ACDA�Ŏ����Ȃ��l�����邩�`�F�b�N
	 */
	public void Claimcheck(){
		for(int i = 0; i < NumOfStudents; i++){
			if (QRDAClaimer[i] != 0 && Arrays.asList(ACDAClaimer).contains(QRDAClaimer[i])) {
				System.out.println("I found it!\n");
			}
		}
	}
	
	/**
	 * �w�����̊����̕\��
	 */
	public void dispAssignmentOfStudents(){
		for(int i=0; i<NumOfStudents; i++){
			System.out.println("student "+i+":"+assignmentOfStudents[i]+" ");
		}
	}
	
	/**
	 * ���̊����ɕʂ�Assignment�̊w����������
	 * @param addAssign: ������ʂ̊���
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
