package Main;

import java.util.Arrays;

/**
 * ������͂���N���X
 */
public class AnalysisAssignment {

	boolean disp_envy_flag = false;//envy��\������ꍇ��true�Ƃ���
	boolean disp_claim_flag = false;//claim��\������ꍇ��true�Ƃ���
	
	private Problem prob; // ���̃C���X�^���X
	private Assignment assign; // ����
	private int NumOfStudents; // �w����
	private int NumOfSchools; // �w�Z��
	private int Difference; // difference constraint
	
	/**
	 * ���Ԗڂɖ]�܂����w�Z�ɔz�����ꂽ��
	 *  welfare[i] = i�ԖڂɍD�ފw�Z�Ɋ��蓖�Ă��Ă���w����
	 */
	private int[] welfare;
	private int[] welfareCumulative; // welfare�̗ݐϕ��z

	private int NumOfClaimingStudents;
	private int NumOfClaims;
	private int NumOfStudentsWithEnvy;
	
	/* �萔�錾 */
	private final int NOTASSIGNED = -1;
	
	/**
	 * AnalysisAssignment���Ăяo���ꂽ�Ƃ��Ɏ��s
	 */
	public AnalysisAssignment(Problem pr, Assignment as) {
	
		// �����ݒ�
		this.prob = pr;
		this.assign = as;
		NumOfStudents = prob.getNumOfStudents();
		NumOfSchools = prob.getNumOfSchools();
		Difference = prob.getDif();
		
		// ������
		welfare = new int[NumOfSchools];
		for(int i=0; i<NumOfSchools; i++){
			welfare[i] = 0;
		}

		welfareCumulative = new int[NumOfSchools];
		for(int i=0; i<NumOfSchools; i++){
			welfareCumulative[i] = 0;
		}
		
		// �e�f�[�^���Z�b�g
		this.setClaimingStudents();
		this.setStudentsWithEnvy();
		this.setWelfare();
		this.setWelfareCumulative();
	}

	/**
	 * envy �̌v�Z
	 */
	private void setStudentsWithEnvy() {
		//�w����envy�������ǂ����̃t���O
		boolean[] envyFlag = new boolean[NumOfStudents];
		//������
		for (int i = 0; i < NumOfStudents; i++) {
			envyFlag[i] = false;
		}
		//���ׂĂ̊w���𒲂ׂ�
		for (int i = 0; i < NumOfStudents; i++) {
			int student = i;
			int currentSchool = assign.getAssignedSchool(student);
			int currentRank = prob.getPreferenceStudentToSchool(student,
					currentSchool);
			//�����蓖�Ă��Ă���w�Z���D�ފw�Z�𒲂ׂ�
			for (int j = 0; j < currentRank; j++) {
				int targetSchool = prob.getPreferenceOfStudents()[student][j];
				//���D�ފw�Z�Ɋ��蓖�Ă��Ă���w���S�Ă𒲂ׂ�
				for (int m = 0; m < assign.getNumOfAssignStudents(targetSchool); m++) {
					int targetStudent = assign.getAssignedStudnets(targetSchool,m);

					int studentRank = prob.getPreferenceSchoolToStudent(
							targetSchool, student);
					int targetStudentRank = prob
							.getPreferenceSchoolToStudent(targetSchool,
									targetStudent);
					// �u�w��i��j <
					// currentRank�ԖڂɍD���Ȋw�ZtargetSchool�v�̗D�揇���ɂ��āCtargetSchool�Ɋ��蓖�Ă��Ă���w��targetStudent���C�w��i�������Ă���
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
	 * claim �̌v�Z
	 */
	private void setClaimingStudents() {
		//�w����claim�������ǂ����̃t���O
		boolean[] claimingStudentFlag = new boolean[NumOfStudents];
		//������
		NumOfClaims = 0;
		int order = 0;
		for (int i = 0; i < NumOfStudents; i++) {
			claimingStudentFlag[i] = false;
		}
		//���ׂĂ̊w���𒲂ׂ�
		for (int i = 0; i < NumOfStudents; i++) {
			boolean flag = false;
			int student = i;
			int currentSchool = assign.getAssignedSchool(student);
			int currentRank = prob.getPreferenceStudentToSchool(student,
					currentSchool);
			for (int j = 0; j < currentRank; j++) {
				//�����蓖�Ă��Ă���w�Z���D�ފw�Z�𒲂ׂ�
				int targetSchool = prob.getPreferenceOfStudents()[student][j];
				if (Difference == -1) {
					// Flexible uniform min/max constraint�̂Ƃ�
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
					// difference constraint�̂Ƃ�
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
	 * �V���Ȋ�����difference constraint�Ɉᔽ���邩(claim�̔���ɗp����)
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
		// �������̔z��������\�[�g
		Arrays.sort(copy_assign);
		// �������ᔽ���邩
		if (copy_assign[NumOfSchools - 1] - copy_assign[0] > prob.getDif()){
			flag = true;//�ᔽ
		}
		return flag;			

	}
	
	/**
	 * �V���Ȋ�����flexible constraint�Ɉᔽ���邩(claim�̔���ɗp����)
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
		// �������̔z��������\�[�g
		Arrays.sort(copy_assign);
		// �������ᔽ���邩
		if ((copy_assign[0] < prob.getMinQuotas() || copy_assign[NumOfSchools - 1] > prob.getMaxQuotas()) && CalcManhattan(prob, assign) > prob.getManhattan()){
			flag = true;//�ᔽ
		}
		return flag;			

	}
	
	/**
	 * �}���n�b�^���������v�Z
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
	 * �w�������Ԗڂɖ]�܂����w�Z�Ɋ��蓖�Ă�ꂽ���̕��z
	 */
	private void setWelfare() {
		int assignedSchool; // �w�������蓖�Ă��Ă���w�Z
		int preferNum; // ���蓖�Ă��Ă���w�Z�͑I�D�ŉ��Ԗڂ�

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
	 * welfare�̗ݐϕ��z�D (welfare���Q�Ƃ��ĎZ�o����̂ŁCsetWelfare�̌�ɌĂԂ���)
	 */
	private void setWelfareCumulative() {
		welfareCumulative[0] = welfare[0];
		for (int i = 1; i < prob.getNumOfSchools(); i++) {
			welfareCumulative[i] = welfare[i] + welfareCumulative[i - 1];
		}
	}

	/**
	 * claim�����w���̐l����Ԃ�
	 */
	public int getNumOfClaimingStudents() {
		return this.NumOfClaimingStudents;
	}

	/**
	 * claim�̌�����Ԃ�
	 */
	public int getNumOfClaims() {
		return this.NumOfClaims;
	}
	
	/**
	 * envy�����w���̐l����Ԃ�
	 */
	public int getNumOfStudentsWithEnvy() {
		return this.NumOfStudentsWithEnvy;
	}
	
	/**
	 * welfare��Ԃ�
	 */
	public int[] getWelfare() {
		return this.welfare;
	}

	/**
	 * welfare�̗ݐϕ��z��Ԃ�
	 */
	public int[] getWelfareCumulative() {
		return this.welfareCumulative;
	}
	
	/**
	 * �w���̃{���_�X�R�A�̕��ς�Ԃ�
	 * 
	 * �w����0�ʂ̊w�Z�ɂ���FBordaScore = m
	 * �w����1�ʂ̊w�Z�ɂ���FBordaScore = m-1
	 * �w����m-1�ʂ̊w�Z�ɂ���FBordaScore = 1
	 * �w�����ǂ̊w�Z�ɂ����蓖�Ă��Ă��Ȃ��FBordaScore = 0
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
	 * Envy�������Ȃ��w���̊�����Ԃ�
	 */
	public double getRatioOfStudentsWithoutEnvy() {
		return 1.0 - (double)NumOfStudentsWithEnvy/NumOfStudents;
	}
	
	/**
	 * Claim�������Ȃ��w���̊�����Ԃ�
	 */
	public double getRatioOfStudentsWithoutClaim() {
		return 1.0 - (double)NumOfClaimingStudents/NumOfStudents;
	}
}
