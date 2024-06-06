package view;

import service.UserClientService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class zbh_OnlineUserFrame extends JFrame{
    private JButton Button1;
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton Button2;
    private JPanel onlineframe;
    private JTextField textField2;
    private JButton Button3;
    private JTextArea textArea2;
    private JLabel Label2;
    private JTextField textField3;
    private JButton Button4;
    private JTextArea textArea3;
    private JButton Button5;

    private String userId;
    private UserClientService userClientService=new UserClientService();
    public zbh_OnlineUserFrame(String userId,UserClientService userClientServices) {
        //System.out.println("创建了");
        this.userClientService = userClientServices;
        this.setTitle(userId + " 聊天用户窗口");
        /*  调用UserClientService类 starThread方法
         * 	传入聊天界面, 在该方法中启动线程
         */

        userClientService.startThread(userId, zbh_OnlineUserFrame.this);

        Button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                userClientService.onlineFriendList(userId);
            }
        });
        Button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
// 若 启动输入框 不为空
                if(!Objects.equals(textField1.getText(), "")) {
                    /*  再new 一个UserClientService对象
                     * 		启动线程, 开启聊天窗口 chatFrame
                     */
                    //判断是否是好友关系，不是的话跳出提醒先加好友
                    //如果内容是群聊
                    if(textField1.getText().equals("群聊")) {
                        zbh_chatFrame zbhChatFrame=new zbh_chatFrame(userId, textField1.getText());
                        zbhChatFrame.showForm();
                        // 启动输入框 及 显示面板 置空
                        textField1.setText(null);
                        //JTextArea.setText(null);
                    }
                    else{
                        userClientService.checkfriend(userId, textField1.getText());
                        try {
                            Thread.sleep(1000); // 1000毫秒，即1秒
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        if(UserClientService.friend_flag) {
                            zbh_chatFrame zbhChatFrame=new zbh_chatFrame(userId, textField1.getText());
                            zbhChatFrame.showForm();
                            // 启动输入框 及 显示面板 置空
                            textField1.setText(null);
                            UserClientService.friend_flag=false;
                            //JTextArea.setText(null);
                        }else {
                            JOptionPane.showMessageDialog(null, "请先添加好友");
                        }
                    }

                }
                else{
                    //弹出窗口“请输入需求”
                    JOptionPane.showMessageDialog(null, "请输入需求");
                }
            }
        });
        Button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String getter_id=textField2.getText();
                if(getter_id.equals("")){
                    JOptionPane.showMessageDialog(null, "请输入用户名");
                }else {
                    userClientService.addfriend(userId, getter_id);
                    try {
                        Thread.sleep(1000); // 1000毫秒，即1秒
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    if(UserClientService.friend_send_flag) {
                        JOptionPane.showMessageDialog(null, "好友请求发送成功！");
                        UserClientService.friend_send_flag=false;
                    }else {
                        JOptionPane.showMessageDialog(null, "好友请求发送失败！");
                    }
                }
            }
        });
        Button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userClientService.insertfriendship(userId, textField3.getText());
                try {
                    Thread.sleep(1000); // 1000毫秒，即1秒
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                if(UserClientService.friend_add_flag) {
                    JOptionPane.showMessageDialog(null, "添加好友成功");
                    UserClientService.friend_add_flag=false;
                }else {
                    JOptionPane.showMessageDialog(null, "添加好友失败！");
                }
            }
        });
        Button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userClientService.get_friends(userId);
            }
        });
    }

    public JTextArea getTextArea1() {
        return textArea1;
    }

    public JTextArea getTextArea2() {
        return textArea2;
    }

    public JTextArea getTextArea3() {
        return textArea3;
    }

    public void showForm(){
        JFrame frame = new JFrame("在线用户功能窗口");
        this.setTitle(userId + " 聊天用户窗口");
        frame.setContentPane(onlineframe);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(500, 150, 700, 470);
        frame.setVisible(true);
    }
}
