package texttovoice;

import javax.swing.SwingUtilities;

public class TextToVoice {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TextToVoiceFrame().setVisible(true);
        });
    }
}
