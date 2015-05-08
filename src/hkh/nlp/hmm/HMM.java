package hkh.nlp.hmm;

import hkh.nlp.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HMM {

	private ArrayList<State> states = null;
	private ArrayList<CorpusEntry> corpus = null;
	private int[] backtrace = null;
	private int V = 0;
	
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
		state.addPosPair("");
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
				state.addPosPair(splits[1]);
			}
		}
		
		backtrace = new int[states.size()];
	}
	
	public void setCorpus(ArrayList<CorpusEntry> corpus) {
		this.corpus = corpus;
		Set<String> distinctSet = new HashSet<String>();
		for (CorpusEntry entry : corpus) {
			distinctSet.add(entry.pos);
		}
		V = distinctSet.size();
		setObservationProb(corpus);
	}
	
	public void viterbi() {
		backtrace[0] = 0;
		
		for (int i=1; i<states.size(); i++) {
			State state = states.get(i);
			State before = states.get(i-1);
			calcTrainsitionProbability(corpus, before.getOptimalPos(), state);
			
			ArrayList<PosPair> posList = state.getPosList();
			double maximum = -1*Float.MAX_VALUE;
			int optimalIndex = 0;
			for (int j=0; j<posList.size(); j++) {
				PosPair pos = posList.get(j);
				double prob = Math.log10(pos.observationProb)+Math.log10(pos.transitionProb);
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
				PosPair pair = state.getPosList().get(i);
				String pos = Util.getPosSequence(pair.name);
				calcObservationProbability(corpus, pair, pos);
			}
		}
	}
	
	/**
	 * Observation 확률계산
	 * @param corpus
	 * @param pair
	 * @param pos
	 * @return
	 */
	private double calcObservationProbability(ArrayList<CorpusEntry> corpus, PosPair pair, String pos) {
		double prob = 0f;
		int L = 0; 		// L 개수
		int W = 0;		// (W, L) 조합개수
		
		for (CorpusEntry corpusEntry : corpus) {
			if (Util.equals(corpusEntry.posPair, pair.name)) {
				W++;
			}
			if (Util.equals(corpusEntry.pos, pos)) {
				L++;
			}
		}
		
		// laplace smoothing
		pair.observationProb = (W+1)/(double)(L+V);
		System.out.println(String.format("%s: (%d/%d) = %.8f", pair.name, (W+1), (L+V), pair.observationProb));
		
		return prob;
	}
	
	/**
	 * 
	 * @param corpus
	 * @param before	bigram 첫 번째 state
	 * @param state		bigram 두 번째 state
	 * @param posPair
	 * @return
	 */
	private double calcTrainsitionProbability(ArrayList<CorpusEntry> corpus, PosPair optimalPOS, State state) {
		double prob = 0f;
		int L_bigram = 0; 	// C(Wn-1 Wn)
		int L_unigram = 0;	// C(Wn-1)
		
		ArrayList<PosPair> posList = state.getPosList();
		for (PosPair pair : posList) {
			for (int i=0; i<corpus.size()-1; i++) {
				String corpusPos1 = corpus.get(i).pos;
				String corpusPos2 = corpus.get(i+1).pos;
				if (Util.equals(optimalPOS.name, corpusPos1) && Util.equals(pair.name, corpusPos2)) {
					L_bigram++;
				}
				if (Util.equals(optimalPOS.name, corpusPos1)) {
					L_unigram++;
				}
			}
			// laplace smoothing
			pair.transitionProb = (L_bigram+1)/(double)(L_unigram+V);
			System.out.println(String.format("%s: (%d/%d) = %.8f", pair.name, L_bigram+1, L_unigram+V, pair.transitionProb));
		}
		
		return prob;
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
