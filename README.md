# random-calculator
2023 fa 데이터통신 과제

## 패키지 구조
- `Source code`
  - `server` : 서버 소스코드
  - `client` : 클라이언트 소스코드
  - `log`    : 작성된 로그 파일
- `AllDefinedLogs.txt`
  - 프로그램에서 정의한 모든 Log message format 명세 및 설명
- `download.txt`
  - 5분 이내 설명 동영상 download link
- `Readme.txt`
  - 조 이름, 모든 조원 학번&이름
    - 학생별 역할 명시
  - 프로그램 구성요소 설명
  - 소스코드 컴파일 방법 명시
  - 프로그램 실행환경 및 실행방법 설명
  - Error or Additional Message Handling 에 대한 사항 설명
  - Additional Comments : 추가로 과제 제출 관련 언급할 내용 작성

## 실행방법
- client

```shell
gcc -o client.exe client.c -lws2_32
./client.exe ${client_id}
# 컴파일은 1번만 실행하면 됩니다.
# 예시 ./client.exe 1
```

- server
  
```shell
./server/run.sh
```
shell 파일을 실행하면 jar 파일 생성 후 실행합니다.
shell 파일이 실행되지 않을 경우 권한 추가가 필요합니다.
```shell
#예시
chmod +x ./client/build.sh
```
