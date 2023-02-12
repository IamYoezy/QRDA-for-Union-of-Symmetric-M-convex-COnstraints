package Main;

/**
 * ���J�j�Y�����Ǘ�����N���X
 */
public class Mechanisms {
	private Assignment assignment;
	private Problem problem;

	/**
	 * �����ݒ�
	 */
	public void init(Assignment assignment, Problem problem) {
		this.assignment = assignment;
		this.problem = problem;
	}
	
	/**
	 * ACDA�ŉ���
	 * (ArtiricialCap�̉���DA���s��)
	 */
	public void solveWithACDA() {
		MC_ACDA acda = new MC_ACDA();
		acda.solve(problem, assignment);
	}
	
	/**
	 * QRDA (DifferenceConstraint) �ŉ���
	 */
	public void solveWithQRDA_Dif() {
		MC_QRDA_Dif qrda = new MC_QRDA_Dif();
		qrda.solve(problem, assignment);
	}
	
	/**
	 * QRDA (Min + Max + Manhattan Constraint) �ŉ���
	 */
	public void solveWithQRDA_MinMaxMan() {
		MC_QRDA_MinMaxMan qrda = new MC_QRDA_MinMaxMan();
		qrda.solve(problem, assignment);
	}

	/**
	 * SD�ŉ���
	 * (ML���ɁC�w�����ł��D�ފw�Z��feasible�ɂȂ�悤���蓖�Ă�)
	 */
	public void solveWithSD() {
		MC_SD sd = new MC_SD();
		sd.solve(problem, assignment);
	}

	/**
	 * ADA�ŉ���
	 * (DefaultCap��ML���Ɋ�Â����㓡����̃��J�j�Y���Ŋ��蓖�Ă�)
	 */
	public void solveWithADA() {
		MC_ADA ada = new MC_ADA();
		ada.solve(problem, assignment);
	}
	
	/**
	 * Random�ŉ���
	 * (ML���ɁC�w���������_����feasible�ɂȂ�悤���蓖�Ă�)
	 */
	public void solveWithRandom() {
		MC_Random random = new MC_Random();
		random.solve(problem, assignment);
	}
	
	/**
	 * ESDA�ŉ���
	 * 
	 */
	public void solveWithESDA() {
		MC_ESDA2 esda = new MC_ESDA2();
		esda.solve(problem, assignment);
	}

}
