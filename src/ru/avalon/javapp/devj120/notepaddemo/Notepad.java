package ru.avalon.javapp.devj120.notepaddemo;

import javax.security.auth.Destroyable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;


public class Notepad extends JFrame {

    private JTextArea textArea;
    private JScrollPane scrollPane;

    private JMenuBar menuBar;
    private  JMenu menu;

    private  JMenuItem newFile;
    private  JMenuItem openFile;
    private  JMenuItem save;
    private  JMenuItem saveAs;
    private  JMenuItem exit;



    private File[] children;
    private JList list;
    private JScrollPane scrollPane1;

    public Notepad() {
        setSize (600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        setJMenuBar(createMenuBar());
        setVisible(true);
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

//        newFile.addActionListener(());


        openFile.addActionListener((e -> {
            if(textArea.getText().equals("")) {
                goToDir(System.getProperty("user.dir"));
            }
            else {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Вы не сохранили результат. Сохранить?",
                        "Open file",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {

                }
                if(result == JOptionPane.NO_OPTION){
                    textArea.setText("");
                    goToDir(System.getProperty("user.dir"));
                }
            }
        }));


//        save.addActionListener(());


//        saveAs.addActionListener(());

        exit.addActionListener((e -> {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "Вы действительно хотите закрыть блокнот?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
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



    public static void main(String[] args) {
                new Notepad();
    }

    private void goToDir(String path) {

        createOpenFileJframe();
        File dir = new File(path);
        File[] a = dir.listFiles();
        if(a == null) {
            textArea.setText("Error reading directory");
            return;
        }
        setTitle(dir.toString());
        children = a;
        Arrays.sort(children, Notepad::compareFiles);

        File parent = dir.getParentFile();
        if(parent != null) {
            File[] ch = new File[children.length + 1];
            ch[0] = parent;
            System.arraycopy(children, 0, ch, 1, children.length);
            children = ch;
        }
        String[] names = new String[children.length];
        for(int i = 0; i < names.length; i++) {
            names[i] = children[i].getName();
        }
        if(parent != null) {
            names[0] = "...";
        }
        list.setListData(names);
    }

    private static int compareFiles(File f1, File f2) {
        if(f1.isDirectory() && f2.isFile())
            return -1;
        if(f1.isFile() && f2.isDirectory())
            return 1;
        return f1.getName().compareTo(f2.getName());
    }

    private void createOpenFileJframe() {
        JFrame jFrame = new JFrame();
        jFrame.setSize (400, 300);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        list = new JList();
//        list.addListSelectionListener(e -> listSelectionChanged());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    int ndx = list.getSelectedIndex();
                    if(ndx >= 0 && children[ndx].isDirectory()) {
                        goToDir(children[ndx].getAbsolutePath());
                        jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING));
                    }
                    if(ndx >= 0 && children[ndx].isFile()) {
                        jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING));
                        listSelectionChanged();
                    }
                }
            }
        });
        list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int ndx = list.getSelectedIndex();
                    if(ndx >= 0 && children[ndx].isDirectory()) {
                        goToDir(children[ndx].getAbsolutePath());
                        jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING));
                    }
                    if(ndx >= 0 && children[ndx].isFile()) {
                        listSelectionChanged();
                        jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING));
                    }
                }
            }
        });

        scrollPane1 = new JScrollPane(list);
        jFrame.add(scrollPane1, BorderLayout.CENTER);

        jFrame.setVisible(true);
    }

    private void listSelectionChanged() {
        int ndx = list.getSelectedIndex();
        if(ndx == -1)
            return;
        if(children[ndx].isDirectory()) {
//            textArea.setText("");
            return;
        }

        try (FileReader r = new FileReader(children[ndx])) {
                    StringBuilder sb = new StringBuilder();
                    char[] buf = new char[4_096];
                    int n;
                    while ((n = r.read(buf)) >= 0) {
                        sb.append(buf, 0, n);
                    }
                    textArea.setText(sb.toString());
                    textArea.setCaretPosition(0);
        } catch (IOException e) {
                    textArea.setText("Error reading file" + e.getMessage() + ".");

        }
    }
}
