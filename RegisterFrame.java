package java_lms_final;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*; // Import RMI

public class RegisterFrame extends JFrame {

    // Components
    JLabel lTitle;
    JTextField tfId, tfName;
    JPasswordField pfPass, pfConfirmPass;
    JButton bRegister, bBack;

    public RegisterFrame() {
        // 1. Frame Setup
        setTitle("New Student Registration");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 2. North: Title
        lTitle = new JLabel("Register New Account", JLabel.CENTER);
        lTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(lTitle, BorderLayout.NORTH);

        // 3. Center: Form Fields
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        // ID
        formPanel.add(new JLabel("  User ID:"));
        tfId = new JTextField();
        formPanel.add(tfId);

        // Name
        formPanel.add(new JLabel("  Full Name:"));
        tfName = new JTextField();
        formPanel.add(tfName);

        // Password
        formPanel.add(new JLabel("  Password:"));
        pfPass = new JPasswordField();
        formPanel.add(pfPass);

        // Confirm Password
        formPanel.add(new JLabel("  Confirm Password:"));
        pfConfirmPass = new JPasswordField();
        formPanel.add(pfConfirmPass);

        add(formPanel, BorderLayout.CENTER);

        // 4. South: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); 
        
        bRegister = createStyledButton("Create Account", new Color(0, 102, 102)); // Teal
        bBack = createStyledButton("Back", new Color(100, 100, 100)); // Dark Gray for "Back"
        
        buttonPanel.add(bRegister);
        buttonPanel.add(bBack);
        
        add(buttonPanel, BorderLayout.SOUTH);

        // 5. Event Handling
        bRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        bBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); 
            }
        });

        setVisible(true);
    }

    public void registerUser() {
        String uid = tfId.getText();
        String name = tfName.getText();
        String pass = new String(pfPass.getPassword());
        String confirm = new String(pfConfirmPass.getPassword());

        // Validation
        if(uid.equals("") || name.equals("") || pass.equals("")) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }
        
        if(!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        try {
            // --- RMI CONNECTION START ---
            LibraryInterface server = (LibraryInterface) Naming.lookup("rmi://localhost/LibraryService");
            
            // Call the remote method
            // Note: We parse the String uid to Integer here because the Interface expects an int
            String result = server.registerUser(Integer.parseInt(uid), name, pass);
            // --- RMI CONNECTION END ---
            
            if(result.equals("Success")) {
                JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.");
                dispose(); // Close register window
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed: " + result);
            }
            
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "User ID must be numbers only!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    // --- HELPER METHOD TO STYLE BUTTONS ---
public JButton createStyledButton(String text, Color bgColor) {
    JButton btn = new JButton(text);
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setFont(new Font("SansSerif", Font.BOLD, 12)); 
    return btn;
}
}