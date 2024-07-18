import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Car extends JPanel implements Runnable {
    private int x, y;
    private final int speed;
    private final TrafficLight trafficLight;
    private final String direction;
    private boolean running = true;
    private boolean hasPassedLight = false;
    private final List<Car> cars;
    private static final int CAR_DISTANCE_THRESHOLD = 50; // Minimum distance between cars

    public Car(int startX, int startY, int speed, TrafficLight trafficLight, String direction, List<Car> cars) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.trafficLight = trafficLight;
        this.direction = direction;
        this.cars = cars;
        setBackground(Color.BLUE);
        setBounds(x, y, direction.equals("NS") || direction.equals("SN") ? 20 : 40, direction.equals("NS") || direction.equals("SN") ? 40 : 20);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(50); // Adjust sleep time for smoother or faster movement
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            move();
        }
    }

    public void move() {
        boolean shouldMove = true;

        // Check distance to other cars
        for (Car car : cars) {
            if (car != this) {
                int distance = calculateDistance(this.x, this.y, car.getXPosition(), car.getYPosition());
                if (isCarInPath(car) && distance < CAR_DISTANCE_THRESHOLD) {
                    shouldMove = false;
                    break;
                }
            }
        }

        // Check traffic light
        synchronized (trafficLight) {
            if (!hasPassedLight) {
                if ((trafficLight.getState() == TrafficLightState.RED || trafficLight.getState() == TrafficLightState.YELLOW) &&
                    ((direction.equals("EW") && x >= trafficLight.getX() - 60) ||
                     (direction.equals("WE") && x <= trafficLight.getX() + 70) ||
                     (direction.equals("NS") && y >= trafficLight.getY() - 10) ||
                     (direction.equals("SN") && y <= trafficLight.getY() + 70))) {
                    shouldMove = false;
                } else if (trafficLight.getState() == TrafficLightState.GREEN) {
                    if ((direction.equals("EW") && x >= trafficLight.getX() - 90) ||
                        (direction.equals("WE") && x <= trafficLight.getX() + 40) ||
                        (direction.equals("NS") && y >= trafficLight.getY() - 42) ||
                        (direction.equals("SN") && y <= trafficLight.getY() + 46)) {
                        hasPassedLight = true;
                    }
                }
            }
        }

        // Move car if safe distance is maintained and traffic light allows
        if (shouldMove) {
            switch (direction) {
                case "EW" -> x += speed;
                case "WE" -> x -= speed;
                case "NS" -> y += speed;
                case "SN" -> y -= speed;
            }
        }

        // Update car position
        setLocation(x, y);
    }

    public int getXPosition() {
        return x;
    }

    public int getYPosition() {
        return y;
    }

    public String getDirection() {
        return direction;
    }

    public void startCar() {
        running = true;
    }

    public void stopCar() {
        running = false;
    }

    private int calculateDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private boolean isCarInPath(Car car) {
        switch (direction) {
            case "EW":
                return car.getDirection().equals("EW") && car.getXPosition() > x;
            case "WE":
                return car.getDirection().equals("WE") && car.getXPosition() < x;
            case "NS":
                return car.getDirection().equals("NS") && car.getYPosition() > y;
            case "SN":
                return car.getDirection().equals("SN") && car.getYPosition() < y;
        }
        return false;
    }
}
