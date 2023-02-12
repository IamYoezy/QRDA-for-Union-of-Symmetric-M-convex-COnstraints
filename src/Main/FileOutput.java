package Main;

/**
 * 結果をファイルとして出力するクラス
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
	
	// 実験結果のファイルの出力先を指定
	private final String OUTPUT_DIR_NAME = "C:\\MatchingData\\MatchingProblemWithRelativeConstraintDATA_Solution\\";
	// 問題ファイルの入力元のPLTフォルダを指定
	private final String BACEPLTFILE_DIR_NAME = "C:\\MatchingData\\MatchingProblemWithRelativeConstraintDATA\\PLT\\";
	String dirName;
	String dataDirName;

	FileOutput() {

		Calendar calendar = Calendar.getInstance();

		// 新しいディレクトリ
		dirName = OUTPUT_DIR_NAME;
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int date = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		dirName += year + "-" + month + "-" + date + "_" + hour + "-" + minute; // 現在の時間を加える

		// ディレクトリ生成
		File newdir = new File(dirName);
		newdir.mkdir();

		// データのディレクトリ生成
		dataDirName = dirName + "\\data";
		File dataDir = new File(dataDirName);
		dataDir.mkdir();
	}

	/**
	 * pltファイルを作成する関数
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
				// 最後に来たら終了
				if ((line = br.readLine()) == null) {
					break;
				}

				// 出力ファイル指定処理
				if (line.equals("set output")) {
					line = line + "\"" + fileName + ".eps\"";
				}

				// plot data 指定
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
	 * 分布図のためのpltファイルを作成する関数
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
				// 最後に来たら終了
				if ((line = br.readLine()) == null) {
					break;
				}

				// 出力ファイル指定処理
				if (line.equals("set output")) {
					line = line + "\"" + fileName + ".eps\"";
				}

				// plot data 指定
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
	 * WhichLikeのためのpltファイルを作成する関数
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
				//最後に来たら終了
				if((line = br.readLine()) == null){
                	break;
                }

				//出力ファイル指定処理
				if(line.equals("set output")){
					line = line + "\"" + fileName + ".eps\"";
				}

				//plot data 指定（algorithmName.length = 2）
				if(line.equals("plot")){
					for(int i=algorithmName.length-1;i>=0;i--){
						if(i == algorithmName.length-1){//最初の1行
							line = line + " \"data/" + fileName + ".txt\"" +  "using 1:($2+$3) ti \""+ algorithmName[i]+"\"  w filledcurves x1 lc rgb \"gray30\" , \\";
						}
						else if(i == 0){//最後の行
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
	 * データをそのまま出力する関数
	 */
	public void output(double[] data, String dataName,
			double[] parameter, String[] parameterName,
			boolean[] graphParameterFlag, String[] algorithmName, boolean makePltFlag) {

		try {
			// ファイル名
			String fileName;

			// グラフの変数になっているものだけ
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

				// 始めて書き込む場合はメカニズム名を書き込む
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

				// plt ファイルを作る
				if (makePltFlag) {
					this.makePltFile(dataName, fileName, algorithmName);
				}

			}

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	/**
	 * データを学生の人数で割った値を出力する関数
	 */
	public void outputRatioOfStudents(double[] data, String dataName,
			double[] parameter, String[] parameterName,
			boolean[] graphParameterFlag, String[] algorithmName,
			double NumOfStudents, boolean makePltFlag) {

		try {
			// ファイル名
			String fileName;

			// グラフの変数になっているものだけ
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

				// 始めて書き込む場合はメカニズム名を書き込む
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

				// plt ファイルを作る
				if (makePltFlag) {
					this.makePltFile(dataName, fileName, algorithmName);
				}

			}

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	/**
	 * welfareを出力する関数
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
			/* ファイル出力の準備 */
			FileOutputStream fos;
			OutputStreamWriter osw;
			BufferedWriter bw;

			fos = new FileOutputStream(txtFileName);
			osw = new OutputStreamWriter(fos);
			bw = new BufferedWriter(osw);

			/* データを書き込み */
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

			/* ファイル出力の後始末 */
			bw.close();
			osw.close();
			fos.close();

			if (makePltFlag) {
				this.makePltFile(dataName, fileName, algorithmName);
			}

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	/**
	 * データを分布図用に出力する関数
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
				/* ファイル出力の準備 */
				FileOutputStream fos;
				OutputStreamWriter osw;
				BufferedWriter bw;
	
				fos = new FileOutputStream(txtFileName);
				osw = new OutputStreamWriter(fos);
				bw = new BufferedWriter(osw);
	
				/* データを書き込み */
				bw.write("#instance"+"\t"+"x"+"\t"+"y");
				bw.newLine();
				for (int c = 0; c < x_data[a].length; c++) {
					bw.write("" + (c + 1));
					bw.write("\t" + Double.toString(x_data[a][c]));
					bw.write("\t" + Double.toString(y_data[a][c]));
					bw.newLine();
				}
	
				/* ファイル出力の後始末 */
				bw.close();
				osw.close();
				fos.close();
	
				if (makePltFlag) {
					this.makePltFileScatter(dataName, fileName, algorithmName);
				}
	
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	/**
	 * WhichLike用に出力する関数
	 */
	public void outputWhichLike(double[] data,int Alg_1, int Alg_2, 
			String dataName, double[] parameter,
			String[] parameterName,boolean[] graphParameterFlag,
			String[] algorithmName, boolean makePltFlag){

		try {
			//ファイル名
			String fileName;

			//グラフの変数になっているものだけ
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

				//始めて書き込む場合はメカニズム名を書き込む
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


				//plt ファイルを作る
				if(makePltFlag){
					this.makePltFileWhichLike(dataName, fileName, algorithmName);
				}

			}

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
