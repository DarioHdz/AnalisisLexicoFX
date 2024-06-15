module com.automatas.analisislexicofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.automatas.analisislexicofx to javafx.fxml;
    exports com.automatas.analisislexicofx;
}