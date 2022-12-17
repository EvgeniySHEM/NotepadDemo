package ru.avalon.javapp.devj120.notepaddemo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class Notepad extends JFrame {

    private JTextArea textArea;

    private JMenuBar menuBar;
    private  JMenu menu;

    private  JMenuItem newFile;
    private  JMenuItem openFile;
    private  JMenuItem save;
    private  JMenuItem saveAs;
    private  JMenuItem exit;

    private  JFileChooser fileChooser;

    private String startText = "";

    public Notepad() {
        setSize (600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new DemoWindowAdapter());

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        setTitle("New file");
        setJMenuBar(createMenuBar());
        setVisible(true);
    }
    private class DemoWindowAdapter extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            closing();
        }
    }

    public  JMenuBar createMenuBar() {
        newFile = new JMenuItem("New file");
        openFile = new JMenuItem("Open file");
        save = new JMenuItem("Save", KeyEvent.VK_S);
        saveAs = new JMenuItem("Save as", KeyEvent.VK_A);
        exit = new JMenuItem("Exit");
        exit.setBackground(Color.gray);

        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                KeyEvent.META_DOWN_MASK));
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                KeyEvent.META_DOWN_MASK));
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.META_DOWN_MASK));
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                KeyEvent.META_DOWN_MASK));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                KeyEvent.META_DOWN_MASK));

        newFile.addActionListener((e -> {
            if(textArea.getText().equals(startText)) {
                clearing();
            }
            else {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Вы не сохранили результат. Сохранить?",
                        "New file",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    saveFileMethod();
                    clearing();

                }
                if(result == JOptionPane.NO_OPTION){
                    clearing();
                }
            }
            setTitle("New file");
        }));


        openFile.addActionListener((e -> {
            if(textArea.getText().equals(startText)) {
                openFileMethod();
            }
            else {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Вы не сохранили результат. Сохранить?",
                        "Open file",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    saveFileMethod();
                    clearAndOpen();
                }
                if(result == JOptionPane.NO_OPTION){
                    clearAndOpen();
                }
            }
        }));

        save.addActionListener((e) -> {
            if(getTitle().equals("New file")) {
                saveFileMethod();
            }
            else {
                saving();
            }
            });


        saveAs.addActionListener((e) -> {
            saveFileMethod();
        });

        exit.addActionListener((e -> {
            closing();
        }));

        menu = new JMenu("Menu");
        menu.setMnemonic(KeyEvent.VK_A);

        menu.add(newFile);
        menu.add(openFile);
        menu.addSeparator();
        menu.add(save);
        menu.add(saveAs);
        menu.addSeparator();
        menu.add(exit);

        menuBar = new JMenuBar();
        menuBar.add(menu);

        return menuBar;
    }
    private void openFileMethod() {
        fileChooser.setDialogTitle("Open file");
        int result = fileChooser.showOpenDialog(Notepad.this);
        if (result == JFileChooser.APPROVE_OPTION ) {
            readingFile();
            setTitle(fileChooser.getSelectedFile().toString());
        }
    }

    private int saveFileMethod() {
        fileChooser.setDialogTitle("Сохранение файла");
        int result = fileChooser.showSaveDialog(Notepad.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            if (fileChooser.getDialogType() == JFileChooser.SAVE_DIALOG) {
                File selectedFile = fileChooser.getSelectedFile();
                if ((selectedFile != null) && selectedFile.exists()) {
                    int response = JOptionPane.showConfirmDialog(this,
                            "Файл " + selectedFile.getName() +
                                    " уже существует. Вы хотите заменить существующий файл?",
                            "Ovewrite file", JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (response != JOptionPane.YES_OPTION)
                        return -1;
                }
            }
            saving();
        }
        return 0;
    }

    private void saving() {
        writeFile();
        startText = textArea.getText();
        setTitle(fileChooser.getSelectedFile().toString());
        JOptionPane.showMessageDialog(Notepad.this,
                "Файл '" + fileChooser.getSelectedFile() +
                        " ) сохранен");
    }

    private int exitFileMethod() {
        if(textArea.getText().equals(startText)) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "Вы действительно хотите закрыть блокнот?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                dispose();
            }
            if(result == JOptionPane.NO_OPTION){
                return -1;
            }
        }
        else {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "Вы не сохранили результат. Сохранить?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                int a = saveFileMethod();
                return a;
            }
            if(result == JOptionPane.NO_OPTION){
                dispose();
            }
        }
        return 0;
    }

    private void clearing() {
        textArea.setText("");
        startText = textArea.getText();
    }

    private void clearAndOpen() {
        textArea.setText("");
        openFileMethod();
    }

    private void closing() {
        int a = exitFileMethod();
        if(a == 0){
            dispose();
        }
    }

    public static void main(String[] args) {
                new Notepad();
    }


    private void readingFile() {
        File file = fileChooser.getSelectedFile();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                        sb.append(s + "\n");
                    }
                    textArea.setText(sb.toString());
                    startText = textArea.getText();
                    textArea.setCaretPosition(0);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading file" + e.getMessage() + ".", "Error reading file",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void writeFile() {
        File file = fileChooser.getSelectedFile();

        try (BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter(file))) {
            bufferedWriter.write(textArea.getText());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving file" + e.getMessage() + ".", "Error write file",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
