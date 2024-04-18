package com.example.javamongodb;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Filters.*;

public class ProductForm extends JFrame {
    private static final String MONGO_URI = "mongodb://localhost:27017";
    private static final String DATABASE = "elkunDB";
    private static final String COLLECTION = "Products";
    private JTextField idField, nameField, priceField, descriptionField;

    public ProductForm() {
        setupUI();
    }

    private void setupUI() {
        setTitle("CRUD MongoDB + JAVA");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        initializeFields(panel);
        initializeButtons(panel);

        add(panel);
        setVisible(true);
    }

    private void initializeFields(JPanel panel) {
        panel.add(new JLabel("ID:"));
        idField = new JTextField();
        panel.add(idField);

        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Price:"));
        priceField = new JTextField();
        panel.add(priceField);

        panel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        panel.add(descriptionField);
    }

    private void initializeButtons(JPanel panel) {
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addProduct());
        panel.add(addButton);

        JButton readButton = new JButton("Read");
        readButton.addActionListener(e -> readProduct());
        panel.add(readButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateProduct());
        panel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteProduct());
        panel.add(deleteButton);
    }

    private void addProduct() {
        executeInMongoClient(mongoClient -> {
            MongoCollection<Document> collection = getCollection(mongoClient);
            Document document = new Document("name", nameField.getText())
                    .append("price", Double.parseDouble(priceField.getText()))
                    .append("description", descriptionField.getText());

            collection.insertOne(document);
            showMessage("Product added successfully!");
            clearFields();
        });
    }

    private void deleteProduct() {
        executeInMongoClient(mongoClient -> {
            MongoCollection<Document> collection = getCollection(mongoClient);
            ObjectId objectId = new ObjectId(idField.getText());
            Document document = collection.findOneAndDelete(eq("_id", objectId));

            showMessage("Product deleted successfully!");
            clearFields();
        });
    }

    private void readProduct() {
        executeInMongoClient(mongoClient -> {
            MongoCollection<Document> collection = getCollection(mongoClient);
            ObjectId objectId = new ObjectId(idField.getText());
            Document document = collection.find(eq("_id", objectId)).first();

            JOptionPane.showConfirmDialog(this, document);
            clearFields();
        });
    }

    private void updateProduct() {
        executeInMongoClient(mongoClient -> {
            MongoCollection<Document> collection = getCollection(mongoClient);
            ObjectId objectId = new ObjectId(idField.getText());
            Document update = new Document()
                    .append("name", nameField.getText())
                    .append("price", Double.parseDouble(priceField.getText()))
                    .append("description", descriptionField.getText());

            UpdateResult result = collection.updateOne(eq("_id", objectId), new Document("$set", update));
            showMessage("Product updated successfully!: " + result);
            clearFields();
        });
    }

    private void executeInMongoClient(Consumer<MongoClient> operation) {
        try (MongoClient mongoClient = MongoClients.create(MONGO_URI)) {
            operation.accept(mongoClient);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private MongoCollection<Document> getCollection(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(DATABASE);
        return database.getCollection(COLLECTION);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        descriptionField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProductForm::new);
    }
}
