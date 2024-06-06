package service;

import util.Message;
import util.MyObjectOutputStream;
import util.code;
import view.zbh_chatFrame;

import java.io.IOException;
import java.util.Date;

public class MessageClientService {
    private zbh_chatFrame chatFrame;   // 聊天窗口类 println方法
    //public static boolean check_send=false;
    // 构造器初始化
    public MessageClientService(zbh_chatFrame chatFrame) {
        super();
        this.chatFrame = chatFrame;
    }
    public void sendMessageToAll(String senderId, String content) {
        Message message = new Message();

        message.setMessType(code.MESSAGE_ToAll_MES);    // 设置信息类型 群聊
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTime(new Date().toString());    // 设置发送时间

        println(senderId + "(我):\t\t   " + new Date().toString());
        println(content + "\n");

        // 调用线程管理类 方法 输入发送者id  获取线程
        ClientConnectThread thread =
                ClientConnectThreadManage.getThread(senderId, "群聊");
        // 发送给服务端
        try {
            MyObjectOutputStream oos =
                    new MyObjectOutputStream(thread.getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendMessageToOne(String senderId, String getterId, String content) {
        Message message = new Message();
        message.setMessType(code.MESSAGE_COMM_MES);    // 设置信息类型 普通消息包
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new Date().toString());    // 设置发送时间, 调用util包的方法

        println(senderId + "(我):\t\t   " + new Date().toString());
        println(content + "\n");


        // 调用线程管理类 方法 输入发送者id,聊天对象(状态)  获取线程
        ClientConnectThread thread =
                ClientConnectThreadManage.getThread(senderId, getterId);
        // 发送给服务端
        try {
            MyObjectOutputStream oos =
                    new MyObjectOutputStream(thread.getSocket().getOutputStream());
            oos.writeObject(message);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void println(String s) {
        if (s != null) {
            chatFrame.getTextArea1().setText(chatFrame.getTextArea1().getText() + s + "\n");
            System.out.println(s + "\n");
        }
    }
}
