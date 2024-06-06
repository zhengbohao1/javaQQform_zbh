package view;

import org.junit.Test;
import service.UserClientService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class zbh_LoginFrame extends JFrame  {
    private JTextField textField1;

    JFrame real_frame=new JFrame("用户界面");
    private JLabel my_label;

    private boolean logincorrect=false;
    public zbh_LoginFrame() {
        Button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                zbh_RegisterFrame frame=new zbh_RegisterFrame();
                frame.showForm();
            }
        });
        Button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = textField1.getText().trim();
                String password = new String(passwordField1.getPassword()).trim();

                if ("".equals(userId) || userId == null) {
                    JOptionPane.showMessageDialog(null, "请输入帐号！！");
                    return;
                }
                if ("".equals(password) || password == null) {
                    JOptionPane.showMessageDialog(null, "请输入密码！！");
                    return;
                }
                UserClientService userClientService = new UserClientService();
                if(userClientService.checkUser(userId, password)) {

                    // 弹窗登录成功
                    JOptionPane.showMessageDialog(null, "登录成功！");
                    logincorrect=true;
                    dispose();
                    setVisible(false);
                    real_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    // 启动在线用户窗口
                    SwingUtilities.invokeLater(() -> {
                        zbh_OnlineUserFrame onlineUserFrame = new zbh_OnlineUserFrame(userId, userClientService);
                        onlineUserFrame.showForm();
                    });
                    //real_frame.setVisible(false);
                   //this=null;
                } else {
                    //JOptionPane.showMessageDialog(null, "登陆失败！用户名或密码错误");
                }
            }
        });
    }


    public  void showForm() {
        //JFrame frame = new JFrame("用户界面");
        real_frame.setContentPane(new zbh_LoginFrame().zbh_logingui);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        real_frame.pack();
        real_frame.setBounds(500, 150, 445, 460);
        real_frame.setVisible(true);
    }

    private JPanel zbh_logingui;
    private JButton Button1;
    private JButton Button2;
    private JPasswordField passwordField1;


}
