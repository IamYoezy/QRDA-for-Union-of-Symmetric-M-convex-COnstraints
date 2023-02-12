package Main;

import java.util.Arrays;

/**
 * SerialDictatorship�ŉ����N���X
 */
public class MC_SD {

	/* �ϐ��錾 */
	private int NumOfStudents;
	private int NumOfSchools;
	private double Ratio;
	private int[][] preferenceOfStudents; // �w���̑I�D���i�[
	private int[] masterList; // �w���̃}�X�^�[���X�g
	
	boolean disp_flag = false;//������e��\������ꍇ��true�Ƃ���
	
	
	/**
	 * SD���s��
	 */
	public void solve(Problem prob, Assignment assign) {

		/* �ϐ��錾 */
		int targetStudent;
		int targetSchool;
		int restStudents; // ���蓖�Ă��Ă��Ȃ��w����
		
		// �����ݒ�
		NumOfStudents = prob.getNumOfStudents();
		NumOfSchools = prob.getNumOfSchools();
		restStudents = NumOfStudents;
		masterList = new int[NumOfStudents];
		preferenceOfStudents = new int[NumOfStudents][NumOfSchools];
		Ratio = prob.getRatio();
		
		// �I�D���擾
		masterList = prob.getMasterList();
		preferenceOfStudents = prob.getPreferenceOfStudents();

		// �X�e�[�W��(�w����)��J��Ԃ�
		for (int i = 0; i < NumOfStudents; i++) {
			
			if(disp_flag) System.out.println("---stage:"+i+"---");
			
			targetStudent = masterList[i];
			restStudents -= 1;
			
			for (int j = 0; j < NumOfSchools; j++) {
				targetSchool = preferenceOfStudents[targetStudent][j];

				//�V���Ȋ�������feasible�ƂȂ銄����1�ł�����Ȃ�C���蓖�Ă�
				if (!this.isNotSatisfyRatio(prob, assign, targetSchool,restStudents)) {
					if(disp_flag) System.out.println("student "+targetStudent+" is assigned to school "+targetSchool);
					assign.setStudent(targetStudent, targetSchool);
					break;
				}
			}
		}
	}

	/**
	 * �V���Ȋ������C����ǂ̂悤�Ɋ��蓖�ĂĂ�ratio�Ɉᔽ���Ă��܂���
	 */
	private boolean isNotSatisfyRatio(Problem prob, Assignment assign,int school,int restStudents) {
		boolean flag = false;
		int copy_assign[];
		copy_assign = new int[NumOfSchools];
		for (int i = 0; i < NumOfSchools; i++){
			if (school == i){
				copy_assign[i] = assign.getNumOfAssignStudents(i) + 1 ;
			}else{
				copy_assign[i] = assign.getNumOfAssignStudents(i) ;
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
		return flag;
	}
}
