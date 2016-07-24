# soma_mobile


soma7th - 클라이언트(안드로이드)

soma7thserver - 서버(Servlet, MySQL)

구글 플레이 주소 : https://play.google.com/store/apps/details?id=com.sangjin.soma7th


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

이외 회원 데이터, 친구 관계 데이터 등은 서버에서 관리. MySQL DBMS를 사용, JDBC를 통해 SQL문으로 쿼리 전송.

CMS의 주소는 http://52.40.227.27:8080/soma7th/management.jsp


aws ec2 클라우드에 서버를 구축함.


DBMS는 MySQL을 사용하였으며, 테이블은 총 2개임.

1. Member 테이블

id(varchar, PK)

passwoard(varchar)

name(varchar)

point(int)

dol(int)

mode(int)

win(int)

lose(int)

regid(varchar)

2. friends 테이블

id(varchar, PK, FK - from Member id)

friendid(varchar, PK, FK - from Member id)

/////////////////////////////////////////////////////////////

클래스 설명 (Client)

FriendActivity : 친구 목록을 보여주는 액티비티. 친구를 추가할 수 있고, 친구에게 오목알 선물도 가능.

GameActivity : 게임이 실행될때의 액티비티

JoinActivity : 회원가입 액티비티

LoginActivity : 로그인 액티비티. 일반 로그인 및 페이스북 로그인 지원

MainActivity : 메인 액티비티. 버튼을 추가하고 GCM의 regID를 얻어온다.

MultiActivity : 대전 게임을 위한 액티비티. 게임에 등록된 모든 사용자를 보여주며 터치할 시 그 사용자에게 대전 알림이 간다.

ProfileActivity : 자신의 프로필을 보여줌. 승, 패, 설치 시간 등등을 볼 수 있으며, 오목알을 커스텀할 수 있다.

RankActivity : 회원들의 랭킹을 보여준다.

beans패키지의 클래스들 : 네트워크 통신을 위한 클래스들.

BackPressCloseHandler : 뒤로가기 두번 클릭시 종료하는 로직을 담은 클래스.

CreatRegID : GCM을 사용하기 위해 regstration id를 가져온다.

DateManage : 현재 시간을 알아와서 String형태로 포맷변환한다.

Dol : 오목알의 좌표와 플레이어 정보를 담고 있음.

GCMIntentService : GCM푸시 메시지를 받는 서비스 클래스

MemberAdapter, RankAdapter : 리스트뷰 어댑터 클래스

Music : 배경음악을 나오게 한다.

OmokControl : 오목 게임에 대한 전반적인 알고리즘이 담겨있다. 인공지능 로직, 승패 판단, 네트워크 통신 로직 등이 있다.

OmokView : 오목 판 뷰.

PreferenceData : SharedPrefernce 클래스를 사용하기 편하게 래핑한 클래스.

SendPost : 백엔드 웹서버와 통신하기 위한 통신로직을 담은 클래스.


클래스 설명 (Server)

MainServlet : 회원 데이터 관련 클라이언트 요청을 처리하는 서블릿

ManageServlet : CMS에서 온 요청을 처리하는 서블릿

MultiServlet : 2인 대전 관련 요청을 처리하는 서블릿

MemberDAO : 회원 관련 데이터베이스 액세스 클래스.

FriendDAO : 친구 관련 데이터베이스 액세스 클래스.

SendMessage : GCMServer 라이브러리를 사용하기 편하도록 래핑한 클래스.

나머지 클래스들은 모두 Json파싱하여 데이터 전송에 활용됩니다.