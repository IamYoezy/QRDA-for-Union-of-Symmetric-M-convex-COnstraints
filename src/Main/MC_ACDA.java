package Main;

import java.util.Arrays;

/**
 * ArtificialCapDA�ŉ����N���X
 */
public class MC_ACDA {

	/* �ϐ��錾 */
	private int NumOfStudents;
	private int NumOfSchools;
	private int[][] preferenceOfStudents; // �w���̑I�D���i�[
	
	boolean disp_flag = false;//������e��\������ꍇ��true�Ƃ���
	

	/**
	 * �w���̊w�Z�ւ̃v���|�[�Y 
	 * proposeOfStudents[i] = �w��i���v���|�[�Y���Ă���w�Z
	 */
	private int[] proposeOfStudents;

	/**
	 * ���ۂ��ꂽ�w�Z 
	 * rejectedSchools[i][j] = �w��i�̊w�Zj�ɑ΂����� 
	 * NotRejected:0, Rejected:1
	 */
	private int[][] rejectedSchools;

	/**
	 * �w���̊�����Ԃ��i�[ 
	 * assignedStatus[i] = �w��i�̊������
	 * NotAssigned:0, Assigned:1
	 */
	private int[] assignedStatus;

	/**
	 * �w���̃v���|�[�Y���s��ŊǗ��D
	 *  proposeMatrix[i][j] = �w�Zi��j�ԖڂɍD�ފw���̃v���|�[�Y
	 */
	private int[][] proposeMatrix;
	
	/* �萔�錾 */
	private final int NOTREJECTED = 0; // ���ۂ���Ă��Ȃ�
	private final int REJECTED = 1; // ���ۂ��ꂽ

	private final int NOTSCHOOL = -1; //�w�����L���Ȋw�Z�ɐ\������ł��Ȃ����Ƃ��Ӗ�����
	private final int NOTSTUDENT = -1; //�w�Z�ɐ\������ł���w�������݂��Ȃ����Ƃ��Ӗ�����
	
	private final int NOTASSIGNED = 0; // ���蓖�Ă��Ă��Ȃ�
	private final int ASSIGNED = 1; // ���蓖�Ă��Ă���
	
	/**
	 * ACDA���s��
	 */
	public void solve(Problem prob, Assignment assign) {

		/* �ϐ��錾 */
		int rejectedStudents; // ���̃��E���h�ŋ��ۂ��ꂽ�w�������J�E���g
		int targetSchool;
		int targetStudent;
		int round;
		
		/* �����ݒ� */
		NumOfStudents = prob.getNumOfStudents();
		NumOfSchools = prob.getNumOfSchools();
		round = 0;
		rejectedStudents = NumOfStudents;
		preferenceOfStudents = new int[NumOfStudents][NumOfSchools];
		proposeOfStudents = new int[NumOfStudents];

		proposeMatrix = new int[NumOfSchools][NumOfStudents];


		// �I�D���擾
		preferenceOfStudents = prob.getPreferenceOfStudents();

		// ���ۂ��ꂽ�w�Z��������
		rejectedSchools = new int[NumOfStudents][NumOfSchools];
		for (int i = 0; i < NumOfStudents; i++) {
			for (int j = 0; j < NumOfSchools; j++) {
				rejectedSchools[i][j] = NOTREJECTED;
			}
		}

		/* �����J�n */

		// �͂������w�������Ȃ��Ȃ�܂Ń��[�v
		while (rejectedStudents > 0) {

			if(disp_flag) System.out.println("---round:"+round+"---");
			
			rejectedStudents = 0;

			/*
			 * �����̓��E���h�Ń��Z�b�g
			 */
			assign.init();

			// �w���̊����󋵂�������
			assignedStatus = new int[NumOfStudents];
			for (int i = 0; i < NumOfStudents; i++) {
				assignedStatus[i] = NOTASSIGNED;
			}

			// �w���̃v���|�[�Y�������
			setProposeOfStudents();
			setProposeMatrix(prob);

			if(disp_flag){
				for (int i = 0; i < NumOfStudents; i++) {
					System.out.println("student "+i+" is proposing to school "+proposeOfStudents[i]);
				}
			}
			
			// �S�w�Z�ɐU��ꂽ�w���̏����i����̐ݒ�ł͋N���Ȃ��j
			for (int i = 0; i < NumOfStudents; i++) {
				targetStudent = i;
				targetSchool = proposeOfStudents[i];
				if (targetSchool == NOTSCHOOL) {
					assign.setStudent(targetStudent, targetSchool);
					assignedStatus[targetStudent] = ASSIGNED;
				}
			}

			//�w�Z�̔ԍ����ɂ݂Ă���
			for(int j=0;j<NumOfSchools;j++){
				targetSchool = j;

				for(int i=0;i<NumOfStudents;i++){
					int rank = i;
					//rank�̊w�����v���|�[�Y���Ă��邩
					if(proposeMatrix[targetSchool][rank] != NOTSTUDENT){
						targetStudent = proposeMatrix[targetSchool][rank];

						//����ɒB���Ă��Ȃ��Ȃ犄�蓖�Ă�
						if(assign.getNumOfAssignStudents(targetSchool) < prob.getArtificialCap(targetSchool)){
							assign.setStudent(targetStudent, targetSchool);
							assignedStatus[targetStudent] = ASSIGNED;
						}
						//����ɒB���Ă���Ȃ�͂���
						else{
							rejectedSchools[targetStudent][targetSchool] = REJECTED;

							rejectedStudents ++;
						}
					}
				}
			}
			round++;
		}
		if (isNotSatisfyRatio(prob, assign)) {
			System.out.println("ACDA returns infeasible matches.");
		}

	}

	/**
	 * �w���̃v���|�[�Y���s��Őݒ�
	 */
	private void setProposeMatrix(Problem prob) {
		int targetStudent;
		int targetSchool;
		int preferenceNum;

		// �v���|�[�Y�s���������
		for (int i = 0; i < NumOfSchools; i++) {
			for (int j = 0; j < NumOfStudents; j++) {
				proposeMatrix[i][j] = NOTSTUDENT;//�����l��NOTSTUDENT
			}
		}

		for (int i = 0; i < NumOfStudents; i++) {
			targetStudent = i;
			targetSchool = proposeOfStudents[targetStudent];

			// �w����NOTSCHOOL�Ƀv���|�[�Y���Ă���ꍇ�i�S�Ă̊w�Z�ɐU��ꂽ�ꍇ�j�X�L�b�v
			if (targetSchool == NOTSCHOOL) {
				continue;
			}

			preferenceNum = prob.getPreferenceSchoolToStudent(targetSchool,
					targetStudent);
			proposeMatrix[targetSchool][preferenceNum] = targetStudent;
		}
	}

	/**
	 * �w�����v���|�[�Y����w�Z��ݒ�
	 */
	private void setProposeOfStudents() {
		int targetSchool;
		for (int i = 0; i < NumOfStudents; i++) {
			proposeOfStudents[i] = NOTSCHOOL;//�����l��NOTSCHOOL
		}

		// ���ׂĂ̊w����ݒ�
		for (int i = 0; i < NumOfStudents; i++) {
			// �f���Ă��Ȃ��w�Z��T��
			for (int j = 0; j < NumOfSchools; j++) {

				targetSchool = preferenceOfStudents[i][j];

				// �f���Ă��Ȃ��Ȃ�v���|�[�Y
				if (rejectedSchools[i][targetSchool] != REJECTED) {
					// ���łɊ��蓖�Ă��Ă��Ă�����school�Ƀv���|�[�Y��������
					proposeOfStudents[i] = targetSchool;
					break;
				}
			}
		}
	}
	
	/**
	 * ������ratio�Ɉᔽ���邩
	 */
	private boolean isNotSatisfyRatio(Problem prob, Assignment assign) {
		boolean flag = false;
		int copy_assign[];
		copy_assign = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++){
			copy_assign[i] = assign.getNumOfAssignStudents(i);
		}
		// �������̔z��������\�[�g
		Arrays.sort(copy_assign);
		// ������ratio�Ɉᔽ���邩
		if ((double) copy_assign[0]/copy_assign[NumOfSchools-1] < prob.getRatio()){
				flag = true;//ratio�Ɉᔽ
		}
		return flag;
	}

}
