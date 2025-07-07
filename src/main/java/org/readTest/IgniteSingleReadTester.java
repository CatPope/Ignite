package org.readTest;

import org.apache.ignite.Ignition;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.query.ScanQuery;

import javax.cache.Cache;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IgniteSingleReadTester implements IgniteTestConstants {

    private static final String JDBC_SQL = "SELECT FACE_TMP FROM FACE.TBL_USER_AUTH WHERE USER_NO = 400000"; // 단건 조회 SQL
    private static final BigDecimal SEARCH_KEY = new BigDecimal(MAX_NUM); // 검색 키
    private static final int SCAN_COUNT = 1; // 조회 건수

    // JDBC 단건 조회 테스트
    public static void runJdbcSingleReadTest() throws Exception {
        System.out.println("======= JDBC 단건 조회 테스트 =======");

        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            for (int i = 1; i <= REPEAT_COUNT; i++) {
                long start = System.nanoTime(); // 시간 측정 시작

                try (PreparedStatement stmt = conn.prepareStatement(JDBC_SQL);
                     ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) { // 조회 결과 처리
                        String tmp = rs.getString(1);
                    }
                }

                long elapsed = System.nanoTime() - start; // 경과 시간
                double elapsedNs = elapsed / 1_000.0;
                System.out.printf("[ %d ] 데이터 : %d건  |  시간 : %.2fms\n", i, SCAN_COUNT, elapsedNs);

                PerformanceLogger.log("SINGLE", "ScanQuery", i, SCAN_COUNT, elapsedNs, "Ns");

                Thread.sleep(DELAY_MS); // 반복 간 대기
            }
        }
    }

    // KeyValue 단건 조회 테스트
    public static void runKeyValueSingleReadTest() throws Exception {
        System.out.println("======= KeyValue 단건 검색 조회 테스트 =======");

        ClientConfiguration cfg = new ClientConfiguration().setAddresses(ADDRESS);

        try (IgniteClient client = Ignition.startClient(cfg)) {
            ClientCache<BigDecimal, BinaryObject> cache = client.cache(CACHE_NAME).withKeepBinary();

            for (int i = 1; i <= REPEAT_COUNT; i++) {
                long start = System.nanoTime(); // 시간 측정 시작

                BinaryObject obj = cache.get(SEARCH_KEY); // 키 기반 단건 조회
                if (obj != null) {
                    String tmp = obj.field("FACE_TMP");
                }

                long elapsed = System.nanoTime() - start; // 경과 시간
                double elapsedNs = elapsed / 1_000.0;
                System.out.printf("[ %d ] 데이터 : %d건  |  시간 : %.2fms\n", i, SCAN_COUNT, elapsedNs);

                PerformanceLogger.log("SINGLE", "ScanQuery", i, SCAN_COUNT, elapsedNs, "Ns");

                Thread.sleep(DELAY_MS); // 반복 간 대기
            }
        }
    }

    // ScanQuery 단건 조회 테스트
    public static void runScanQuerySingleReadTest() throws Exception {
        System.out.println("======= ScanQuery 단건 검색 조회 테스트 =======");

        ClientConfiguration cfg = new ClientConfiguration().setAddresses("192.168.10.175:10800");

        try (IgniteClient client = Ignition.startClient(cfg)) {
            ClientCache<BigDecimal, BinaryObject> cache = client.cache(CACHE_NAME).withKeepBinary();

            for (int i = 1; i <= REPEAT_COUNT; i++) {
                long start = System.nanoTime(); // 시간 측정 시작

                for (Cache.Entry<Object, Object> entry : cache.query(new ScanQuery<>())) { // 전체 순회 검색
                    BigDecimal key = (BigDecimal) entry.getKey();
                    if (SEARCH_KEY.equals(key)) { // 키 일치 시 처리
                        BinaryObject obj = (BinaryObject) entry.getValue();
                        if (obj != null) {
                            String tmp = obj.field("FACE_TMP");
                        }
                        break; // 단건 검색 후 중단
                    }
                }

                long elapsed = System.nanoTime() - start; // 경과 시간
                double elapsedNs = elapsed / 1_000.0;
                System.out.printf("[ %d ] 데이터 : %d건  |  시간 : %.2fms\n", i, SCAN_COUNT, elapsedNs);

                PerformanceLogger.log("SINGLE", "ScanQuery", i, SCAN_COUNT, elapsedNs, "Ns");

                Thread.sleep(DELAY_MS); // 반복 간 대기
            }
        }
    }
}
