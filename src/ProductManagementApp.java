import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.json.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Vector;

public class ProductManagementApp extends JFrame {
    private JTextField nameField;
    private JCheckBox inStockCheckBox;
    private JRadioButton typeRadio1;
    private JRadioButton typeRadio2;
    private JComboBox<String> categoryComboBox;
    private JTextArea descriptionTextArea;
    private JComboBox<String> carComboBox;
    private JButton saveButton;
    private JButton cancelButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String JSON_FILE = "products.json";

    public ProductManagementApp() {
        nameField = new JTextField(20);
        inStockCheckBox = new JCheckBox("In Stock");
        typeRadio1 = new JRadioButton("Type 1");
        typeRadio2 = new JRadioButton("Type 2");
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(typeRadio1);
        typeGroup.add(typeRadio2);
        String[] categories = {"Category 1", "Category 2", "Category 3"};
        categoryComboBox = new JComboBox<>(categories);
        descriptionTextArea = new JTextArea(5, 20);
        String[] cars = {"Car 1", "Car 2", "Car 3"};
        carComboBox = new JComboBox<>(cars);
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        String[] columnNames = {"Name", "In Stock", "Type", "Category", "Description", "Car"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        try {
            loadData();
        } catch (JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data! " + e.getMessage());
        }

        add(new JLabel("Name:"));
        add(nameField);
        add(inStockCheckBox);
        add(new JLabel("Type:"));
        add(typeRadio1);
        add(typeRadio2);
        add(new JLabel("Category:"));
        add(categoryComboBox);
        add(new JLabel("Description:"));
        add(new JScrollPane(descriptionTextArea));
        add(new JLabel("Car:"));
        add(carComboBox);
        add(saveButton);
        add(cancelButton);
        add(new JLabel("Product List:"));
        add(tableScrollPane);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    saveData();
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(400, 600);
        setVisible(true);
    }

    private void saveData() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", nameField.getText());
        json.put("inStock", inStockCheckBox.isSelected());
        json.put("type", typeRadio1.isSelected() ? "Type 1" : "Type 2");
        json.put("category", categoryComboBox.getSelectedItem().toString());
        json.put("description", descriptionTextArea.getText());
        json.put("car", carComboBox.getSelectedItem().toString());

        tableModel.addRow(new Object[]{
                nameField.getText(),
                inStockCheckBox.isSelected(),
                (typeRadio1.isSelected() ? "Type 1" : "Type 2"),
                categoryComboBox.getSelectedItem().toString(),
                descriptionTextArea.getText(),
                carComboBox.getSelectedItem().toString()
        });

        JSONArray jsonArray;

        try {
            // Gestionarea NoSuchFileException și crearea fișierului dacă nu există
            String jsonString;
            try {
                jsonString = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
            } catch (NoSuchFileException ex) {
                Files.createFile(Paths.get(JSON_FILE));
                jsonString = "[]";
            }

            jsonArray = new JSONArray(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading JSON file!");
            return;
        }

        jsonArray.put(json);

        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
            jsonArray = new JSONArray(jsonString);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error processing JSON data!");
            return;
        }

        try (FileWriter file = new FileWriter(JSON_FILE)) {
            file.write(jsonArray.toString());
            file.flush();
            JOptionPane.showMessageDialog(this, "Product saved successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving product!");
        }
    }

    private void loadData() throws JSONException {
        File jsonFile = new File(JSON_FILE);
        if (jsonFile.exists()) {
            try {
                String jsonString = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
                JSONArray jsonArray = new JSONArray(jsonString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    Vector<String> row = new Vector<>();
                    row.add(jsonObj.optString("name", ""));
                    row.add(String.valueOf(jsonObj.optBoolean("inStock")));
                    row.add(jsonObj.optString("type", ""));
                    row.add(jsonObj.optString("category", ""));
                    row.add(jsonObj.optString("description", ""));
                    row.add(jsonObj.optString("car", ""));
                    tableModel.addRow(row);
                }
            } catch (IOException e) {
                throw new JSONException("Error loading data! " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ProductManagementApp();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

