package org.nodeTest;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Collections;

public class ServerNode1 {
    public static void main(String[] args) {
        // Ignite 설정
        IgniteConfiguration cfg = new IgniteConfiguration();

        // 서버 모드 (기본값: false)
        cfg.setClientMode(false);

        // 클러스터에서 코드 공유 허용 여부
        cfg.setPeerClassLoadingEnabled(false);

        // 멀티캐스트 기반 서버 노드 찾기
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        // Ignite 노드 시작
        Ignite ignite = Ignition.start(cfg);

        System.out.println(">>> 서버 노드가 클러스터에 참여하였습니다. 노드 ID: " + ignite.cluster().localNode().id());

        // 서버 노드는 계속 살아있어야 하므로 main thread를 블록
        while (true) {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        ignite.close();
        System.out.println(">>> 서버 노드가 종료되었습니다.");
    }
}
