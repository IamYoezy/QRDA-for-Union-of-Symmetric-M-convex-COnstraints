package Main;

/**
 * メカニズムを管理するクラス
 */
public class Mechanisms {
	private Assignment assignment;
	private Problem problem;

	/**
	 * 初期設定
	 */
	public void init(Assignment assignment, Problem problem) {
		this.assignment = assignment;
		this.problem = problem;
	}
	
	/**
	 * ACDAで解く
	 * (ArtiricialCapの下でDAを行う)
	 */
	public void solveWithACDA() {
		MC_ACDA acda = new MC_ACDA();
		acda.solve(problem, assignment);
	}
	
	/**
	 * QRDA (DifferenceConstraint) で解く
	 */
	public void solveWithQRDA_Dif() {
		MC_QRDA_Dif qrda = new MC_QRDA_Dif();
		qrda.solve(problem, assignment);
	}
	
	/**
	 * QRDA (Min + Max + Manhattan Constraint) で解く
	 */
	public void solveWithQRDA_MinMaxMan() {
		MC_QRDA_MinMaxMan qrda = new MC_QRDA_MinMaxMan();
		qrda.solve(problem, assignment);
	}

	/**
	 * SDで解く
	 * (ML順に，学生を最も好む学校にfeasibleになるよう割り当てる)
	 */
	public void solveWithSD() {
		MC_SD sd = new MC_SD();
		sd.solve(problem, assignment);
	}

	/**
	 * ADAで解く
	 * (DefaultCapとML順に基づいた後藤さんのメカニズムで割り当てる)
	 */
	public void solveWithADA() {
		MC_ADA ada = new MC_ADA();
		ada.solve(problem, assignment);
	}
	
	/**
	 * Randomで解く
	 * (ML順に，学生をランダムにfeasibleになるよう割り当てる)
	 */
	public void solveWithRandom() {
		MC_Random random = new MC_Random();
		random.solve(problem, assignment);
	}
	
	/**
	 * ESDAで解く
	 * 
	 */
	public void solveWithESDA() {
		MC_ESDA2 esda = new MC_ESDA2();
		esda.solve(problem, assignment);
	}

}
