package org.example.vo;

import java.net.InetAddress;

// 어느 클라이언트로부터 청크를 받을지 관리
public class ResClientInfo {
    private int clientId;
    private InetAddress ip;

    public int getClientId() {
        return clientId;
    }
}
