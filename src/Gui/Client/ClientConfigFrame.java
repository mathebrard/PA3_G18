package Gui.Client;

import Gui.Common.Components.BorderButtonPanel;
import Gui.Common.Components.LabeledTextFieldPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Client Configuration interface
 */
public class ClientConfigFrame extends JFrame {
    private final JPanel mainPanel = new JPanel();
    private final LabeledTextFieldPanel ipField = new LabeledTextFieldPanel("Client IP: ", "localhost");
    private final LabeledTextFieldPanel portField = new LabeledTextFieldPanel("Client Port: ", "10000");
    private final LabeledTextFieldPanel idField = new LabeledTextFieldPanel("Client ID: ", "99");
    private IStartCallback callback = (ip, port, id) -> System.out.println(ip + " " + port + " " + id);

    /**
     * Creates a new client configuration interface
     * with the default width and height.
     */
    public ClientConfigFrame() {
        this(500, 500);
    }

    /**
     * Creates a new client configuration interface
     * with the specified width and height.
     * @param width Width of the interface in pixels.
     * @param height Height of the interface in pixels.
     */
    public ClientConfigFrame(int width, int height) {
        super();
        setTitle("Client Configuration");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Ensuring all the components have the same alignments
        // So the Boxlayout behaves properly
        ipField.setAlignmentX(LEFT_ALIGNMENT);
        portField.setAlignmentX(LEFT_ALIGNMENT);
        idField.setAlignmentX(LEFT_ALIGNMENT);

        BorderButtonPanel startButton = new BorderButtonPanel("Start");
        startButton.setAlignmentX(LEFT_ALIGNMENT);
        // Boxlayout respects the maximum size of the elements and nothing else
        startButton.setMaximumSize(new Dimension(200, 70));
        startButton.addActionListener((e) -> {

            //TODO - fix button
            startButton.setEnabled(false);
            int port = Integer.parseInt(portField.getValue());
            int id = Integer.parseInt(idField.getValue());
            this.callback.call(ipField.getValue(), port, id);
            startButton.setEnabled(true);
        });

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(ipField);
        mainPanel.add(portField);
        mainPanel.add(idField);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(startButton);
        add(mainPanel);
    }

    /**
     * Set a callback function for when the start
     * button is pressed.
     * @param callback The function to be called when the button is pressed.
     */
    public void setStartCallback(IStartCallback callback) {
        this.callback = callback;
    }
}
