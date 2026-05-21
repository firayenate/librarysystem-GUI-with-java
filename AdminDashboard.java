package java_lms_final;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*; // Import RMI
import java.util.ArrayList; // Import ArrayList

public class AdminDashboard extends JFrame {

    // Components
    JPanel pCenter, pManageBooks, pManageMembers;
    JButton bNavBooks, bNavMembers, bLogout;
    JLabel lHeader;

    // Manage Books Components
    JTextField tfId, tfTitle, tfAuthor, tfQty;
    JButton bAddBook, bDeleteBook, bUpdateBook;
    JTable bookTable;
    DefaultTableModel bookModel;

    // Manage Members Components
    JTextField tfSearchMember,tfMemId, tfMemName, tfMemPass; 
    JButton bSearchMember;
    JTable memberTable;
    DefaultTableModel memberModel;
    JButton bDeleteMember, bUpdateMember;
    
    JButton bViewReport; // Add this with other buttons

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. HEADER
        JPanel pTop = new JPanel();
        pTop.setBackground(new Color(102, 0, 0)); 
        pTop.setPreferredSize(new Dimension(800, 60));
        lHeader = new JLabel("ADMINISTRATOR CONTROL");
        lHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lHeader.setForeground(Color.WHITE);
        pTop.add(lHeader);
        add(pTop, BorderLayout.NORTH);

        // 2. NAVIGATION
        JPanel pNav = new JPanel(new GridLayout(4, 1, 5, 5));
        pNav.setPreferredSize(new Dimension(160, 0));
        pNav.setBackground(Color.LIGHT_GRAY);

        bNavBooks = new JButton("Manage Books");
        bNavMembers = new JButton("Manage Members");
        bLogout = new JButton("Logout");
        bNavBooks = createStyledButton("Manage Books",  new Color(0, 102, 102));
        bNavMembers = createStyledButton("Manage Members",  new Color(0, 102, 102));
        bLogout = createStyledButton("Logout", new Color(200, 50, 50));
        

        pNav.add(bNavBooks);
        pNav.add(bNavMembers);
        pNav.add(new JLabel("")); 
        pNav.add(bLogout);
        
        add(pNav, BorderLayout.WEST);

        // 3. CENTER
        pCenter = new JPanel(new BorderLayout());
        add(pCenter, BorderLayout.CENTER);

        initBookPanel();
        initMemberPanel();
        
        switchToBooks();

        // Events
        
        
        bNavBooks.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { switchToBooks(); }});
        bNavMembers.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { switchToMembers(); }});
        bLogout.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        

        setVisible(true);
    }
    
    public JButton createStyledButton(String text, Color bgColor) {
    JButton btn = new JButton(text);
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    // Optional: Add a font setting for consistency
    btn.setFont(new Font("SansSerif", Font.BOLD, 12)); 
    return btn;
}

    void initBookPanel() {
    pManageBooks = new JPanel(new BorderLayout());
    
    // --- FORM PANEL ---
    JPanel pForm = new JPanel(new FlowLayout());
    
    tfId = new JTextField(4);
    tfTitle = new JTextField(10); 
    tfAuthor = new JTextField(10); 
    tfQty = new JTextField(4);
    
    bAddBook = new JButton("Add");
    bUpdateBook = new JButton("Update"); // Initialize new button
    bUpdateBook = createStyledButton("Update", new Color(0, 102, 102));
    bAddBook = createStyledButton("Add", new Color(0, 102, 102));
    
    pForm.add(new JLabel("ID:")); pForm.add(tfId);
    pForm.add(new JLabel("Title:")); pForm.add(tfTitle);
    pForm.add(new JLabel("Author:")); pForm.add(tfAuthor);
    pForm.add(new JLabel("Qty:")); pForm.add(tfQty);
    
    pForm.add(bAddBook);
    pForm.add(bUpdateBook); // Add to panel

    // Table
    String[] cols = {"ID", "Title", "Author", "Qty"};
    bookModel = new DefaultTableModel(cols, 0);
    bookTable = new JTable(bookModel);
    styleTable(bookTable); 

    // --- MOUSE LISTENER (Click row to fill fields) ---
    bookTable.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            int row = bookTable.getSelectedRow();
            if (row != -1) {
                // Get data from table and put into text fields
                tfId.setText(bookModel.getValueAt(row, 0).toString());
                tfTitle.setText(bookModel.getValueAt(row, 1).toString());
                tfAuthor.setText(bookModel.getValueAt(row, 2).toString());
                tfQty.setText(bookModel.getValueAt(row, 3).toString());
                
                // Optional: Make ID read-only so they don't accidentally change the key
                // tfId.setEditable(false); 
            }
        }
    });
    
 

    JPanel pSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Added gap
    
    // Existing Delete Button
    bDeleteBook = createStyledButton("Delete Selected Book", new Color(200, 50, 50)); 
    
    // NEW: Report Button
    bViewReport = createStyledButton("View Borrowed Report", new Color(0, 102, 102)); // Teal
    
    pSouth.add(bDeleteBook);
    pSouth.add(bViewReport); // Add it to the panel

    pManageBooks.add(pForm, BorderLayout.NORTH);
    pManageBooks.add(new JScrollPane(bookTable), BorderLayout.CENTER);
    pManageBooks.add(pSouth, BorderLayout.SOUTH);

    // --- ACTIONS ---
    bAddBook.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { addBook(); }});
    bUpdateBook.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { updateBook(); }});
    bDeleteBook.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { deleteBook(); }});
    
    // NEW: Report Action
    bViewReport.addActionListener(new ActionListener() { 
        public void actionPerformed(ActionEvent e) { 
            showReportDialog(); 
        }
    });
}

void updateBook() {
    try {
        LibraryInterface server = getServer();
        
        int id = Integer.parseInt(tfId.getText());
        String title = tfTitle.getText();
        String author = tfAuthor.getText();
        String qty = tfQty.getText();

        String result = server.updateBook(id, title, author, qty);
        
        if(result.equals("Success")) {
            JOptionPane.showMessageDialog(null, "Book Updated Successfully");
            
            // Clear fields and Reload
            tfId.setText(""); tfTitle.setText(""); tfAuthor.setText(""); tfQty.setText("");
            // tfId.setEditable(true); // Re-enable if you disabled it
            loadBooks(); 
        } else {
            JOptionPane.showMessageDialog(null, "Server Error: " + result);
        }
        
    } catch(NumberFormatException ex) {
        JOptionPane.showMessageDialog(null, "Error: ID and Qty must be numbers.");
    } catch(Exception e) { 
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage()); 
    }
}

void initMemberPanel() {
    pManageMembers = new JPanel(new BorderLayout());
    
    // --- TOP PANEL: EDIT FORM ---
    JPanel pForm = new JPanel(new FlowLayout());
    
    tfMemId = new JTextField(5);
    tfMemName = new JTextField(10);
    tfMemPass = new JTextField(10);
    bUpdateMember = new JButton("Update Member Info");
    bUpdateMember = createStyledButton("Update Member Info", new Color(0, 102, 102));
    
    pForm.add(new JLabel("User ID:")); pForm.add(tfMemId);
    pForm.add(new JLabel("Name:")); pForm.add(tfMemName);
    pForm.add(new JLabel("Password:")); pForm.add(tfMemPass);
    pForm.add(bUpdateMember);
    
    // --- CENTER: TABLE ---
    // Ensure column headers match what we did in the last step
    String[] cols = {"User ID", "Name", "Password"};
    memberModel = new DefaultTableModel(cols, 0);
    memberTable = new JTable(memberModel);
    styleTable(memberTable); 
    

    // --- MOUSE LISTENER (Click row to fill fields) ---
    memberTable.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            int row = memberTable.getSelectedRow();
            if (row != -1) {
                // Fill the text fields with the selected row's data
                tfMemId.setText(memberModel.getValueAt(row, 0).toString());
                tfMemName.setText(memberModel.getValueAt(row, 1).toString());
                tfMemPass.setText(memberModel.getValueAt(row, 2).toString());
                
                // Optional: Disable ID editing so they don't change the Primary Key
                // tfMemId.setEditable(false);
            }
        }
    });

    // --- BOTTOM PANEL: SEARCH & DELETE ---
    JPanel pSouth = new JPanel(new FlowLayout());
    
    tfSearchMember = new JTextField(10);
    bSearchMember = new JButton("Search ID");
    bDeleteMember = new JButton("Delete Selected");
    bSearchMember = createStyledButton("Search ID", new Color(0, 102, 102));
    bDeleteMember = createStyledButton("Delete Selected", new Color(200, 50, 50)); // Red for delete
    
    pSouth.add(new JLabel("Find ID:"));
    pSouth.add(tfSearchMember);
    pSouth.add(bSearchMember);
    pSouth.add(Box.createHorizontalStrut(20)); // Spacing
    pSouth.add(bDeleteMember);

    // Add panels to main layout
    pManageMembers.add(pForm, BorderLayout.NORTH);
    pManageMembers.add(new JScrollPane(memberTable), BorderLayout.CENTER);
    pManageMembers.add(pSouth, BorderLayout.SOUTH);

    // --- BUTTON ACTIONS ---
    bDeleteMember.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { deleteMember(); }});
    
    bSearchMember.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            searchMember(tfSearchMember.getText());
        }
    });
    
    // NEW: Update Action
    bUpdateMember.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            updateMember();
        }
    });
}






   


    void switchToBooks() {
        pCenter.removeAll();
        pCenter.add(pManageBooks, BorderLayout.CENTER);
        lHeader.setText("ADMIN - MANAGE BOOKS");
        loadBooks();
        pCenter.revalidate(); pCenter.repaint();
    }

    void switchToMembers() {
        pCenter.removeAll();
        pCenter.add(pManageMembers, BorderLayout.CENTER);
        lHeader.setText("ADMIN - MANAGE MEMBERS");
        loadMembers(); 
        pCenter.revalidate(); pCenter.repaint();
    }

    // --- RMI METHODS (Replaced DB Logic) ---
    
    // Connect helper
    private LibraryInterface getServer() throws Exception {
        return (LibraryInterface) Naming.lookup("rmi://localhost/LibraryService");
    }

    void loadBooks() {
        bookModel.setRowCount(0);
        try {
            LibraryInterface server = getServer();
            ArrayList<String[]> books = server.getAllBooks();
            for(String[] row : books) {
                bookModel.addRow(row);
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    void loadMembers() {
        memberModel.setRowCount(0);
        try {
            LibraryInterface server = getServer();
            ArrayList<String[]> members = server.getAllMembers();
            for(String[] row : members) {
                memberModel.addRow(row);
            }
        } catch(Exception e) { e.printStackTrace(); }
    }
    
    void searchMember(String userId) {
        if(userId.trim().isEmpty()) { loadMembers(); return; }
        memberModel.setRowCount(0);
        try {
            LibraryInterface server = getServer();
            ArrayList<String[]> members = server.searchMember(userId);
            for(String[] row : members) {
                memberModel.addRow(row);
            }
        } catch(Exception e) { JOptionPane.showMessageDialog(null, "Error: " + e.getMessage()); }
    }

    void addBook() {
        try {
            LibraryInterface server = getServer();
            
            int id = Integer.parseInt(tfId.getText());
            String title = tfTitle.getText();
            String author = tfAuthor.getText();
            String qty = tfQty.getText();

            String result = server.addBook(id, title, author, qty);
            
            if(result.equals("Success")) {
                JOptionPane.showMessageDialog(null, "Book Added Successfully");
                // Clear fields
                tfId.setText(""); tfTitle.setText(""); tfAuthor.setText(""); tfQty.setText("");
                loadBooks(); 
            } else {
                JOptionPane.showMessageDialog(null, "Server Error: " + result);
            }
            
        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Error: ID and Qty must be numbers.");
        } catch(Exception e) { 
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage()); 
        }
    }

    void deleteBook() {
        int r = bookTable.getSelectedRow();
        if(r == -1) return;
        String id = bookModel.getValueAt(r, 0).toString();
        try {
            LibraryInterface server = getServer();
            String result = server.deleteBook(id);
            if(result.equals("Success")) {
                JOptionPane.showMessageDialog(null, "Book Deleted");
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(null, result);
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    void deleteMember() {
        int r = memberTable.getSelectedRow();
        if(r == -1) return;
        String id = memberModel.getValueAt(r, 0).toString();
        try {
            LibraryInterface server = getServer();
            String result = server.deleteMember(id);
            if(result.equals("Success")) {
                JOptionPane.showMessageDialog(null, "Member Removed");
                loadMembers();
            } else {
                JOptionPane.showMessageDialog(null, result);
            }
        } catch(Exception e) { e.printStackTrace(); }
    }
    
void updateMember() {
    try {
        LibraryInterface server = getServer();
        
        int id = Integer.parseInt(tfMemId.getText());
        String name = tfMemName.getText();
        String pass = tfMemPass.getText();

        String result = server.updateMember(id, name, pass);
        
        if(result.equals("Success")) {
            JOptionPane.showMessageDialog(null, "Member Updated Successfully");
            
            // Clear fields and Reload
            tfMemId.setText(""); tfMemName.setText(""); tfMemPass.setText("");
            // tfMemId.setEditable(true); // Re-enable if disabled
            loadMembers(); 
        } else {
            JOptionPane.showMessageDialog(null, "Server Error: " + result);
        }
        
    } catch(NumberFormatException ex) {
        JOptionPane.showMessageDialog(null, "Error: User ID must be a number.");
    } catch(Exception e) { 
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage()); 
    }
}

void showReportDialog() {
    // 1. Create a Pop-up Dialog
    JDialog dialog = new JDialog(this, "Borrowed Books Report", true); // 'true' means modal (must close to go back)
    dialog.setSize(950, 600);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    // 2. Create Table
    String[] cols = {"User Name", "Book Title", "Date Borrowed"};
    DefaultTableModel repModel = new DefaultTableModel(cols, 0);
    JTable repTable = new JTable(repModel);
    styleTable(repTable); // Use your styling helper!

    // 3. Load Data from Server
    try {
        LibraryInterface server = getServer();
        ArrayList<String[]> data = server.getBorrowedBooksReport();
        
        for(String[] row : data) {
            // The query returns: {UserID, Name, Title, Date}
            // Let's just show Name, Title, Date (Indices 1, 2, 3)
            repModel.addRow(new String[]{ row[1], row[2], row[3] });
        }
        
    } catch(Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading report: " + e.getMessage());
    }

    // 4. Add to Dialog
    dialog.add(new JScrollPane(repTable), BorderLayout.CENTER);
    
    JButton bClose = createStyledButton("Close", new Color(100, 100, 100));
    bClose.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) { dialog.dispose(); }
    });
    
    JPanel pBot = new JPanel();
    pBot.add(bClose);
    dialog.add(pBot, BorderLayout.SOUTH);

    dialog.setVisible(true);
}
// Helper method to make tables look modern
void styleTable(JTable table) {
    // Header Style
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    table.getTableHeader().setBackground(new Color(0, 102, 102)); // Teal Header
    table.getTableHeader().setForeground(Color.WHITE);
    
    // Row Style
    table.setRowHeight(30); // Taller rows
    table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    table.setSelectionBackground(new Color(204, 229, 255)); // Light blue selection
    table.setSelectionForeground(Color.BLACK);
    table.setShowVerticalLines(false);
    table.setIntercellSpacing(new Dimension(0, 0));
}
}

