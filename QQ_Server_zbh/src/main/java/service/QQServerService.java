package service;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import domain.User;
import mapper.userMapper;
import util.*;
import util.*;
import view.ServerFrame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class QQServerService {
    public ServerFrame serverFrame=new ServerFrame();
    private ServerSocket server = null;


    SqlSessionFactory factory= SqlSessionFactoryutils.get_SqlSessionFactory();



    public QQServerService(ServerFrame serverFrame) {
        this.serverFrame = serverFrame;
        try {
            server = new ServerSocket(9999);
            println("服务器已启动");
            while (true) {
                // 若没有客户端连接, 阻塞
                Socket socket = server.accept();

                // 3. 建立连接后, 接收客户端传来的User对象
                MyObjectInputStream ois = new MyObjectInputStream(socket.getInputStream());
                //得到user
                System.out.println("服务器接收到客户端传来的user");
                User user = (User) ois.readObject();
                System.out.println(user);
                //4.
                MyObjectOutputStream oos =
                        new MyObjectOutputStream(socket.getOutputStream());
                Message message = new Message();
                // 5.若 消息类型为 MESSAGE_REGIST_REQUEST 客户端请求注册
                if(user.getRegistMessageType() != null && user.getRegistMessageType().equals(code.MESSAGE_REGIST_REQUEST) )  {

                    if(registUser(user.getUserId(), user)) {   // 账号未被注册, 且将新账号加入集合

                        println("用户 " + user.getUserId() + " 注册成功");

                        // 给messageType消息类型设置 注册成功
                        message.setMessType(code.MESSAGE_REGIST_SUCCEED);
                        oos.writeObject(message);   // 发送

                    } else {
                        // 账号已存在
                        println("用户 " + user.getUserId() + " 已存在, 注册失败");

                        // 给messageType消息类型设置 注册失败
                        message.setMessType(code.MESSAGE_REGIST_FAIL);
                        oos.writeObject(message);   // 发送
                    }
                    user = null;
                    // 注册结束都要 关闭, 不不论成功与否
                    socket.close();


                    // 客户端 启动聊天窗口
                } else if (user.getState() != null) {
                        // 创建线程 维护
                        ServerConnectThread thread =
                                new ServerConnectThread(socket, user.getUserId(), serverFrame);
                        thread.start();

                        // 加入集合                                用户名             状态             线程
                        new ServerConnectThreadManage().addThread(user.getUserId(), user.getState(), thread);

                        if(user.getState() == "群聊") {
                            println(user.getUserId() + " 创建群聊窗口");
                        } else {
                            println(user.getUserId() + " 创建与 " + user.getState() + " 的聊天窗口");
                        }
                    // 客户端请求登录
                } else {
                    // 验证 userId与密码  调用本类的checkUser()方法
                    if (checkUser(user.getUserId(), user.getPassword())) {
                        // 验证通过
                        //   消息方式定义为:    MESSAGE_LOGIN_SUCCEED 登录成功
                        message.setMessType(code.MESSAGE_LOGIN_SUCCEED);
                        oos.writeObject(message);   // 返回


                        // 创建一个 线程, 维护该socket对象
                        ServerConnectThread thread =
                                new ServerConnectThread(socket, user.getUserId(), serverFrame);
                        thread.start();  // 启动

                        // 将该线程 对象放入集合中 进行管理
                        /*
                         * 	调用ServerConnectClientThreadManage 线程管理类类的
                         * 	addThread方法 传入线程 和其对应的userId, 状态为"在线"
                         */
                        new ServerConnectThreadManage().addThread(user.getUserId(), "在线", thread);


                    } else {  // 登录失败
                        println("用户 " + user.getUserId() + " 登录验证失败");

                        // 给messageType消息类型设置 登录失败
                        message.setMessType(code.MESSAGE_LOGIN_FAIL);
                        oos.writeObject(message);   // 发送

                        // 关闭
                        socket.close();
                    }
                }

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            // 当服务端退出时, 即while循环结束
            // 关闭 ServerSocket对象
            try {
                server.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /*public void setFriend_flag(boolean friend_flag) {
        this.friend_flag = friend_flag;
    }*/

    /**  登录验证方法:
     *   验证 用户id是否 存在于集合的方法
     *
     * @param userId   用户名
     * @param password 密码
     * @return boolean true为验证通过
     */
    private boolean checkUser(String userId, String password) {
       /* // 通过key键 userId 取出 user对象
        User user = validUsers.get(userId);

        // 若集合中 没有传入的userId, 则取出的user为空
        if(user == null) {
            return false;
        }
        // 传入的密码错误
        if( ! (user.getPassword().equals(password))) {
            return false;
        }

        return true;*/
        //获取sqlsession
        SqlSession sqlSession= factory.openSession(true);
        //获取mapper
        userMapper mapper = sqlSession.getMapper(userMapper.class);
        //调用函数

        User user=mapper.get_user(userId,password);
        sqlSession.close();
        if(user==null){
            return false;
        }
        return true;
    }


    /**  注册方法:
     * 	1. 验证账号是否已存在, 存在返回fail
     *  2. 不存在返回true 并将user对象加入 ConcurrentHashMap
     *
     * @param userId   用户名
     * @param user     对应user
     * @return boolean true注册成功, false用户已存在
     */
    private boolean registUser(String userId, User user) {
        /*// 获取user对象
        User userVerify = validUsers.get(userId);
        // 账号已注册 返回失败
        if(userVerify != null) {
            return false;
        } else {

            // 未被注册, 将用户加入
            validUsers.put(userId, user);
            return true;
        }*/
        //获取sqlsession
        SqlSession sqlSession= factory.openSession(true);
        //获取mapper
        userMapper mapper = sqlSession.getMapper(userMapper.class);
        //调用函数
        User user1=mapper.select_by_userid(userId);
        if(user1==null){
            mapper.insert_user(userId,user.getPassword());
            return true;
        }
        else return false;
    }
    public void println(String s) {
        if (s != null) {
            serverFrame.getTextArea1().setText(serverFrame.getTextArea1().getText() + s + "\n");
            System.out.println(s + "\n");
        }
    }


}
