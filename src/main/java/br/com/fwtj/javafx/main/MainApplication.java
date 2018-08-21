/*
    Maven-JavaFX-Package-Example
    Copyright (C) 2017-2018 Luca Bognolo

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.

 */
package br.com.fwtj.javafx.main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;

/**
 * Main application class.
 */
public class MainApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApplication.class);

    public static String NOME = "Projeto Basico Java FX";

    private Stage stage;

    private java.awt.SystemTray tray = null;
    private java.awt.TrayIcon trayIcon;
    private java.awt.Image iconeTray = null;
    private Image icone = null;


    @Override
    public void start(Stage stage) throws Exception {

        File logoDisco = new File("l.cfg");
        if (logoDisco.exists()) {
            InputStream logoInputStream = new FileInputStream(logoDisco);
            InputStream logoInputStream2 = new FileInputStream(logoDisco);
            icone = new Image(logoInputStream);
            iconeTray = ImageIO.read(logoInputStream2).getScaledInstance(16, 16, 16);
        } else {
            icone = new Image("/img/l.cfg");
            iconeTray = ImageIO.read(getClass().getResource("/img/l.cfg")).getScaledInstance(16, 16, 16);
        }

        this.stage = stage;
        stage.setTitle("Exemplo Java FX, Maven");
        Pane pane = loadMainPane();
        Scene scene = createScene(pane);
        stage.setScene(scene);
        stage.show();
        Thread.sleep(1000);
        stage.hide();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stage.hide();
                    }
                });
            }
        });

        Platform.setImplicitExit(false);
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
    }

    /**
     * Loads the main fxml layout. Sets up the vista switching VistaNavigator.
     * Loads the first vista into the fxml layout.
     *
     * @return the loaded pane.
     * @throws IOException if the pane could not be loaded.
     */
    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane mainPane = loader.load(ClassLoader.getSystemResourceAsStream("br/com/fwtj/javafx/view/main.fxml"));
        return mainPane;
    }

    /**
     * Creates the main application scene.
     *
     * @param mainPane the main application layout.
     * @return the created scene.
     */
    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);
        scene.getStylesheets().setAll(ClassLoader.getSystemResource("br/com/fwtj/javafx/style/vista.css").toExternalForm());
        return scene;
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    private void addAppToTray() {
        try {
            java.awt.Toolkit.getDefaultToolkit();

            if (!java.awt.SystemTray.isSupported()) {
                log.error("Sistema operacional nao suporta system tray!");
            }

            tray = java.awt.SystemTray.getSystemTray();
            trayIcon = new java.awt.TrayIcon(iconeTray);

            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            java.awt.MenuItem openItem = new java.awt.MenuItem("Maximizar " + NOME);
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

            java.awt.MenuItem exitItem = new java.awt.MenuItem("Sair");
            exitItem.addActionListener(event -> {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        Platform.exit();
                        System.exit(0);

                    }
                });

            });

            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            MouseMotionListener mml = new MouseMotionListener() {
                public void mouseDragged(MouseEvent e) {
                }

                public void mouseMoved(MouseEvent e) {

                    trayIcon.setToolTip(NOME);
                }
            };
            trayIcon.addMouseMotionListener(mml);

            tray.add(trayIcon);

        } catch (Exception e) {
            log.error("Erro ao carregar system tray!");
        }
    }

}
