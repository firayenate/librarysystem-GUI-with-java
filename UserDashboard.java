package java_lms_final;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*; // Import RMI
import java.util.ArrayList; // Import ArrayList

public class UserDashboard extends JFrame {

    
    JPanel pCenter, pViewBooks, pSearchBooks, pBorrowedBooks;
    JButton bNavView, bNavSearch, bNavBorrowed, bLogout;
    JLabel lHeader;
    
    
    JTextField tfSearch;
    JButton bSearchAction, bSearchBorrow;
    JTable searchTable;
    DefaultTableModel searchModel;

    
    JTable bookTable;
    DefaultTableModel bookModel;
    JButton bViewBorrow;

    
    JTable borrowedTable;
    DefaultTableModel borrowedModel;
    JButton bReturnAction;


    String currentUserId;
    

    public UserDashboard(String userId) {
        this.currentUserId = userId;
        
       
        setTitle("User Dashboard - ID: " + userId);
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 2. THEME HEADER
        JPanel pTop = new JPanel();
        pTop.setBackground(new Color(0, 102, 102));
        pTop.setLayout(new FlowLayout(FlowLayout.CENTER));
        pTop.setPreferredSize(new Dimension(800, 50));
        
        lHeader = new JLabel("LIBRARY STUDENT PORTAL");
        lHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lHeader.setForeground(Color.WHITE);
        pTop.add(lHeader);
        add(pTop, BorderLayout.NORTH);

        
        JPanel pNav = new JPanel();
        pNav.setBackground(new Color(230, 230, 230));
        pNav.setLayout(new GridLayout(5, 1, 5, 5));
        pNav.setPreferredSize(new Dimension(160, 0));

        bNavView = new JButton("View All Books");
        bNavSearch = new JButton("Search Books");
        bNavBorrowed = new JButton("My Borrowed Books");
        bLogout = new JButton("Logout");
        bNavView = createStyledButton("View All Books", new Color(0, 102, 102));
        bNavSearch = createStyledButton("Search Books", new Color(0, 102, 102));
        bNavBorrowed = createStyledButton("My Borrowed Books", new Color(0, 102, 102));
        bLogout = createStyledButton("Logout", new Color(200, 50, 50));

        pNav.add(bNavView);
        pNav.add(bNavSearch);
        pNav.add(bNavBorrowed);
        pNav.add(new JLabel(""));
        pNav.add(bLogout);
        
        add(pNav, BorderLayout.WEST);

        
        pCenter = new JPanel(new CardLayout());
        pCenter.setLayout(new BorderLayout());
        add(pCenter, BorderLayout.CENTER);

        
        initViewPanel();
        initSearchPanel();
        initBorrowedPanel();

        
        switchToViewBooks();

        
        bNavView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { switchToViewBooks(); }
        });

        bNavSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { switchToSearchBooks(); }
        });

        bNavBorrowed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { switchToBorrowedBooks(); }
        });

        bLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        setVisible(true);
    }
    private JButton createStyledButton(String text, Color bgColor) {
    JButton btn = new JButton(text);
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    // Optional: Add a font setting for consistency
    btn.setFont(new Font("SansSerif", Font.BOLD, 12)); 
    return btn;
}

    
    void initViewPanel() {
        pViewBooks = new JPanel(new BorderLayout());
        
        String[] cols = {"ID", "Title", "Author", "Qty"};
        bookModel = new DefaultTableModel(cols, 0);
        bookTable = new JTable(bookModel);
        styleTable(bookTable); 
        
        JPanel pBtn = new JPanel(new FlowLayout());
        bViewBorrow = new JButton("Borrow Selected Book");
        bViewBorrow = createStyledButton("Borrow Selected Book", new Color(0, 102, 102));
        pBtn.add(bViewBorrow);
        

        pViewBooks.add(new JScrollPane(bookTable), BorderLayout.CENTER);
        pViewBooks.add(pBtn, BorderLayout.SOUTH);

        bViewBorrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = bookTable.getSelectedRow();
                if(row != -1) {
                    String bid = bookModel.getValueAt(row, 0).toString();
                    borrowBook(bid);
                } else {
                    JOptionPane.showMessageDialog(null, "Select a book first.");
                }
            }
        });
    }

    // --- SEARCH PANEL ---
    void initSearchPanel() {
        pSearchBooks = new JPanel(new BorderLayout());
        
        JPanel pSearchTop = new JPanel(new FlowLayout());
        tfSearch = new JTextField(20);
        bSearchAction = new JButton("Find");
        bSearchAction = createStyledButton("Find", new Color(0, 102, 102));
        
        pSearchTop.add(new JLabel("Title/Author:"));
        pSearchTop.add(tfSearch);
        pSearchTop.add(bSearchAction);

        String[] cols = {"ID", "Title", "Author", "Qty"};
        searchModel = new DefaultTableModel(cols, 0);
        searchTable = new JTable(searchModel);
        styleTable(searchTable); 

        JPanel pSearchBot = new JPanel(new FlowLayout());
        bSearchBorrow = new JButton("Borrow Selected Book");
        bSearchBorrow = createStyledButton("Borrow Selected Book", new Color(0, 102, 102));
        pSearchBot.add(bSearchBorrow);

        pSearchBooks.add(pSearchTop, BorderLayout.NORTH);
        pSearchBooks.add(new JScrollPane(searchTable), BorderLayout.CENTER);
        pSearchBooks.add(pSearchBot, BorderLayout.SOUTH);

        bSearchAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch(tfSearch.getText());
            }
        });

        bSearchBorrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = searchTable.getSelectedRow();
                if(row != -1) {
                    String bid = searchModel.getValueAt(row, 0).toString();
                    borrowBook(bid);
                } else {
                    JOptionPane.showMessageDialog(null, "Select a book first.");
                }
            }
        });
    }

    // --- MY BORROWED BOOKS PANEL ---
    void initBorrowedPanel() {
        pBorrowedBooks = new JPanel(new BorderLayout());

        String[] cols = {"Book ID", "Title", "Issue Date"};
        borrowedModel = new DefaultTableModel(cols, 0);
        borrowedTable = new JTable(borrowedModel);

        JPanel pBtn = new JPanel(new FlowLayout());
        styleTable(borrowedTable); 
        bReturnAction = new JButton("Return Selected Book");
        bReturnAction = createStyledButton("Return Selected Book", new Color(0, 102, 102));
        pBtn.add(bReturnAction);

        pBorrowedBooks.add(new JScrollPane(borrowedTable), BorderLayout.CENTER);
        pBorrowedBooks.add(pBtn, BorderLayout.SOUTH);

        bReturnAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = borrowedTable.getSelectedRow();
                if(row != -1) {
                    String bid = borrowedModel.getValueAt(row, 0).toString();
                    returnBook(bid);
                } else {
                    JOptionPane.showMessageDialog(null, "Select a book to return.");
                }
            }
        });
    }

    // --- SWITCHING LOGIC ---
    void switchToViewBooks() {
        pCenter.removeAll();
        pCenter.add(pViewBooks, BorderLayout.CENTER);
        lHeader.setText("LIBRARY - VIEW ALL BOOKS");
        loadAllBooks();
        pCenter.revalidate(); pCenter.repaint();
    }

    void switchToSearchBooks() {
        pCenter.removeAll();
        pCenter.add(pSearchBooks, BorderLayout.CENTER);
        lHeader.setText("LIBRARY - SEARCH BOOKS");
        pCenter.revalidate(); pCenter.repaint();
    }

    void switchToBorrowedBooks() {
        pCenter.removeAll();
        pCenter.add(pBorrowedBooks, BorderLayout.CENTER);
        lHeader.setText("LIBRARY - MY BORROWED BOOKS");
        loadBorrowedBooks();
        pCenter.revalidate(); pCenter.repaint();
    }

    // --- RMI ACTIONS (Replaced Database Code) ---
    
    // Connect helper
    private LibraryInterface getServer() throws Exception {
        return (LibraryInterface) Naming.lookup("rmi://localhost/LibraryService");
    }

    void loadAllBooks() {
        bookModel.setRowCount(0);
        try {
            LibraryInterface server = getServer();
            ArrayList<String[]> books = server.getAllBooks();
            
            for(String[] row : books) {
                bookModel.addRow(row);
            }
        } catch(Exception e) {
            System.out.println("Error loading books: " + e);
        }
    }

    void performSearch(String keyword) {
        searchModel.setRowCount(0);
        try {
            LibraryInterface server = getServer();
            ArrayList<String[]> books = server.searchBooks(keyword);
            
            for(String[] row : books) {
                searchModel.addRow(row);
            }
        } catch(Exception e) {
            System.out.println("Error searching: " + e);
        }
    }

    void loadBorrowedBooks() {
        borrowedModel.setRowCount(0);
        try {
            LibraryInterface server = getServer();
            ArrayList<String[]> myBooks = server.getMyBorrowedBooks(currentUserId);
            
            for(String[] row : myBooks) {
                borrowedModel.addRow(row);
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    void borrowBook(String bookId) {
        try {
            LibraryInterface server = getServer();
            String result = server.borrowBook(currentUserId, bookId);
            
            JOptionPane.showMessageDialog(null, result);

            
            if(pViewBooks.isShowing()) loadAllBooks();
            if(pSearchBooks.isShowing()) performSearch(tfSearch.getText());

        } catch(Exception e) { JOptionPane.showMessageDialog(null, "Error: " + e.getMessage()); }
    }

    void returnBook(String bookId) {
        try {
            LibraryInterface server = getServer();
            String result = server.returnBook(currentUserId, bookId);
            
            JOptionPane.showMessageDialog(null, result);
            loadBorrowedBooks(); 

        } catch(Exception e) { JOptionPane.showMessageDialog(null, "Error: " + e.getMessage()); }
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