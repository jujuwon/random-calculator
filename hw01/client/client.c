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

	// Socket Connection
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
	
	// File Naming
    LOG_FILE[0] = '\0';
	strcat(LOG_FILE, fileNaming(argv[1]));
	fp = fopen(LOG_FILE, "w");

	// Connection Logging
	informConnection(fp, argv[1]);
	
	// Code for Generating Random Numbers
	srand(time(NULL));

	// Socket Communication
	while(1){
		// Receive Message
		strLen = recv(clientSock, message, sizeof(message) - 1, 0);
		if(strLen <= 0)
			errorHandling("read() error!");

        systemClock[0] = message[strLen - 1] = clock[0] = '\0';
        
		// Timeout Checking
		if(!strncmp(message, "TIMEOUT", 7)){
            strcat(systemClock, message + 8);
			informTermination(fp, systemClock, argv[1]);
			break;
		}        

        strncat(systemClock, message, 7);

		// Message Reception Logging
		informReceiving(fp, systemClock, message + 8);

		// Calculation
		answer = computeAnswer(message);
        itoa(answer, res, 10);
        next = rand() % 5 + 1;
        itoa(next, sec, 10);

		message[0] = '\0';
        strcat(message, makeMessage(sec, res));
		
		// Send Message
		send(clientSock, message, strlen(message) + 1, 0);

		// Message Transmission Logging
        strcat(clock, addGetSystemClock(systemClock, next));
		informSending(fp, clock, res);
	}
	
    fclose(fp);
	closesocket(clientSock);
	WSACleanup();
	
	return 0;
}