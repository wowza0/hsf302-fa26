package com.fu.ch08.t5;

import com.fu.ch08.common.AlertUtil;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.web.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class BrowserMediaApp extends Application {

    private static final String HOME = "https://openjfx.io";
    private MediaPlayer player;
    private final MediaView mediaView = new MediaView();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(new Tab("🌐 Trình duyệt", buildBrowser(stage)), new Tab("🎬 Media Player", buildPlayer(stage)));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        stage.setScene(new Scene(tabs, 1100, 720));
        stage.show();
    }

    // ================= BROWSER =================
    private Node buildBrowser(Stage stage) {
        WebView web = new WebView();
        WebEngine engine = web.getEngine();
        WebHistory history = engine.getHistory();

        Button btnBack = new Button("◀"), btnFwd = new Button("▶"), btnReload = new Button("⟳"), btnHome = new Button("🏠");
        TextField tfUrl = new TextField(HOME);
        HBox.setHgrow(tfUrl, Priority.ALWAYS);
        Button btnLocal = new Button("Trang HTML nội bộ"), btnCount = new Button("Đếm thẻ <a>");
        ProgressBar pb = new ProgressBar(); pb.setMaxWidth(Double.MAX_VALUE);

        // TODO 1: Điều hướng
        tfUrl.setOnAction(e -> {
            String url = normalize(tfUrl.getText());
            if (url == null) { AlertUtil.warn("URL không hợp lệ"); return; }
            engine.load(url);
        });
        btnBack.setOnAction(e -> history.go(-1));
        btnFwd.setOnAction(e -> history.go(1));
        btnReload.setOnAction(e -> engine.reload());
        btnHome.setOnAction(e -> engine.load(HOME));

        // TODO 2: Binding trạng thái
        btnBack.disableProperty().bind(history.currentIndexProperty().lessThanOrEqualTo(0));
        btnFwd.disableProperty().bind(Bindings.createBooleanBinding(
                () -> history.getCurrentIndex() >= history.getEntries().size() - 1,
                history.currentIndexProperty(), history.getEntries()));
        pb.progressProperty().bind(engine.getLoadWorker().progressProperty());
        pb.visibleProperty().bind(engine.getLoadWorker().runningProperty());
        engine.locationProperty().addListener((o, a, loc) -> tfUrl.setText(loc));
        stage.titleProperty().bind(Bindings.createStringBinding(
                () -> "T5 - " + (engine.getTitle() == null ? "Mini Browser" : engine.getTitle()), engine.titleProperty()));

        // TODO 3: Lỗi tải trang
        engine.getLoadWorker().stateProperty().addListener((o, a, st) -> {
            if (st == Worker.State.FAILED) {
                engine.loadContent("<h2 style='color:#c00'>Không tải được trang</h2><p>"
                        + engine.getLoadWorker().getException() + "</p>");
            }
        });

        // TODO 4: alert() trong JavaScript → mặc định KHÔNG hiển thị gì, phải tự xử lý
        engine.setOnAlert(ev -> AlertUtil.info("[JavaScript] " + ev.getData()));

        // TODO 5: Load HTML string + gọi JS
        btnLocal.setOnAction(e -> engine.loadContent("""
                <html><body style="font-family:Segoe UI">
                  <h1>Xin chào từ HTML nội bộ</h1>
                  <a href="https://openjfx.io">OpenJFX</a> | <a href="https://gluonhq.com">Gluon</a>
                  <p><button onclick="alert('Hi from JavaScript!')">Gọi alert()</button></p>
                </body></html>"""));
        btnCount.setOnAction(e -> {
            if (engine.getLoadWorker().getState() != Worker.State.SUCCEEDED) { AlertUtil.warn("Trang chưa tải xong"); return; }
            Object n = engine.executeScript("document.getElementsByTagName('a').length");   // trả về Integer
            AlertUtil.info("Trang có " + n + " liên kết");
        });

        engine.load(HOME);
        HBox bar = new HBox(6, btnBack, btnFwd, btnReload, btnHome, tfUrl, btnLocal, btnCount);
        bar.setPadding(new Insets(6));
        VBox box = new VBox(bar, pb, web);
        VBox.setVgrow(web, Priority.ALWAYS);
        return box;
    }

    /** Thêm https:// nếu thiếu; null nếu không hợp lệ */
    static String normalize(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String url = raw.trim();
        if (!url.matches("(?i)^(https?|file)://.*")) url = "https://" + url;
        try {
            URI uri = new URI(url);
            return uri.getHost() == null && !url.startsWith("file") ? null : uri.toString();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    // ================= MEDIA PLAYER =================
    private Node buildPlayer(Stage stage) {
        Button btnOpen = new Button("📂 Mở file"), btnPlay = new Button("▶ Play"), btnStop = new Button("⏹ Stop");
        Slider slTime = new Slider(0, 1, 0), slVolume = new Slider(0, 1, 0.7);
        Label lblTime = new Label("00:00 / 00:00");
        CheckBox chkLoop = new CheckBox("Lặp lại");
        HBox.setHgrow(slTime, Priority.ALWAYS);
        HBox controls = new HBox(8, btnPlay, btnStop, slTime, lblTime, new Label("🔊"), slVolume, chkLoop);
        controls.setDisable(true);

        StackPane screen = new StackPane(mediaView, new Label("Chưa mở file"));
        screen.setStyle("-fx-background-color: black;");
        mediaView.fitWidthProperty().bind(screen.widthProperty());
        mediaView.fitHeightProperty().bind(screen.heightProperty());
        mediaView.setPreserveRatio(true);

        // TODO 6: Mở file
        btnOpen.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Media", "*.mp4", "*.mp3", "*.wav", "*.m4a"));
            File f = fc.showOpenDialog(stage);
            if (f == null) return;
            if (player != null) {                                  // giải phóng player cũ
                player.volumeProperty().unbindBidirectional(slVolume.valueProperty());
                player.dispose();
            }

            final Media media;                                     // khai báo NGOÀI try để các lambda bên dưới dùng được
            try {
                media = new Media(f.toURI().toString());
                player = new MediaPlayer(media);
            } catch (MediaException ex) {
                AlertUtil.error("Không hỗ trợ định dạng: " + ex.getMessage());
                return;
            }
            mediaView.setMediaPlayer(player);
            screen.getChildren().setAll(mediaView);
            controls.setDisable(false);

            // TODO 7: Gắn sự kiện cho player mới
            player.setOnError(() -> AlertUtil.error("Lỗi phát: " + player.getError().getMessage()));
            player.setOnReady(() -> {
                slTime.setMax(media.getDuration().toSeconds());
                btnOpen.setText("📂 " + f.getName());   // không setTitle: titleProperty đang bind ở tab Browser
            });
            player.currentTimeProperty().addListener((o, a, t) -> {
                if (!slTime.isValueChanging()) slTime.setValue(t.toSeconds());   // không giật khi đang kéo
                lblTime.setText(fmt(t) + " / " + fmt(media.getDuration()));
            });
            player.volumeProperty().bindBidirectional(slVolume.valueProperty());  // cùng kiểu DoubleProperty
            player.cycleCountProperty().bind(Bindings.when(chkLoop.selectedProperty())
                    .then(MediaPlayer.INDEFINITE).otherwise(1));
            player.statusProperty().addListener((o, a, st) ->
                    btnPlay.setText(st == MediaPlayer.Status.PLAYING ? "⏸ Pause" : "▶ Play"));
            player.setOnEndOfMedia(() -> { if (!chkLoop.isSelected()) { player.stop(); } });
            player.play();
        });

        // TODO 8: Điều khiển
        btnPlay.setOnAction(e -> {
            if (player == null) return;
            if (player.getStatus() == MediaPlayer.Status.PLAYING) player.pause(); else player.play();
        });
        btnStop.setOnAction(e -> { if (player != null) player.stop(); });
        slTime.valueChangingProperty().addListener((o, wasChanging, changing) -> {
            if (!changing && player != null) player.seek(Duration.seconds(slTime.getValue()));   // thả chuột → tua
        });
        slTime.setOnMouseClicked(e -> { if (player != null) player.seek(Duration.seconds(slTime.getValue())); });

        VBox box = new VBox(8, btnOpen, screen, controls);
        box.setPadding(new Insets(8));
        VBox.setVgrow(screen, Priority.ALWAYS);
        return box;
    }

    private static String fmt(Duration d) {
        if (d == null || d.isUnknown()) return "--:--";
        int s = (int) d.toSeconds();
        return "%02d:%02d".formatted(s / 60, s % 60);
    }

    // TODO 9: Giải phóng tài nguyên
    @Override
    public void stop() {
        if (player != null) player.dispose();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
