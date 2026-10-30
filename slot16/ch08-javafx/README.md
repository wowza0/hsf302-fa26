# ch08-javafx — Lời giải bài tập Chapter 08 (JavaFX)

Project Maven chứa lời giải đầy đủ của 12 bài trong `Chapter8_final.md`, mỗi bài một package.

**Môi trường:** JDK 21 · JavaFX 21 · Maven · IntelliJ IDEA (Scene Builder 23 để mở các file `.fxml`)

## Cách chạy

**IntelliJ:** File → Open → chọn `pom.xml` → đợi Maven tải JavaFX → mở class `XxxApp` của bài cần chạy → bấm ▶ cạnh `main()`.

**Maven (dòng lệnh):**
```bash
mvn clean javafx:run                                                         # chạy B1 (mặc định)
mvn clean javafx:run -Dmain.class=com.fu.ch08/com.fu.ch08.b3.RegisterApp     # chạy bài khác
```
> Trên PowerShell đặt tham số trong nháy kép: `mvn javafx:run "-Dmain.class=com.fu.ch08/com.fu.ch08.b3.RegisterApp"`

## Danh sách bài

| Bài | Nội dung | Class chạy (`-Dmain.class=com.fu.ch08/...`) | FXML / CSS |
|---|---|---|---|
| B1 | Controls & Xử lý sự kiện (Đặt đồ uống) | `com.fu.ch08.b1.DrinkOrderApp` | — (code thuần) |
| B2 | Layout thông dụng & Form đăng nhập | `com.fu.ch08.b2.LoginLayoutApp` | — (code thuần) |
| B3 | Form đăng ký + Validate + Đăng nhập | `com.fu.ch08.b3.RegisterApp` | — (code thuần) |
| B4 | FXML + Controller + CSS (Đăng nhập) | `com.fu.ch08.b4.LoginApp` | `login-view.fxml`, `welcome-view.fxml`, `login.css` |
| B5 | Layout Gallery | `com.fu.ch08.b5.LayoutApp` | `main-layout.fxml` |
| B6 | Phiếu khảo sát (Controls) | `com.fu.ch08.b6.SurveyApp` | `survey-view.fxml` |
| T1 | Quản lý sinh viên: TreeView + TableView + CSV | `com.fu.ch08.t1.StudentApp` | `student-view.fxml` |
| T2 | Shape Editor (2D, sự kiện chuột/phím, animation) | `com.fu.ch08.t2.ShapeEditorApp` | — |
| T3 | Concurrency: Task, ProgressBar, Cancel | `com.fu.ch08.t3.ConcurrencyApp` | — |
| T4 | Charts Dashboard real-time | `com.fu.ch08.t4.ScoreDashboardApp` | — |
| T5 | Mini Browser & Media Player | `com.fu.ch08.t5.BrowserMediaApp` | — (cần Internet) |
| T6 | 3D Viewer (bonus) | `com.fu.ch08.t6.Viewer3DApp` | — |

FXML/CSS nằm trong `src/main/resources/com/fu/ch08/<bài>/` — **cùng đường dẫn package** với class.

## Tài khoản mẫu

| Bài | Username | Password | Ghi chú |
|---|---|---|---|
| B2 | `admin` / `student` | `Admin@123` / `Student@123` | |
| B3 | `admin` | `Admin@123` | Hoặc tự đăng ký tài khoản mới |
| B4 | `student` / `teacher` | `Student@123` / `Teacher@123` | Chọn đúng vai trò Sinh viên / Giảng viên |

## Cấu trúc

```
ch08-javafx/
├─ pom.xml
└─ src/main/
   ├─ java/
   │  ├─ module-info.java
   │  └─ com/fu/ch08/
   │     ├─ common/AlertUtil.java
   │     ├─ b1/ … b3/        # UI viết bằng code
   │     ├─ b4/ … b6/        # FXML + Controller
   │     └─ t1/ … t6/        # Bài trung bình
   └─ resources/com/fu/ch08/
      ├─ b4/  login-view.fxml · welcome-view.fxml · login.css
      ├─ b5/  main-layout.fxml
      ├─ b6/  survey-view.fxml
      └─ t1/  student-view.fxml
```

## Lỗi thường gặp

- **`Location is not set`** → sai đường dẫn FXML, hoặc chưa Rebuild để resource được copy vào `target/classes`.
- **`IllegalAccessException ... cannot access class ... (in module com.fu.ch08)`** → thiếu `opens <package> to javafx.fxml` trong `module-info.java`.
- **TableView cột trống** (T1) → thiếu `opens com.fu.ch08.t1 to javafx.base`.
- **`JavaFX runtime components are missing`** → đang chạy theo classpath; hãy chạy bằng `mvn javafx:run` hoặc để IntelliJ nhận `module-info.java`.
- Xem thêm Phần C trong `Chapter8_final.md`.
