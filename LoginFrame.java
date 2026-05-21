package java_lms_final;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.rmi.*; // Import RMI

public class LoginFrame extends JFrame {

    // 1. Define Components
    JLabel lRole, lId, lPass;
    JTextField tfId;
    JPasswordField pfPass;
    JButton bLogin, bRegister;
    JComboBox<String> cbRole;
    
    

    public LoginFrame() {
        // 2. Setup Frame
            setTitle("LMS - Secure Login");
    setSize(450, 400); // Slightly bigger
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    // Use a Main Panel with padding
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(null);
    mainPanel.setBackground(new Color(245, 245, 245)); // Light Gray Background
    setContentPane(mainPanel);

    
    JPanel pHeader = new JPanel();
    pHeader.setBackground(new Color(0, 102, 102)); // Teal color
    pHeader.setBounds(0, 0, 450, 60);
    JLabel lTitle = new JLabel("Library Login");
    lTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
    lTitle.setForeground(Color.WHITE);
    pHeader.add(lTitle);
    mainPanel.add(pHeader);

    // 3. Components (Styled)
    Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
    Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

    // Role
    lRole = new JLabel("Select Role:");
    lRole.setBounds(60, 90, 100, 30);
    lRole.setFont(labelFont);
    mainPanel.add(lRole);

    String[] roles = {"User", "Admin"};
    cbRole = new JComboBox<>(roles);
    cbRole.setBounds(170, 90, 200, 30);
    cbRole.setBackground(Color.WHITE);
    mainPanel.add(cbRole);

    // ID
    lId = new JLabel("User ID:");
    lId.setBounds(60, 140, 100, 30);
    lId.setFont(labelFont);
    mainPanel.add(lId);

    tfId = new JTextField();
    tfId.setBounds(170, 140, 200, 30);
    tfId.setFont(fieldFont);
    mainPanel.add(tfId);

   
    lPass = new JLabel("Password:");
    lPass.setBounds(60, 190, 100, 30);
    lPass.setFont(labelFont);
    mainPanel.add(lPass);

    pfPass = new JPasswordField();
    pfPass.setBounds(170, 190, 200, 30);
    pfPass.setFont(fieldFont);
    mainPanel.add(pfPass);

    // 4. Buttons (Flat Design)
    bLogin = new JButton("LOGIN");
    bLogin.setBounds(60, 260, 150, 40);
    bLogin.setBackground(new Color(0, 102, 102)); 
    bLogin.setForeground(Color.WHITE);
    bLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
    bLogin.setFocusPainted(false); // Remove dotted line
    mainPanel.add(bLogin);

    bRegister = new JButton("Register");
    bRegister.setBounds(220, 260, 150, 40);
    bRegister.setBackground(new Color(100, 100, 100)); // Dark Gray
    bRegister.setForeground(Color.WHITE);
    bRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
    bRegister.setFocusPainted(false);
    mainPanel.add(bRegister);

        // 4. Events / Logic
        
        // A. Toggle Register Button Visibility
        cbRole.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String selectedRole = (String) cbRole.getSelectedItem();
                if(selectedRole.equals("Admin")) {
                    bRegister.setVisible(false);
                } else {
                    bRegister.setVisible(true);
                }
            }
        });

        // B. Login Button Logic (RMI VERSION)
        bLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idInput = tfId.getText();
                String passInput = new String(pfPass.getPassword());
                String roleInput = (String) cbRole.getSelectedItem();
                      
                if(idInput.equals("") || passInput.equals("")) {
                     JOptionPane.showMessageDialog(null, "Please enter ID and Password");
                     return;
                }

                try {
                    // --- RMI CONNECTION START ---
                    // 1. Locate the Server Registry (Slide 20)
                    LibraryInterface server = (LibraryInterface) Naming.lookup("rmi://localhost/LibraryService");
                    
                    // 2. Call the Remote Method
                    String result = server.login(idInput, passInput, roleInput);
                    
                    // --- RMI CONNECTION END ---

                    if(result.equals(roleInput)) {
                        JOptionPane.showMessageDialog(null, "Login Successful as " + roleInput);
                        dispose(); // Close login window
                        
                        if(roleInput.equals("User")) {
                            new UserDashboard(idInput); 
                        } else {  
                            new AdminDashboard(); 
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid ID or Password", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(null, "Connection Error: Is the Server running?\n" + ex.getMessage());
                }
            }
        });

        // C. Register Button Logic
        bRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RegisterFrame(); 
            }
        });
        
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}