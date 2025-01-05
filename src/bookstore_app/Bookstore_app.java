package bookstore_app;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.io.*;
import java.util.*;
import javafx.collections.FXCollections;

public class Bookstore_app extends Application {

    //Book Objects
    public static final List<Book> books = new ArrayList<>();
    
    //Customer Objects
    public static final List<Customer> customers = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        loadBooks();
        loadCustomers();

        primaryStage.setTitle("Bookstore App");
        primaryStage.setScene(createLoginScene(primaryStage));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            saveBooks();
            saveCustomers();
        });
    }

    //Function for Formatting all Buttons
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-family: 'MS UI Gothic';");
        return button;
    }
    
    //Function for Formatting Pages
    private BorderPane createBaseLayout() {
        BorderPane layout = new BorderPane();

        HBox banner = new HBox();
        banner.setStyle("-fx-background-color: black; -fx-padding: 10;");

        Label bookstoreLabel = new Label("Bookstore");
        bookstoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-style: italic; -fx-font-weight: bold; -fx-font-family: 'Palatino Linotype';");

        Label dotJLabel = new Label(".java");
        dotJLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-style: normal; -fx-font-family: 'Monospaced';");

        banner.getChildren().addAll(bookstoreLabel, dotJLabel);

        layout.setTop(banner);
        layout.setStyle("-fx-background-color: white; -fx-font-family: 'MS UI Gothic';");
        return layout;
    }

    //Login Page
    private Scene createLoginScene(Stage stage) {
        BorderPane layout = createBaseLayout();

        VBox loginLayout = new VBox(10);
        loginLayout.setStyle("-fx-padding: 20;");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = createStyledButton("Login");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.equals("admin") && password.equals("admin")) {
                stage.setScene(createOwnerScene(stage));
            } else {
                for (Customer customer : customers) {
                    if (customer.getUsername().equals(username) && customer.getPassword().equals(password)) {
                        stage.setScene(createCustomerScene(stage, customer));
                        return;
                    }
                }
                showAlert("Invalid credentials");
            }
        });

        loginLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton);
        layout.setCenter(loginLayout);
        return new Scene(layout, 400, 300);
    }

    //Admin Page
    private Scene createOwnerScene(Stage stage) {
        BorderPane layout = createBaseLayout();

        VBox ownerLayout = new VBox(20); 
        ownerLayout.setStyle("-fx-padding: 20; -fx-alignment: center;"); 

        Label welcomeLabel = new Label("Welcome ");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-style: italic; -fx-font-family: 'Palatino Linotype';");

        Label adminLabel = new Label("Admin");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-style: italic; -fx-font-family: 'Palatino Linotype';");

        Label restMessageLabel = new Label(". Manage your bookstore below.");
        restMessageLabel.setStyle("-fx-font-size: 16px; -fx-font-style: italic; -fx-font-family: 'Palatino Linotype';");

        HBox welcomeBox = new HBox(welcomeLabel, adminLabel, restMessageLabel);
        welcomeBox.setStyle("-fx-alignment: center;"); 

        Button booksButton = createStyledButton("Books");
        Button customersButton = createStyledButton("Customers");
        Button logoutButton = createStyledButton("Logout");

        booksButton.setOnAction(e -> stage.setScene(createBooksScene(stage)));
        customersButton.setOnAction(e -> stage.setScene(createCustomersScene(stage)));
        logoutButton.setOnAction(e -> stage.setScene(createLoginScene(stage)));

        ownerLayout.getChildren().addAll(welcomeBox, booksButton, customersButton, logoutButton);
        layout.setCenter(ownerLayout);

        return new Scene(layout, 400, 300);
    }
   
    //Admin Book Inventory Page
    private Scene createBooksScene(Stage stage) {
        BorderPane layout = createBaseLayout();

        VBox booksLayout = new VBox(10);
        booksLayout.setStyle("-fx-padding: 20;");

        TableView<Book> booksTable = new TableView<>(FXCollections.observableArrayList(books));
        booksTable.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: black;");

        TableColumn<Book, String> nameColumn = new TableColumn<>("Book Title");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Book, Double> priceColumn = new TableColumn<>("Book Price");
        priceColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPrice()));

        booksTable.getColumns().addAll(nameColumn, priceColumn);

        TextField nameField = new TextField();
        nameField.setPromptText("Title");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        Button addButton = createStyledButton("Add");
        addButton.setOnAction(e -> {
            String name = nameField.getText();
            try {
                double price = Double.parseDouble(priceField.getText());
                Book newBook = new Book(name, price);
                books.add(newBook);
                booksTable.getItems().add(newBook);
            } catch (NumberFormatException ex) {
                showAlert("Invalid price");
            }
        });
        
        Button deleteButton = createStyledButton("Delete");
        deleteButton.setOnAction(e -> {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                books.remove(selectedBook);
                booksTable.getItems().remove(selectedBook);
            } else {
                showAlert("No book selected to delete.");
            }
        });

        Button backButton = createStyledButton("Back");
        backButton.setOnAction(e -> stage.setScene(createOwnerScene(stage)));

        HBox buttonBox = new HBox(10, addButton, deleteButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        booksLayout.getChildren().addAll(booksTable, nameField, priceField, buttonBox);
        layout.setCenter(booksLayout);

        return new Scene(layout, 600, 400);
    }

    //Admin Customer Accounts Page
    private Scene createCustomersScene(Stage stage) {
        BorderPane layout = createBaseLayout();

        VBox customersLayout = new VBox(10);
        customersLayout.setStyle("-fx-padding: 20;");

        TableView<Customer> customersTable = new TableView<>(FXCollections.observableArrayList(customers));
        customersTable.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: black;");

        TableColumn<Customer, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        
        TableColumn<Customer, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPassword()));
        
        TableColumn<Customer, Integer> pointsColumn = new TableColumn<>("Points");
        pointsColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPoints()));
        
        TableColumn<Customer, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        customersTable.getColumns().addAll(usernameColumn, passwordColumn, pointsColumn, statusColumn);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        Button addButton = createStyledButton("Add");

        addButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            Customer newCustomer = new Customer(username, password, 0);
            customers.add(newCustomer);
            customersTable.getItems().add(newCustomer);
        });

        Button deleteButton = createStyledButton("Delete");
        deleteButton.setOnAction(e -> {
            Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                customers.remove(selectedCustomer);
                customersTable.getItems().remove(selectedCustomer);
            } else {
                showAlert("No customer selected to delete.");
            }
        });

        Button backButton = createStyledButton("Back");
        backButton.setOnAction(e -> stage.setScene(createOwnerScene(stage)));

        HBox buttonBox = new HBox(10, addButton, deleteButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        customersLayout.getChildren().addAll(customersTable, usernameField, passwordField, buttonBox);
        layout.setCenter(customersLayout);

        return new Scene(layout, 600, 400);
    }

    //Customer Books Selection Page
    private Scene createCustomerScene(Stage stage, Customer customer) {
        BorderPane layout = createBaseLayout();

        VBox customerLayout = new VBox(10);
        customerLayout.setStyle("-fx-padding: 20;");

        Label welcomeLabel = new Label("Welcome ");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-style: italic; -fx-font-family: 'Palatino Linotype';");

        Label usernameLabel = new Label(customer.getUsername());
        usernameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Palatino Linotype';");

        Label pointsLabel = new Label("Points: ");
        pointsLabel.setStyle("-fx-font-size: 16px; -fx-font-style: italic; -fx-font-family: 'Palatino Linotype';");

        Label pointsValueLabel = new Label(String.valueOf(customer.getPoints()));
        pointsValueLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Palatino Linotype';");

        Label statusLabel = new Label("Status: ");
        statusLabel.setStyle("-fx-font-size: 16px; -fx-font-style: italic; -fx-font-family: 'Palatino Linotype';");

        Label statusValueLabel = new Label(customer.getStatus());
        statusValueLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Palatino Linotype';");

        HBox headerBox = new HBox(10);
        headerBox.setStyle("-fx-padding: 10;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox welcomeBox = new HBox(welcomeLabel, usernameLabel);
        HBox pointsStatusBox = new HBox(pointsLabel, pointsValueLabel, new Label("  "), statusLabel, statusValueLabel);

        headerBox.getChildren().addAll(welcomeBox, spacer, pointsStatusBox);

        TableView<Book> booksTable = new TableView<>(FXCollections.observableArrayList(books));
        booksTable.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: black;");

        TableColumn<Book, String> nameColumn = new TableColumn<>("Book Title");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Book, Double> priceColumn = new TableColumn<>("Book Price");
        priceColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPrice()));

        TableColumn<Book, CheckBox> selectColumn = new TableColumn<>("Select");
        selectColumn.setCellValueFactory(data -> {
            Book book = data.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> book.setSelected(newValue));
            return new SimpleObjectProperty<>(checkBox);
        });

        booksTable.getColumns().addAll(nameColumn, priceColumn, selectColumn);

        // Buttons in a horizontal layout
        Button buyButton = createStyledButton("Buy");
        buyButton.setOnAction(e -> {
            Set<Book> selectedBooks = new HashSet<>();
            for (Book book : books) {
                if (book.isSelected()) {
                    selectedBooks.add(book);
                }
            }

            if (selectedBooks.isEmpty()) {
                showAlert("No books selected.");
            } else {
                double totalPrice = selectedBooks.stream().mapToDouble(Book::getPrice).sum();
                int points = (int) (totalPrice * 10);
                customer.setPoints(customer.getPoints() + points);
                stage.setScene(createReceiptScene(stage, totalPrice, points, customer, selectedBooks, 0));
            }
        });

        Button redeemButton = createStyledButton("Redeem Points and Buy");
        redeemButton.setOnAction(e -> {
            Set<Book> selectedBooks = new HashSet<>();
            for (Book book : books) {
                if (book.isSelected()) {
                    selectedBooks.add(book);
                }
            }

            if (selectedBooks.isEmpty()) {
                showAlert("No books selected.");
            } else if (customer.getPoints() <= 0) {
                showAlert("You have 0 points. Redeeming points is not possible.");
            } else {
                double totalPrice = selectedBooks.stream().mapToDouble(Book::getPrice).sum();
                double discount = Math.min(customer.getPoints() / 100.0, totalPrice);
                double finalPrice = totalPrice - discount;
                int pointsUsed = (int) (discount * 100);

                int earnedPoints = (int) (finalPrice * 10);
                customer.setPoints(customer.getPoints() - pointsUsed + earnedPoints);

                stage.setScene(createReceiptScene(stage, finalPrice, earnedPoints, customer, selectedBooks, discount));
            }
        });

        Button logoutButton = createStyledButton("Logout");
        logoutButton.setOnAction(e -> stage.setScene(createLoginScene(stage)));

        HBox buttonBox = new HBox(10, buyButton, redeemButton, logoutButton);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 20;");

        customerLayout.getChildren().addAll(headerBox, booksTable, buttonBox);
        layout.setCenter(customerLayout);

        return new Scene(layout, 600, 400);
    }

    //Customer Receipt Page
    private Scene createReceiptScene(Stage stage, double totalPrice, int points, Customer customer, Set<Book> purchasedBooks, double discount) {
    BorderPane layout = createBaseLayout();

    VBox receiptLayout = new VBox(10);
    receiptLayout.setStyle("-fx-padding: 20;");

    Label headerLabel = new Label("Receipt");
    headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Palatino Linotype';");

    Label purchasedBooksLabel = new Label("Books Purchased:");
    purchasedBooksLabel.setStyle("-fx-font-size: 14px; -fx-font-family: 'Palatino Linotype';");
    ListView<String> purchasedBooksList = new ListView<>();
    double subtotal = 0;
    for (Book book : purchasedBooks) {
        purchasedBooksList.getItems().add(book.getName() + " - $" + book.getPrice());
        subtotal += book.getPrice();
    }

    // Create rows with left and right alignment
    HBox subtotalBox = createRow("Subtotal: ", "$" + String.format("%.2f", subtotal));
    HBox discountBox = createRow("Points Discount: ", "-$" + String.format("%.2f", discount));
    HBox totalBox = createRow("Total: ", "$" + String.format("%.2f", totalPrice));
    HBox pointsBox = createRow("You have earned: ", points + " points");
    HBox statusBox = createRow("Your status: ", customer.getStatus());

    // Black Separator
    Separator separator = new Separator();
    separator.setStyle("-fx-background-color: black; -fx-border-width: 2;");

    // Thank You Message (Centered)
    Label thankYouLabel = new Label("Thank you for shopping with us!");
    thankYouLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-font-family: 'Palatino Linotype';");
    VBox thankYouBox = new VBox(thankYouLabel);
    thankYouBox.setAlignment(Pos.CENTER);

    // Back to Login Button (Centered)
    Button backButton = createStyledButton("Back to Login");
    backButton.setOnAction(e -> stage.setScene(createLoginScene(stage)));
    VBox buttonBox = new VBox(backButton);
    buttonBox.setAlignment(Pos.CENTER);

    // Add all components to the layout
    receiptLayout.getChildren().addAll(
        headerLabel,
        purchasedBooksLabel,
        purchasedBooksList,
        subtotalBox,
        discountBox,
        totalBox,
        pointsBox,
        statusBox,
        separator,
        thankYouBox,
        buttonBox
    );
    layout.setCenter(receiptLayout);

    return new Scene(layout, 600, 400);
}

    // Helper method to create rows with left-aligned labels and right-aligned values
    private HBox createRow(String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 14px; -fx-font-family: 'Palatino Linotype';");

        Label value = new Label(valueText);
        value.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Palatino Linotype';");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, label, spacer, value);
        return row;
    }

    //Admin Book Entry Input
    private void loadBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    try {
                        String name = parts[0].trim();
                        double price = Double.parseDouble(parts[1].trim());
                        books.add(new Book(name, price));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price format for line: " + line);
                    }
                } else {
                    System.out.println("Invalid book entry: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Books file not found, starting with empty list.");
        }
    }

    //Admin Customer Entry Input
    private void loadCustomers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    try {
                        String username = parts[0].trim();
                        String password = parts[1].trim();
                        int points = Integer.parseInt(parts[2].trim());
                        customers.add(new Customer(username, password, points));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid points format for line: " + line);
                    }
                } else {
                    System.out.println("Invalid customer entry: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Customers file not found, starting with empty list.");
        }
    }

    //Admin Books Entry in File
    private void saveBooks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("books.txt"))) {
            for (Book book : books) {
                writer.write(book.getName() + "," + book.getPrice() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving books.");
        }
    }

    //Admin Customer Entry in File
    private void saveCustomers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt"))) {
            for (Customer customer : customers) {
                writer.write(customer.getUsername() + "," + customer.getPassword() + "," + customer.getPoints() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving customers.");
        }
    }

    //Alert Message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}