import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

class Item implements Serializable {
    String id;
    String name;
    int quantity;
    double price;

    public Item(String id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public void displayItem(JTextArea textArea) {
        textArea.append(String.format("%-5s\t\t   | %-15s\t\t    | %-15d\t\t    | K%.2f%n", id, name, quantity, price));
    }
}

public class MainClass extends JFrame {
    private static final String FILE_NAME = "inventory.txt";
    private static List<Item> inventory = new ArrayList<>();
    private JTextArea textArea;

    public MainClass() {
        loadInventory();
        createUI();
    }

    private void createUI() {
        setTitle("INVENTORY MANAGEMENT SYSTEM");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Text area to display output
        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 7));

        String[] buttons = {
            "View Inventory", "Add New Item", "Update Item", 
            "Remove Item", "Search for Item", "View Reports", "Exit"
        };

        for (String button : buttons) {
            JButton btn = new JButton(button);
            btn.addActionListener(new ButtonClickListener());
            buttonPanel.add(btn);
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            textArea.setText(""); // Clear text area before displaying new content

            switch (command) {
                case "View Inventory":
                    viewInventory();
                    break;
                case "Add New Item":
                    addItem();
                    break;
                case "Update Item":
                    updateItem();
                    break;
                case "Remove Item":
                    removeItem();
                    break;
                case "Search for Item":
                    searchItem();
                    break;
                case "View Reports":
                    viewReports();
                    break;
                case "Exit":
                    saveInventory();
                    JOptionPane.showMessageDialog(MainClass.this, "Exiting The System...... Data saved!\n");
                    System.exit(0);
                    break;
                default:
                    textArea.append("Invalid choice! Please try again.\n");
                    break;
            }
        }
    }

    private void viewInventory() {
        if (inventory.isEmpty()) {
            textArea.append("Oops! Inventory is empty!\n");
            return;
        }

        textArea.append(" ID\t\t  | NAME\t\t | QUANTINTY\t\t   | PRICE\n");
        textArea.append("--------------------------------------------------------------------------------------------------------------------------------------------------\n");
        for (Item item : inventory) {
            item.displayItem(textArea);
        }
    }

    private void addItem() {
        String id = JOptionPane.showInputDialog(this, "Enter Item ID:");
        String name = JOptionPane.showInputDialog(this, "Enter Item Name:");
        int quantity = 0;
        double price = 0;

        try {
            quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Quantity In Kilograms(KG):"));
            price = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter Price Per Item In Kwacha(MWK):"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter numeric values for quantity and price.");
            return;
        }

        inventory.add(new Item(id, name, quantity, price));
        saveInventory();
        JOptionPane.showMessageDialog(this, "Item added successfully!");
    }

    private void updateItem() {
        String id = JOptionPane.showInputDialog(this, "Enter Item ID to update:");
        for (Item item : inventory) {
            if (item.id.equals(id)) {
                try {
                    int newQuantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new quantity:"));
                    double newPrice = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter new price:"));
                    item.quantity = newQuantity;
                    item.price = newPrice;
                    saveInventory();
                    JOptionPane.showMessageDialog(this, "Item updated successfully!");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid input! Please enter numeric values for quantity and price.");
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Sorry: Item not found!");
    }

    private void removeItem() {
        String id = JOptionPane.showInputDialog(this, "Enter Item ID to remove:");
        boolean removed = inventory.removeIf(item -> item.id.equals(id));
        if (removed) {
            saveInventory();
            JOptionPane.showMessageDialog(this, "Item removed successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Sorry: Item not found!");
        }
    }

    private void searchItem() {
        String input = JOptionPane.showInputDialog(this, "Enter Item Name or ID:");
        boolean found = false;

        for (Item item : inventory) {
            if (item.id.equals(input) || item.name.equalsIgnoreCase(input)) {
                textArea.append("\nItem Found!!\n");
                item.displayItem(textArea);
                found = true;
                break;
            }
        }

        if (!found) {
            textArea.append("Sorry: Item not found!\n");
        }
    }

    private void viewReports() {
        textArea.append("\nLOW STOCK ITEMS (Quantity < 5):\n");
        textArea.append("---------------------------------------\n");
        textArea.append("  ID    |   NAME          |   QUANTINTY  |   PRICE\n");
        textArea.append("---------------------------------------\n");
        for (Item item : inventory) {
            if (item.quantity < 5) {
                item.displayItem(textArea);
            }
        }
    }

    private static void saveInventory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving inventory: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadInventory() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            inventory = (List<Item>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            inventory = new ArrayList<>(); // Start with empty inventory if file is missing
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainClass().setVisible(true);
        });
    }
}