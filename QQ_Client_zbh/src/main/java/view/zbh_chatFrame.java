package view;

import service.FileClientService;
import service.MessageClientService;
import service.UserClientService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class zbh_chatFrame extends JFrame {
    private JPanel panel1;
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton Button1;
    private JTextField textField2;
    private JButton Button2;
    private JButton Button3;
    private String sendFileName = null;       // 发送文件 的文件名
    private MessageClientService messageClientService = null;   // 聊天类
    private FileClientService fileClinetService = null;         // 文件传输类

    public JTextArea getTextArea1() {
        return textArea1;
    }

    public zbh_chatFrame(String userId, String getterId) {
        System.out.println(Thread.currentThread().getName());
        // 初始化文件传输类
        this.fileClinetService = new FileClientService();
        // 初始化 聊天类
        this.messageClientService = new MessageClientService(zbh_chatFrame.this);

        // 初始化 启动线程
        UserClientService userClientService = new UserClientService();
        userClientService.startThreadChat(userId, getterId, zbh_chatFrame.this);

        textArea1.setText("\n----------------------------- 欢迎登录网络聊天室 ------------------------------\n");
        if(getterId.equals("群聊")) {
            setTitle(userId + "    的群聊聊天室窗口");
        } else {
            setTitle(userId + "  =>  " + getterId + "  的聊天窗口");
        }
        Button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(getterId.equals("群聊")) {
                    /* 调用聊天类messageClientService
                     * 		的sendMessageToAll()群聊方法  向服务端发送请求
                     */
                    messageClientService.sendMessageToAll(userId, textField1.getText());
                    textField1.setText("");   // 换行
                } else {
                    messageClientService.sendMessageToOne(userId, getterId, textField1.getText());
                    textField1.setText("");
                }
            }
        });
        // 客户端退出
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                // 弹窗提示
                JOptionPane.showMessageDialog(null, "是否退出?",
                        "确定", JOptionPane.QUESTION_MESSAGE);
                /*	调用userClientService类
                 * 		的 logout()方法向服务端发送退出提示  无异常退出
                 */
                userClientService.logout(userId, getterId);

                this.setVisible(false);  // 点击关闭后 隐藏

            }

            private void setVisible(boolean b) {}
        });
        Button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();

                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//文件名和文件

                /* JFileChooser 文件选择器
                 * 参数:
                 *     parent: 文件选取器对话框的父组件, 对话框将会尽量显示在靠近 parent 的中心; 如果传 null, 则显示在屏幕中心。
                 *
                 * 返回值:
                 *     JFileChooser.CANCEL_OPTION: 点击了取消或关闭
                 *     JFileChooser.APPROVE_OPTION: 点击了确认或保存
                 *     JFileChooser.ERROR_OPTION: 出现错误
                 */
                // 在获取用户选择的文件之前，通常先验证返回值是否为 APPROVE_OPTION.
                int num = chooser.showOpenDialog(null);

                // 若选择了文件，则打印选择的文件路径
                if(num == JFileChooser.APPROVE_OPTION)
                {
                    File file = chooser.getSelectedFile();         // 获取文件
                    sendFileName = file.getName();                 // 保存文件名
                    textField2.setText(file.getAbsolutePath());  // 输出文件路径在txt_SendFile
                }
            }
        });
        Button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(getterId.equals("群聊")) {

                    fileClinetService.sendFileToAll(userId, textField2.getText(),
                            sendFileName, zbh_chatFrame.this);
                } else {

                    fileClinetService.sendFileToOne(userId, getterId, textField2.getText(),
                            sendFileName, zbh_chatFrame.this);
                }

                textField2.setText("");
                sendFileName = null;
            }
        });
    }
    public void showForm()
    {
        JFrame frame = new JFrame("zbh_charFrame");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(500, 150, 445, 460);
        frame.setVisible(true);
    }

}
