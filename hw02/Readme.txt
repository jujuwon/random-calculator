1. 학생별 역할
 - 조 이름 : 데이터 통신 과제 17조

 - 조원 및 역할
  - 이주원 (20183085) : 서버 구현, 프로그램 설계
  - 윤태현 (20193075) : 클라이언트 구현, 영상 촬영, 텍스트 파일 정리


2. 프로그램 구성요소
 - server
  - 시스템 클락 객체를 관리하며, 클라이언트 4개가 연결되면 동작을 시작합니다.
  - 각 라운드가 시작될 때마다 클라이언트에 시작 메시지를 전달합니다.
  - 라운드 100이 종료될 때까지 클라이언트의 행렬 연산 결과를 받아 저장합니다.
  - 라운드 100까지 종료되면, 각 라운드 별 소요 시간과 행렬 결과를 출력합니다.

 - client
  - 서버와 연결하여, 서버에게 행렬 연산 결과를 전송합니다.
  - 클라이언트 간 행과 열을 전달하여 연산을 진행합니다.
  - 각 라운드가 끝날 때마다 서버에 종료 메시지를 전달합니다.
  - 각 라운드를 100번 반복하고 종료합니다.


3. 소스코드 컴파일 방법
 - server : 프로그램 실행 방법에 명시되어 있는대로 실행시키면 됩니다.
  - Linux 환경에서 run.sh 파일 실행 시 바로 실행됩니다.
  - gradle 을 이용해 jar 파일로 빌드 후, 빌드된 jar 파일을 실행합니다.

 - client : 일반적인 java 컴파일 방식을 사용합니다.
  - 총 4개의 클라이언트 파일로 구성되어 있어 4번의 javac Client{id} 를 실행합니다.
   ex) javac Client1.java

4. 프로그램 실행 환경 및 실행 방법
 - 프로그램 실행 환경
   - 서버 : Ubuntu22.04, Java 11
   - 클라이언트 : Windows, Java 17(Java 11)
  
 - 프로그램 실행 방법
  - PuTTY 실행 방법
   -> 첨부한 개인키가 사용되지 않으면 후술한 방법대로 개인키를 생성하고 개인키를 더블 클릭해준 뒤 PuTTY를 실행시킵니다.
     윈도우 환경이므로 ubuntu를 입력하면 PuTTY 실행이 완료됩니다.
     그 후 서버 폴더에서 서버를 실행시키면 됩니다.
  - PuTTY 개인키 생성 방법
   -> PuTTYgen 에서 random.pem 키를 Load한 후 Save private key를 눌러 개인키를 생성하고 저장해줍니다.
   
  - server : ./server/run.sh
  - client : ./client/java Client{id}
   ex) java Client1


5. Server-Client 및 Client간 synchronization 및 serialization 수행 방법
 - synchronized 옵션을 사용해 synchronization을 수행하였다.
 - 따로 serialization은 고려하지 않았다.


6. Error or Additional Message Handling
 - 서버
  - 멀티쓰레딩 환경에서 전역 시계에 접근할 때, 동시성 문제를 해결하기 위해 AtomicInteger 와 synchronized 를 활용하였습니다.
  - Logging 을 위한 File IO 시, IOException 이 발생하면 stackTrace 를 출력하도록 예외처리를 하였습니다.
  
 - 클라이언트
  - 멀티쓰레딩 환경에서 클라이언트 간 메시지를 주고 받고, 행렬 연산할 때 동시성 문제를 해결하기 위해 synchronized 를 활용하였습니다.
  - Logging 을 위한 File IO 시, IOException 이 발생하면 stackTrace 를 출력하도록 예외처리를 하였습니다.


7. Additional Comments
 - 조원 1명 불참 (추가 정리)
 - 행과 열을 임의로 뽑지 않고 순서대로
