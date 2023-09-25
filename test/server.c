/*
WSAStartup(WORD wVersionRequested, LPWSADATA IpWSAData)
-> winsock을 초기화하고, 초기화된 정보를 wsaData에 대입.
-> 인자값으로 사용하고자 하는 winsock의 버전과 초기화된 정보를 받을 wsaData 구조체임

socket(int af, int type, int protocol)
-> 인자값으로 프로토콜 체계와 통신방식, 프로토콜을 받아 소켓을 생성.
-> PE_INET : IPv4 주소체계, SOCK_STREAM : TCP/IP 통신

bind(SOCKET s, const sockaddr *name, int namelen)
-> 소켓의 주소와 포트번호를 담는 구조체인 SOCKADDR을 인자값으로 넘겨 값을 설정
-> htonl, htons : IP와 Port 초기화 (시스템에 따라 바이트 순서가 다를 수 있으므로 네트워크 바이트 순서로 맞추기 위함)

listen(SOCKET s, int backlog)
-> 소켓의 값이 설정되었으므로 클라이언트의 접속을 기다림
-> backlog는 동시에 여러대의 클라이언트가 접속요청을 할 때 대기시킬 숫자

accept(SOCKET s, sockaddr *addr, int *addrlen)
-> 연결을 수락하고 클라이언트와 통신할 수 있도록 인자값으로 넣은 변수에 주소 정보를 저장
-> sockaddr구조체 : 소켓이 연결되어야 할 주소정보를 넣는 구조체

send(SOCKET s, const char *buf, int len, int flags)
-> 인자값으로 넣은 소켓의 정보를 통해 인자값으로 넣은 버퍼의 메세지 전송

closesocket(SOCKET s)
-> 인자값으로 넣은 소켓을 해제하고, 초기화한 WSA구조체들의 정보를 해제
*/

#include <stdio.h>
#include <stdlib.h>
#include <winsock2.h>

// 예외 처리
void ErrorHandling(char* message){
    fputs(message, stderr);
    fputc('\n', stderr);
    exit(1);
}

int main(int argc, char *argv[]){
    WSADATA     _wsaData;
    SOCKET      _serverSock, _clientSock;
    SOCKADDR_IN _serverAddr, _clientAddr;

    int     _port = 5001;
    char    _message[] = "Hello World!";
    int     _clientAddrSize;

    // Windows Socket Initiation (WSAData)
    if(WSAStartup(MAKEWORD(2, 2), &_wsaData) != 0)
        ErrorHandling("WSAStartup() error!");

    // Make Socket
    _serverSock = socket(PF_INET, SOCK_STREAM, 0);
    if(_serverSock == INVALID_SOCKET)
        ErrorHandling("hSocket() error!");
    memset(&_serverAddr, 0, sizeof(_serverAddr));
    _serverAddr.sin_family = AF_INET;
    _serverAddr.sin_addr.S_un.S_addr = htonl(INADDR_ANY);
    _serverAddr.sin_port = htons(_port);

    // Socket Link Set
    if(bind(_serverSock, (SOCKADDR*)&_serverAddr, sizeof(_serverAddr)) == SOCKET_ERROR)
        ErrorHandling("bind() error!");
    
    // Socket Link Wait
    if(listen(_serverSock, 5) == SOCKET_ERROR)
        ErrorHandling("listen() error!");
    
    // Accept Link
    _clientAddrSize = sizeof(_clientAddr);
    _clientSock = accept(_serverSock, (SOCKADDR*)&_clientAddr, &_clientAddrSize);
    if(_clientSock == INVALID_SOCKET)
        ErrorHandling("accept() error!");
    
    // Send Message
    send(_clientSock, _message, sizeof(_message), 0);

    // Close Socket
    closesocket(_clientSock);
    closesocket(_serverSock);
    WSACleanup();

    return 0;
}