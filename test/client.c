#include <stdio.h>
#include <stdlib.h>
#include <winsock2.h>
#include <time.h>

void errorHandling(char* message);

int computeAnswer(char *message);

void writeToLog(FILE *fp, char *message);

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
	char log[40];
	int answer;
	int next;
	char tmp[10];

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
	
	strcat(log, "Client");
	strcat(log, argv[1]);
	strcat(LOG_FILE, log);
	strcat(LOG_FILE, ".txt");
	fp = fopen(LOG_FILE, "w");

	strcat(log, " 연결됨");
	writeToLog(fp, log);
	
	srand(time(NULL));

	while(1){
		message[0] = '\0';
		strLen = recv(clientSock, message, sizeof(message) - 1, 0);
		if(strLen <= 0)
			errorHandling("read() error!");
		log[0] = '\0';
		if(!strncmp(message, "TIMEOUT", 7)){
			strcat(log, "Client");
			strcat(log, argv[1]);
			strcat(log, " 연결종료");
			writeToLog(fp, log);
			break;
		}
		
		strcat(log, "질문 받음: ");
		strcat(log, message);
		writeToLog(fp, log);
		answer = computeAnswer(message);

		log[0] = '\0';
		strcat(log, "계산 결과: ");
		itoa(answer, tmp, 10);
		strcat(log, tmp);
		writeToLog(fp, log);

		next = rand() % 5;
		message[0] = '\0';
		itoa(next, tmp, 10);
		strcat(message, tmp);
		strcat(message, " ");
		itoa(answer, tmp, 10);
		strcat(message, tmp);
		strcat(message, "\n");
		
		send(clientSock, message, strlen(message) + 1, 0);

		log[0] = '\0';
		strcat(log, "결과 전송: ");
		itoa(answer, tmp, 10);
		strcat(log, tmp);
		writeToLog(fp, log);
	}
	
	closesocket(clientSock);
	WSACleanup();
	
	return 0;
}

void errorHandling(char* message){
	WSACleanup();
	fputs(message, stderr);
	fputc('\n', stderr);
	exit(1);
}

int computeAnswer(char *message){
	int i, num1, num2, num3, temp;
    char operator1, operator2;
    for(i = 0; message[i] != ' '; i++);
    for(i++, temp = 0; message[i] != ' '; i++){
        temp *= 10;
        temp += (int)(message[i] - 48);
    }
    num1 = temp;
    operator1 = message[++i];
    for(i += 2, temp = 0; message[i] != ' '; i++){
        temp *= 10;
        temp += (int)(message[i] - 48);
    }
    num2 = temp;
    operator2 = message[++i];
    for(i += 2, temp = 0; message[i] != ' '; i++){
        temp *= 10;
        temp += (int)(message[i] - 48);
    }
    num3 = temp;

    if(operator1 == '*'){
        num1 *= num2;
        switch(operator2){
            case '+':
                return num1 + num3;
            case '-':
                return num1 - num3;
            case '*':
                return num1 * num3;
            case '/':
                return (num3 != 0) ? num1 / num3 : -1;
            default:
                return -1;
        }
    }
    else if(operator1 == '/'){
        if (num2 == 0)
            return -1;
        num1 /= num2;
        switch (operator2) {
            case '+':
                return num1 + num3;
            case '-':
                return num1 - num3;
            case '*':
                return num1 * num3;
            case '/':
                return (num3 != 0) ? num1 / num3 : -1;
            default:
                return -1;
        }
    }
    else if(operator1 == '+'){
        switch(operator2){
            case '+':
                return num1 + num2 + num3;
            case '-':
                return num1 + num2 - num3;
            case '*':
                return num1 + num2 * num3;
            case '/':
                return (num3 != 0) ? num1 + num2 / num3 : -1;
            default:
                return -1;
        }
    }
    else if(operator1 == '-'){
        switch(operator2){
            case '+':
                return num1 - num2 + num3;
            case '-':
                return num1 - num2 - num3;
            case '*':
                return num1 - num2 * num3;
            case '/':
                return (num3 != 0) ? num1 - num2 / num3 : -1;
            default:
                return -1;
        }
    }
    else
        return -1;
}

void writeToLog(FILE *fp, char *message){
	printf("%s\n", message);
	fprintf(fp, message);
}