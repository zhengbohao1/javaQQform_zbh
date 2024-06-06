package view;

import service.QQServerService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerFrame extends JFrame{
    private JPanel ServerFrame;
    private JButton Button2;
    private JPanel initframe;
    private JTextArea textArea1;

    private QQServerService server;
    public ServerFrame() {
        super("服务器端");
        Button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    @Override
                    public void run() {
                        // 点击 启动服务 时, 初始化QQServer 启动服务端socket 和线程
                        server = new QQServerService(ServerFrame.this);

                    }
                }.start();
            }
        });
        // 服务端退出
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // 弹窗提示
                int a = JOptionPane.showConfirmDialog(null, "确定关闭吗？", "温馨提示",
                        JOptionPane.YES_NO_OPTION);
                if (a == 1) {

                    System.exit(0); // 关闭
                }
            }
        });
    }

    public JTextArea getTextArea1() {
        return textArea1;
    }

    public void showForm(){
        JFrame frame = new JFrame("服务器界面");
        frame.setContentPane(new ServerFrame().ServerFrame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(500, 150, 445, 460);
        frame.setVisible(true);
    }

}
