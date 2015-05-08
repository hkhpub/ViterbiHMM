# ViterbiHMM
HMM POS Tagging in java using viterbi algorithm 

by hkh 2015-04-18

# 1. 실행 방법

	1) 형태소분석결과를 result.txt 파일명으로 프로젝트 폴더에 저장합니다. (EUC-KR) encoding 되어야 함.
	
	2) 사전을 train.txt 파일명으로 프로젝트 폴더에 저장합니다.
	
	3) hkh.nlp.Main을 실행합니다.
	
	4) 품사 태깅 결과는 pos_result.txt파일에 저장되고 , 분석 과정은 console에 출력합니다.

	
# 2. 프로그램 내부 구조.

	1) 본 프로그램의 코어 로직은 HMM 클래스로 추상화 되었습니다. 다음과 같은 과정으로 품사 태깅합니다.

		HMM hmm = new HMM();
		hmm.setNetwork(morphemes);		// 형태소분석 결과로 HMM 네트워크를 구성합니다.
		hmm.setCorpus(corpus);			// train.txt 사전을 등록합니다.
		hmm.viterbi();						// viterbi 알고리즘으로 품사 태깅합니다.
		hmm.printSolution();
		hmm.getSolution(sb);				// 파일에 저장할 솔루션을 출력합니다.
		
	2) State.class
	
		 어절별 상태 - "너를 사랑해" -> state0 (시작) - state1 (너를) - state2 (사랑해)
	
	3) PosPair.class
	
		특정 상태에 해당하는 품사조합
		
		생성확률, 전이확률, 품사조합(같은, VA+ETM), 품사(VA+ETM)
	
	4) 기타 자세한 사항은 프로그램에 주석처리로 명시했습니다. 


