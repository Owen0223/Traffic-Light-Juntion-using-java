import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrafficLightJunction extends JFrame implements ActionListener {
    private final TrafficLight lightUL, lightUR, lightLL, lightLR;
    private final JButton startButton, stopButton;
    private ScheduledExecutorService scheduler;
    private int greenDelay = 5000;
    private int yellowDelay = 2000;
    private int redDelay = 1000;
    private final List<Car> cars;
    private Thread carGeneratorThread;
    private volatile boolean generatingCars;

    public TrafficLightJunction() {
        super("Traffic Light Junction Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLayout(null); // Use null layout for absolute positioning

        BackgroundPanel backgroundPanel = new BackgroundPanel("C:/Users/owenk/Downloads/Background.jpg");
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(null);

        lightUL = new TrafficLight();
        lightUR = new TrafficLight();
        lightLL = new TrafficLight();
        lightLR = new TrafficLight();

        // Set bounds for each traffic light (x, y, width, height)
        lightUL.setBounds(625, 240, 40, 100);
        lightUR.setBounds(840, 240, 40, 100);
        lightLL.setBounds(625, 520, 40, 100);
        lightLR.setBounds(840, 520, 40, 100);

        add(lightUL);
        add(lightUR);
        add(lightLL);
        add(lightLR);

        lightUL.setState(TrafficLightState.GREEN);
        lightUR.setState(TrafficLightState.RED);
        lightLL.setState(TrafficLightState.RED);
        lightLR.setState(TrafficLightState.GREEN);

        JPanel controlPanel = new JPanel();
        controlPanel.setBounds(1100, 650, 140, 40); // Position the control panel at the bottom
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        backgroundPanel.add(controlPanel);

        // Initialize cars
        cars = new CopyOnWriteArrayList<>();

        setVisible(true);
    }

    private void createAndStartCar(int startX, int startY, String direction, TrafficLight trafficLight) {
        Car car = new Car(startX, startY, 5, trafficLight, direction, cars);
        cars.add(car);
        add(car);
        Thread carThread = new Thread(car);
        carThread.start();
    }

    private void startGeneratingCars() {
        generatingCars = true;
        carGeneratorThread = new Thread(() -> {
            Random rand = new Random();
            while (generatingCars) {
                try {
                    Thread.sleep(2000); // Adjust interval for car generation as needed
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                int direction = rand.nextInt(4);
                switch (direction) {
                    case 0 -> createAndStartCar(-150, 385, "EW", lightUL); // From west to east
                    case 1 -> createAndStartCar(1550, 460, "WE", lightLR); // From east to west
                    case 2 -> createAndStartCar(785, 0, "NS", lightUR); // From north to south
                    case 3 -> createAndStartCar(705, 750, "SN", lightLL); // From south to north
                }
            }
        });
        carGeneratorThread.start();
    }

    private void stopGeneratingCars() {
        generatingCars = false;
        if (carGeneratorThread != null) {
            carGeneratorThread.interrupt();
        }
    }

    private void startTrafficLights() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(this::changeToGreen, 0, TimeUnit.MILLISECONDS);
    }

    private void stopTrafficLights() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
    
    private void changeToGreen() {
        setTrafficLightsState(TrafficLightState.GREEN, TrafficLightState.RED, TrafficLightState.RED, TrafficLightState.GREEN);
        scheduler.schedule(this::changeToYellowFromGreen, greenDelay, TimeUnit.MILLISECONDS);
    }

    private void changeToYellowFromGreen() {
        setTrafficLightsState(TrafficLightState.YELLOW, TrafficLightState.RED, TrafficLightState.RED, TrafficLightState.YELLOW);
        scheduler.schedule(this::changeToRedFromYellow1, yellowDelay, TimeUnit.MILLISECONDS);
    }

    private void changeToRedFromYellow1() {
        setTrafficLightsState(TrafficLightState.RED, TrafficLightState.GREEN, TrafficLightState.GREEN, TrafficLightState.RED);
        scheduler.schedule(this::changeToYellowFromGreen2, greenDelay, TimeUnit.MILLISECONDS);
    }

    private void changeToYellowFromGreen2() {
        setTrafficLightsState(TrafficLightState.RED, TrafficLightState.YELLOW, TrafficLightState.YELLOW, TrafficLightState.RED);
        scheduler.schedule(this::changeToRedFromYellow2, yellowDelay, TimeUnit.MILLISECONDS);
    }
    
        private void changeToRedFromYellow2() {
        setTrafficLightsState(TrafficLightState.GREEN, TrafficLightState.RED, TrafficLightState.RED, TrafficLightState.GREEN);
        scheduler.schedule(this::changeToYellowFromGreen, greenDelay, TimeUnit.MILLISECONDS);
    }

    private void setTrafficLightsState(TrafficLightState stateUL, TrafficLightState stateUR, TrafficLightState stateLL, TrafficLightState stateLR) {
        lightUL.setState(stateUL);
        lightUR.setState(stateUR);
        lightLL.setState(stateLL);
        lightLR.setState(stateLR);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            startGeneratingCars();
            startTrafficLights();
        } else if (e.getSource() == stopButton) {
            stopGeneratingCars();
            stopTrafficLights();
        }
    }
}