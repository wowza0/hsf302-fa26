package com.fu.ch08.t6;

import com.fu.ch08.common.AlertUtil;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public class Viewer3DApp extends Application {
    private final Rotate rx = new Rotate(-20, Rotate.X_AXIS), ry = new Rotate(30, Rotate.Y_AXIS);
    private double lastX, lastY;

    @Override
    public void start(Stage stage) {
        // TODO 1: Kiểm tra hỗ trợ 3D
        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            AlertUtil.error("Máy không hỗ trợ 3D (kiểm tra driver card đồ họa)");
            Platform.exit(); return;
        }

        // TODO 2: Khối 3D + vật liệu
        Box box = new Box(100, 100, 100);          box.setTranslateX(-160);
        Sphere sphere = new Sphere(60);
        Cylinder cyl = new Cylinder(40, 130);      cyl.setTranslateX(160);
        Map<String, Shape3D> shapes = new LinkedHashMap<>();
        shapes.put("Hộp", box); shapes.put("Cầu", sphere); shapes.put("Trụ", cyl);
        Color[] colors = {Color.CRIMSON, Color.DODGERBLUE, Color.SEAGREEN};
        int i = 0;
        for (Shape3D s : shapes.values()) s.setMaterial(new PhongMaterial(colors[i++]));

        Group model = new Group(box, sphere, cyl);
        model.getTransforms().addAll(rx, ry);

        // TODO 3: Ánh sáng + camera + SubScene có depth buffer
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-300); light.setTranslateY(-300); light.setTranslateZ(-400);
        Group world = new Group(model, light, new AmbientLight(Color.gray(0.3)));
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1); camera.setFarClip(5000); camera.setTranslateZ(-700);
        SubScene sub = new SubScene(world, 800, 600, true, SceneAntialiasing.BALANCED);
        sub.setFill(Color.web("#202530"));
        sub.setCamera(camera);

        // TODO 4: Panel điều khiển
        ComboBox<String> cbShape = new ComboBox<>(FXCollections.observableArrayList(shapes.keySet()));
        cbShape.setValue("Hộp");
        ColorPicker cp = new ColorPicker(colors[0]);
        Slider slSize = new Slider(0.3, 2, 1);
        CheckBox chkWire = new CheckBox("Wireframe"), chkAuto = new CheckBox("Tự xoay");
        cbShape.valueProperty().addListener((o, a, n) -> {
            Shape3D s = shapes.get(n);
            cp.setValue(((PhongMaterial) s.getMaterial()).getDiffuseColor());
            slSize.setValue(s.getScaleX());
            chkWire.setSelected(s.getDrawMode() == DrawMode.LINE);
        });
        cp.setOnAction(e -> ((PhongMaterial) shapes.get(cbShape.getValue()).getMaterial()).setDiffuseColor(cp.getValue()));
        slSize.valueProperty().addListener((o, a, v) -> {
            Shape3D s = shapes.get(cbShape.getValue());
            s.setScaleX(v.doubleValue()); s.setScaleY(v.doubleValue()); s.setScaleZ(v.doubleValue());
        });
        chkWire.setOnAction(e -> shapes.get(cbShape.getValue()).setDrawMode(chkWire.isSelected() ? DrawMode.LINE : DrawMode.FILL));

        // TODO 5: Tự xoay bằng Timeline
        Timeline spin = new Timeline(new KeyFrame(Duration.millis(16), e -> ry.setAngle(ry.getAngle() + 0.5)));
        spin.setCycleCount(Animation.INDEFINITE);
        chkAuto.selectedProperty().addListener((o, a, on) -> { if (on) spin.play(); else spin.pause(); });

        // TODO 6: Chuột xoay, con lăn zoom (giới hạn khoảng cách camera)
        sub.setOnMousePressed(e -> { lastX = e.getSceneX(); lastY = e.getSceneY(); });
        sub.setOnMouseDragged(e -> {
            ry.setAngle(ry.getAngle() + (e.getSceneX() - lastX) * 0.4);
            rx.setAngle(Math.max(-90, Math.min(90, rx.getAngle() - (e.getSceneY() - lastY) * 0.4)));
            lastX = e.getSceneX(); lastY = e.getSceneY();
        });
        sub.setOnScroll(e -> camera.setTranslateZ(Math.max(-2000, Math.min(-200, camera.getTranslateZ() + e.getDeltaY()))));

        VBox panel = new VBox(8, new Label("Khối"), cbShape, new Label("Màu"), cp, new Label("Kích thước"), slSize, chkWire, chkAuto,
                new Label("Kéo chuột: xoay\nLăn chuột: zoom\nR: reset"));
        panel.setPadding(new Insets(10)); panel.setPrefWidth(180);
        BorderPane root = new BorderPane(sub, null, null, null, panel);
        sub.widthProperty().bind(root.widthProperty().subtract(panel.prefWidthProperty()));
        sub.heightProperty().bind(root.heightProperty());

        Scene scene = new Scene(root, 1000, 640);
        scene.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.R) { rx.setAngle(-20); ry.setAngle(30); camera.setTranslateZ(-700); } });
        stage.setTitle("T6 - 3D Viewer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
