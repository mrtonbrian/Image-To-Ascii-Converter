import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImageToAsciiConverter extends JFrame {
    private JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
    JProgressBar jp;

    private ImageToAsciiConverter() {
        super("Image File To Ascii Art Converter");

        //Sets Look And Feel To System Default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Sets Up Panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        //Widget Initialization
        JButton fileOpener = new JButton("Open. . .");
        JButton SubmitButton = new JButton("Submit!");
        SubmitButton.setEnabled(false);
        JLabel pic = new JLabel();
        jp = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        jp.setValue(0);
        jp.setStringPainted(true);

        panel.add(fileOpener);
        panel.add(SubmitButton);
        panel.add(pic);
        panel.add(jp);

        add(panel);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();

        fileOpener.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fc.resetChoosableFileFilters();
                    fc.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "GIF", "wbmp", "bmp"));
                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    int returnValue = fc.showOpenDialog(null);

                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        SubmitButton.setEnabled(true);
                        File returnedFile = fc.getSelectedFile();
                        String filename = returnedFile.getAbsolutePath();
                        fileOpener.setText(filename);
                        pic.setIcon(new ImageIcon(filename));
                        pack();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        SubmitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String filePath = fileOpener.getText();
                    String directPath = getOuterDirectoryFromFile(filePath);
                    fc.setCurrentDirectory(new File(directPath));
                    fc.setFileFilter(new FileNameExtensionFilter("Text File", "txt"));
                    fc.setSelectedFile(new File(filePath.substring(0, filePath.lastIndexOf("."))));
                    int returnValue = fc.showSaveDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        Converter c = new Converter();
                        String fn = fc.getSelectedFile().getAbsolutePath();
                        if (!fn.endsWith(".txt")) {
                            fn += ".txt";
                        }
                        beep();

                        JOptionPane.showMessageDialog(null, "Starting \n It May Take A Few Seconds");
                        //c.toAscii(filePath, fn);
                        class runner implements Runnable {
                            String i;
                            String o;
                            Converter p;

                            runner(String inpFile, String outFile, Converter u) {
                                i = inpFile;
                                o = outFile;
                                p = u;
                            }

                            @Override
                            public void run() {
                                p.toAscii(i, o);
                            }
                        }
                        Thread t = new Thread(new runner(filePath, fn, c));
                        t.start();
                        jp.setValue(0);


                        class looper implements Runnable {
                            Converter conv;
                            looper(Converter p) {
                                conv = p;
                            }
                            @Override
                            public void run() {
                                while (true) {
                                    int progress = conv.getProgress();
                                    update(progress);

                                    //Waits Until Progressbar is Done Updating (Made To Not Scare User)
                                    if (jp.getValue() == 100) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                beep();
                                                JOptionPane.showMessageDialog(null, "Finished!");
                                            }
                                        });
                                        break;
                                    }
                                }
                            }
                        }
                        Thread looperThread = new Thread(new looper(c));
                        looperThread.start();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static String getOuterDirectoryFromFile(String s) {
        try {
            File f = new File(s);
            String absolutePath = f.getAbsolutePath();
            return absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void update(int val) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jp.setValue(val);
            }
        });

    }

    public void showFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, "Finished!");
            }
        });
    }

    public static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }

    public static void main(String[] args) {
        new ImageToAsciiConverter();
    }
}
