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
char* makeMessage(char *sec, char *res);
char* addGetSystemClock(char *clock, int next);
void informSending(FILE *fp, char *systemClock, char *res);

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

char* makeMessage(char *sec, char *res){
    static char MSG[30];
    MSG[0] = '\0';
    strcat(MSG, sec);
	strcat(MSG, " ");
	strcat(MSG, res);
	strcat(MSG, "\n");
    return MSG;
}

char* addGetSystemClock(char *systemclock, int next){
    static char time[10];
    time[0] = '[';

    int min = 0, sec = 0, sc = 0;
    if(systemclock[1] == '1')
        min += 600;
    min += (int)(systemclock[2] - 48) * 60;
    sec = (int)(systemclock[4] - 48) * 10 + (int)(systemclock[5] - 48);
    sc = min + sec + next;
    
    if(sc >= 600){
        time[1] = '1';
        time[2] = '0';
    }
    else{
        time[1] = '0';
        time[2] = (char)(sc / 60 + 48);
    }
    time[3] = ':';
    sc %= 60;
    time[4] = (char)(sc / 10 + 48);
    time[5] = (char)(sc % 10 + 48);
    time[6] = ']';
    time[7] = '\0';
    return time;
}

void informSending(FILE *fp, char *systemclock, char *res){
    char log[30] = {};
    strcat(log, systemclock);
    strcat(log, " Send Answer : ");
	strcat(log, res);
	writeToLog(fp, log);
}