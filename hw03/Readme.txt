1. 조 이름, 조원, 역할
 - 조 이름
  - 데이터 통신 과제 17조
 
 - 조원 및 역할
  - 이주원(20183085) : 서버 구현
  - 윤태현(20193075) : 클라이언트 구현, 영상 제작, 문서 작업
  - 배호재


2. 프로그램 구성요소 설명
 - server


 - client


3. 소스코드 컴파일 방법
 - server : 프로그램 실행 방법에 명시되어 있는대로 실행시키면 됩니다.
  - Linux 환경에서 run.sh 파일 실행 시 바로 실행됩니다.
  - gradle 을 이용해 jar 파일로 빌드 후, 빌드된 jar 파일을 실행합니다.

 - client : 프로그램 실행 방법에 명시되어 있는대로 실행시키면 됩니다.
  - Linux 환경에서 run{id}.sh 파일 실행 시 실행됩니다.


4. 프로그램 실행 환경 및 실행 방법
 - 프로그램 실행 환경
   - 서버 : Ubuntu22.04, Java 11
   - 클라이언트 : Windows 10, Java 17(Java 11)
  
 - 프로그램 실행 방법
  - server : ./server/run.sh
  - client 1, 2 : ./client/run{id}.sh
   ex) ./run1.sh
  - client 3, 4 : window 환경에서는 client 폴더에서 powershell을 열고 run{id}.sh의 내용을 복사 붙여넣기해서 실행합니다.
                  shell script가 있다면 run{id}.sh를 실행시키면 됩니다.

  - PuTTY 실행 방법
   -> 첨부한 개인키가 사용되지 않으면 후술한 방법대로 개인키를 생성하고 개인키를 더블 클릭해준 뒤 PuTTY를 실행시킵니다.
     윈도우 환경이므로 ubuntu를 입력하면 PuTTY 실행이 완료됩니다.
     그 후 서버 폴더에서 서버를 실행시키면 됩니다.

  - PuTTY 개인키 생성 방법
   -> PuTTYgen 에서 random.pem 키를 Load한 후 Save private key를 눌러 개인키를 생성하고 저장해줍니다.
   

5. 구현한 최적의 알고리즘 제시 및 설명(Pseudo Code 작성 및 설명)
 - 구현한 최적의 알고리즘
  - 메시지 전송이 적게 드는 client에 청크가 있다면 그 client로부터 청크를 받고 없다면 그 다음으로 거리가 먼 클라이언트로부터 청크를 받는다.
   즉, 서버에 있는 클라이언트나 로컬에 있는 클라이언트끼리 가능한 청크를 주고 받으며,
   이후에 서버는 로컬과, 로컬은 서버와 청크를 주고 받는다.

 - Pseudo Code
  

6. Error or Additional message Handling


7. Additional Comments
 - 파일 이름은 A, B, C, D 대신 1, 2, 3, 4로 수정하였습니다.

 - GitHub URL : https://github.com/jujuwon/random-calculator/tree/main/hw03
