package org.chack;

import java.sql.*;
import java.util.Scanner;

/*
데이터 파악을 위해 JDBC로 구성한 코드입니다.
SQL문을 입력하여 정보를 확인 할 수 있습니다.
아래는 sql문 예시입니다.

- 스키마 확인
SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA;
- 스키마의 테이블 목록 확인
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'FACE';
- 테이블 컬럼 정보 확인
SELECT COLUMN_NAME, TYPE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'FACE' AND TABLE_NAME = 'TBL_USER_AUTH';
- 필드의 개 수 확인
SELECT COUNT(*) FROM FACE.TBL_USER_AUTH;
- USER_NO로 템플릿 조회
SELECT * FROM FACE.FACE_TMP WHERE USER_NO = 12345;
- 앞에서 부터 10개 조회
SELECT * FROM FACE.TBL_USER_AUTH LIMIT 10;
- 특정 컬럼 10개 조회
SELECT _KEY FROM FACE.TBL_USER_AUTH LIMIT 10;
 */

public class IgniteJDBCExplorer {
    public static void main(String[] args) {
        String url = "jdbc:ignite:thin://192.168.10.175:10800?keepBinary=true"; // 바이너리 직렬화
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("Ignite JDBC 접속 완료.");
            while (true) {
                System.out.print("\nSQL 입력 (종료하려면 'exit'): ");

                String sql = scanner.nextLine().trim();

                if ("exit".equalsIgnoreCase(sql)) break;

                long start = System.nanoTime();
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {


                    ResultSetMetaData meta = rs.getMetaData();
                    int colCount = meta.getColumnCount();

                    long elapsed = System.nanoTime() - start;

                    // 헤더 출력
                    System.out.println("\n===================");
                    for (int i = 1; i <= colCount; i++) {
                        System.out.print(meta.getColumnName(i) + "\t");
                    }
                    System.out.println("\n===================");

                    // 내용 출력
                    while (rs.next()) {
                        for (int i = 1; i <= colCount; i++) {
                            System.out.print(rs.getString(i) + "\t");
                        }
                        System.out.println();
                    }

                } catch (SQLException e) {
                    System.out.println("쿼리 오류: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("DB 연결 실패: " + e.getMessage());
        }

        scanner.close();
        System.out.println("종료되었습니다.");
    }
}
