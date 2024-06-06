package mapper;

import domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface userMapper {
    User get_user(@Param("username") String username, @Param("password") String password);
    User select_by_userid(@Param("username") String userid);
    void insert_user(@Param("username")String username, @Param("password")String password);
    int checkfriend(@Param("senderId") String senderId, @Param("getterId") String getterId);
    void insert_friend(@Param("senderId") String senderId, @Param("getterId") String getterId);
    List<String> get_friends_1(@Param("userId") String userId);
    List<String> get_friends_2(@Param("userId") String userId);
}
