package hkh.nlp.hmm;

/**
 * 품사 CLASS
 * @author hkh
 *
 */
public class Pos {
	
	public String name = null;
	
	/**
	 * Observation Probability - Log10 값
	 */
	public double observationProb = 0f;
	
	/**
	 * Transition Probability - Log10 값
	 */
	public double transitionProb = 0f;
	
	public Pos(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
