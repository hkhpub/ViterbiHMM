package hkh.nlp.hmm;

import java.util.ArrayList;

/**
 * 어절별 상태 - "너를 사랑해" -> state1 (시작) - state2 (너를) - state3 (사랑해)
 * @author hkh
 *
 */
public class State {
	
	public static String START = "!<start>!";
	/**
	 * 형태소명
	 */
	public String morpheme = null;
	
	/**
	 * 품사 리스트
	 */
	private ArrayList<Pos> posList = null;
	
	/**
	 * 선택된 optimal pos index
	 */
	public int optimal = 0;
	
	public State(String morpheme) {
		this.morpheme = morpheme;
		this.posList = new ArrayList<Pos>();
	}
	
	public Pos getOptimalPos() {
		return getPosList().get(optimal);
	}
	
	public void addPos(String str) {
		Pos pos = new Pos(str);
		posList.add(pos);
	}
	
	public ArrayList<Pos> getPosList() {
		return posList;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(">>"+morpheme+"\n");
		for (int i=0; i<posList.size(); i++) {
			sb.append(posList.get(i)+"\n");
		}
		return sb.toString();
	}
}
