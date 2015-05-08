package hkh.nlp.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	/**
	 * 
	 * @param fileNm
	 * @param encode - "utf-8"
	 * @return
	 */
	public static ArrayList<String> readFile(String fileNm, String encode) {
		ArrayList<String> lines = null;
		
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(new FileInputStream(fileNm), encode);
			br = new BufferedReader(isr);
			lines = new ArrayList<String>();
			
			String line = null;
			while ((line=br.readLine())!=null) {
				lines.add(line);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isr != null) try { isr.close(); } catch (Exception ex) {};
			if (br != null) try { br.close(); } catch (Exception ex) {};
		}
		
		return lines;
	}
	
	public static ArrayList<String> readFile(String fileNm) {
		ArrayList<String> lines = null;
		
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(new File(fileNm));
			br = new BufferedReader(fr);
			lines = new ArrayList<String>();
			
			String line = null;
			while ((line=br.readLine())!=null) {
				lines.add(line);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fr != null) try { fr.close(); } catch (Exception ex) {};
			if (br != null) try { br.close(); } catch (Exception ex) {};
		}
		
		return lines;
	}
	
	public static void WriteFile(String fileNm, String text) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(new File(fileNm));
			bw = new BufferedWriter(fw);
			bw.write(text);
			bw.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if (fw != null) try { fw.close(); } catch (Exception ex) {};
			if (bw != null) try { bw.close(); } catch (Exception ex) {};
		}
	}
	
	public static String readStringFromFile(String fileNm) {
		StringBuffer sb = new StringBuffer();
		List<String> lines = readFile(fileNm);
		for (String line : lines) {
			sb.append(line);
		}
		return sb.toString();
	}
}
