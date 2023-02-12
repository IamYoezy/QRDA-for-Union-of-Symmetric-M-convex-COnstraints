package Main;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Randomで解くクラス
 */
public class MC_Random {

	/* 変数宣言 */
	private int NumOfStudents;
	private int NumOfSchools;
	private double Ratio;
	private int[] masterList; // 学生のマスターリスト
	
	boolean disp_flag = false;//動作内容を表示する場合はtrueとする

	/**
	 * Randomを行う
	 */
	public void solve(Problem prob, Assignment assign) {

		/* 変数宣言 */
		int targetStudent;
		int targetSchool;
		int restStudents; // 割り当てられていない学生数
		
		// 初期設定
		NumOfStudents = prob.getNumOfStudents();
		NumOfSchools = prob.getNumOfSchools();
		restStudents = NumOfStudents;
		masterList = new int[NumOfStudents];
		Ratio = prob.getRatio();
		
		masterList = prob.getMasterList();
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int j=0 ; j<NumOfSchools ; j++){
			list.add(j);
		}
	
		// ステージを(学生数)回繰り返す
		for (int i = 0; i < NumOfStudents; i++) {
			
			if(disp_flag) System.out.println("---stage:"+i+"---");

			targetStudent = masterList[i];
			restStudents -= 1;
			
			Collections.shuffle(list , new Random());//学校のリストをシャッフルする（疑似的なtargetStudentの選好とする）
			
			for (int j = 0; j < NumOfSchools; j++) {
				targetSchool = list.get(j);
				
				//新たな割当からfeasibleとなる割当が1つでもあるなら，割り当てる
				if (!this.isNotSatisfyRatio(prob, assign, targetSchool,restStudents)) {
					if(disp_flag) System.out.println("student "+targetStudent+" is assigned to school "+targetSchool);
					assign.setStudent(targetStudent, targetSchool);
					break;
				}
			}
		}
	}
	
	/**
	 * 新たな割当が，今後どのように割り当ててもratioに違反してしまうか
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
		 * restStudentsの学生一人ひとりについて，
		 * 割当数の配列を昇順ソートし，割当数が最小の学校にその学生を割り当てる場合を考える
		 * （すなわち，割当数の分散が最も抑えられるケースを考える）
		 */
		for(int i = 0; i < restStudents; i++){
			Arrays.sort(copy_assign);
			copy_assign[0] += 1;
		}
		// 割当数の分散が最も抑えられるケースで，ratioに違反するか
		Arrays.sort(copy_assign);
		if ((double) copy_assign[0]/copy_assign[NumOfSchools-1] < Ratio){
			flag = true;//ratioに違反
		}
		return flag;
	}
}
