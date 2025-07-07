package org.readTest;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 성능 로그 CSV 기록용 클래스
public class PerformanceLogger {
    private static final String LOG_FILE = "logs/ignite_perf_log.csv"; // 로그 파일 경로
    private static boolean initialized = false; // 헤더 기록 여부

    // 테스트 성능 로그 기록
    public static synchronized void log(String testType, String testName, int iteration, int rowCount, double elapsed, String elapsedUint) {
        try {
            if (!initialized) { // 최초 호출 시 CSV 헤더 기록
                try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, false))) {
                    pw.println("Timestamp,TestType,TestName,Iteration,RowCount,Elapsed,Uint");
                }
                initialized = true;
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) { // 로그 한 줄 추가
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                pw.printf("%s,%s,%s,%d,%d,%.2f,%s%n", timestamp, testType, testName, iteration, rowCount, elapsed, elapsedUint);
            }

        } catch (IOException e) {
            System.out.println("CSV 로그 기록 실패: " + e.getMessage());
        }
    }
}
