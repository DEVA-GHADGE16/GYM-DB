import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class GymManagementGUI {
    private JFrame frame;
    private JTextField nameField, ageField, contactField, membershipField;
    private JTextArea outputArea;
    private JComboBox<String> genderBox;
    public GymManagementGUI() {
        frame = new JFrame("Gym Management System");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        JPanel panel;
        panel = new JPanel();
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(15);
        JLabel ageLabel = new JLabel("Age:");
        ageField = new JTextField(5);
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JLabel membershipLabel = new JLabel("Membership:");
        membershipField = new JTextField(10);
        JLabel contactLabel = new JLabel("Contact:");
        contactField = new JTextField(15);

        JButton addButton = new JButton("Add Member");
        JButton viewButton = new JButton("View Members");
        outputArea = new JTextArea(10, 40);
        panel = new JPanel();
        panel.add(new JLabel("Gender:"));
        panel = new JPanel();
        panel.add(genderBox);
        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(ageLabel);
        frame.add(ageField);
        frame.add(panel);
        frame.add(membershipLabel);
        frame.add(membershipField);
        frame.add(contactLabel);
        frame.add(contactField);
        frame.add(addButton);
        frame.add(viewButton);
        frame.add(new JScrollPane(outputArea));

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMember();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewMembers();
            }
        });

        frame.setVisible(true);
    }

    private void addMember() {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String gender = (String) genderBox.getSelectedItem();  // Get selected gender
        String membership = membershipField.getText();
        String contact = contactField.getText();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Members (Name, Age, Gender, MembershipType, Contact) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, gender);  // Store gender
            stmt.setString(4, membership);
            stmt.setString(5, contact);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Member Added Successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void viewMembers() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Members");
             ResultSet rs = stmt.executeQuery()) {

            // Convert ResultSet into Table Model
            JTable table = new JTable(buildTableModel(rs));
            JOptionPane.showMessageDialog(null, new JScrollPane(table), "Members List", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Column names
        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        // Data rows
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
    }

    public static void main(String[] args) {
        new GymManagementGUI();
    }
}
