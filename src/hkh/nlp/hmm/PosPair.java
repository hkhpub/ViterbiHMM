package hkh.nlp.hmm;

import hkh.nlp.util.Util;

/**
 * 품사 CLASS
 * @author hkh
 *
 */
public class PosPair {
	
	public String name = null;
	
	public String pos = null;
	/**
	 * Observation Probability
	 */
	public double observationProb = 0f;
	public double transitionProb = 0f;
	
	public PosPair(String posPair) {
		this.name = posPair;
		this.pos = Util.getPosSequence(posPair);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
