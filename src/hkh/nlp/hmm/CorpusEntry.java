package hkh.nlp.hmm;

public class CorpusEntry {

	/**
	 * 문장 시작점 여부
	 */
	public boolean isBegin = false;
	
	/**
	 * 단어 - 예: (결정을)
	 */
	public String word = null;
	
	/**
	 * 품사단어 조합 - 예: (결정/NNG+을/JKO)
	 */
	public String lex = null;
}
