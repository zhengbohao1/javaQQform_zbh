package service;

import util.Message;
import util.MyObjectOutputStream;
import util.code;
import view.zbh_chatFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class FileClientService {
    private zbh_chatFrame chatFrame = null;

    /**  群发文件方法
     *
     * @param senderId  发送方
     * @param srcPath   本地文件路径
     * @param fileName  文件名
     * @param chatFrame 聊天界面
     */
    public void sendFileToAll(String senderId, String srcPath, String fileName, zbh_chatFrame chatFrame) {

        this.chatFrame = chatFrame;

        // 若文件名为空, 说明没有选择文件
        if(fileName == null) {
            return;
        }

        // 创建message
        Message message = new Message();
        message.setMessType(code.MESSAGE_File_MES_TOALL);   // 设置类型
        message.setSender(senderId);
        message.setFileName(fileName);
        message.setSendTime(new Date().toString());  // 发送时间

        // 读取 源文件 路径srcPath
        FileInputStream fis = null;
        // 获取文件长度 转为int型
        //这段代码的功能是读取指定路径（srcPath）的文件，并将其内容以字节数组的形式存储在fileBytes数组中。
        //首先，通过new File(srcPath).length()获取文件的长度，将其转换为int类型，并使用该值创建一个长度相同的字节数组fileBytes。
        byte []fileBytes = new byte[(int)new File(srcPath).length()];

        try {
            fis = new FileInputStream(srcPath);
            fis.read(fileBytes);   // 将src源文件 读取到程序数组
            //使用fis.read(fileBytes)方法将文件内容读取到fileBytes数组中。该方法将文件的字节数据按顺序读入字节数组，直到读取到文件末尾。

            // 将字节数组 存入message
            message.setFileBytes(fileBytes);

            // 发送message 给服务端
            ClientConnectThread thread = ClientConnectThreadManage.getThread(senderId, "群聊");

            MyObjectOutputStream oos =
                    new MyObjectOutputStream(thread.getSocket().getOutputStream());

            oos.writeObject(message);

            // 提示信息
            println(senderId + "(我)  发送文件:\t\t   " + new Date().toString());
            println("   路径为:  " + srcPath + "\n");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


   /* *//**  单发文件方法
     *
     * @param senderId 发送者id
     * @param getterId 接收者id
     * @param srcPath  源文件路径
     * 、、@param destPath 接收文件存入路径
     */
    public void sendFileToOne(String senderId, String getterId, String srcPath, String fileName, zbh_chatFrame chatFrame) {

        this.chatFrame = chatFrame;

        // 若文件名为空, 说明没有选择文件
        if(fileName == null) {
            return;
        }

        // 创建message
        Message message = new Message();
        message.setMessType(code.MESSAGE_File_MES);   // 设置类型
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setFileName(fileName);
        message.setSendTime(new Date().toString());  // 发送时间

        // 读取 源文件 路径srcPath
        FileInputStream fis = null;

        // 获取文件长度 转为int型
        byte []fileBytes = new byte[(int)new File(srcPath).length()];

        try {
            fis = new FileInputStream(srcPath);

            fis.read(fileBytes);   // 将src源文件 读取到程序数组
            // 将字节数组 存入message
            message.setFileBytes(fileBytes);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        try {
            // 发送message 给服务端
            ClientConnectThread thread = ClientConnectThreadManage.getThread(senderId, getterId);

            MyObjectOutputStream oos =
                    new MyObjectOutputStream(thread.getSocket().getOutputStream());

            oos.writeObject(message);

            // 发送成功 打印
            println(senderId + "(我)  发送文件:\t\t   " + new Date().toString());
            println("   路径为:  " + srcPath + "\n");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    // 将内容输出到 chatFrame窗口面板
    public void println(String s) {
        if (s != null) {
            chatFrame.getTextArea1().setText(chatFrame.getTextArea1().getText() + s + "\n");
            System.out.println(s + "\n");
        }
    }
}
