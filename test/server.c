#include <stdio.h>
#include <stdlib.h>
#include <winsock2.h> // 윈도우즈기 때문에 들어 감 
#pragma comment(lib, "ws2_32.lib")

void ErrorHandling(char * message);

int main(int argc, char *argv[]) {
	
	WSADATA wsaData; // 윈도우즈최적화 
	SOCKET hServSock, hClntSock;
	SOCKADDR_IN servAddr, clntAddr;
	
	int szClntAddr;
	char message[30];
	int strLen;
	
	
	if(argc != 2){
		printf("Usage : %s <port>\n", argv[0]);
		exit(1);
	} 
	// 윈도우즈이기 때문에 WSAStartup 이 들어감.
	//소켓 만들기 전에 들어가는데, MAKEWORD 는 버전 확인용이라고 생각하면 된다. 
	if(WSAStartup(MAKEWORD(2,2),&wsaData)!=0)
		ErrorHandling("WSAStartup() error!");
	
	hServSock = socket(PF_INET, SOCK_STREAM, 0);
	if(hServSock == INVALID_SOCKET)
		ErrorHandling("socket() error");
	
	memset(&servAddr, 0, sizeof(servAddr));
	servAddr.sin_family=AF_INET;
	servAddr.sin_addr.s_addr = htonl(INADDR_ANY);
	servAddr.sin_port=htons(atoi(argv[1]));
	
	if(bind(hServSock, (SOCKADDR*) &servAddr, sizeof(servAddr))==SOCKET_ERROR)
		ErrorHandling("bind() error");
	
	if(listen(hServSock, 5)==SOCKET_ERROR)
		ErrorHandling("listen() error");
	
	szClntAddr = sizeof(clntAddr);
	hClntSock = accept(hServSock, (SOCKADDR*)&clntAddr,&szClntAddr);
	if(hClntSock == INVALID_SOCKET)
		ErrorHandling("accept() error");
	while(1){
		memset(message, '\0', 30);
		strLen = recv(hClntSock, message, sizeof(message)-1, 0);
		if(strLen == -1)
			ErrorHandling("read() error!");
		printf("Message from client : %s \n", message);
		printf("메시지 입력 >>");
		gets(message);
		send(hClntSock,message,strlen(message)+1,0);
		
	}
	
	closesocket(hClntSock);
	closesocket(hServSock);
	WSACleanup();
	
	return 0;
}

void ErrorHandling(char* message){
	fputs(message, stderr);
	fputc('\n', stderr);
	exit(1);
}