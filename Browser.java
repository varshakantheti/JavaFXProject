import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
//animation stuff
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
/**
* @author Varsha Kantheti
* @version 1.0
*/
public class Browser extends Application {
    private BorderPane pane1;
    private TextField urlBar;
    private Forum forumPage;
    private int motdVisitCount = 0;
    @Override
    public void start(Stage primaryStage) {
        pane1 = new BorderPane();
        forumPage = new Forum();
        // Top URL bar
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: pink");
        ImageView imageView = new ImageView(new Image("shell.gif"));
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        topBar.getChildren().add(imageView);
        urlBar = new TextField();
        urlBar.setPromptText("Enter URL (e.g., javadiscussion.com)");
        urlBar.setStyle("-fx-background-color: lightyellow");
        Button goButton = new Button("Go");
        goButton.setStyle("-fx-background-color: lightblue");
        // Anonymous inner class for Go button
        goButton.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                loadPage(urlBar.getText().trim());
            }
        });
        topBar.getChildren().add(urlBar);
        topBar.getChildren().add(goButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        pane1.setTop(topBar);
        Label homeText = new Label("Welcome. Type a URL in the URL bar to get started :)");
        StackPane homePane = new StackPane(homeText);
        pane1.setCenter(homePane);
        Scene scene = new Scene(pane1, 700, 500);
        primaryStage.setTitle("Internet Explorer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void loadPage(String url) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        if (url.equals("javadiscussion.com")) {
            Tab forumTab = new Tab("javadiscussion.com", forumPage.getForumUI());
            tabPane.getTabs().add(forumTab);
        } else if (url.equals("1331motd.com")) {
            Tab motdTab = new Tab("1331motd.com", generateMotdPane(motdVisitCount % 3));
            motdVisitCount++;
            tabPane.getTabs().add(motdTab);
        } else {
            showError("Invalid URL. Only javadiscussion.com and 1331motd.com are supported.");
            return;
        }
        pane1.setCenter(tabPane);
    }
    private VBox generateMotdPane(int index) {
        VBox motdBox = new VBox(15);
        motdBox.setPadding(new Insets(15));
        motdBox.setAlignment(Pos.CENTER);
        Text message;
        switch (index) {
        case 0:
            Rectangle rect = new Rectangle(100, 40, Color.SKYBLUE);
            Circle circle = new Circle(30, Color.CORAL);
            Ellipse ellipse = new Ellipse(50, 25);
            ellipse.setFill(Color.LIGHTGREEN);
            Polygon triangle = new Polygon(40, 50, 4, 40, 50, 20);
            triangle.setFill(Color.YELLOW);
            message = new Text("Message of the day 1: Java's name was derived from a type of coffee!");
            motdBox.getChildren().addAll(rect, circle, ellipse, triangle, message);                break;
        case 1:
            Rectangle banner = new Rectangle(120, 50, Color.DARKSEAGREEN);
            Line line = new Line(0, 0, 100, 0);
            line.setStroke(Color.FIREBRICK);
            line.setStrokeWidth(3);
            Polygon diamond = new Polygon(60, 30, 90, 60, 60, 90, 30, 60);
            diamond.setFill(Color.LIGHTPINK);
            message = new Text("Message of the day 2: Make sure to use good variable names!");
            motdBox.getChildren().addAll(banner, diamond, line, message);                break;
        case 2:
            Ellipse background = new Ellipse(60, 30);
            background.setFill(Color.PLUM);
            Polygon star = new Polygon(
                50, 0, 60, 35, 100, 35,
                68, 58, 80, 100, 50, 75,
                20, 100, 32, 58, 0, 35, 40, 35
            );
            star.setFill(Color.GOLD);
            message = new Text("Message of the day 3: JavaFX is super fun especially with animations!");
            motdBox.getChildren().addAll(background, star, message);
            // Heres the Animation to make the star move up and down
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> star.setTranslateY(-5)),
                new KeyFrame(Duration.seconds(1), e -> star.setTranslateY(5)),
                new KeyFrame(Duration.seconds(2), e -> star.setTranslateY(0))
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
            break;
        default:
            message = new Text("MOTD: Welcome to CS 1331!");
            motdBox.getChildren().add(message);
        }
        return motdBox;
    }
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid URL");
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @Override
    public void stop() {
        forumPage.savePosts();
    }
    private class Forum {
        private final String fileName = "post_history.txt";
        private ArrayList<Post> posts = new ArrayList<>();
        private VBox postList;
        Forum() {
            posts = loadPosts();
        }
        public VBox getForumUI() {
            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));
            Label header = new Label("Java Discussion Forum");
            header.setStyle("-fx-background-color: prink");
            header.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
            Button newPostBtn = new Button("New Post");
            newPostBtn.setStyle("-fx-background-color: lightblue");
            // Lambda expression
            newPostBtn.setOnAction(e -> showNewPostDialog());
            postList = new VBox(10);
            ScrollPane scroll = new ScrollPane(postList);
            scroll.setFitToWidth(true);
            scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            refreshPosts();
            layout.getChildren().addAll(header, newPostBtn, scroll);
            return layout;
        }
        private void refreshPosts() {
            postList.getChildren().clear();
            if (posts.isEmpty()) {
                postList.getChildren().add(new Text("Be the first to make a post!"));
            } else {
                for (int i = posts.size() - 1; i >= 0; i--) {
                    Post post = posts.get(i);
                    VBox postBox = createPostBox(post);
                    postList.getChildren().add(postBox);
                }
            }
        }
        private VBox createPostBox(Post post) {
            VBox box = new VBox(5);
            box.setStyle("-fx-border-color: black; -fx-padding: 10;");
            Text title = new Text("[" + post.category + "] " + post.author + "  #" + post.number);
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            Text body = new Text(post.body);
            body.setStyle("-fx-font-size: 12;");
            Button replyBtn = new Button("Reply");
            replyBtn.setOnAction(e -> {
                Stage dialog = new Stage();
                dialog.setTitle("Reply to Post");
                dialog.initModality(Modality.APPLICATION_MODAL);
                VBox dialogContent = new VBox(10);
                dialogContent.setPadding(new Insets(10));
                TextField replyName = new TextField();
                replyName.setPromptText("Your name");
                TextArea replyText = new TextArea();
                replyText.setPromptText("Your reply...");
                replyText.setWrapText(true);
                Button postReply = new Button("Submit Reply");
                postReply.setOnAction(ev -> {
                    String name = replyName.getText().trim();
                    String replyBody = replyText.getText().trim();
                    if (!replyBody.isEmpty()) {
                        if (name.isEmpty()) {
                            name = "Anonymous";
                        }
                        Post reply = new Post(name, post.replies.size() + 1, replyBody, "Reply");
                        post.replies.add(reply);
                        refreshPosts();
                        dialog.close();
                    }
                });
                dialogContent.getChildren().addAll(new Label("Name:"), replyName,
                        new Label("Reply:"), replyText, postReply);
                dialog.setScene(new Scene(dialogContent, 300, 250));
                dialog.showAndWait();
            });
            box.getChildren().addAll(title, body, replyBtn);
            for (Post reply : post.replies) {
                VBox replyBox = new VBox(3);
                replyBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5; -fx-border-color: gray;");
                replyBox.setPadding(new Insets(5, 5, 5, 20)); // indent
                Text replyText = new Text(reply.author + ": " + reply.body);
                replyText.setStyle("-fx-font-size: 11;");
                replyBox.getChildren().add(replyText);
                box.getChildren().add(replyBox);
            }
            return box;
        }
        private void showNewPostDialog() {
            Stage dialog = new Stage();
            dialog.setTitle("New Post");
            dialog.initModality(Modality.APPLICATION_MODAL);
            VBox content = new VBox(10);
            content.setPadding(new Insets(10));
            TextField nameField = new TextField();
            nameField.setPromptText("Name");
            TextArea bodyField = new TextArea();
            bodyField.setPromptText("Your post here...");
            bodyField.setWrapText(true);
            ComboBox<String> categoryBox = new ComboBox<>();
            categoryBox.getItems().addAll("Syntax", "OOP", "Debugging", "Other");
            categoryBox.setValue("Other");
            Button submit = new Button("Post");
            submit.setStyle("-fx-background-color: lightblue");
            submit.setOnAction(e -> {
                String name = nameField.getText().trim();
                String body = bodyField.getText().trim();
                String category = categoryBox.getValue();
                if (body.isEmpty()) {
                    showError("Post body cannot be empty.");
                } else {
                    if (name.isEmpty()) {
                        name = "Anonymous";
                    }
                    Post post = new Post(name, posts.size() + 1, body, category);
                    posts.add(post);
                    refreshPosts();
                    dialog.close();
                }
            });
            content.getChildren().addAll(new Label("Name:"), nameField,
                    new Label("Category:"), categoryBox,
                    new Label("Post:"), bodyField, submit);
            Scene dialogScene = new Scene(content, 300, 300);
            dialog.setScene(dialogScene);
            dialog.showAndWait();
        }
        public void savePosts() {
            try (PrintWriter out = new PrintWriter(new File(fileName))) {
                for (Post p : posts) {
                    out.println(p.toFileString());
                }
            } catch (IOException e) {
                System.err.println("Could not save posts.");
            }
        }
        private ArrayList<Post> loadPosts() {
            ArrayList<Post> list = new ArrayList<>();
            File file = new File(fileName);
            if (!file.exists()) {
                return list;
            }
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNextLine()) {
                    list.add(Post.fromFileString(sc.nextLine()));
                }
            } catch (IOException e) {
                System.err.println("Could not load posts.");
            }
            return list;
        }
    }
    //Post class to store post information.
    private static class Post {
        private String author;
        private int number;
        private String body;
        private String category;
        private ArrayList<Post> replies = new ArrayList<>();
        Post(String author, int number, String body, String category) {
            this.author = author;
            this.number = number;
            this.body = body;
            this.category = category;
        }
        public String toFileString() {
            return author + "\u001C" + number + "\u001C" + category + "\u001C" + body;
        }
        public static Post fromFileString(String line) {
            String[] parts = line.split("\u001C", 4);
            return new Post(parts[0], Integer.parseInt(parts[1]), parts[3], parts[2]);
        }
    }
    //for vscode
    /**
     * @param args main method arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}