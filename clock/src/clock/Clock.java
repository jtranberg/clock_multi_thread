package clock;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.*;

/**
 * The Clock class is responsible for setting up the GUI and starting the
 * threads that update and display the current time and date for multiple time zones.
 * Using 5 time zones. Uses Runnable Interface for thread control.
 */
// clock class
public class Clock {
    private static JFrame frame;
    private static JLabel[] timeLabels;
    private static final String[] timeZones = { "Asia/Tokyo", "Europe/Paris", "Europe/London", "Africa/Johannesburg", "America/Vancouver" };
    private static final String[] destinations = { "Tokyo", "Paris", "London", "Johannesburg", "Vancouver" };
    private static volatile boolean running = true;
    private static Thread[] updaterThreads;
    private static Thread[] displayerThreads;
/**
 * calling the clock function
 */
    public Clock() {
        // General initialization
    }
/**
 * main
 * @param args main program
 * starts the main program
 */
    public static void main(String[] args) {
        // The GUI setup
        newGUI();
        updaterThreads = new Thread[timeZones.length];
        displayerThreads = new Thread[timeZones.length];
/**
 * set priorities for threads.
 * iterate for multi threads
 */
        // Create and start time update threads for each time zone
        for (int i = 0; i < timeZones.length; i++) {
            updaterThreads[i] = new Thread(new TimeUpdater(), "TimeUpdater-" + destinations[i]);
            updaterThreads[i].setPriority(Thread.MIN_PRIORITY);
            updaterThreads[i].start();

            displayerThreads[i] = new Thread(new TimeDisplayer(i, timeZones[i]), "TimeDisplayer-" + destinations[i]);
            displayerThreads[i].setPriority(Thread.MAX_PRIORITY);
            displayerThreads[i].start();
        }
/**
 * adding a shutdown hook to close threads properly 
 */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            try {
                for (Thread thread : updaterThreads) {
                    if (thread != null) {
                        thread.join();
                    }
                }
                for (Thread thread : displayerThreads) {
                    if (thread != null) {
                        thread.join();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }
/**
 * create JFrame viewer so the clock will be shown externally
 * This also gives us options to style the clock
 */
    //Gui for externat viewing of clock
    private static void newGUI() {
        frame = new JFrame("Time Zones Around The World");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new GridLayout(timeZones.length + 1, 1));
        frame.getContentPane().setBackground(Color.BLACK);

        timeLabels = new JLabel[timeZones.length];
        for (int i = 0; i < timeZones.length; i++) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel destinationLabel = new JLabel(destinations[i] + ": ");
            destinationLabel.setFont(new Font("Arial", Font.BOLD, 20));
            panel.add(destinationLabel, BorderLayout.WEST);
            panel.setBackground(Color.CYAN);
            timeLabels[i] = new JLabel("Loading...", SwingConstants.CENTER);
            timeLabels[i].setFont(new Font("Arial", Font.BOLD, 20));
            panel.add(timeLabels[i], BorderLayout.CENTER);

            frame.add(panel);
        }
/**
 * toggle for clock to hide the clock . turn them on and off
 */
        //jframe toggle button
        JButton toggleButton = new JButton("Turn Off Clocks");
        toggleButton.addActionListener(e -> {
            running = !running;
            for (JLabel timeLabel : timeLabels) {
                timeLabel.setVisible(running);
            }
            toggleButton.setText(running ? "Turn Off Clocks" : "Turn On Clocks");
        });
        frame.add(toggleButton);

        frame.setVisible(true);
    }

    static class TimeUpdater implements Runnable {
        private Date currentTime;

        @Override
        public void run() {
            while (running) {
                currentTime = new Date();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public Date getCurrentTime() {
            return currentTime;
        }
    }
/**
 * Implementin runnable as to use
 * and interface for running multiple threads
 */
    // runnable interface for multi thread
    static class TimeDisplayer implements Runnable {
        private final int index;
        private final String timeZone;

        public TimeDisplayer(int index, String timeZone) {
            this.index = index;
            this.timeZone = timeZone;
        }
/**
 * set the format of the viewer and add
 * a sleep timer at 1 second interval
 */
        //override to set format and sleep timer
        @Override
        public void run() {
            while (running) {
                Date currentTime = new TimeUpdater().getCurrentTime();
                if (currentTime != null && running) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                    formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
                    String formattedTime = formatter.format(currentTime);
                    SwingUtilities.invokeLater(() -> timeLabels[index].setText(formattedTime));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

