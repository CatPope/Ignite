package org.chack;

import org.apache.ignite.Ignition;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;

import java.util.Collection;

public class CheckCache {
    public static void main(String[] args) {
        ClientConfiguration cfg = new ClientConfiguration()
                .setAddresses("192.168.10.175:10800");

        try (IgniteClient igniteClient = Ignition.startClient(cfg)) {
            Collection<String> cacheNames = igniteClient.cacheNames();

            System.out.println("=== 현재 존재하는 캐시 목록 ===");
            for (String name : cacheNames) {
                System.out.println(name);
            }
        } catch (Exception e) {
            System.err.println("Ignite 클러스터 연결 또는 캐시 조회 중 오류 발생:");
            e.printStackTrace();
        }
    }
}
