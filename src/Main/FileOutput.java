package Main;

/**
 * ���ʂ��t�@�C���Ƃ��ďo�͂���N���X
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class FileOutput {
	
	// �������ʂ̃t�@�C���̏o�͐���w��
	private final String OUTPUT_DIR_NAME = "C:\\MatchingData\\MatchingProblemWithRelativeConstraintDATA_Solution\\";
	// ���t�@�C���̓��͌���PLT�t�H���_���w��
	private final String BACEPLTFILE_DIR_NAME = "C:\\MatchingData\\MatchingProblemWithRelativeConstraintDATA\\PLT\\";
	String dirName;
	String dataDirName;

	FileOutput() {

		Calendar calendar = Calendar.getInstance();

		// �V�����f�B���N�g��
		dirName = OUTPUT_DIR_NAME;
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int date = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		dirName += year + "-" + month + "-" + date + "_" + hour + "-" + minute; // ���݂̎��Ԃ�������

		// �f�B���N�g������
		File newdir = new File(dirName);
		newdir.mkdir();

		// �f�[�^�̃f�B���N�g������
		dataDirName = dirName + "\\data";
		File dataDir = new File(dataDirName);
		dataDir.mkdir();
	}

	/**
	 * plt�t�@�C�����쐬����֐�
	 */
	private void makePltFile(String dataName, String fileName,
			String[] algorithmName) {
		try {
			FileReader in = new FileReader(BACEPLTFILE_DIR_NAME + dataName
					+ ".plt");
			BufferedReader br = new BufferedReader(in);

			File pltFile = new File(dirName + "\\" + fileName + ".plt");
			if (pltFile.exists()) {
				br.close();
				return;
			}
			FileWriter filewriter = new FileWriter(pltFile);

			String line;

			while (true) {
				// �Ō�ɗ�����I��
				if ((line = br.readLine()) == null) {
					break;
				}

				// �o�̓t�@�C���w�菈��
				if (line.equals("set output")) {
					line = line + "\"" + fileName + ".eps\"";
				}

				// plot data �w��
				if (line.equals("plot")) {
					for (int i = 0; i < algorithmName.length; i++) {
						if (i == 0) {
							line = line + " \"data/" + fileName + ".txt\""
									+ "using 1:" + Integer.toString(i + 2)
									+ " ti \"" + algorithmName[i]
									+ "\"  w lp lw 5, \\";
						} else if (i == algorithmName.length - 1) {
							line = " \"data/" + fileName + ".txt\""
									+ "using 1:" + Integer.toString(i + 2)
									+ " ti \"" + algorithmName[i]
									+ "\"  w lp lw 5";
						} else {
							line = " \"data/" + fileName + ".txt\""
									+ "using 1:" + Integer.toString(i + 2)
									+ " ti \"" + algorithmName[i]
									+ "\"  w lp lw 5, \\";
						}
						filewriter.write(line);
						filewriter.write("\r\n");
					}
					continue;
				}

				filewriter.write(line);
				filewriter.write("\r\n");
				// System.out.println(line);
			}
			br.close();
			in.close();

			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���z�}�̂��߂�plt�t�@�C�����쐬����֐�
	 */
	private void makePltFileScatter(String dataName, String fileName,
			String[] algorithmName) {
		try {
			FileReader in = new FileReader(BACEPLTFILE_DIR_NAME + dataName
					+ ".plt");
			BufferedReader br = new BufferedReader(in);

			File pltFile = new File(dirName + "\\" + fileName + ".plt");
			if (pltFile.exists()) {
				br.close();
				return;
			}
			FileWriter filewriter = new FileWriter(pltFile);

			String line;

			while (true) {
				// �Ō�ɗ�����I��
				if ((line = br.readLine()) == null) {
					break;
				}

				// �o�̓t�@�C���w�菈��
				if (line.equals("set output")) {
					line = line + "\"" + fileName + ".eps\"";
				}

				// plot data �w��
				if (line.equals("plot")) {
					for (int i = 0; i < algorithmName.length; i++) {
						if (i == 0) {
							line = line + " \"data/" +  fileName + "_" + algorithmName[i] + ".txt\""
									+ "using 2:3"
									+ " ti \"" + algorithmName[i]
									+ "\" pt "+ (i+1) +" lc "+ (i+1) +", \\";
						} else if (i == algorithmName.length - 1) {
							line = " \"data/" +  fileName + "_" + algorithmName[i] + ".txt\""
									+ "using 2:3"
									+ " ti \"" + algorithmName[i]
									+ "\" pt "+ (i+1) +" lc "+ (i+1);
						} else {
							line = " \"data/" +  fileName + "_" + algorithmName[i] + ".txt\""
									+ "using 2:3"
									+ " ti \"" + algorithmName[i]
									+ "\" pt "+ (i+1) +" lc "+ (i+1) +", \\";
						}
						filewriter.write(line);
						filewriter.write("\r\n");
					}
					continue;
				}

				filewriter.write(line);
				filewriter.write("\r\n");
				// System.out.println(line);
			}
			br.close();
			in.close();

			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * WhichLike�̂��߂�plt�t�@�C�����쐬����֐�
	 */
	private void makePltFileWhichLike(String dataName,String fileName, String[] algorithmName){
		try{
			FileReader in = new FileReader(BACEPLTFILE_DIR_NAME + dataName +".plt");
			BufferedReader br = new BufferedReader(in);

			File pltFile = new File(dirName + "\\" + fileName +".plt");
			if(pltFile.exists()){
				br.close();
				return;
			}
			FileWriter filewriter = new FileWriter(pltFile);

			String line;


			while (true) {
				//�Ō�ɗ�����I��
				if((line = br.readLine()) == null){
                	break;
                }

				//�o�̓t�@�C���w�菈��
				if(line.equals("set output")){
					line = line + "\"" + fileName + ".eps\"";
				}

				//plot data �w��ialgorithmName.length = 2�j
				if(line.equals("plot")){
					for(int i=algorithmName.length-1;i>=0;i--){
						if(i == algorithmName.length-1){//�ŏ���1�s
							line = line + " \"data/" + fileName + ".txt\"" +  "using 1:($2+$3) ti \""+ algorithmName[i]+"\"  w filledcurves x1 lc rgb \"gray30\" , \\";
						}
						else if(i == 0){//�Ō�̍s
							line = " \"data/" + fileName + ".txt\"" +  "using 1:2 ti \""+ algorithmName[i]+"\"  w filledcurves x1 lc rgb \"gray60\"";
						}
						filewriter.write(line);
						filewriter.write("\r\n");
					}
					continue;
				}

				filewriter.write(line);
				filewriter.write("\r\n");
//				System.out.println(line);
            }
			br.close();
			in.close();

			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * �f�[�^�����̂܂܏o�͂���֐�
	 */
	public void output(double[] data, String dataName,
			double[] parameter, String[] parameterName,
			boolean[] graphParameterFlag, String[] algorithmName, boolean makePltFlag) {

		try {
			// �t�@�C����
			String fileName;

			// �O���t�̕ϐ��ɂȂ��Ă�����̂���
			for (int p = 0; p < parameter.length; p++) {
				if (!graphParameterFlag[p]) {
					continue;
				}

				fileName = dataName;
				for (int q = 0; q < parameter.length; q++) {
					if (q != p) {
						fileName = fileName + "_" + parameterName[q]
								+ parameter[q];
					}
				}
				String txtFileName = dataDirName + "\\" + fileName + ".txt";

				File file = new File(txtFileName);

				// �n�߂ď������ޏꍇ�̓��J�j�Y��������������
				if (!file.exists()) {
					FileWriter fileWirter = new FileWriter(file, true);

					fileWirter.write("#" + parameterName[p]);
					for (int a = 0; a < algorithmName.length; a++) {
						fileWirter.write("\t" + algorithmName[a]);
					}
					fileWirter.write("\r\n");

					fileWirter.close();
				}
				FileWriter fileWirter = new FileWriter(file, true);
				fileWirter.write(parameter[p] + "");
				for (int a = 0; a < algorithmName.length; a++) {

					fileWirter
							.write("\t" + data[a]);

				}
				fileWirter.write("\r\n");
				fileWirter.close();

				// plt �t�@�C�������
				if (makePltFlag) {
					this.makePltFile(dataName, fileName, algorithmName);
				}

			}

		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}
	
	/**
	 * �f�[�^���w���̐l���Ŋ������l���o�͂���֐�
	 */
	public void outputRatioOfStudents(double[] data, String dataName,
			double[] parameter, String[] parameterName,
			boolean[] graphParameterFlag, String[] algorithmName,
			double NumOfStudents, boolean makePltFlag) {

		try {
			// �t�@�C����
			String fileName;

			// �O���t�̕ϐ��ɂȂ��Ă�����̂���
			for (int p = 0; p < parameter.length; p++) {
				if (!graphParameterFlag[p]) {
					continue;
				}

				fileName = dataName;
				for (int q = 0; q < parameter.length; q++) {
					if (q != p) {
						fileName = fileName + "_" + parameterName[q]
								+ parameter[q];
					}
				}
				String txtFileName = dataDirName + "\\" + fileName + ".txt";

				File file = new File(txtFileName);

				// �n�߂ď������ޏꍇ�̓��J�j�Y��������������
				if (!file.exists()) {
					FileWriter fileWirter = new FileWriter(file, true);

					fileWirter.write("#" + parameterName[p]);
					for (int a = 0; a < algorithmName.length; a++) {
						fileWirter.write("\t" + algorithmName[a]);
					}
					fileWirter.write("\r\n");

					fileWirter.close();
				}
				FileWriter fileWirter = new FileWriter(file, true);
				fileWirter.write(parameter[p] + "");
				for (int a = 0; a < algorithmName.length; a++) {

					fileWirter
							.write("\t" + data[a] / (double) NumOfStudents);

				}
				fileWirter.write("\r\n");
				fileWirter.close();

				// plt �t�@�C�������
				if (makePltFlag) {
					this.makePltFile(dataName, fileName, algorithmName);
				}

			}

		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}
	
	/**
	 * welfare���o�͂���֐�
	 */
	public void outputCDF(double[][] data, String dataName, double[] parameter,
			String[] parameterName, boolean[] graphParameterFlag,
			String[] algorithmName, boolean makePltFlag) {
		try {
			String fileName;

			fileName = dataName;
			for (int q = 0; q < parameter.length; q++) {
				fileName = fileName + "_" + parameterName[q] + parameter[q];
			}
			String txtFileName = dataDirName + "\\" + fileName + ".txt";
			/* �t�@�C���o�͂̏��� */
			FileOutputStream fos;
			OutputStreamWriter osw;
			BufferedWriter bw;

			fos = new FileOutputStream(txtFileName);
			osw = new OutputStreamWriter(fos);
			bw = new BufferedWriter(osw);

			/* �f�[�^���������� */
			bw.write("#rank");
			for (int a = 0; a < algorithmName.length; a++) {
				bw.write("\t" + algorithmName[a]);
			}
			bw.newLine();
			for (int c = 0; c < data[0].length; c++) {
				bw.write("" + (c + 1));
				for (int a = 0; a < algorithmName.length; a++) {
					bw.write("\t" + Double.toString(data[a][c]));
				}
				bw.newLine();
			}

			/* �t�@�C���o�͂̌�n�� */
			bw.close();
			osw.close();
			fos.close();

			if (makePltFlag) {
				this.makePltFile(dataName, fileName, algorithmName);
			}

		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}
	
	/**
	 * �f�[�^�𕪕z�}�p�ɏo�͂���֐�
	 */
	public void outputScatter(double[][] x_data, double[][] y_data, String dataName, double[] parameter,
			String[] parameterName, boolean[] graphParameterFlag,
			String[] algorithmName, boolean makePltFlag) {
		for (int a = 0; a < algorithmName.length; a++) {
			try {
				String fileName;
	
				fileName = dataName;
				for (int q = 0; q < parameter.length; q++) {
					fileName = fileName + "_" + parameterName[q] + parameter[q];
				}
				String txtFileName = dataDirName + "\\" + fileName + "_" + algorithmName[a] + ".txt";
				/* �t�@�C���o�͂̏��� */
				FileOutputStream fos;
				OutputStreamWriter osw;
				BufferedWriter bw;
	
				fos = new FileOutputStream(txtFileName);
				osw = new OutputStreamWriter(fos);
				bw = new BufferedWriter(osw);
	
				/* �f�[�^���������� */
				bw.write("#instance"+"\t"+"x"+"\t"+"y");
				bw.newLine();
				for (int c = 0; c < x_data[a].length; c++) {
					bw.write("" + (c + 1));
					bw.write("\t" + Double.toString(x_data[a][c]));
					bw.write("\t" + Double.toString(y_data[a][c]));
					bw.newLine();
				}
	
				/* �t�@�C���o�͂̌�n�� */
				bw.close();
				osw.close();
				fos.close();
	
				if (makePltFlag) {
					this.makePltFileScatter(dataName, fileName, algorithmName);
				}
	
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}
	}

	/**
	 * WhichLike�p�ɏo�͂���֐�
	 */
	public void outputWhichLike(double[] data,int Alg_1, int Alg_2, 
			String dataName, double[] parameter,
			String[] parameterName,boolean[] graphParameterFlag,
			String[] algorithmName, boolean makePltFlag){

		try {
			//�t�@�C����
			String fileName;

			//�O���t�̕ϐ��ɂȂ��Ă�����̂���
			for(int p=0;p<parameter.length;p++){
				if(!graphParameterFlag[p]){
					continue;
				}

				fileName = dataName;
				for(int q=0;q<parameter.length;q++){
					if(q != p){
						fileName = fileName + "_" + parameterName[q] + parameter[q];
					}
				}
				String txtFileName = dataDirName + "\\" + fileName + ".txt";

				File file = new File(txtFileName);

				//�n�߂ď������ޏꍇ�̓��J�j�Y��������������
				if(!file.exists()){
					FileWriter fileWirter = new FileWriter(file,true);

						fileWirter.write("#"+parameterName[p]);
						for(int a=0;a<=algorithmName.length;a++){
							if(a == Alg_1 || a == Alg_2) fileWirter.write("\t"+algorithmName[a]);
							else if(a == algorithmName.length) fileWirter.write("\t"+"indifferent");
						}
						fileWirter.write("\r\n");

					fileWirter.close();
				}
				FileWriter fileWirter = new FileWriter(file,true);
				fileWirter.write(parameter[p]+"");
				for(int a=0;a<=algorithmName.length;a++){

					if(a == Alg_1 || a == Alg_2 || a == algorithmName.length)fileWirter.write("\t"+data[a]);

				}
				fileWirter.write("\r\n");
				fileWirter.close();


				//plt �t�@�C�������
				if(makePltFlag){
					this.makePltFileWhichLike(dataName, fileName, algorithmName);
				}

			}

		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}
}
