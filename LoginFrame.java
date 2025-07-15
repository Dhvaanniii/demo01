import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn, registerBtn;
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public LoginFrame() {
        setTitle("Hostel Management - Login");
        setSize(320, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        add(panel);

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBounds(120, 10, 100, 25);
        panel.add(titleLabel);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(LABEL_FONT);
        userLabel.setBounds(20, 50, 80, 20);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setFont(LABEL_FONT);
        usernameField.setBounds(100, 50, 180, 22);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(LABEL_FONT);
        passwordLabel.setBounds(20, 80, 80, 20);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setFont(LABEL_FONT);
        passwordField.setBounds(100, 80, 180, 22);
        panel.add(passwordField);

        loginBtn = new JButton("Login");
        loginBtn.setFont(BUTTON_FONT);
        loginBtn.setBackground(PRIMARY_COLOR);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBounds(40, 130, 100, 28);
        panel.add(loginBtn);

        registerBtn = new JButton("Register");
        registerBtn.setFont(BUTTON_FONT);
        registerBtn.setBackground(PRIMARY_COLOR);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setBounds(170, 130, 100, 28);
        panel.add(registerBtn);

        setupButtonListeners();
        setVisible(true);
    }

    private void setupButtonListeners() {
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> {
            new RegistrationFrame();
            dispose();
        });
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT role FROM users WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + username, "Success", JOptionPane.INFORMATION_MESSAGE);

                if (role.equals("admin")) {
                    new AdminDashboard(username);
                } else {
                    new StudentDashboard(username);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
