import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Converter {
    private double finishedPercent = 0;
    public void toAscii(String fn, String outputFilePath,double scalingFactor) {
        int scalingMethod = Image.SCALE_SMOOTH;
        try {
            char[] pixelChars = {'@', '%', '#', 'x', '+', '=', ':', '-', '.', ' '};
            BufferedImage image = ImageIO.read(new File(fn));

            //Prevents Unnecessary Scaling
            if (scalingFactor != 1.0d) {
                System.out.println("Scaled");
                image = imgToBuff(image.getScaledInstance((int) (image.getWidth() * scalingFactor), -1, scalingMethod));
            }

            for (int y = 0; y < image.getHeight(); y += 1) {
                StringBuilder sb = new StringBuilder();
                finishedPercent = ((double)(y))/image.getHeight();
                for (int x = 0; x < image.getWidth(); x += 1) {
                    Color pixelColor = new Color(image.getRGB(x, y));
                    sb.append(grayscaleToChar(convertToGrayVal(pixelColor), pixelChars));
                }
                writeStringToFile(sb.toString(), outputFilePath);
            }
            finishedPercent = 1;
        } catch (IOException e) {
            System.out.println("Couldn't Find Image");
            System.exit(1);
        }
    }

    private static char grayscaleToChar(double grayval, char[] chars) {
        /*
        More Types Of Chars Changes Picture Shape (Without Monospace Text I think)

        Example Char Lists:
        char[] asciiChars = {'@', '%', '#', 'x', '+', '=', ':', '-', '.', ' '};
        char[] asciiChars = {'$', '@', 'B', '%', '8', '&', 'W', 'M', '#', '*', 'o', 'a', 'h', 'k', 'b', 'd', 'p', 'q', 'w', 'm', 'Z', 'O', '0', 'Q', 'L', 'C', 'J', 'U', 'Y', 'X', 'z', 'c', 'v', 'u', 'n', 'x', 'r', 'j', 'f', 't', '/', '\\', '|', '(', ')', '1', '{', '}', '[', ']', '?', '-', '_', '+', '~', '<', '>', 'i', '!', 'l', 'I', ';', ':', ',', '"', '^', '`', '\'', '.'};

        */
        int charIndex = (int) ((grayval / 255.0d) * (chars.length - 1));
        return chars[charIndex];
    }

    private static double convertToGrayVal(Color c) {
        double redAmount = c.getRed();
        double blueAmount = c.getBlue();
        double greenAmount = c.getGreen();
        /*
        https://www.tutorialspoint.com/dip/grayscale_to_rgb_conversion.htm
        Formula:
        (red *.3) + (green * .59) + (blue * .11)
         */
        return ((redAmount * 0.3d) + (greenAmount * .59d) + (blueAmount * .11d));
    }

    private static void writeStringToFile(String s, String outputFn) {
        try {
            FileWriter fw = new FileWriter(outputFn, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter writer = new PrintWriter(bw);
            writer.println(s);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getProgress() {
        return (int) (finishedPercent * 100);
    }

    private static BufferedImage imgToBuff(Image i) {
        BufferedImage out = new BufferedImage(i.getWidth(null),i.getHeight(null),BufferedImage.TYPE_INT_RGB);

        Graphics2D graphs = out.createGraphics();
        graphs.drawImage(i,0,0,null);
        graphs.dispose();

        return out;
    }
}
