import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageToAsciiConverter extends JFrame {
    private JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
    private JProgressBar jp;
    private int maxWidth = 1280;
    private int maxHeight = 720;
    private int scalingMethod = Image.SCALE_SMOOTH;
    private ImageToAsciiConverter() {
        super("Image File To Ascii Art Converter");

        //Sets Look And Feel To System Default

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

        JPanel textboxPanel = new JPanel();
        textboxPanel.setLayout(new FlowLayout());
        textboxPanel.add(new JLabel("Scaling Factor (Decimal): "));
        //TextField Is Used For Thread Safety
        TextField scalingFactor = new TextField();
        scalingFactor.setText("1");
        Thread formattingThread = new Thread(new Formatter(scalingFactor));
        formattingThread.start();

        textboxPanel.add(scalingFactor);

        panel.add(fileOpener);
        panel.add(textboxPanel);
        panel.add(SubmitButton);
        panel.add(pic);
        panel.add(jp);

        add(panel);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        SwingUtilities.updateComponentTreeUI(this);
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
                        BufferedImage selectedImage = ImageIO.read(new File(filename));
                        Image scaledImage;

                        //Checks If Both Height And Width Are Too Big
                        if (selectedImage.getHeight() > maxHeight) {
                            scaledImage = selectedImage.getScaledInstance(-1,maxHeight,scalingMethod);
                            if (scaledImage.getWidth(null) > maxWidth) {
                                scaledImage = scaledImage.getScaledInstance(maxWidth,-1,scalingMethod);
                            }
                        } else if (selectedImage.getWidth() > maxWidth) {
                            scaledImage = selectedImage.getScaledInstance(maxWidth,-1,scalingMethod);
                            if (scaledImage.getHeight(null) > maxHeight) {
                                scaledImage = scaledImage.getScaledInstance(-1,maxHeight,scalingMethod);
                            }
                        } else {
                            scaledImage = selectedImage;
                        }

                        pic.setIcon(new ImageIcon(scaledImage));
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
                        class runner implements Runnable {
                            private String i;
                            private String o;
                            private Converter p;
                            private double s;
                            private runner(String inpFile, String outFile, Converter u, double scaling) {
                                i = inpFile;
                                o = outFile;
                                p = u;
                                s = scaling;
                            }

                            @Override
                            public void run() {
                                p.toAscii(i, o, s);
                            }
                        }
                        if (scalingFactor.getText().equals("")) {
                            scalingFactor.setText("1");
                        }
                        Thread t = new Thread(new runner(filePath, fn, c, Double.parseDouble(scalingFactor.getText())));
                        t.start();
                        jp.setValue(0);


                        class looper implements Runnable {
                            private Converter conv;

                            private looper(Converter p) {
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

    private static String getOuterDirectoryFromFile(String s) {
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

    private static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new ImageToAsciiConverter();
    }
}
