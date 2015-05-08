package hkh.nlp;

import hkh.nlp.hmm.CorpusEntry;
import hkh.nlp.hmm.HMM;
import hkh.nlp.util.FileUtil;
import hkh.nlp.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {

	static String RESULT_FILE = "result.txt";
	static String TRAIN_FILE = "train.txt";
	
	public static void main(String args[]) {
		
		/**
		 * If the result.txt file encoded in "euc-kr"
		 */
		ArrayList<String> results = FileUtil.readFile(RESULT_FILE, "euc-kr");
		
		/**
		 * otherwise read as utf-8
		 */
//		ArrayList<String> morphemes = FileUtil.readFile(RESULT_FILE);
		
		System.out.println("Loading corpus...");
		ArrayList<String> rawCorpus = FileUtil.readFile(TRAIN_FILE);
		ArrayList<CorpusEntry> corpus = convertCorpus(rawCorpus);
		rawCorpus = null;
		
		ArrayList<String> morphemes = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		
		// ANSI 파일을 읽으면 공백이 생김..
		int count = 0;
		for (int i=0; i<results.size(); i++) {
			if (results.get(i).length() == 0) {
				count++;
				if (count == 3) {
					HMM hmm = new HMM();
					hmm.setNetwork(morphemes);
					hmm.setCorpus(corpus);
					hmm.viterbi();
					hmm.printSolution();
					hmm.getSolution(sb);
					
					morphemes.clear();
					count = 0;
				}
			} else {
				count = 0;
				morphemes.add(results.get(i));
			}
		}

		FileUtil.WriteFile("pos_result.txt", sb.toString());
	}
	
	public static ArrayList<CorpusEntry> convertCorpus(ArrayList<String> rawCorpus) {
		System.out.println("Parsing corpus...");
		ArrayList<CorpusEntry> corpus = new ArrayList<CorpusEntry>();
		for (String line : rawCorpus) {
			CorpusEntry entry = new CorpusEntry();
			if (line.length() == 0) {
				entry.isBegin = true;
				entry.word = "";
				entry.posPair = "";
				corpus.add(entry);
				continue;
			}
			String[] splits = line.split("\\t");
			if (splits.length != 2) {
				continue;
			}
			entry.word = splits[0];
			entry.posPair = splits[1];
			entry.pos = Util.getPosSequence(entry.posPair);
			
			corpus.add(entry);
		}
		System.out.println("Parsing corpus complete!");
		return corpus;
	}
}
