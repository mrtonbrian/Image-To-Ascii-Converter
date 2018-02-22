import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Converter {
    private static void toAscii(String fn) {
        try {
            char[] pixelChars = {'@', '%', '#', 'x', '+', '=', ':', '-', '.', ' '};
            BufferedImage image = ImageIO.read(new File(fn));
            for (int y = 0; y < image.getHeight(); y += 1) {
                StringBuilder sb = new StringBuilder();
                for (int x = 0; x < image.getWidth(); x += 1) {
                    Color pixelColor = new Color(image.getRGB(x, y));
                    sb.append(grayscaleToChar(convertToGrayVal(pixelColor), pixelChars));
                }
                writeStringToFile(sb.toString(), fn.substring(0, fn.length() - 3) + "txt");
            }
            System.out.println("Done");
        } catch (IOException e) {
            System.out.println("Couldn't Find Image");
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("Not A Valid Image File!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1 || args[0].equals("help"))
            System.out.println("Usage:   Converter imagefilename");
        else
            toAscii(args[0]);
    }
    private static char grayscaleToChar(double grayval, char[] chars) {
        /*
        More Types Of Chars Changes Picture Shape (Without Monospace Text I think)

        Example Char Lists:
        char[] asciiChars = {'@', '%', '#', 'x', '+', '=', ':', '-', '.', ' '};
        char[] asciiChars = {'$', '@', 'B', '%', '8', '&', 'W', 'M', '#', '*', 'o', 'a', 'h', 'k', 'b', 'd', 'p', 'q', 'w', 'm', 'Z', 'O', '0', 'Q', 'L', 'C', 'J', 'U', 'Y', 'X', 'z', 'c', 'v', 'u', 'n', 'x', 'r', 'j', 'f', 't', '/', '\\', '|', '(', ')', '1', '{', '}', '[', ']', '?', '-', '_', '+', '~', '<', '>', 'i', '!', 'l', 'I', ';', ':', ',', '"', '^', '`', '\'', '.'};

        */
        int charIndex = (int)((grayval / 255.0d) * (chars.length-1));
        return chars[charIndex];
    }
    private static double convertToGrayVal(Color c) {
        double redAmount   = c.getRed();
        double blueAmount  = c.getBlue();
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
        } catch (IOException e) {}
    }
}
