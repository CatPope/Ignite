package org.readTest;

// Ignite 테스트용 상수 정의
public interface IgniteTestConstants {
    String CACHE_NAME = "SQL_FACE_TBL_USER_AUTH"; // 테스트 대상 캐시 이름
    String JDBC_URL = "jdbc:ignite:thin://192.168.10.175:10800?keepBinary=true"; // JDBC 접속 URL
    int MAX_NUM = 400_000; // 최대 조회 키 번호
    int REPEAT_COUNT = 50; // 반복 횟수
    int DELAY_MS = 3000; // 반복 간 딜레이(ms)
    String ADDRESS = "192.168.10.175:10800"; // Ignite 서버 주소
}
