package view;

import service.UserClientService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class zbh_RegisterFrame extends JFrame{
    private JPanel zbh_register;
    private JTextField textField1;
    private JButton button1;
    private JLabel my_label;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private JButton Button2;

    public void showForm(){
        JFrame frame = new JFrame("注册界面");
        frame.setContentPane(new zbh_RegisterFrame().zbh_register);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(500, 150, 445, 460);
        frame.setVisible(true);
    }
    public zbh_RegisterFrame() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = textField1.getText().trim();
                String password = new String(passwordField1.getPassword()).trim();
                String confimPwd = new String(passwordField2.getPassword()).trim();

                if ("".equals(userId) || userId == null) {
                    JOptionPane.showMessageDialog(null, "请输入用户名");
                    return;
                }
                if ("".equals(password) || password == null) {
                    JOptionPane.showMessageDialog(null, "请输入密码");
                    return;
                }
                if ("".equals(confimPwd) || confimPwd == null) {
                    JOptionPane.showMessageDialog(null, "请再次输入密码");
                    return;
                }
                if(password.length() < 6 || password.length() > 20) {
                    JOptionPane.showMessageDialog(null, "请保证密码长度为6到20之间");
                    return;
                }
                if(!confimPwd.equals(password)) {
                    JOptionPane.showMessageDialog(null, "前后两次输入密码不一致！");
                    return;
                }
                UserClientService userClientService = new UserClientService();
                if(userClientService.registUser(userId, password)) {
                    JOptionPane.showMessageDialog(null, "注册成功！");
                    setVisible(false);
                    zbh_LoginFrame loginFrame = new zbh_LoginFrame();
                    loginFrame.showForm();
                } else {
                    JOptionPane.showMessageDialog(null, "服务器未开启！");
                }
            }
        });
    }
}
