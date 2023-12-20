package loom.loader;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;

import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Frame extends JFrame {

    public static String overrideConfigPath;

    private JPanel root;
    private JButton installButton;
    private JButton cancelButton;
    private JTextField pathField;
    private JButton pickButton;
    private JTextPane logPane;

    {
        $$$setupUI$$$();
    }

    public Frame() {
        super("Equilinox Fabric Installer");
        setContentPane(root);
        pack();

        pickButton.addActionListener(ev -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Select game directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                pathField.setText(chooser.getSelectedFile().toPath().toString());
            }
        });

        cancelButton.addActionListener(ev -> {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });

        installButton.addActionListener(ev -> {
            Path gameDirectory = Paths.get(pathField.getText());
            if (pathField.getText().isEmpty()) {
                showError("No game directory selected!");
                return;
            }
            if (!Files.exists(gameDirectory)) {
                showError(gameDirectory + " does not exist!");
                return;
            }
            if (!Files.isDirectory(gameDirectory)) {
                showError(gameDirectory + " is not a directory!");
                return;
            }
            installButton.setEnabled(false);
            cancelButton.setEnabled(false);
            install(gameDirectory);
        });
    }

    private void showError(String message) {
        log("ERROR: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void install(Path gameDir) {
        try {
            Installer i = new Installer(gameDir.toString());
            i.onLog = this::log;
            i.onDone = this::onInstallDone;
            i.execute();
        } catch (Exception e) {
            showError(e.toString());
        }
    }

    private void log(String s) {
        logPane.setText(logPane.getText() + s + "\n");
        revalidate();
        repaint();
    }

    private void onInstallDone(Exception e) {
        if (e != null) showError(e.toString());
        cancelButton.setEnabled(true);
        cancelButton.setText("Done");
    }

    public static void main(String[] args) {
        if(args.length > 0) overrideConfigPath = args[0];

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ignored) {}
        Frame view = new Frame();
        view.setLocationRelativeTo(null);
        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.setResizable(false);
        view.setVisible(true);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new BorderLayout(0, 0));
        root.setPreferredSize(new Dimension(500, 300));
        root.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        JPanel footer = new JPanel();
        footer.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        root.add(footer, BorderLayout.SOUTH);
        installButton = new JButton();
        installButton.setText("Install");
        footer.add(installButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        footer.add(cancelButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        footer.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        JPanel main = new JPanel();
        main.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        root.add(main, BorderLayout.CENTER);
        main.setBorder(BorderFactory.createTitledBorder(null, "Select game directory:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        pathField = new JTextField();
        pathField.setEditable(true);
        main.add(pathField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        main.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        logPane = new JTextPane();
        logPane.setEditable(false);
        scrollPane1.setViewportView(logPane);
        pickButton = new JButton();
        pickButton.setText("...");
        main.add(pickButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }
}
