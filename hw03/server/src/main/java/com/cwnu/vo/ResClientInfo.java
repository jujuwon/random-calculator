package com.cwnu.vo;

import java.net.InetAddress;

/**
 * 서버가 클라이언트에게 해당 청크를 가지고 있는 클라이언트 정보를 응답할 때 사용하는 클래스
 * 예시
 * {
 * 	"client_id": 1,
 * 	"ip": "127.0.0.1"
 * }
 */
public class ResClientInfo {
	private int client_id;
	private InetAddress ip;

	public ResClientInfo(int id, InetAddress ip) {
		this.client_id = id;
		this.ip = ip;
	}
}
