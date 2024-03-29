package project_package;

import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JDialog;
import javax.swing.event.ListSelectionEvent;

public class userPage extends javax.swing.JFrame {

    private static int userId;

    public userPage(int userId) {
        initComponents();

        userPage.userId = userId; // Set user ID

        populateUserTable();

        // Add ListSelectionListener to the userTable
        userTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                // Get the selected row
                int selectedRow = userTable.getSelectedRow();

                if (selectedRow != -1) { // If a row is selected
                    // Get the employee ID from the selected row
                    selectedEmployeeId = (int) userTable.getValueAt(selectedRow, 0);
                }
            }
        });
    }

    // Define a variable to store the selected employee ID
    private int selectedEmployeeId;

    // Method to populate adminViewTable with data from the database
    private void populateUserTable() {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM employee_off_time WHERE employee_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    DefaultTableModel model = (DefaultTableModel) userTable.getModel();
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

    private void showAddPopup(int employeeId, String startTime, String endTime) {
        // Create a confirmation message
        String message = "Are you sure you want to log the selcted time?";

        // Create custom options for the JOptionPane
        Object[] options = {"Confirm", "Close"};

        // Show the confirmation dialog
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[0]);
        JDialog dialog = pane.createDialog("Confirmation");
        dialog.setVisible(true);

        // Handle user choice
        Object choice = pane.getValue();

        // Handle user choice
        if (choice.equals(options[0])) {
            // User clicked "Confirm"
            logUserTime(employeeId, startTime, endTime);
        } else if (choice.equals(options[1])) {
            // User clicked "Close" or closed the dialog
            dialog.dispose();
        }
    }

    private boolean isEndTimeAfterStartTime(String startTime, String endTime) {
        DateFormat format = new SimpleDateFormat("EEE, MMM dd yyyy HH:mm", Locale.ENGLISH);
        try {
            Date start = format.parse(startTime);
            Date end = format.parse(endTime);
            return end.after(start);
        } catch (ParseException e) {
            return false; // Return false if parsing fails
        }
    }

    private void logUserTime(int employeeId, String startTime, String endTime) {
        if (!isEndTimeAfterStartTime(startTime, endTime)) {
            JOptionPane.showMessageDialog(this, "End time must be after start time.");
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();

            // Prepare SQL statement for insertion
            String query = "INSERT INTO employee_off_time (employee_id, start_time, end_time) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query);

            // Set parameters for the query
            statement.setInt(1, employeeId);
            statement.setString(2, startTime);
            statement.setString(3, endTime);

            // Execute the insertion query
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Data added succesfully!");

                populateUserTable();
            } else {
                JOptionPane.showMessageDialog(this, "Error in adding data!");
            }

        } catch (SQLException | HeadlessException se) {
            JOptionPane.showMessageDialog(null, se.getMessage());
        } finally {
            try {
                // Close resources
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    private void showDeletePopup() {
        // Create a confirmation message
        String message = "Are you sure you want to delete the selected time?";

        // Create custom options for the JOptionPane
        Object[] options = {"Confirm", "Close"};

        // Show the confirmation dialog
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[0]);
        JDialog dialog = pane.createDialog("Confirmation");
        dialog.setVisible(true);

        // Handle user choice
        Object choice = pane.getValue();

        // Handle user choice
        if (choice.equals(options[0])) {
            // User clicked "Confirm"
            deleteUserTime();
        } else if (choice.equals(options[1])) {
            // User clicked "Close" or closed the dialog
            dialog.dispose();
        }
    }

    private void deleteUserTime() {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();

            // Prepare SQL statement for deletion
            String query = "DELETE FROM employee_off_time WHERE off_time_id = ?";
            statement = connection.prepareStatement(query);

            // Set parameter for the query
            statement.setInt(1, selectedEmployeeId);

            // Execute the insertion query
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Data deleted succesfully!");

                populateUserTable();
            } else {
                JOptionPane.showMessageDialog(this, "Error in deleting data!");
            }

        } catch (SQLException | HeadlessException se) {
            JOptionPane.showMessageDialog(null, se.getMessage());
        } finally {
            try {
                // Close resources
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
            }
        }
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
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/employees", "root", "")) {
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

        timeOff = new javax.swing.JLabel();
        userAccount = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        addTime = new javax.swing.JButton();
        removeTime = new javax.swing.JButton();
        logoutButton = new javax.swing.JButton();
        calculate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        endHr = new javax.swing.JSpinner();
        endMin = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        startHr = new javax.swing.JSpinner();
        startMin = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        endDate = new javax.swing.JTextField();
        startDate = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        timeOff.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        timeOff.setText("My Off Time Management");
        getContentPane().add(timeOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 17, 240, -1));

        userAccount.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        userAccount.setText("User");
        getContentPane().add(userAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 20, 44, -1));

        userTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(userTable);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 540, 398));

        addTime.setBackground(new java.awt.Color(0, 0, 204));
        addTime.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addTime.setForeground(new java.awt.Color(255, 255, 255));
        addTime.setText("Add Time");
        addTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTimeActionPerformed(evt);
            }
        });
        getContentPane().add(addTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 528, -1, 30));

        removeTime.setBackground(new java.awt.Color(0, 0, 204));
        removeTime.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        removeTime.setForeground(new java.awt.Color(255, 255, 255));
        removeTime.setText("Remove Time");
        removeTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTimeActionPerformed(evt);
            }
        });
        getContentPane().add(removeTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 528, -1, 30));

        logoutButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        logoutButton.setText("Logout");
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });
        getContentPane().add(logoutButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(706, 528, -1, 30));

        calculate.setBackground(new java.awt.Color(0, 0, 204));
        calculate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        calculate.setForeground(new java.awt.Color(255, 255, 255));
        calculate.setText("Calculate");
        calculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateActionPerformed(evt);
            }
        });
        getContentPane().add(calculate, new org.netbeans.lib.awtextra.AbsoluteConstraints(506, 528, -1, 30));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Pick time you were off:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 60, 210, 30));

        endHr.setPreferredSize(new java.awt.Dimension(64, 64));
        getContentPane().add(endHr, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 260, -1, 30));

        endMin.setPreferredSize(new java.awt.Dimension(64, 64));
        getContentPane().add(endMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 260, -1, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 51, 51));
        jLabel3.setText("Enter date format (dd/mm/yyyy)");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 100, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("End Date and Time:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 260, -1, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Start Date and Time:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 150, -1, -1));

        startHr.setPreferredSize(new java.awt.Dimension(64, 64));
        getContentPane().add(startHr, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 140, 70, 30));

        startMin.setPreferredSize(new java.awt.Dimension(64, 64));
        getContentPane().add(startMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 140, -1, 30));
        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 570, 770, 50));
        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 70, 100, 570));

        endDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endDateActionPerformed(evt);
            }
        });
        getContentPane().add(endDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 250, 180, 50));

        startDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startDateActionPerformed(evt);
            }
        });
        getContentPane().add(startDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 130, 180, 50));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 51, 51));
        jLabel6.setText("* Kindly note time is in 24 hrs");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 90, 200, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutButtonActionPerformed

        dispose();
        new loginPage().setVisible(true);
    }//GEN-LAST:event_logoutButtonActionPerformed

    private void addTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTimeActionPerformed
        String starts = startDate.getText().trim();
        String ends = endDate.getText().trim();

        try {
            if (starts.isEmpty() || ends.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select valid date values");
            } else {
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date startDate = inputFormat.parse(starts);
                Date endDate = inputFormat.parse(ends);

                SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MMM dd yyyy");
                String formattedStartDate = outputFormat.format(startDate);
                String formattedEndDate = outputFormat.format(endDate);

                // Get the selected start and end hours and minutes
                int startHour = (int) startHr.getValue();
                int startMinute = (int) startMin.getValue();
                int endHour = (int) endHr.getValue();
                int endMinute = (int) endMin.getValue();

                // Validate form inputs
                if (startHour < 0 || startHour > 23 || startMinute < 0 || startMinute > 59 || endHour < 0 || endHour > 23 || endMinute < 0 || endMinute > 59) {
                    JOptionPane.showMessageDialog(this, "Please select valid date and time values");
                } else {
                    // Insert data into the database or show confirmation popup
                    showAddPopup(userId, formattedStartDate + " " + startHour + ":" + startMinute,
                            formattedEndDate + " " + endHour + ":" + endMinute);
                }
            }

        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Error processing date and time values: " + ex.getMessage());
        } catch (ParseException ex) {
            Logger.getLogger(userPage.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_addTimeActionPerformed

    private void removeTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTimeActionPerformed
        // Check if an employee is selected
        if (selectedEmployeeId != 0) {
            // Perform deletion operation
            showDeletePopup();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a time entry to delete.");
        }
    }//GEN-LAST:event_removeTimeActionPerformed

    private void calculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateActionPerformed
        try {
            // Retrieve off time entries from the database
            List<OffTimeEntry> offTimeEntries = getOffTimeEntriesFromDatabase();

            // Example hourly rate
            double hourlyRate = 5.0;

            // Calculate total payment
            double totalPayment = calculateTotalPayment(hourlyRate, offTimeEntries);

            JOptionPane.showMessageDialog(this, "Based on your logged time off, your total payment is: $" + totalPayment);
        } catch (SQLException ex) {
            Logger.getLogger(userPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_calculateActionPerformed

    private void endDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_endDateActionPerformed

    private void startDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startDateActionPerformed

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
            java.util.logging.Logger.getLogger(userPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(userPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(userPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(userPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new userPage(1).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTime;
    private javax.swing.JButton calculate;
    private javax.swing.JTextField endDate;
    private javax.swing.JSpinner endHr;
    private javax.swing.JSpinner endMin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton logoutButton;
    private javax.swing.JButton removeTime;
    private javax.swing.JTextField startDate;
    private javax.swing.JSpinner startHr;
    private javax.swing.JSpinner startMin;
    private javax.swing.JLabel timeOff;
    private javax.swing.JLabel userAccount;
    private javax.swing.JTable userTable;
    // End of variables declaration//GEN-END:variables
}
