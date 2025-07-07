package org.nodeTest;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Collections;

public class HelloWorld {
    public static void main(String[] args) throws IgniteException {
        // Java API를 사용해 IgniteConfiguration 준비
        IgniteConfiguration cfg = new IgniteConfiguration();

        // 노드를 클라이언트 모드로 시작
        cfg.setClientMode(true);

        // 사용자 정의 Java 로직 클래스들을 네트워크를 통해 전송 가능하도록 설정
        cfg.setPeerClassLoadingEnabled(true);

        // 클라이언트가 서버 노드를 찾을 수 있도록 IP Finder 설정
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        // 노드 시작
        Ignite ignite = Ignition.start(cfg);

        // 캐시 생성 및 값 저장
        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("myCache");
        cache.put(1, "안녕하세요");
        cache.put(2, "세계!");

        System.out.println(">> 캐시를 생성하고 값을 추가했습니다.");

        // 서버 노드에서 사용자 정의 연산 실행
        ignite.compute(ignite.cluster().forServers()).broadcast(new RemoteTask());

        System.out.println(">> 연산 작업이 실행되었습니다. 서버 노드의 출력 결과를 확인하세요.");

        // 클러스터와 연결 종료
        ignite.close();
    }

    /**
     * 노드 ID, OS, JRE 정보를 출력하고,
     * 캐시에 저장된 데이터를 읽어 출력하는 연산 작업
     */
    private static class RemoteTask implements IgniteRunnable {
        @IgniteInstanceResource
        Ignite ignite;

        @Override
        public void run() {
            System.out.println(">> 연산 작업을 실행 중입니다.");

            System.out.println(
                    "   노드 ID: " + ignite.cluster().localNode().id() + "\n" +
                            "   OS: " + System.getProperty("os.name") +
                            "   JRE: " + System.getProperty("java.runtime.name"));

            IgniteCache<Integer, String> cache = ignite.cache("myCache");

            System.out.println(">> " + cache.get(1) + " " + cache.get(2));
        }
    }
}
