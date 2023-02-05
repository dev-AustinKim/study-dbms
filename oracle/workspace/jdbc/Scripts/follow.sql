/*팔로우, 쪽지, 채팅 등*/

/*팔로우 테이블*/
CREATE SEQUENCE SEQ_FOLLOW;

CREATE TABLE TBL_FOLLOW(
   FOLLOW_ID NUMBER CONSTRAINT PK_FOLLOW PRIMARY KEY,
   FOLLOWING_USER_ID NUMBER NOT NULL,
   FOLLOWER_USER_ID NUMBER NOT NULL,
   CONSTRAINT FK_FOLLOW_FOLLOWING FOREIGN KEY(FOLLOWING_USER_ID) REFERENCES TBL_USER(USER_ID) ON DELETE CASCADE,
   CONSTRAINT FK_FOLLOW_FOLLOWER FOREIGN KEY(FOLLOWER_USER_ID) REFERENCES TBL_USER(USER_ID) ON DELETE CASCADE
);

SELECT * FROM TBL_USER;

ALTER TABLE TBL_USER RENAME COLUMN FOLLOWINT_COUNT TO FOLLOWING_COUNT;

--UPDATE TBL_USER SET FOLLOWING_USER_ID = FOLLOWING_USER_ID + 1 WHERE 

-- 언팔했을 때
UPDATE TBL_USER SET FOLLOWING_COUNT = (FOLLOWING_COUNT - 1) WHERE USER_ID = ? -- 로그인한 나의 아이디로 내 팔로잉 카운트를 1 감소.
UPDATE TBL_USER SET FOLLOWER_COUNT = (FOLLOWER_COUNT-1) WHERE USER_ID = ? -- 매개변수로 받아온 userId의 팔로워 카운트를 1 감소.

DELETE FROM TBL_FOLLOW WHERE FOLLOWING_USER_ID = ? AND FOLLOWER_USER_ID = ? -- 언팔했을 때 TBL_FOLLOW에서도 삭제해준다. 로그인한 상태니까 FOLLOWER_USER_ID는 UserDAO.userId로 받고  FOLLOWING_USER_ID는 매개변수인 UserId를 사용한다.

SELECT * FROM TBL_FOLLOW;

SELECT COUNT(FOLLOWING_USER_ID)
FROM TBL_USER U JOIN TBL_FOLLOW F
ON U.USER_ID = F.FOLLOWING_USER_ID AND FOLLOWING_USER_ID = ?;


/*팔로우 ID*/
INSERT INTO TBL_FOLLOW (FOLLOW_ID, FOLLWING_USER_ID, FOLLOWER_USER_ID)
VALUES(SEQ_FOLLOW.NEXTVAL, ?, ?)


/*팔로잉 ID(USER_ID)*/
SELECT U.USER_ID, USER_IDENTIFICATION, USER_NAME, USER_PASSWORD, USER_PHONE, USER_NICKNAME, USER_EMAIL, USER_ADDRESS, USER_BIRTH, USER_GENDER, USER_RECOMMENDER_ID, 
FROM TBL_USER U JOIN TBL_FOLLOW F 
ON U.USER_ID = F.FOLLOWING_USER_ID AND F.FOLLOWER_USER_ID = ?



/*팔로워 ID(USER_ID)*/
SELECT U.USER_ID, USER_IDENTIFICATION, USER_NAME, USER_PASSWORD, USER_PHONE, USER_NICKNAME, USER_EMAIL, USER_ADDRESS, USER_BIRTH, USER_GENDER, USER_RECOMMENDER_ID, 
FROM TBL_USER U JOIN TBL_FOLLOW F 
ON U.USER_ID = F.FOLLOWING_USER_ID AND F.FOLLOWING_USER_ID = ?

SELECT FOLLOWING_USER_ID FROM TBL_FOLLOW
WHERE FOLLOWER_USER_ID = ?




