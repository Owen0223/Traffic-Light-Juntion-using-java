import javax.swing.*;
import java.awt.*;

public class TrafficLight extends JPanel {
    private Color redColor = Color.GRAY;
    private Color yellowColor = Color.GRAY;
    private Color greenColor = Color.GRAY;
    private TrafficLightState state;

    public TrafficLight() {
        setPreferredSize(new Dimension(45, 170)); // Adjusted size for a smaller traffic light
        state = TrafficLightState.RED; // Default state
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the black rectangle background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 40, 100);

        // Draw the red light
        g.setColor(redColor);
        g.fillOval(7, 5, 20, 20);

        // Draw the yellow light
        g.setColor(yellowColor);
        g.fillOval(7, 30, 20, 20);

        // Draw the green light
        g.setColor(greenColor);
        g.fillOval(7, 55, 20, 20);
    }

    public void setState(TrafficLightState state) {
        this.state = state;
        switch (state) {
            case RED -> {
                redColor = Color.RED;
                yellowColor = Color.GRAY;
                greenColor = Color.GRAY;
            }
            case YELLOW -> {
                redColor = Color.GRAY;
                yellowColor = Color.YELLOW;
                greenColor = Color.GRAY;
            }
            case GREEN -> {
                redColor = Color.GRAY;
                yellowColor = Color.GRAY;
                greenColor = Color.GREEN;
            }
        }
        repaint();
    }

    public TrafficLightState getState() {
        return state;
    }
}

enum TrafficLightState {
    RED, YELLOW, GREEN
}
