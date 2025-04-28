package texttovoice;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.sun.speech.freetts.*;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextToVoiceFrame extends JFrame {
    private JTextField textField;
    private JButton speakButton;
    private JButton saveButton;
    private JSlider speedSlider;
    private JSlider volumeSlider;

    private static final String VOICE_NAME = "kevin16";

    public TextToVoiceFrame() {
        setTitle("Text to Voice");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JLabel titleLabel = new JLabel("Text to Speech", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(18f));

        textField = new JTextField();
        speakButton = new JButton("Speak");
        saveButton = new JButton("Save as WAV");

        speedSlider = new JSlider(100, 300, 150); // min=100, max=300 wpm
        volumeSlider = new JSlider(0, 100, 100);  // 0-100%

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.add(new JLabel("Speaking Speed (Words Per Minute):"));
        controlsPanel.add(speedSlider);
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(new JLabel("Volume:"));
        controlsPanel.add(volumeSlider);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(speakButton);
        buttonsPanel.add(saveButton);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(titleLabel);
        add(Box.createVerticalStrut(10));
        add(textField);
        add(Box.createVerticalStrut(10));
        add(controlsPanel);
        add(Box.createVerticalStrut(10));
        add(buttonsPanel);

        // Button actions
        speakButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                speakText(textField.getText());
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTextToWav(textField.getText());
            }
        });
    }

    private void speakText(String text) {
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        VoiceManager vm = VoiceManager.getInstance();
        Voice voice = vm.getVoice(VOICE_NAME);

        if (voice != null) {
            voice.allocate();
            voice.setRate(speedSlider.getValue());
            voice.setVolume(volumeSlider.getValue() / 100f);

            voice.speak(text);

            voice.deallocate();
        } else {
            JOptionPane.showMessageDialog(this, "Voice not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveTextToWav(String text) {
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        VoiceManager vm = VoiceManager.getInstance();
        Voice voice = vm.getVoice(VOICE_NAME);

        if (voice != null) {
            try {
                voice.allocate();
                voice.setRate(speedSlider.getValue());
                voice.setVolume(volumeSlider.getValue() / 100f);

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save WAV file");
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!path.endsWith(".wav")) {
                        path += ".wav";
                    }

                    SingleFileAudioPlayer player = new SingleFileAudioPlayer(path.replace(".wav", ""), javax.sound.sampled.AudioFileFormat.Type.WAVE);
                    voice.setAudioPlayer(player);
                    voice.speak(text);
                    player.close();

                    JOptionPane.showMessageDialog(this, "Saved to: " + path, "Success", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                voice.deallocate();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Voice not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
