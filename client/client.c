#include "client_func.h"

int main(int argc, char *argv[]) {

	WSADATA wsaData;
	SOCKET clientSock;
	SOCKADDR_IN servAddr;
	
	char host[] = "127.0.0.1";
	int port = 8080;
	char message[30];
	int strLen;
	
	FILE *fp;
	char LOG_FILE[20];
    char systemClock[10];
    char clock[10];
	int answer;
	char res[10];
    int next;
    char sec[5];

	if(WSAStartup(MAKEWORD(2,2), &wsaData) != 0)
		errorHandling("WSAStartup() error!");
	
	clientSock = socket(PF_INET, SOCK_STREAM, 0);
	if(clientSock == INVALID_SOCKET)
		errorHandling("socket() error!");
	
	memset(&servAddr, 0, sizeof(servAddr));
	servAddr.sin_family = AF_INET;
	servAddr.sin_addr.s_addr = inet_addr(host);
	servAddr.sin_port = htons(port);
	
	if(connect(clientSock, (SOCKADDR*)&servAddr, sizeof(servAddr)) == SOCKET_ERROR)
		errorHandling("connect() error!");
	
    LOG_FILE[0] = '\0';
	strcat(LOG_FILE, fileNaming(argv[1]));
	fp = fopen(LOG_FILE, "w");

	informConnection(fp, argv[1]);
	
	srand(time(NULL));

	while(1){
		strLen = recv(clientSock, message, sizeof(message) - 1, 0);
		if(strLen <= 0)
			errorHandling("read() error!");

        systemClock[0] = message[strLen - 1] = clock[0] = '\0';
        
		if(!strncmp(message, "TIMEOUT", 7)){
            strcat(systemClock, message + 8);
			informTermination(fp, systemClock, argv[1]);
			break;
		}        

        strncat(systemClock, message, 7);

		informReceiving(fp, systemClock, message + 8);

		answer = computeAnswer(message);
        itoa(answer, res, 10);
        next = rand() % 5;
        itoa(next, sec, 10);

		message[0] = '\0';
        strcat(message, makeMessage(sec, res));
		
		send(clientSock, message, strlen(message) + 1, 0);

        strcat(clock, addGetSystemClock(systemClock, next));

		informSending(fp, clock, res);
	}
	
    fclose(fp);
	closesocket(clientSock);
	WSACleanup();
	
	return 0;
}