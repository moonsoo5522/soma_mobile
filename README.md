# soma_mobile


soma7th - 클라이언트(안드로이드)

soma7thserver - 서버(Servlet, MySQL)


1인, 2인 대전 오목 게임.

1인 대전의 경우 가중치 기반의 간단한 인공지능을 구현.
2인 대전의 경우 GCM 푸시 기반으로 대전 게임기능 구현

오목알을 커스텀할 수 있음. 총 8개의 오목알이 있으며, 구입비는 1000원.
한 게임을 이길 때마다 500원씩 적립됨.

페이스북 아이디로 로그인할 수 있음.
친구 추가 페이지가 있으며, 일반 회원(직접 회원가입), 페이스북 회원 모두 서로 친구 추가 가능함.
친구에게 오목알을 선물해줄 수 있음.

랭킹 기능 구현.
GCM푸시를 이용해 대전게임, 선물이나 친구추가시 푸시알림 전송 가능

설치 시간, 게임 시작 시간, 종료 시간, 마지막 플레이 시간 등은 ShardPreference를 통해 로컬에서 관리.

이외 회원 데이터, 친구 관계 데이터 등은 서버에서 관리. MySQL DBMS를 통해서 SQL문으로 쿼리 전송.

CMS의 주소는 http://52.40.227.27:8080/soma7th/management.jsp


