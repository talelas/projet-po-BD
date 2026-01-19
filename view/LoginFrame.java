package view;

import exception.AuthentificationException;
import model.User;
import service.AuthentificationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;
    private AuthentificationService authService;

    public LoginFrame() {
        authService = new AuthentificationService();
        initializeUI();
        checkDatabaseConnection();
    }

    private void initializeUI() {
        setTitle("Pharmacy Management System - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 248, 250));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(500, 110));

        JLabel titleLabel = new JLabel("Pharmacy System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(24, 0, 6, 0));

        JLabel subtitleLabel = new JLabel("Secure access to the back-office", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 235, 245));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // Username Field
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password Field
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login Button
        btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Status Label
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(41, 128, 185));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assembly
        formPanel.add(lblUsername);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(txtUsername);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(lblPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(txtPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        formPanel.add(btnLogin);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(lblStatus);

        add(formPanel, BorderLayout.CENTER);

        // Actions
        btnLogin.addActionListener(e -> performLogin());
        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
    }

    private void checkDatabaseConnection() {
        try {
            db.Db.getConnection().close();
            lblStatus.setText("✓ Database connected");
            lblStatus.setForeground(new Color(46, 204, 113));
        } catch (Exception e) {
            lblStatus.setText("⚠ Database offline - using demo mode");
            lblStatus.setForeground(new Color(231, 76, 60));
        }
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("⚠ Please fill in all fields");
            lblStatus.setForeground(new Color(231, 76, 60));
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");
        lblStatus.setText("Authenticating...");
        lblStatus.setForeground(new Color(41, 128, 185));

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.authenticate(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    lblStatus.setText("✓ Login successful!");
                    lblStatus.setForeground(new Color(46, 204, 113));
                    dispose();
                    SwingUtilities.invokeLater(() -> new MainFrame(user).setVisible(true));
                } catch (Exception ex) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("LOGIN");
                    lblStatus.setText("✗ " + (ex.getCause() instanceof AuthentificationException ? 
                        ex.getCause().getMessage() : "Login failed"));
                    lblStatus.setForeground(new Color(231, 76, 60));
                }
            }
        };
        worker.execute();
    }
}
