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
import java.sql.ResultSet;
import java.sql.Statement;

public class IgniteAllReadTester implements IgniteTestConstants {

    // JDBC 방식 전체 조회 테스트
    public static void runJdbcAllReadTest() throws Exception {
        System.out.println("======= JDBC 전체 조회 테스트 =======");
        for (int i = 1; i <= 3; i++) {
            try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
                long start = System.nanoTime(); // 시간 측정 시작
                int count = 0;
                String sql = "SELECT FACE_TMP FROM FACE.TBL_USER_AUTH LIMIT " + MAX_NUM;
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) { // 결과 순회
                        rs.getString(1);
                        count++;
                    }
                }
                long elapsed = System.nanoTime() - start; // 경과 시간
                long elapsedMs = elapsed / 1_000_000;
                System.out.printf("[ %d ] 데이터 : %d건  |  시간 : %dms\n", i, count, elapsedMs);

                PerformanceLogger.log("ALL", "JDBC", i, count, elapsedMs, "Ms");

                Thread.sleep(DELAY_MS); // 딜레이 후 반복
            }
        }
    }

    // KeyValue 방식 전체 조회 테스트
    public static void runKeyValueAllReadTest() throws Exception {
        System.out.println("======= KeyValue 전체 조회 테스트 =======");

        ClientConfiguration cfg = new ClientConfiguration().setAddresses(ADDRESS);

        for (int i = 1; i <= 3; i++) {
            try (IgniteClient client = Ignition.startClient(cfg)) {
                ClientCache<BigDecimal, BinaryObject> cache = client.cache(CACHE_NAME).withKeepBinary();

                long start = System.nanoTime(); // 시간 측정 시작
                int count = 0;
                for (int key = 1; key <= MAX_NUM; key++) { // 키 순회 조회
                    BinaryObject obj = cache.get(new BigDecimal(key));
                    if (obj != null) obj.field("FACE_TMP");
                    count++;
                }
                long elapsed = System.nanoTime() - start; // 경과 시간
                long elapsedMs = elapsed / 1_000_000;
                System.out.printf("[ %d ] 데이터 : %d건  |  시간 : %dms\n", i, count, elapsedMs);

                PerformanceLogger.log("ALL", "KeyValue", i, count, elapsedMs, "Ms");

                Thread.sleep(DELAY_MS); // 딜레이 후 반복
            }
        }
    }

    // ScanQuery 방식 전체 조회 테스트
    public static void runScanQueryAllReadTest() throws Exception {
        System.out.println("======= ScanQuery 전체 조회 테스트 =======");

        ClientConfiguration cfg = new ClientConfiguration().setAddresses(ADDRESS);

        try (IgniteClient client = Ignition.startClient(cfg)) {
            ClientCache<BigDecimal, BinaryObject> cache = client.cache(CACHE_NAME).withKeepBinary();

            for (int i = 1; i <= 3; i++) {
                long startScan = System.nanoTime(); // 시간 측정 시작
                int scanCount = 0;
                for (Cache.Entry<Object, Object> entry : cache.query(new ScanQuery<>())) { // ScanQuery 순회
                    BinaryObject obj = (BinaryObject) entry.getValue();
                    obj.field("FACE_TMP");
                    scanCount++;
                }
                long elapsedScan = System.nanoTime() - startScan; // 경과 시간
                long elapsedMs = elapsedScan / 1_000_000;
                System.out.printf("[ %d ] 데이터 : %d건  |  시간 : %dms\n", i, scanCount, elapsedMs);

                PerformanceLogger.log("ALL", "ScanQuery", i, scanCount, elapsedMs, "Ms");

                Thread.sleep(DELAY_MS); // 딜레이 후 반복
            }
        }
    }
}
