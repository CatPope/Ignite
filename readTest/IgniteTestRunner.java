package org.readTest;

public class IgniteTestRunner {

    public static void main(String[] args) throws Exception {
        System.out.println("========= Apache Ignite 성능 테스트 시작 =========");

        // 단일 읽기 테스트
        IgniteSingleReadTester.runJdbcSingleReadTest();
        IgniteSingleReadTester.runKeyValueSingleReadTest();
        IgniteSingleReadTester.runScanQuerySingleReadTest();
        // 전체 읽기 테스트
        IgniteAllReadTester.runJdbcAllReadTest();
        IgniteAllReadTester.runKeyValueAllReadTest();
        IgniteAllReadTester.runScanQueryAllReadTest();

        System.out.println("========= Apache Ignite 성능 테스트 종료 =========");
    }
}
