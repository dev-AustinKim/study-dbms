package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import domain.UserVO;

public class MyUserDAO {

	public Connection connection;  // 연결 객체를 담아줄 전역 변수
	//   public Statement statement;     // 이게 별로여서 preparedStatement 사용함
	public PreparedStatement preparedStatement;   // 쿼리 관리 객체
	public ResultSet resultSet;  // 결과 테이블 객체
	private final int KEY = 3; // 암호화를 위한 키값.

	//   아이디 중복검사
	public boolean checkId(String userIdentification) {
		String query = "SELECT COUNT(USER_ID) FROM TBL_USER WHERE USER_IDENTIFICATION = ?";
		boolean result = false;
		connection = DBConnecter.getConnection();
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userIdentification);
//			select만 executeQuery, 나머지들은 executeUpdate 사용해야한다. 
//			executeQuery는 조회를 하기 때문에 그 조회한 결과를 보통 2차원 배열인 resultSet에 넣어준 후 사용한다.
//			executeUpdate는 업데이트를 하기 때문에 그 결과를 resultSet에 넣지 않고 그냥 executeUpdate 사용한다.
			resultSet = preparedStatement.executeQuery();   

			resultSet.next();
			result = resultSet.getInt(1) == 0; // COUNT(USER_ID) == 0의 boolean 형식을 result에 넣어준다.

		} catch (SQLException e) {
			System.out.println("checkId(String) SQL문 오류");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(resultSet != null) {
					resultSet.close();
				}
				if(preparedStatement != null) {
					preparedStatement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return result;   // 중복이 없을 때 true 사용가능하면 true 사용 불가능하면 false
	}

	//   회원가입
	public void signUp(UserVO userVO) {
		String query = "INSERT INTO TBL_USER"
				+ "(USER_ID, USER_IDENTIFICATION, USER_NAME, USER_PASSWORD,"
				+ "USER_PHONE, USER_NICKNAME, USER_EMAIL, USER_ADDRESS,"
				+ "USER_BIRTH, USER_GENDER, USER_RECOMMENDER_ID) "
				+ "VALUES(SEQ_USER.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		connection = DBConnecter.getConnection();

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userVO.getUserIdentification());
			preparedStatement.setString(2, userVO.getUserName());
			preparedStatement.setString(3, encryptPassword(userVO.getUserPassword()));
			preparedStatement.setString(4, userVO.getUserPhone());
			preparedStatement.setString(5, userVO.getUserNickname());
			preparedStatement.setString(6, userVO.getUserEmail());
			preparedStatement.setString(7, userVO.getUserAddress());
			preparedStatement.setString(8, userVO.getUserBirth());
			preparedStatement.setString(9, userVO.getUserGender());
			preparedStatement.setString(10, userVO.getUserRecommenderId());
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println("signUp SQL문 오류");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(preparedStatement != null) {
					preparedStatement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException();
			}
		}

	}

	//   로그인
	public Long login(String userIdentification, String userPassword) {
		String query = "SELECT USER_ID FROM TBL_USER WHERE USER_IDENTIFICATION = ? AND USER_PASSWORD = ? ";
		Long result = 0L;
		connection = DBConnecter.getConnection();

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userIdentification);
			preparedStatement.setString(2, encryptPassword(userPassword));

			resultSet = preparedStatement.executeQuery();

//			 행이 있어야만 가져올 수 있다. DB쪽에서 null일 수도 있으니까 여기서 확인하고 아이디와 비밀번호에 맞는 userId가 있다면 if문을 진입하는 것이다.
			if(resultSet.next()) { 
				result = resultSet.getLong(1);
			}

		} catch (SQLException e) {
			System.out.println("login SQL문 오류");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(resultSet != null) {
					resultSet.close();
				}
				if(preparedStatement != null) {
					preparedStatement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				throw new RuntimeException();
			}
		}

		return result;
	}   

	//   암호화 -> 앞으로는 base64를 이용.
	public String encryptPassword(String userPassword) {
		String result = "";
//		매개변수로 받아온 password를 첫번째 자리부터 하나하나 key값을 곱해줘서 result에 연결해준다.
		for (int i = 0; i < userPassword.length(); i++) {
			result += userPassword.charAt(i) * KEY;
		}
		return result;
	}


	//   회원탈퇴
	public void delete(Long userId) {
		String query = "DELETE FROM TBL_USER WHERE USER_ID = ?";
		connection = DBConnecter.getConnection();

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, userId);

			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println("delete SQL문 오류");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(preparedStatement != null) {
					preparedStatement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				throw new RuntimeException();
			}
		}

	}

	//   아이디 찾기
	public String findId(String userPhone) {
		String query = 
				"SELECT USER_IDENTIFICATION FROM TBL_USER WHERE USER_PHONE = ?";
		connection = DBConnecter.getConnection();
		String result = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userPhone);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				result = resultSet.getString(1); // 쿼리문으로 받아온 USER_IDENTIFICATION을 result에 넣어준다.
			}

		} catch (SQLException e) {
			System.out.println("select(String) SQL문 오류");
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(resultSet != null) {
					resultSet.close();
				}
				if(preparedStatement != null) {
					preparedStatement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	//   비밀번호 변경
	public void changePassword(String userIdentification, String userPassword) {
		String query = "UPDATE TBL_USER SET USER_PASSWORD = ? WHERE USER_IDENTIFICATION = ?";
		connection = DBConnecter.getConnection();

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, encryptPassword(userPassword));
			preparedStatement.setString(2, userIdentification);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("changePassword SQL문 오류");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(preparedStatement != null) {
					preparedStatement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException();
			}
		}
	}

	//   회원정보 수정
	public void update(UserVO userVO) {
		String query = "UPDATE TBL_USER "
				+ "SET USER_NAME = ?, USER_PASSWORD = ?, USER_PHONE = ?, "
				+ "USER_NICKNAME = ?, USER_EMAIL = ?, USER_ADDRESS = ? "
				+ "WHERE USER_ID = ?";

		connection = DBConnecter.getConnection();
		try {

			preparedStatement = connection.prepareStatement(query);
			// preparedStatement.setString은 물음표를 채워주는 것인데 쿼리문에서 왼쪽에서 오른쪽으로 물음표를 채워준다. 즉 쿼리문에서 맨 왼쪽에 있는 물음표가 1번이고 두번째 있는 물음표가 2번이고... 맨 오른쪽에 있는 물음표가 마지막 번호이다.
			preparedStatement.setString(1, userVO.getUserName()); 
			preparedStatement.setString(2, encryptPassword(userVO.getUserPassword()));
			preparedStatement.setString(3, userVO.getUserPhone());
			preparedStatement.setString(4, userVO.getUserNickname());
			preparedStatement.setString(5, userVO.getUserEmail());
			preparedStatement.setString(6, userVO.getUserAddress());
			preparedStatement.setLong(7, userVO.getUserId());

			preparedStatement.executeUpdate();


		} catch (SQLException e) {
			System.out.println("select(Long) SQL문 오류");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	//   회원정보 조회
	public UserVO select(Long userId) {
		String query = 
				"SELECT USER_ID, USER_IDENTIFICATION, USER_NAME, USER_PASSWORD,"
						+ " USER_PHONE, USER_NICKNAME, USER_EMAIL, USER_ADDRESS, USER_BIRTH,"
						+ " USER_GENDER, USER_RECOMMENDER_ID "
						+ "FROM TBL_USER WHERE USER_ID = ?";
		UserVO userVO = null;
		connection = DBConnecter.getConnection();

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, userId);
			resultSet = preparedStatement.executeQuery();

			if(resultSet.next()) {
				userVO = new UserVO();
				userVO.setUserId(resultSet.getLong(1));
				userVO.setUserIdentification(resultSet.getString(2)); 
				userVO.setUserName(resultSet.getString(3));
				userVO.setUserPassword(resultSet.getString(4));
				userVO.setUserPhone(resultSet.getString(5));
				userVO.setUserNickname(resultSet.getString(6));
				userVO.setUserEmail(resultSet.getString(7));
				userVO.setUserAddress(resultSet.getString(8));
				userVO.setUserBirth(resultSet.getString(9));
				userVO.setUserGender(resultSet.getString(10));
				userVO.setUserRecommenderId(resultSet.getString(11));
			}

		} catch (SQLException e) {
			System.out.println("select(Long) SQL문 오류");
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(resultSet != null) {
					resultSet.close();
				}
				if(preparedStatement != null) {
					preparedStatement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		return userVO;
	}
	//  추천수
	public int countRecommend(String userIdentification) {
		String query = "SELECT COUNT(USER_RECOMMENDER_ID) FROM TBL_USER WHERE USER_RECOMMENDER_ID = ?";
		int result = 0;
		connection = DBConnecter.getConnection();

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userIdentification);
			resultSet = preparedStatement.executeQuery();

			resultSet.next();
			result = resultSet.getInt(1); // result에 COUNT(USER_RECOMMENDER_ID)의 값을 넣어준다.

		} catch (SQLException e) {
			System.out.println("countRecommend(String) SQL문 오류");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}



	//  나를 추천한 사람
	public ArrayList<UserVO> recommendedMe(String userRecommenderId) { // 내 아이디를 받아서 추천한 사람에 내 아이디가 있으면 그 사람의 객체 정보를 담는데 나를 추천한 사람이 몇명인지 모르니까 Arraylist에 담는다.
		String query = "SELECT USER_ID, USER_IDENTIFICATION, USER_NAME, USER_PASSWORD,"
				+ " USER_PHONE, USER_NICKNAME, USER_EMAIL, USER_ADDRESS, USER_BIRTH, "
				+ "USER_GENDER, USER_RECOMMENDER_ID " + "FROM TBL_USER WHERE USER_RECOMMENDER_ID = ?";
		ArrayList<UserVO> users = new ArrayList<UserVO>();
		UserVO userVO = null;
		connection = DBConnecter.getConnection();
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userRecommenderId); // 첫번째 물음표에 userRecommenderId를 넣어준다.
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) { // 혹시 모르니까 SELECT로 조회하는 값이 있는지, 즉 NULL이 아닌지 항상 검사해주자.
				userVO = new UserVO();
				userVO.setUserId(resultSet.getLong(1)); // SELECT 첫번째 값인 USER_ID에 결과 표인 resultSet의 userVO의 첫번째 값인 userId를 넣어준다.
				userVO.setUserIdentification(resultSet.getString(2)); // SELECT 두번째 값인 USER_IDENTIFICATION에 결과 표인 resultSet의 userVO의 두번째 값인 userIdentification을 넣어준다.
				userVO.setUserName(resultSet.getString(3)); // ...
				userVO.setUserPassword(resultSet.getString(4)); // ...
				userVO.setUserPhone(resultSet.getString(5)); // ...
				userVO.setUserNickname(resultSet.getString(6)); // ...
				userVO.setUserEmail(resultSet.getString(7)); // ...
				userVO.setUserAddress(resultSet.getString(8)); // ...
				userVO.setUserBirth(resultSet.getString(9)); // ...
				userVO.setUserGender(resultSet.getString(10)); // ...
				userVO.setUserRecommenderId(resultSet.getString(11)); // ...

				users.add(userVO);
			}

		} catch (SQLException e) {
			System.out.println("recommendMe(String) SQL문 오류");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return users;
	}
	//  내가 추천한 사람
	public UserVO recommendMe(String userIdentification) { // 내 아이디를 매개변수로 받아서 내가 추천한 사람을 조회한다.
		String query = "SELECT USER_ID, USER_IDENTIFICATION, USER_NAME, USER_PASSWORD,"
				+ " USER_PHONE, USER_NICKNAME, USER_EMAIL, USER_ADDRESS, USER_BIRTH, "
				+ "USER_GENDER, USER_RECOMMENDER_ID "
				+ "FROM TBL_USER WHERE USER_IDENTIFICATION = "
				+ "(SELECT USER_RECOMMENDER_ID FROM TBL_USER WHERE USER_IDENTIFICATION = ?)";
		UserVO userVO = null;
		connection = DBConnecter.getConnection();
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userIdentification);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				userVO = new UserVO();
				userVO.setUserId(resultSet.getLong(1));
				userVO.setUserIdentification(resultSet.getString(2));
				userVO.setUserName(resultSet.getString(3));
				userVO.setUserPassword(resultSet.getString(4));
				userVO.setUserPhone(resultSet.getString(5));
				userVO.setUserNickname(resultSet.getString(6));
				userVO.setUserEmail(resultSet.getString(7));
				userVO.setUserAddress(resultSet.getString(8));
				userVO.setUserBirth(resultSet.getString(9));
				userVO.setUserGender(resultSet.getString(10));
				userVO.setUserRecommenderId(resultSet.getString(11));
			}

		} catch (SQLException e) {
			System.out.println("recommendMe(String) SQL문 오류");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		return userVO;
	}

}