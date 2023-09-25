/*
connect(SOCKET s, const sockaddr *name, int namelen)
-> 인자값으로 넣은 소켓과 소켓 주소정보를 이용해 서버에 연결을 신청하는 함수

recv(SOCKET s, char *buf, int len, int flags)
-> 인자값으로 넣은 소켓을 통해서 메세지 수신
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>

// 예외 처리
void ErrorHandling(char* message){
    fputs(message, stderr);
    fputc('\n', stderr);
    exit(1);
}

int main(int argc, _TCHAR *argv[]){
    WSADATA     _wsaData;
    SOCKET      _socket;
    SOCKADDR_IN _serverAddr;

    int     _port = 5001;
    char    _message[30];
    int     _strlen;

    // Windows Socket Initiation (WSAData)
    if(WSAStartup(MAKEWORD(2, 2), &_wsaData) != 0)
        ErrorHandling("WSAStartup() error!");

    // Make Socket
    _socket = socket(PF_INET, SOCK_STREAM, 0);
    if(_socket == INVALID_SOCKET)
        ErrorHandling("hSocket() error!");
    memset(&_serverAddr, 0, sizeof(_serverAddr));
    _serverAddr.sin_family = AF_INET;
    _serverAddr.sin_port = htons(_port);
    inet_pton(AF_INET, "127.0.0.1", &_serverAddr.sin_addr.s_addr);

    // Connet Link
    if(connect(_socket, (SOCKADDR*)&_serverAddr, sizeof(_serverAddr)) == SOCKET_ERROR)
        ErrorHandling("connect() error!");
    
    // Receive Message
    _strlen = recv(_socket, _message, sizeof(_message) - 1, 0);
    if(~_strlen)
        ErrorHandling("read() error!");
    
    // Output Message
    printf("message from server : %s\n", _message);

    // Close Socket
    closesocket(_socket);
    WSACleanup();

    return 0;
}