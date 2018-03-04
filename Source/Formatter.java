import java.util.ArrayList;
import java.awt.*;
public class Formatter extends Thread {

    private final TextField textField;

    Formatter(TextField txtfield) {
        textField = txtfield;
    }

    public void run() {
        ArrayList<String> possibleChars = new ArrayList<>();

        possibleChars.add(".");
        for (int i = 0; i < 10; i++) {
            possibleChars.add(Integer.toString(i));
        }
        while (true) {
            boolean alreadyFoundDecimal = false;
            String text = textField.getText();
            StringBuilder stringBuilder = new StringBuilder();
            for (Character c : text.toCharArray()) {
                if (c != '.' && possibleChars.indexOf("" + c) != -1) {
                    stringBuilder.append(c);
                } else if (c == '.' && !alreadyFoundDecimal) {
                    alreadyFoundDecimal = true;
                    stringBuilder.append(c);
                }
            }
            try {
                if ((stringBuilder.toString().length() > 0 || !(stringBuilder.toString().equals(text))) && textField.getSelectedText().length() == 0 && !(textField.getCaretPosition() < textField.getText().length())) {
                    textField.setText(stringBuilder.toString());
                    textField.setCaretPosition(stringBuilder.toString().length());
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        System.exit(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
