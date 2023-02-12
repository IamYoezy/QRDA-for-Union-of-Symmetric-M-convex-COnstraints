package Main;

import java.util.Arrays;

/**
 * ExtendedSeatDA�ŉ����N���X
 * Extended-school�̊w����standard-school�ɂ܂Ƃ߂�
 */
public class MC_ESDA2 {

	/* �ϐ��錾 */
	private int NumOfStudents;
	private int NumOfSchools;
	private int MaxQuotas;
	private int MinQuotas;
	private int DifQuotas;
	private int Regionale;
	private int[][] preferenceOfStudents; // �w���̑I�D���i�[
	private int[][] preferenceOfSchools; // �f�o�b�O�p	
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
	
	// extended-school�Ɋ��蓖�Ă�ꂽ�w����ۑ�
	private int[] extendedstudents;
	
	/* �萔�錾 */
	private final int NOTREJECTED = 0; // ���ۂ���Ă��Ȃ�
	private final int REJECTED = 1; // ���ۂ��ꂽ

	private final int NOTSCHOOL = -1; //�w�����L���Ȋw�Z�ɐ\������ł��Ȃ����Ƃ��Ӗ�����
	private final int NOTSTUDENT = -1; //�w�Z�ɐ\������ł���w�������݂��Ȃ����Ƃ��Ӗ�����
	
	private final int NOTASSIGNED = 0; // ���蓖�Ă��Ă��Ȃ�
	private final int ASSIGNED = 1; // ���蓖�Ă��Ă���
	
	/**
	 * ESDA���s��
	 */
	public void solve(Problem prob, Assignment assign) {

		/* �ϐ��錾 */
		int rejectedStudents; // �e���E���h�ŋ��ۂ��ꂽ�w�������J�E���g
		int targetSchool;
		int targetStudent;
		int stage;
		
		/* �����ݒ� */
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
		
		// ������
		for (int i = 0; i < NumOfStudents; i++) {
			extendedstudents[i] = -1;
		}
				
		// �I�D���擾
		preferenceOfStudents = prob.getESDAPreferenceOfStudents();
		preferenceOfSchools = prob.getESDAPreferenceOfSchools();
		
//		System.out.println("p" + MinQuotas);
//		System.out.println("q" + MaxQuotas);
		
		while(true) {//�X�e�[�W�ɂ��Ẵ��[�v
		
			// ���ۂ��ꂽ�w����������
			rejectedStudents = NumOfStudents;
			
			// ���ۂ��ꂽ�w�Z��������
			rejectedSchools = new int[NumOfStudents][NumOfSchools * 2];
			for (int i = 0; i < NumOfStudents; i++) {
				for (int j = 0; j < NumOfSchools * 2; j++) {
					rejectedSchools[i][j] = NOTREJECTED;
				}
			}
	
			/* �����J�n */
	
			stage = 0;
			// �͂������w�������Ȃ��Ȃ�܂Ń��[�v
			while (rejectedStudents > 0) {
	
				if(disp_flag) System.out.println("---stage:"+stage+"---");
				
				rejectedStudents = 0;
	
				/*
				 * �����̓��E���h�Ń��Z�b�g
				 */
				assign.ESDAinit();
	
				// �w���̊����󋵂�������
				assignedStatus = new int[NumOfStudents];
				for (int i = 0; i < NumOfStudents; i++) {
					assignedStatus[i] = NOTASSIGNED;
				}
	
				// �w���̃v���|�[�Y�������
				setProposeOfStudents();
				setProposeMatrix(prob);
//				System.out.println("�w�Z��propose��� " + Arrays.deepToString(proposeMatrix));
	
				if(disp_flag){
					for (int i = 0; i < NumOfStudents; i++) {
						System.out.println("student "+i+" is proposing to school "+proposeOfStudents[i]);
					}
				}
				
				// extended-school��apply����\��(accept or reject�͂킩��Ȃ�)�̐l��
				int NumofExtendedApplyStudents = 0;
				for (int i = NumOfSchools; i < NumOfSchools * 2; i++) {
					for (int j = 0; j < NumOfStudents; j++) {
						if(proposeMatrix[i][j] != NOTSTUDENT) {
							NumofExtendedApplyStudents++;
						}
					}
				}
//				System.out.println("�w�Z��propose��� " + Arrays.deepToString(proposeMatrix));
	
				// standard-school
				//�w�Z�̔ԍ����ɂ݂Ă���
				for(int j=0; j < NumOfSchools; j++){
					targetSchool = j;
	
					for(int i=0;i<NumOfStudents;i++){
						int rank = i;
						//rank�̊w�����v���|�[�Y���Ă��邩
						if(proposeMatrix[targetSchool][rank] != NOTSTUDENT){
							targetStudent = proposeMatrix[targetSchool][rank];
	
							//����ɒB���Ă��Ȃ��Ȃ犄�蓖�Ă�
							if(assign.ESDAgetNumOfStudents(targetSchool) < MinQuotas) {
								assign.ESDAsetStudent(targetStudent, targetSchool);
								assignedStatus[targetStudent] = ASSIGNED;	
							}
							//����ɒB���Ă���Ȃ�͂���
							else{
								rejectedSchools[targetStudent][targetSchool] = REJECTED;
								rejectedStudents ++;
								if (disp_flag) System.out.println("student "+ targetStudent + " is rejected by "+ targetSchool);
							}
						}
					}
				}
				
				// extended-school
				//round-robin�ɂ݂Ă��� (����̐ݒ�ł͋t��round-robin�ɂ��Ă���)
				boolean studentcheckflag = false;
				int rrcounter = NumOfSchools * 2 - 1; // �����Ă���w�Z
				int studentcheck = 0; // ���̊w�Z�̉��Ԗڂ̊w�������Ă���
				int extendedcheckedstudents = 0; // extended�ɂ��钆�Ń`�F�b�N���I������w����
				int counter = 0; // extended�Ɋ��蓖�Ă�ꂽ�w���� (���ʂł͂Ȃ��ē���ƂƂ��ɑ����Ă���)
				
				// ���̊w�Z�ň�l�����ă`�F�b�N���Ď��̊w�Z�ɍs��
				while (NumofExtendedApplyStudents != extendedcheckedstudents) {
					while (!studentcheckflag) {
						if (proposeMatrix[rrcounter][studentcheck] != NOTSTUDENT) {
							targetStudent = proposeMatrix[rrcounter][studentcheck];
							proposeMatrix[rrcounter][studentcheck] = NOTSTUDENT;
							studentcheckflag = true;
							extendedcheckedstudents++;
							// �����e�Ɉᔽ���Ȃ��Ȃ犄�蓖�Ă�
							if (assign.ESDAgetNumOfStudents(rrcounter) < DifQuotas && assign.getNumOfExtendedStudents() < Regionale) {
								assign.ESDAsetStudent(targetStudent, rrcounter);
								assignedStatus[targetStudent] = ASSIGNED;
								extendedstudents[counter] = targetStudent;
								counter++;
							} else {
								// �����łȂ����reject
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
			
			// extended-school�̊w����standard-school�Ɉړ�������
			int w = 0;
			while (w < assign.getNumOfExtendedStudents()) {
				targetStudent = extendedstudents[w];
//				assign.debug();
//				System.out.println("w " + w +" targetstudent " + targetStudent + " idousaki " + (assign.ESDAgetAssignedSchool(targetStudent) - NumOfSchools));
				assign.ESDAsetStudent(targetStudent, assign.ESDAgetAssignedSchool(targetStudent) - NumOfSchools);
				w++;
			}
				
			assign.ESDAconvert();
			System.out.println(); // �C���X�^���X�Ԃ̌����ڗp
			return;
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
		for (int i = 0; i < NumOfSchools * 2; i++) {
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
			preferenceNum = prob.getESDAPreferenceSchoolToStudent(targetSchool, targetStudent);
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
			for (int j = 0; j < NumOfSchools * 2; j++) {
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

}
