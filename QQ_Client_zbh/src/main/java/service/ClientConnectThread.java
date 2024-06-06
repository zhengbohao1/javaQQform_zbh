package service;

import java.io.*;
import java.net.Socket;

import javax.swing.JOptionPane;
import util.Message;
import util.MyObjectInputStream;
import util.code;
import view.zbh_OnlineUserFrame;
import view.zbh_chatFrame;


/*这个线程的作用就是接收服务端送来的各种数据*/
public class ClientConnectThread extends Thread{

    // 连接线程需要socket
    private Socket socket;

    private zbh_chatFrame chatFrame;
    private zbh_OnlineUserFrame onlineUserFrame = null;


    // 构造器接收Socket
    public ClientConnectThread(Socket socket, zbh_chatFrame chatFrame) {
        super();
        this.socket = socket;
        this.chatFrame = chatFrame;
    }

    // 构造器接收  在线用户面板 对象
    public ClientConnectThread(Socket socket, zbh_OnlineUserFrame onlineUserFrame) {
        super();
        this.socket = socket;
        this.onlineUserFrame = onlineUserFrame;
        //System.out.println("创建了");
    }

    // 通过判断message的不同 进行不同操作
    @Override
    public void run() {
        int activeThreads = Thread.activeCount();
        // 由于 客户端需要 不断与服务端 保持通信, 使用while循环
        while(true) {
            try {
                //System.out.println("here");
                MyObjectInputStream ois = new MyObjectInputStream(socket.getInputStream());
                // 若服务端没有发送Messsage 对象, 程序会阻塞(暂停)
                //System.out.println("这里有问题吗1");
                Message message = (Message)ois.readObject();
                //System.out.println("这里有Message吗");
                System.out.println(message);
                // 判断messageType类型进行不同操作
                if(message.getMessType().equals(code.MESSAGE_RET_ONLINE_FRIEND)) {
                    // 类型一. 服务端 返回在线用户列表

                    /* 4.0
                     *   接收服务端传来的 在线用户 字符串
                     *   用 "\n  " 分割, 直接输出即可
                     */
                    //System.out.println("here");
                    onlineUserPrintln("拉取时间\n" + message.getSendTime()
                            + "\n======= 在线用户 =======\n  群聊\n" + message.getContent()
                            +"\n请在下方第一个框内输入用户名 发出加好友请求"
                            + "\n请在下方第二个框内输入用户名 进入聊天");

                }  else if (message.getMessType().equals(code.MESSAGE_COMM_MES)) {
                    // 类型二. 客户端 接收私聊 内容(客户端1-->服务端-->本客户端)    普通消息包

                    // 直接打印输出
                    println(message.getSender() + ":\t\t\t" + message.getSendTime());
                    println(message.getContent() + "\n");

                }   else if (message.getMessType().equals(code.MESSAGE_ToAll_MES)) {
                    // 类型三. 客户端 接收群聊内容

                    println(message.getSender() + ":\t\t\t" + message.getSendTime());
                    println(message.getContent() + "\n");

                } else if (message.getMessType().equals(code.MESSAGE_FRIENDSHIP_INSERT_SUCCESS)) {
                    UserClientService.friend_add_flag=true;
                    onlineUserPrintln("成功添加一名好友，请更新好友表查看。");
                } else if (message.getMessType().equals(code.MESSAGE_File_MES)) {
                    // 类型四. 客户端文件传输   接收文件(客户端1-->服务端-->本客户端)

                    println(message.getSender() + "\t发送文件:\t\t" + message.getSendTime());
                    println("   " + message.getFileName() + "\n");

                    // 弹窗提示输入 保存文件 的路径
                    // 调用本类定义方法       SaveFileAddress(使用者, 发送者,发送的文件名)
                    String srcPsth = saveFileAddress(message.getGetter(), message.getSender(),
                            message.getFileName());

                    if(srcPsth != null) {
                        // 取出message 字节数组  输出流写入磁盘
                        FileOutputStream fis = new FileOutputStream(srcPsth);
                        try {
                            fis.write(message.getFileBytes());
                            fis.close();

                            // 路径错误, 抛出异常并 弹窗提示
                        } catch (FileNotFoundException e) {
                            // TODO: handle exception
                            JOptionPane.showMessageDialog(null, "保存失败 !!", message.getGetter() + " 的弹出提示", JOptionPane.INFORMATION_MESSAGE);
                        }
                        // 弹窗提示
                        JOptionPane.showMessageDialog(null, "文件已保存",  message.getGetter() + " 的弹出提示", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "保存失败 !!", message.getGetter() + " 的弹出提示", JOptionPane.INFORMATION_MESSAGE);
                    }
                }  else if (message.getMessType().equals(code.MESSAGE_CLIENT_EXIT)) {
                    // 类型五. 接收服务端返回 本端发送的 退出系统message,  关闭socket  break退出线程
                    // 该线程的socket 即为 将退出客户端socket, 直接关闭
                    socket.close();
                    // 退出while循环, run方法将结束, 该线程结束
                    break;
                }else if (message.getState()!=null) {
                    if (message.getState().equals(code.MESSAGE_FRIEND_JUDGE_SUCCEED)) {
                        UserClientService.friend_flag=true;
                    } else if (message.getState().equals(code.MESSAGE_FRIEND_JUDGE_FAIL)) {
                        UserClientService.friend_flag=false;
                    } else if (message.getState().equals(code.MESSAGE_FRIEND_NOT_EXIST)) {
                        UserClientService.friend_send_flag=false;
                    }else if(message.getState().equals(code.MESSAGE_FRIEND_ADD_SEND_SUCCEED)){
                        UserClientService.friend_send_flag=true;
                    } else if (message.getState().equals(code.MESSAGE_GET_FRIENDS_SUCCEED)) {
                        onlineUserPrintln3(message.getContent());
                    } else if (message.getState().equals(code.MESSAGE_FRIEND_ADD_REQUEST)) {
                        //别人发给我的好友请求
                        onlineUserPrintln2(message.getSender());
                    }
                    else if(message.getState().equals(code.MESSAGE_SEND_MESSAGE_FAIL)){
                        println("发送无效，对方还未打开聊天界面。");
                    }
                }
                else {
                    // 暂时不做处理
                }


            } catch (EOFException e) {
                break; // 如果到达流的末尾，跳出循环
            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("IOexception");
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                System.out.println("classnotfoundexception");
            }
        }
    }



    /**  弹窗提示用户输入 保存文件地址
     *
     * @param userId   使用者
     * @param sender   发送者
     * @param fileName 发送过来的文件名
     * @return srcPath 文件保存的路径
     */
    public String saveFileAddress(String userId, String sender, String fileName) {

        String srcPath = JOptionPane.showInputDialog(null,
                sender + "发送文件: " + fileName + "\n请输入保存文件的路径:\n\t形式: C:\\xxx.txt",
                userId + "  的弹窗输入", JOptionPane.INFORMATION_MESSAGE);

        return srcPath;
    }



    public void println(String s) {
        if (s != null) {
            chatFrame.getTextArea1().setText(chatFrame.getTextArea1().getText() + s + "\n");
            System.out.println(s + "\n");
        }
        else{
            System.out.println("null");
        }
    }

    public void onlineUserPrintln(String s) {
        if (s != null) {
            onlineUserFrame.getTextArea1().setText(onlineUserFrame.getTextArea1().getText() +s + "\n");
            System.out.println(s + "\n");
        }
    }
    public void onlineUserPrintln2(String s) {
        if (s != null) {
            onlineUserFrame.getTextArea2().setText(onlineUserFrame.getTextArea2().getText() +s + "\n");
            System.out.println(s + "\n");
        }
    }
    public void onlineUserPrintln3(String s) {
        if (s != null) {
            onlineUserFrame.getTextArea3().setText(onlineUserFrame.getTextArea3().getText() +s + "\n");
            System.out.println(s + "\n");
        }
    }
    /* set get方法 */
    public Socket getSocket() {
        return socket;
    }

    public zbh_chatFrame getChatFrame() {
        return chatFrame;
    }

    public void setChatFrame(zbh_chatFrame chatFrame) {
        this.chatFrame = chatFrame;
    }

}
