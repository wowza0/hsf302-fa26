package com.fu.ch08.b5;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.util.function.BiConsumer;

public class LayoutController {
    @FXML private StackPane centerPane;
    @FXML private VBox sidebar;
    @FXML private Label lblDescription, lblStatus, lblSize;
    @FXML private CheckMenuItem miSidebar;
    @FXML private Accordion accordion;
    @FXML private TitledPane tpBasic;

    @FXML
    public void initialize() {
        // TODO 2: Toggle sidebar — managed=false để center chiếm luôn phần trống
        sidebar.visibleProperty().bind(miSidebar.selectedProperty());
        sidebar.managedProperty().bind(miSidebar.selectedProperty());

        // TODO 3: Status bar cập nhật khi resize
        lblSize.textProperty().bind(Bindings.format("Center: %.0f x %.0f",
                centerPane.widthProperty(), centerPane.heightProperty()));

        accordion.setExpandedPane(tpBasic);
        showFlow();
    }

    private void show(String name, Node content, String desc) {
        centerPane.getChildren().setAll(content);   // setAll: node cũ bị gỡ khỏi scene graph
        lblStatus.setText("Đang xem: " + name);
        lblDescription.setText(desc);
    }

    // TODO 4: FlowPane — tự xuống dòng; thử resize cửa sổ
    @FXML private void showFlow() {
        FlowPane fp = new FlowPane(8, 8);
        for (int i = 1; i <= 30; i++) fp.getChildren().add(box("Item " + i, 60 + (i % 4) * 20, 40));
        show("FlowPane", fp, "Xếp node theo hàng, hết chiều rộng thì xuống dòng. Kích thước node khác nhau.");
    }

    // TODO 5: TilePane — mọi ô có kích thước bằng nhau
    @FXML private void showTile() {
        TilePane tp = new TilePane(8, 8);
        tp.setPrefColumns(5);
        for (int i = 1; i <= 20; i++) tp.getChildren().add(box("Tile " + i, 60 + (i % 4) * 20, 40));
        show("TilePane", tp, "Giống FlowPane nhưng mọi ô bằng nhau (theo node lớn nhất).");
    }

    // TODO 6: GridPane — form 2 cột, cột 2 giãn theo chiều ngang, colspan/rowspan
    @FXML private void showGrid() {
        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.addRow(0, new Label("Họ tên"), new TextField());
        gp.addRow(1, new Label("Email"), new TextField());
        TextArea ta = new TextArea(); ta.setPrefRowCount(4);
        gp.add(new Label("Ghi chú"), 0, 2);
        gp.add(ta, 1, 2, 1, 2);                                    // rowspan = 2
        Button btn = new Button("Lưu"); btn.setMaxWidth(Double.MAX_VALUE);
        gp.add(btn, 0, 4, 2, 1);                                   // colspan = 2
        ColumnConstraints c1 = new ColumnConstraints(80), c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        gp.getColumnConstraints().addAll(c1, c2);
        gp.setGridLinesVisible(true);                              // debug: hiện đường lưới
        show("GridPane", gp, "Lưới hàng/cột; dùng ColumnConstraints để cột giãn. Bật gridLinesVisible để debug.");
    }

    // TODO 7: AnchorPane — neo theo cạnh
    @FXML private void showAnchor() {
        Button tl = new Button("Top-Left"), br = new Button("Bottom-Right");
        TextArea stretch = new TextArea("Neo cả 4 cạnh cách 50px → giãn theo cửa sổ");
        AnchorPane.setTopAnchor(tl, 10.0); AnchorPane.setLeftAnchor(tl, 10.0);
        AnchorPane.setBottomAnchor(br, 10.0); AnchorPane.setRightAnchor(br, 10.0);
        for (var side : List.<BiConsumer<Node, Double>>of(AnchorPane::setTopAnchor, AnchorPane::setBottomAnchor,
                AnchorPane::setLeftAnchor, AnchorPane::setRightAnchor)) side.accept(stretch, 50.0);
        show("AnchorPane", new AnchorPane(stretch, tl, br), "Neo node theo khoảng cách tới các cạnh.");
    }

    // TODO 8: SplitPane — kéo thanh chia
    @FXML private void showSplit() {
        ListView<String> list = new ListView<>(FXCollections.observableArrayList("Một", "Hai", "Ba"));
        Label detail = new Label("Chọn một mục");
        list.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> detail.setText("Đã chọn: " + b));
        SplitPane sp = new SplitPane(list, new StackPane(detail));
        sp.setDividerPositions(0.3);
        show("SplitPane", sp, "Chia vùng có thể kéo thay đổi kích thước (master-detail).");
    }

    // TODO 9: ScrollPane — nội dung lớn hơn vùng hiển thị
    @FXML private void showScroll() {
        VBox content = new VBox(5);
        for (int i = 1; i <= 100; i++) content.getChildren().add(new Label("Dòng " + i));
        ScrollPane sc = new ScrollPane(content);
        sc.setFitToWidth(true);
        show("ScrollPane", sc, "Bọc nội dung lớn, tự hiện thanh cuộn. fitToWidth để nội dung giãn ngang.");
    }

    @FXML private void handleExit() { Platform.exit(); }

    private static Label box(String text, double w, double h) {
        Label l = new Label(text);
        l.setPrefSize(w, h); l.setAlignment(Pos.CENTER);
        l.setStyle("-fx-background-color: hsb(%d, 40%%, 95%%); -fx-border-color: #888;".formatted((int) (w * 3) % 360));
        return l;
    }
}
