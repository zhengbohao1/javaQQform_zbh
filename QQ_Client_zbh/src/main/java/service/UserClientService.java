package service;

import domain.User;
import util.Message;
import util.MyObjectInputStream;
import util.MyObjectOutputStream;
import util.code;
import view.zbh_OnlineUserFrame;
import view.zbh_chatFrame;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class UserClientService {
    private Boolean flag=false;
    private User user=new User();
    private Socket socket;
    public static  boolean friend_flag=false;
    public static boolean friend_send_flag=false;
    public static boolean friend_add_flag=false;


    public boolean registUser(String userId, String password) {
        // 临时变量, 用于返回
        boolean flag = false;

        try {
            // 将数据封装至对象
            user.setUserId(userId);
            user.setPassward(password);
            // 设置为请求注册
            user.setRegistMessageType(code.MESSAGE_REGIST_REQUEST);
            // 创建Socket 连接到服务端            InetAddress.getLocalHost()
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            // 发送user对象给服务端
            MyObjectOutputStream oos =
                    new MyObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);    // 发送给服务端
            System.out.println(user);
            // 接收服务端 回送的message 对象, 用户消息验证
            MyObjectInputStream ois =
                    new MyObjectInputStream(socket.getInputStream());
            Message message = (Message)ois.readObject();
            // 对接收到的message 消息对象进行验证
            if( message.getMessType().equals(code.MESSAGE_REGIST_SUCCEED) ) {
                flag = true;   // 函数返回值
            }
            // 无论成功与否, 关闭socket
            // 关闭 以启动的socket
            socket.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 返回
        return flag;
    }

    public boolean checkUser(String userId, String password) {
        // 将数据封装至对象
        user.setUserId(userId);
        user.setPassward(password);

        // 创建Socket 连接到服务端            InetAddress.getLocalHost()
        try {
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            // 发送user对象给服务端
            MyObjectOutputStream oos =
                    new MyObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

            // 接收服务端 回送的message 对象, 用户消息验证
            MyObjectInputStream ois =
                    new MyObjectInputStream(socket.getInputStream());
            Message message = (Message)ois.readObject();
            // 对接收到的message 消息对象进行验证
            if( message.getMessType().equals(code.MESSAGE_LOGIN_SUCCEED) ) {
                flag = true;
                //oos.close();
            } else {
                // 弹窗提示
                JOptionPane.showMessageDialog(null, "账号或密码错误!");

                // 登陆失败
                // 关闭 已启动的socket
                socket.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "服务器未启动");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        return flag;
    }
    public void startThread(String userId, zbh_OnlineUserFrame onlineUserFrame) {
        /*
         *  启动线程, 封装维护socket
         *  使用 ClientServerThread  线程类
         */
        ClientConnectThread thread = new ClientConnectThread(socket, onlineUserFrame);

        thread.start();   // 启动

        /*	将线程放入 管理线程的集合中
         *  通过ClientConnentServerThreadManage 线程管理类
         *  	addClientThread 方法添加线程
         */
        // 状态标记为 在线
        ClientConnectThreadManage.addThread(userId, "在线", thread);
    }

    public void startThreadChat(String userId, String getterId, zbh_chatFrame zbhChatFrame)  {
        /* 4.0
         *  创建User对象
         *  	getterId 聊天对象 即 状态
         *  	方便服务器 识别使用
         */
        user.setUserId(userId);
        user.setState(getterId);   // 设置状态为 聊天对象

        // 创建Socket 连接到服务端
        try {
            socket = new Socket(InetAddress.getLocalHost(), 9999);


            // 发送user对象给服务端
            MyObjectOutputStream oos =
                    new MyObjectOutputStream(socket.getOutputStream());

            oos.writeObject(user);    // 发送给服务端service，目的是让服务端知道，现在是要开启一个聊天线程，因为state中设置
            //成了要聊天对象的id了。
            //oos.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*	启动线程, 封装维护socket
         */
        ClientConnectThread thread = new ClientConnectThread(socket, zbhChatFrame);

        thread.start();  // 启动

        /* 4.0
         * 将线程放入 管理线程的集合中
         *  	方便 线程管理器 存放管理
         */
        // 状态 为 聊天对象("群聊" "对方用户名")
        ClientConnectThreadManage.addThread(userId, getterId, thread);
    }

    public void logout(String userId, String state) {
        Message message = new Message();
        message.setSender(userId);    // 一定要指定发送者 客户端id
        message.setMessType(code.MESSAGE_CLIENT_EXIT);   // 设置消息类型为 退出

        // 4.0
        message.setGetter(state);   // 接收者定义为状态

        try {

            // 调用线程管理类的 方法获取对应线程
            ClientConnectThread thread =
                    ClientConnectThreadManage.getThread(userId, state);

            // 向服务端发送 退出请求message
            MyObjectOutputStream oos =
                    new MyObjectOutputStream(thread.getSocket().getOutputStream());
            oos.writeObject(message);

            System.out.println(userId + "退出系统!");


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onlineFriendList(String senderId) {
        // 发送一个message对象
        // messageType 定义为MESSAGE_GET_ONLINE_FRIEND 客户端请求
        Message message = new Message();
        message.setSendTime(new Date().toString());  // 发送时间
        message.setMessType(code.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(senderId);         // 设置发送者

        try {
            // 线程管理类 的获取线程方法, 传入userId 和 状态"在线"
            ClientConnectThread thread =
                    ClientConnectThreadManage.getThread(senderId, "在线");

            // 通过线程类 的getScoket方法 得到socket  发送给服务端
            MyObjectOutputStream ois =
                    new MyObjectOutputStream( thread.getSocket().getOutputStream());
            ois.writeObject(message);
            //ois.close();
            //thread.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void checkfriend(String senderId, String getterId){
        Message message = new Message();
        message.setMessType(code.MESSAGE_FRIEND_JUDGE);   // 设置消息类型为
        message.setSender(senderId);
        message.setGetter(getterId);
        try {
            ClientConnectThread thread =
                    ClientConnectThreadManage.getThread(senderId, "在线");
            MyObjectOutputStream oos =
                    new MyObjectOutputStream( thread.getSocket().getOutputStream());
            oos.writeObject(message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void addfriend(String senderId, String getterId){
        Message message = new Message();
        message.setMessType(code.MESSAGE_FRIEND_ADD);
        message.setSender(senderId);
        message.setGetter(getterId);
        try {
            ClientConnectThread thread =
                    ClientConnectThreadManage.getThread(senderId, "在线");
            MyObjectOutputStream oos =
                    new MyObjectOutputStream( thread.getSocket().getOutputStream());
            oos.writeObject(message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void insertfriendship(String senderId, String getterId){
        Message message = new Message();
        message.setMessType(code.MESSAGE_FRIENDSHIP_INSERT);
        message.setSender(senderId);
        message.setGetter(getterId);
        try {
            ClientConnectThread thread =
                    ClientConnectThreadManage.getThread(senderId, "在线");
            MyObjectOutputStream oos =
                    new MyObjectOutputStream( thread.getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void get_friends(String senderId){
        Message message = new Message();
        message.setMessType(code.MESSAGE_GET_FRIENDS);
        message.setSender(senderId);
        try {
            ClientConnectThread thread =
                    ClientConnectThreadManage.getThread(senderId, "在线");
            MyObjectOutputStream oos =
                    new MyObjectOutputStream( thread.getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
