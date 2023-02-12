package Main;

import java.util.Arrays;

/**
 * QuotaReductionDA�ŉ����N���X
 */
public class MC_QRDA_Dif {

	/* �ϐ��錾 */
	private int NumOfStudents;
	private int NumOfSchools;
	private int Dif;
	private int[][] preferenceOfStudents; // �w���̑I�D���i�[
	
	boolean disp_flag = false; //������e��\������ꍇ��true�Ƃ���
	

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
	 * QRDA���s��
	 */
	public void solve(Problem prob, Assignment assign) {

		/* �ϐ��錾 */
		int rejectedStudents; // �e���E���h�ŋ��ۂ��ꂽ�w�������J�E���g
		int targetSchool;
		int targetStudent;
		int stage;
		int round;
		int[] QuotaInEachStage; //�e�X�e�[�W�ŗp�������l
		
		/* �����ݒ� */
		NumOfStudents = prob.getNumOfStudents();
		NumOfSchools = prob.getNumOfSchools();
		Dif = prob.getDif();
		preferenceOfStudents = new int[NumOfStudents][NumOfSchools];
		proposeOfStudents = new int[NumOfStudents];
		proposeMatrix = new int[NumOfSchools][NumOfStudents];
		QuotaInEachStage = new int[NumOfSchools];
		stage = 0;
				
		// �I�D���擾
		preferenceOfStudents = prob.getPreferenceOfStudents();

		while(true) {//�X�e�[�W�ɂ��Ẵ��[�v
			
			if(disp_flag) System.out.println("---stage:"+stage+"---");
			
			//���̃X�e�[�W�ɂ���������ݒ�i�w�Z�̃C���f�b�N�X���ɂȂׂČ��炵�Ă����j			
			for (int i = 0; i < NumOfSchools; i++) {
				QuotaInEachStage[i] = prob.getQRDACap(i) - (stage + NumOfSchools - i -1)/ NumOfSchools;
				if(disp_flag) System.out.println("quota of school "+i+" = "+QuotaInEachStage[i]); 
			}
			
			/* �ȍ~�CArtificialCap�ȊOACDA�ƈ�v */
			
			// ���ۂ��ꂽ�w����������
			rejectedStudents = NumOfStudents;
			
			// ���ۂ��ꂽ�w�Z��������
			rejectedSchools = new int[NumOfStudents][NumOfSchools];
			for (int i = 0; i < NumOfStudents; i++) {
				for (int j = 0; j < NumOfSchools; j++) {
					rejectedSchools[i][j] = NOTREJECTED;
				}
			}
	
			/* �����J�n */
	
			round = 0;
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
							if(assign.getNumOfAssignStudents(targetSchool) < QuotaInEachStage[targetSchool]){
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
			
			if(!isNotSatisfyDif(prob, assign)){//Difference Constraint�Ɉᔽ���Ȃ��Ȃ�C�I��
				if (disp_flag) System.out.println("Last Quota : " + Arrays.toString(QuotaInEachStage));
				break;
			}
			stage++;
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
	 * ������DifferenceConstraint�Ɉᔽ���邩
	 */
	private boolean isNotSatisfyDif(Problem prob, Assignment assign) {
		boolean flag = false;
		int copy_assign[];
		copy_assign = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++){
			copy_assign[i] = assign.getNumOfAssignStudents(i);
		}
		// �������̔z��������\�[�g
		Arrays.sort(copy_assign);
		// ������ratio�Ɉᔽ���邩
		if (copy_assign[NumOfSchools - 1] - copy_assign[0] > prob.getDif()){
				flag = true;//dif constraint�Ɉᔽ
		}
		return flag;
	}

}
