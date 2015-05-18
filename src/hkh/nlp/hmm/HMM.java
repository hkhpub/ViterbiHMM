package hkh.nlp.hmm;

import hkh.nlp.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HMM {

	private ArrayList<State> states = null;
	private ArrayList<CorpusEntry> corpus = null;
	private int[] backtrace = null;
	private int V_lex = 0;
	
	public HMM() {
		this.states = new ArrayList<State>();
	}
	
	/**
	 * 형태소분석 결과로 HMM 네트워크를 구성한다.
	 * @param morphemes
	 */
	public void setNetwork(ArrayList<String> morphemes) {
		// 시작상태 생성
		State state = new State(State.START);
		state.addPos("");
		states.add(state);
		
		for (int i=0; i<morphemes.size(); i++) {
			String morpheme = morphemes.get(i);
			if (morpheme.length() == 0)
				continue;
			
			String[] splits = morpheme.split(".\\s");
			if (splits.length == 1) {
				// 형태소, 새 State 생성
				state = new State(morpheme);
				states.add(state);
			
			} else {
				// 품사추가
				state.addPos(splits[1]);
			}
		}
		
		backtrace = new int[states.size()];
	}
	
	public void setCorpus(ArrayList<CorpusEntry> corpus) {
		this.corpus = corpus;
		Set<String> lexSet = new HashSet<String>();
		for (CorpusEntry entry : corpus) {
			lexSet.add(entry.lex);
		}
		V_lex = lexSet.size();
		setObservationProb(corpus);
	}
	
	public void viterbi() {
		backtrace[0] = 0;
		
		for (int i=1; i<states.size(); i++) {
			State state = states.get(i);
			State before = states.get(i-1);
			calcTrainsitionProbability(corpus, before.getOptimalPos(), state);
			
			ArrayList<Pos> posList = state.getPosList();
			double maximum = -1*Float.MAX_VALUE;
			int optimalIndex = 0;
			for (int j=0; j<posList.size(); j++) {
				Pos pos = posList.get(j);
				double prob = pos.observationProb+pos.transitionProb;
				if (prob > maximum) {
					maximum = prob;
					optimalIndex = j;
				}
			}
			state.optimal = optimalIndex;
			backtrace[i] = optimalIndex;
		}
	}
	
	/**
	 * Observation 확률설정
	 * @param corpus
	 */
	public void setObservationProb(ArrayList<CorpusEntry> corpus) {
		for (State state : states) {
			for (int i=0; i<state.getPosList().size(); i++) {
				Pos pos = state.getPosList().get(i);
				calcObservationProbability(corpus, pos);
			}
		}
	}
	
	/**
	 * Observation 확률계산
	 * @param corpus
	 * @param pos
	 * @return
	 */
	private void calcObservationProbability(ArrayList<CorpusEntry> corpus, Pos pos) {
		double prob = 0f;
		
		// 우리/NP+집/NNG+에/JKB
		// 생성확률
		String[] series = pos.name.split("[+]");
		series = Util.removeEmpty(series);
		for (String item : series) {
			int index = item.indexOf('/');
			int LW = 0; 		// L 개수
			int L = 0;		// (W, L) 조합개수
			double obProb = 0f;
			String word = item.substring(0, index);
			String lex = item.substring(index+1);
			for (CorpusEntry entry : corpus) {
				if (Util.equals(lex, entry.lex)) {
					L++;
					if (Util.equals(word, entry.word)) {
						LW++;
					}
				}
			}
			// laplace smoothing
			obProb = (LW+1)/(double)(L+V_lex);
			prob += Math.log10(obProb);
		}
		
		// 전이확률
		for (int i=0; i<series.length-1; i++) {
			double transitionProb = 0f;
			String item1 = series[i];
			String item2 = series[i+1];
			String lex1 = item1.substring(item1.indexOf('/')+1);
			String lex2 = item2.substring(item2.indexOf('/')+1);
			int L1=0, L12 = 0;
			for (int j=0; j<corpus.size()-1; j++) {
				if (Util.equals(corpus.get(j).lex, lex1)) {
					L1++;
				}
				if (Util.equals(corpus.get(j+1).lex, lex2)) {
					L12++;
				}
			}
			// laplace smoothing
			transitionProb = (L12+1)/(double)(L1+V_lex);
			prob += Math.log10(transitionProb);
		}
		System.out.println(String.format("생성확률 - %s: %f", pos.name, prob));
		
		pos.observationProb = prob;
	}
	
	/**
	 * 어절사이 전이확률 계산.
	 * @param corpus
	 * @param currentPos
	 * @param state
	 * @return
	 */
	private void calcTrainsitionProbability(ArrayList<CorpusEntry> corpus, Pos currentPos, State state) {
		int L12 = 0;
		int L1 = 0;
		String lex1 = "";
		
		// currentPos - 우리/NP+집/NNG+에/JKB
		String[] series1 = currentPos.name.split("[+]");
		if (series1.length > 1) {
			series1 = Util.removeEmpty(series1);
			lex1 = series1[series1.length-1].split("/")[1];		// JKB
		}
		
		ArrayList<Pos> posList = state.getPosList();
		for (Pos nextPos : posList) {
			// nextPos - 오/VV+아/EC+ㅆ/EC+니/EF+?/SF
			// P(optimalPos|pos) 구해야 함
			String[] series2 = nextPos.name.split("[+]");
			series2 = Util.removeEmpty(series2);
			String lex2 = series2[0].split("/")[1];					// VV
			
			for (int i=0; i<corpus.size()-1; i++) {
				String corpusLex1 = corpus.get(i).lex;
				String corpusLex2 = corpus.get(i+1).lex;
				if (Util.equals(lex1, corpusLex1)) {
					L1++;
					if (Util.equals(lex2, corpusLex2)) {
						L12++;
					}
				}
			}
			// laplace smoothing
			nextPos.transitionProb = Math.log10((L12+1)/(double)(L1+V_lex));
			System.out.println(String.format("전이확률 - %s: %.8f (%d/%d)", nextPos.name, nextPos.transitionProb, L12+V_lex, L1+1));
		}
	}
	
	public void printSolution() {
		System.out.println("\n----- 최종 품사 열 -----");
		for (int i=0; i<states.size(); i++) {
			int optimal = backtrace[i];
			System.out.print(states.get(i).getPosList().get(optimal)+" ");
		}
		System.out.println("\n");
	}
	
	public void getSolution(StringBuffer sb) {
		for (int i=0; i<states.size(); i++) {
			int optimal = backtrace[i];
			sb.append(states.get(i).getPosList().get(optimal)+" ");
		}
		sb.append("\n");
	}
	
	public void printHMM() {
		for (State state : states) {
			System.out.println(state.toString());
		}
	}
}
