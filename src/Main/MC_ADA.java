package Main;

import java.util.Arrays;
/**
 * AdaptiveDA�ŉ����N���X
 */
public class MC_ADA {

	/* �ϐ��錾 */
	private int NumOfStudents;
	private int NumOfSchools;
	private double Ratio;
	private int[][] preferenceOfStudents; // �w���̑I�D���i�[
	
	boolean disp_flag = false;//������e��\������ꍇ��true�Ƃ���
	
	
	/**
	 * �w���̒ʏ�g�ւ̃v���|�[�Y
	 * proposeOfStudents[i] = �w��i���v���|�[�Y���Ă���w�Z
	 */
	private int[] proposeOfStudents;

	/**
	 * ���ۂ��ꂽ�w�Z 
	 * rejectedSchools[i][j] = �w��i�̊w�Zj�ɑ΂�����
	 *  NotRejected:0, Rejected:1
	 */
	private int[][] rejectedSchools;

	/**
	 * �w���̊�����Ԃ��i�[
	 *  assignedStatus[i] = �w��i�̊������ 
	 *  NotAssigned:0, Assigned:1, Unparticipant:-1
	 */
	private int[] assignedStatus;

	/**
	 * �w���̃v���|�[�Y���s��ŊǗ��D 
	 * proposeMatrix[i][j] = �w�Zi��j�ԖڂɍD�ފw���̃v���|�[�Y
	 */
	private int[][] proposeMatrix;
	
	/**
	 * �������֎~����w�Z�ł��Ǘ��D 
	 * forbiddenSchool[i] = �w�Zi�ւ̊����͋֎~
	 */
	private boolean[] forbiddenSchool;
	
	/* �萔�錾 */
	private final int NOTREJECTED = 0; // ���ۂ���Ă��Ȃ�
	private final int REJECTED = 1; // ���ۂ��ꂽ

	private final int NOTSCHOOL = -1; //�w�����L���Ȋw�Z�ɐ\������ł��Ȃ����Ƃ��Ӗ�����
	private final int NOTSTUDENT = -1; //�w�Z�ɐ\������ł���w�������݂��Ȃ����Ƃ��Ӗ�����
	
	private final int NOTASSIGNED = 0; // ���蓖�Ă��Ă��Ȃ�
	private final int ASSIGNED = 1; // ���蓖�Ă��Ă���
	private final int UNPARTICIPAIT = -1; // �Q�����Ă��Ȃ�

	
	/**
	 * ADA���s��
	 */
	public void solve(Problem prob, Assignment assign) {

		/* �ϐ��錾 */
		int rejectedStudents; // ���̃��E���h�ŋ��ۂ��ꂽ�w�������J�E���g
		int targetSchool;
		int targetStudent;
		int restStudents; // �����c��w����
		
		/* �����ݒ� */
		NumOfStudents = prob.getNumOfStudents();
		NumOfSchools = prob.getNumOfSchools();
		Ratio = prob.getRatio();

		rejectedStudents = NumOfStudents;
		preferenceOfStudents = new int[NumOfStudents][NumOfSchools];
		proposeOfStudents = new int[NumOfStudents];
		proposeMatrix = new int[NumOfSchools][NumOfStudents];
		forbiddenSchool = new boolean[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++) {
			forbiddenSchool[i] = false;
		}


		// �I�D���擾
		preferenceOfStudents = prob.getPreferenceOfStudents();

		// ���ۂ��ꂽ�w�Z��������
		rejectedSchools = new int[NumOfStudents][NumOfSchools];
		for (int j = 0; j < NumOfStudents; j++) {
			for (int k = 0; k < NumOfSchools; k++) {
				rejectedSchools[j][k] = NOTREJECTED;
			}
		}

		int NumOfAssignedStudents = 0;

		/* �����J�n */

		// �����̏�����
		assign.init();

		// �X�e�[�W�ɂ����銄��
		Assignment assignmentOnStage = new Assignment(NumOfStudents,NumOfSchools);
		
		int stage = 0;
		int round = 0;
		int sub_round;
		restStudents = NumOfStudents;

		// �X�e�[�W
		for (int k = 0; k < NumOfStudents; k++) {
			stage = k;
			if(disp_flag) System.out.println("---stage:"+stage+"---");

			// ���̃X�e�[�W�ɂ����銄���Ă�������
			assignmentOnStage.init();

			// �S�����蓖�Ă���I��
			if (NumOfAssignedStudents >= NumOfStudents) {
				if(disp_flag) System.out.println("Finish ADA.");
				break;// ADA���I��
			}

			// ���E���h�i�}�X�^�[���X�g�ŏ�ʂ̊w������l�����₵�Ă���DA�j
			for (int t = 0; t < NumOfStudents - NumOfAssignedStudents + 1; t++) {
				round = t;

				int newStudent = NumOfAssignedStudents + round;

				/* �X�e�[�W�̏I������ */
				
				// �S�����蓖�Ă��Ƃ�(i)
				if (newStudent == NumOfStudents) {
					NumOfAssignedStudents += round;
					if(disp_flag) System.out.println("Fix the result of DA since all students are assigned.");
					break;// DA���I��
				}

				boolean forbidden_flag = false;
				restStudents = NumOfStudents - newStudent -1; 
				for (int i = 0; i < NumOfSchools; i++) {
					if(!forbiddenSchool[i] && isForbiddenSchool(prob,assign,assignmentOnStage,i,restStudents)){
						if(disp_flag) System.out.println("school "+i+" becomes forbidden! ");
						forbiddenSchool[i] = true;
						forbidden_flag = true;
					}
				}
				// forbidden school������Ƃ�(iii)
				if(forbidden_flag){
					// ���蓖�Ă�ꂽ�w������ǉ�
					NumOfAssignedStudents += round;
					if(disp_flag) System.out.println("Fix the result of DA since new forbidden school appeared.");
					break;// DA���I��
				}

				//�ȍ~�Cforbidden school���Ȃ��Ƃ�(ii)
				assignmentOnStage.init();

				rejectedStudents = 1;
				sub_round = 0;
				// �͂������w�������Ȃ��Ȃ�܂Ń��[�v(DA)
				while (rejectedStudents > 0) {

					if(disp_flag) System.out.println("---round:"+round+"-"+sub_round+"---");

					
					rejectedStudents = 0;

					/*
					 * ���蓖�Ă͖������Z�b�g 
					 * ���蓖�Ă��Ă����w���͓���school�ɍēx�v���|�[�Y
					 */
					assignmentOnStage.init();
					// �w���̊����󋵂�������
					// ���̃��E���h�ŎQ�����Ȃ��l�� UNPARTICIPAIT
					assignedStatus = new int[NumOfStudents];
					for (int j = 0; j < NumOfStudents; j++) {

						if (j < NumOfAssignedStudents) {
							// �O�̃X�e�[�W�܂łŊ��蓖�Ă�ꂽ�l�͎Q�����Ȃ�
							assignedStatus[prob.getMasterList()[j]] = UNPARTICIPAIT;
						} else if (j < NumOfAssignedStudents + round + 1) {
							assignedStatus[prob.getMasterList()[j]] = NOTASSIGNED;
						} else {
							assignedStatus[prob.getMasterList()[j]] = UNPARTICIPAIT;
						}
					}

					// �w���̃v���|�[�Y�������
					setProposeOfStudents();
					setProposeMatrix(prob);
					
					if(disp_flag){
						for (int i = 0; i < NumOfStudents; i++) {
							if(assignedStatus[i]!=UNPARTICIPAIT){
								System.out.println("student "+i+" is proposing to school "+proposeOfStudents[i]);
							}
						}
					}

					// �w�Z�̔ԍ����ɂ݂Ă���
					for (int j = 0; j < NumOfSchools; j++) {
						targetSchool = j;
						
						for (int m = 0; m < NumOfStudents; m++) {
							int rank = m;
							// rank�̊w�����v���|�[�Y���Ă��邩
							if (proposeMatrix[targetSchool][rank] != NOTSTUDENT) {
								targetStudent = proposeMatrix[targetSchool][rank];
								// �Q�����Ă��邩
								if (assignedStatus[targetStudent] != UNPARTICIPAIT) {
									// ����ɒB���Ă��Ȃ�����forbidden�łȂ��Ȃ犄�蓖�Ă�
									if (assign
											.getNumOfAssignStudents(targetSchool)
											+ assignmentOnStage
													.getNumOfAssignStudents(targetSchool) < prob
												.getADACap(targetSchool)
											&& !forbiddenSchool[targetSchool]) {
										assignmentOnStage.setStudent(
												targetStudent, targetSchool);
										assignedStatus[targetStudent] = ASSIGNED;
									}
									// ����ɒB���Ă���Ȃ�͂���
									else {
										rejectedSchools[targetStudent][targetSchool] = REJECTED;
										rejectedStudents++;
									}
								}
							}
						}
					}
					sub_round++;
				}
			}

			/* �X�e�[�W�̊�����ǉ� */
			assign.addAssignmentStudents(assignmentOnStage);
		}
	}

	/**
	 * school��forbidden���ǂ���
	 */
	private boolean isForbiddenSchool(Problem prob, Assignment assign,
			Assignment assignmentOnStage, int school,int restStudents) {
		boolean flag = false;
		//school��full�łȂ�
		if(assignmentOnStage.getNumOfAssignStudents(school) + assign.getNumOfAssignStudents(school) < prob.getADACap(school)){
			int copy_assign[];
			copy_assign = new int[NumOfSchools];
			for (int i = 0; i < NumOfSchools; i++){
				if (school == i){
					copy_assign[i] = assignmentOnStage.getNumOfAssignStudents(i) + assign.getNumOfAssignStudents(i) + 1 ;
				}else{
					copy_assign[i] = assignmentOnStage.getNumOfAssignStudents(i) + assign.getNumOfAssignStudents(i) ;
				}
			}
			
			/*
			 * restStudents�̊w����l�ЂƂ�ɂ��āC
			 * �������̔z��������\�[�g���C���������ŏ��̊w�Z�ɂ��̊w�������蓖�Ă�ꍇ���l����
			 * �i���Ȃ킿�C�������̕��U���ł��}������P�[�X���l����j
			 */
			for(int i = 0; i < restStudents; i++){
				Arrays.sort(copy_assign);
				copy_assign[0] += 1;
			}
			// �������̕��U���ł��}������P�[�X�ŁCratio�Ɉᔽ���邩
			Arrays.sort(copy_assign);
			if ((double) copy_assign[0]/copy_assign[NumOfSchools-1] < Ratio){
				flag = true;//ratio�Ɉᔽ
			}
		}
		return flag;
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
				proposeMatrix[i][j] = NOTSTUDENT;
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
}
