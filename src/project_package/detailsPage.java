package project_package;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class detailsPage extends javax.swing.JFrame {

    private static int userId;
    private static String name;

    public detailsPage(int userId, String name) {
        initComponents();

        detailsPage.userId = userId;
        detailsPage.name = name;

        populateUserTable();
    }

    // Method to populate adminViewTable with data from the database
    private void populateUserTable() {
        detailsTitle.setText(name + "'s Logged Time Off");

        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM employee_off_time WHERE employee_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    DefaultTableModel model = (DefaultTableModel) timeTable.getModel();
                    model.setRowCount(0);

                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    String[] columnNames = new String[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        columnNames[i] = metaData.getColumnName(i + 1);
                    }
                    model.setColumnIdentifiers(columnNames);

                    while (resultSet.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 0; i < columnCount; i++) {
                            row[i] = resultSet.getObject(i + 1);
                        }
                        model.addRow(row);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error retrieving user data from database: " + ex.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/employees_time_off_registry", "root", "");
    }

    // Method to calculate payment for a single off time entry
    public static double calculatePayment(double hourlyRate, Date startTime, Date endTime) {
        long durationInMillis = endTime.getTime() - startTime.getTime();
        double hoursWorked = durationInMillis / (1000 * 60 * 60.0); // Convert milliseconds to hours
        return hoursWorked * hourlyRate;
    }

    // Method to calculate total payment for all off time entries
    public static double calculateTotalPayment(double hourlyRate, List<OffTimeEntry> offTimeEntries) {
        double totalPayment = 0.0;

        for (OffTimeEntry entry : offTimeEntries) {
            Date startTime = parseDateString(entry.getStartTimeString());
            Date endTime = parseDateString(entry.getEndTimeString());
            totalPayment += calculatePayment(hourlyRate, startTime, endTime);
        }

        return totalPayment;
    }

    // Method to parse string representation of date into Date object
    public static Date parseDateString(String dateString) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy HH:mm", Locale.ENGLISH);
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    // Method to retrieve off time entries from database
    public static List<OffTimeEntry> getOffTimeEntriesFromDatabase() throws SQLException {
        List<OffTimeEntry> offTimeEntries = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/employees_time_off_registry", "root", "")) {
            String query = "SELECT start_time, end_time FROM employee_off_time WHERE employee_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                // Set the employee id parameter (replace 123 with actual employee id)
                statement.setInt(1, userId);

                // Execute the query and retrieve results
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String startTimeString = resultSet.getString("start_time");
                        String endTimeString = resultSet.getString("end_time");
                        offTimeEntries.add(new OffTimeEntry(startTimeString, endTimeString));
                    }
                }
            }
        }
        return offTimeEntries;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        detailsTitle = new javax.swing.JLabel();
        adminnName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        timeTable = new javax.swing.JTable();
        calculateButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        backBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        detailsTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        detailsTitle.setText("Employee Details");

        adminnName.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        adminnName.setText("Admin");

        timeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(timeTable);

        calculateButton.setBackground(new java.awt.Color(0, 0, 204));
        calculateButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        calculateButton.setForeground(new java.awt.Color(255, 255, 255));
        calculateButton.setText("Calculate");
        calculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateButtonActionPerformed(evt);
            }
        });

        backBtn.setBackground(new java.awt.Color(0, 0, 204));
        backBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        backBtn.setForeground(new java.awt.Color(255, 255, 255));
        backBtn.setText("Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(detailsTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(adminnName)
                        .addGap(33, 33, 33))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 694, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(217, 217, 217)
                                .addComponent(calculateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 910, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(622, Short.MAX_VALUE)
                    .addComponent(backBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(146, 146, 146)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(42, 42, 42))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(adminnName)
                            .addComponent(detailsTitle))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(calculateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(531, Short.MAX_VALUE)
                    .addComponent(backBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(73, 73, 73)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void calculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateButtonActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            // Retrieve off time entries from the database
            List<OffTimeEntry> offTimeEntries = getOffTimeEntriesFromDatabase();

            // Example hourly rate
            double hourlyRate = 5.0;

            // Calculate total payment
            double totalPayment = calculateTotalPayment(hourlyRate, offTimeEntries);

            JOptionPane.showMessageDialog(this, "Based on " + name + "'s logged time off, your total payment is: $" + totalPayment);
        } catch (SQLException ex) {
            Logger.getLogger(userPage.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_calculateButtonActionPerformed

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnActionPerformed
        // TODO add your handling code here:
        dispose();
        new adminPage().setVisible(true);
    }//GEN-LAST:event_backBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(detailsPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(detailsPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(detailsPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(detailsPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new detailsPage(1, "Prince").setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel adminnName;
    private javax.swing.JButton backBtn;
    private javax.swing.JButton calculateButton;
    private javax.swing.JLabel detailsTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable timeTable;
    // End of variables declaration//GEN-END:variables
}
