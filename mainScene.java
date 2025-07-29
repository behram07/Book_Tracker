package com.paydex.book_tracker;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class mainScene extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Stage primaryStage;
    private Scene homeScene;

    private final String csvPath = "/Users/paydex/Desktop/books.csv";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        File file = new File(csvPath);
        System.out.println("CSV exist? " + file.exists());

        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 30; -fx-alignment: center;");

        Button btnBib = new Button("Library");
        Button btnAdd = new Button("Add Book");
        btnBib.setPrefWidth(150);
        btnAdd.setPrefWidth(150);

        root.getChildren().addAll(btnBib, btnAdd);
        homeScene = new Scene(root, 300, 400);

        btnBib.setOnAction(e ->  openLibraryPage());
        btnAdd.setOnAction(e -> openAddBookPage());

        primaryStage.setResizable(false);
        primaryStage.setTitle("My Library");
        primaryStage.setScene(homeScene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void openLibraryPage(){
        BorderPane root = new BorderPane();
        List<Book> books = CsvHelper.loadBooks(csvPath);
        ObservableList<Book> bookList = FXCollections.observableArrayList(books);

        TableView<Book> table = new TableView<>();
        table.setItems(bookList);

        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<Book, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTitle.setPrefWidth(130);

        TableColumn<Book, String> colAuthor = new TableColumn<>("Author");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colAuthor.setPrefWidth(130);


        table.getColumns().addAll(colTitle, colAuthor);

        table.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    Book clickedBook = row.getItem();
                    openBookDetailDialog(clickedBook, bookList, table);
                }
            });
            return row;
        });

        Button btnBack = new Button("Back");
        Button btnDelete = new Button("Delete");

        btnBack.setOnAction(e -> {
            primaryStage.setScene(homeScene);
            primaryStage.centerOnScreen();
        });

        btnDelete.setOnAction(e -> {
            Book selectedBook = table.getSelectionModel().getSelectedItem();
            if(selectedBook != null){
                bookList.remove(selectedBook);
                CsvHelper.saveBooks(csvPath, bookList);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("You must select a book from the list to delete it!");
                alert.showAndWait();
            }
        });

        HBox bottomButtons = new HBox(10, btnBack, btnDelete);
        bottomButtons.setStyle("-fx-padding: 10; -fx-alignment: center;");

        root.setCenter(table);
        root.setBottom(bottomButtons);

        HBox topBar = new HBox();
        topBar.setStyle("-fx-padding: 10; -fx-alignment: center-left;");
        topBar.getChildren().add(btnBack);

        root.setTop(topBar);
        root.setCenter(table);

        Scene libraryScene = new Scene(root, 600, 500);
        primaryStage.setScene(libraryScene);
        primaryStage.centerOnScreen();

    }
    private void openAddBookPage(){
        VBox root = new VBox();
        root.setStyle("-fx-padding: 30; -fx-alignment: center;");

        Label lblTitle = new Label("Title");
        TextField textTitle = new TextField();

        Label lblAuthor = new Label("Author");
        TextField textAuthor = new TextField();

        Label lblTotalPage = new Label("Total Page");
        TextField textTotalPage = new TextField();

        Label lblPagesRead = new Label("Current Read");
        TextField textPagesRead = new TextField();

        Button btnSave = new Button("Save");
        Button btnBack = new Button("Back");

        HBox buttonBar = new HBox(10);
        buttonBar.setStyle("-fx-padding: 10; -fx-alignment: center;");
        buttonBar.getChildren().addAll(btnSave, btnBack);

        btnSave.setOnAction(e -> {
            try {
                String title = CapatilizedWord.capitalizeWords(textTitle.getText());
                String author = CapatilizedWord.capitalizeWords(textAuthor.getText());
                int totalPages = Integer.parseInt(textTotalPage.getText());
                int pagesRead = Integer.parseInt(textPagesRead.getText());

                // Mevcut kitapları yükle
                List<Book> books = CsvHelper.loadBooks(csvPath);
                System.out.println("Number of books read from CSV: " + books.size());

                // Yeni kitabı ekle
                books.add(new Book(title, author, totalPages, pagesRead));
                System.out.println("Total number of new books: " + books.size());

                // Kaydet
                CsvHelper.saveBooks(csvPath, books);

                System.out.println("Book saved: " + title);
                primaryStage.setScene(homeScene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        btnBack.setOnAction(e -> {
            primaryStage.setScene(homeScene);
            primaryStage.centerOnScreen();
        });

        root.getChildren().addAll(lblTitle, textTitle, lblAuthor, textAuthor, lblTotalPage, textTotalPage,
                lblPagesRead, textPagesRead, buttonBar);

        Scene addBookScene = new Scene(root, 300, 400);
        primaryStage.setScene(addBookScene);
        primaryStage.centerOnScreen();

    }
    private void openBookDetailDialog(Book book, ObservableList<Book> bookList, TableView<Book> table){
        Stage dialog = new Stage();
        dialog.setTitle("Book Details");

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label lblTitle = new Label("Title: " + book.getTitle());
        Label lblAuthor = new Label("Author: " + book.getAuthor());
        Label lblTotalPages = new Label("Total Pages: " + book.getTotalPage());
        Label lblPagesRead = new Label("Pages Read: " + book.getPagesRead());

        Label lblCurrentPage = new Label("Update Current Page");
        TextField txtCurrentPage = new TextField();
        txtCurrentPage.setPromptText("Page Number");
        Button btnUpdate = new Button("Update");

        double percent = (book.getTotalPage() > 0) ? (book.getPagesRead() * 100.0 / book.getTotalPage()) : 0;
        Label lblPercent;
        if(book.getPagesRead() >= book.getTotalPage() && book.getTotalPage() > 0){
            lblPercent = new Label("COMPLETED!");
            lblPercent.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

            txtCurrentPage.setDisable(true);
            btnUpdate.setDisable(true);
        } else {
            lblPercent = new Label(String.format("Progress: %.1f%%", percent));
        }

        btnUpdate.setOnAction(e -> {
            try{
                int newPage = Integer.parseInt(txtCurrentPage.getText());
                if(newPage >= 0 && newPage <= book.getTotalPage()){
                    book.setPagesRead(newPage);
                    CsvHelper.saveBooks(csvPath, bookList);
                    table.refresh();
                    dialog.close();
                } else {
                    showAlert("Warning!", "Page number must be between 0 and " + book.getTotalPage());
                }

                } catch (NumberFormatException ex){
                showAlert("Error", "Please enter a valid number");
            }
        });
        root.getChildren().addAll(lblTitle, lblAuthor, lblTotalPages, lblPagesRead, lblPercent, lblCurrentPage,
                txtCurrentPage, btnUpdate);

        Scene scene = new Scene(root, 300, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showAlert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
