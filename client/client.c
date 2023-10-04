#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <winsock2.h>

void errorHandling(char* message);
int computeAnswer(char *message);
void writeToLog(FILE *fp, char *message);

char* fileNaming(char* id);
void informConnection(FILE *fp, char *id);
void informTermination(FILE *fp, char *systemClock, char *id);
void informReceiving(FILE *fp, char *systemClock, char *message);
char* makeMessage(char *res);
void informSending(FILE *fp, char *systemClock, char *res);

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
	int answer;
	char res[10];

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

        systemClock[0] = message[strLen - 1] = '\0';
        
		if(!strncmp(message, "TIMEOUT", 7)){
            strcat(systemClock, message + 8);
			informTermination(fp, systemClock, argv[1]);
			break;
		}        

        strncat(systemClock, message, 7);

		informReceiving(fp, systemClock, message + 8);

		answer = computeAnswer(message);
        
		message[0] = '\0';
        itoa(answer, res, 10);
		strcat(message, makeMessage(res));
		
		send(clientSock, message, strlen(message) + 1, 0);
		informSending(fp, systemClock, res);
	}
	
    fclose(fp);
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
	fprintf(fp, "%s\n", message);
}

char* fileNaming(char *id){
    static char NAME[25] = {};
    strcat(NAME, "../log/Client");
    strcat(NAME, id);
    strcat(NAME, ".txt");
    return NAME;
}

void informConnection(FILE *fp, char *id){
    char log[30] = {};
    strcat(log, "[00:00] Client");
    strcat(log, id);
    strcat(log, " Connected.");
    writeToLog(fp, log);
}

void informTermination(FILE *fp, char *systemclock, char *id){
    char log[30] = {};
    strcat(log, systemclock);
    strcat(log, " Client");
    strcat(log, id);
    strcat(log, " Terminated.");
    writeToLog(fp, log);
}

void informReceiving(FILE *fp, char *systemclock, char *message){
    char log[40] = {};
    strcat(log, systemclock);
    strcat(log, " Question : \"");
	strcat(log, message);
    strcat(log, "\"");
	writeToLog(fp, log);
}

char* makeMessage(char *res){
    static char MSG[30];
    char sec[5];
    itoa(rand() % 5, sec, 10);
    MSG[0] = '\0';
    strcat(MSG, sec);
	strcat(MSG, " ");
	strcat(MSG, res);
	strcat(MSG, "\n");
    return MSG;
}

void informSending(FILE *fp, char *systemclock, char *res){
    char log[30] = {};
    strcat(log, systemclock);
    strcat(log, " Send Answer : ");
	strcat(log, res);
	writeToLog(fp, log);
}