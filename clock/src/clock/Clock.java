package clock;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * The Clock class is responsible for setting up the GUI and starting the threads
 * Multiple threads showing priority setting.
 * that update and display the current time and date for multiple time zones.
 * Using 5 time zones.
 * git hub repo :    https://github.com/jtranberg/clock_multi_thread/tree/main/clock
 */
// Java clock with multi threads showing priority 
//showing  5 time zones.
//git hub repo...  https://github.com/jtranberg/clock_multi_thread/tree/main/clock
public class Clock {
    private static JFrame frame;
    private static JLabel[] timeLabels;
    private static String[] timeZones = {"Asia/Tokyo", "Europe/Paris", "Europe/London", "Africa/Johannesburg", "America/Vancouver"};
    private static String[] destinations = {"Tokyo", "Paris", "London", "Johannesburg", "Vancouver"};
    private static boolean running = true;

    /**
     * Default constructor for the Clock class.
     */
    public Clock() {
        // General initialization
    }

    /**
     * The main method to start the Clock application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //  The GUI setup
        newGUI();
/**
 * Multi threading using the for loop iteration.
 */
        // Create and start time update threads for each time zone
        //current Thread (1) MIN_PRIORITY
        /**
         * Current Time Thread
         * set to Min_Priority
         */
        for (int i = 0; i < timeZones.length; i++) {
            Thread currentTimeThread = new Thread(new TimeUpdater(timeZones[i]));
            currentTimeThread.setPriority(Thread.MIN_PRIORITY);
            currentTimeThread.start();
            /**
             * Display Thread Max_Priority
             */
            //Display Thread (2) MAX_PRIORITY
            Thread displayOutputThread = new Thread(new TimeDisplayer(i, timeZones[i]));
            displayOutputThread.setPriority(Thread.MAX_PRIORITY);
            displayOutputThread.start();
        }
    }

    /**
     * Sets up the GUI with a JFrame, JLabel, and JButton.
     * Add JFrame description here.
     */
    //JFrame GUI creation with styling
    private static void newGUI() {
        frame = new JFrame("Time Zones Around The World");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new GridLayout(timeZones.length + 1, 1));
        frame.getContentPane().setBackground(Color.BLACK);

        timeLabels = new JLabel[timeZones.length];
/**
 * Iterating for loop to populate display and show multi threads.
 * Add styling here for viewer
 */
        //for loop iterates to populate viewer
        //Add styling here
        for (int i = 0; i < timeZones.length; i++) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel destinationLabel = new JLabel(destinations[i] + ": ");
            destinationLabel.setFont(new Font("Arial", Font.BOLD, 20));
            panel.add(destinationLabel, BorderLayout.WEST);
            panel.setBackground(Color.CYAN); // Set the background color of the JPanel 
            timeLabels[i] = new JLabel("Loading...", SwingConstants.CENTER);
            timeLabels[i].setFont(new Font("Arial", Font.BOLD, 20));
            panel.add(timeLabels[i], BorderLayout.CENTER);

            frame.add(panel);
        }

        /**
         * Toggle Button to hide clocks without closing the program
         */
        //Hide active clocks , program still running 
        JButton toggleButton = new JButton("Turn Off Clocks");
        toggleButton.addActionListener(e -> {
            running = !running;
            for (JLabel timeLabel : timeLabels) {
                timeLabel.setVisible(running);
               
            }
        });
        frame.add(toggleButton);

        frame.setVisible(true);
    }

    /**
     * The TimeUpdater class is responsible for updating the current time for a specific time zone.
     * Implementing Runnable for loop iteration performance
     */
    //Implementing Runnable for this program, better efficiency for loop iteration.
    static class TimeUpdater implements Runnable {
        private static volatile Date currentTime;
        private String timeZone;

        /**
         * Constructor for the TimeUpdater class.
         * @param timeZone the time zone for which to update the time
         */
        //constructor
        public TimeUpdater(String timeZone) {
            this.timeZone = timeZone;
        }

        /**
         * The run method updates the current time every second.
         */
        //@override method
        @Override
        public void run() {
            while (true) {
                // Update the current time
                currentTime = new Date();
                try {
                    // Sleep for 1 second before updating again
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Gets the current time.
         * @return the current time
         */
        //calls current time details
        public static Date getCurrentTime() {
            return currentTime;
        }
    }

    /**
     * The TimeDisplayer class is responsible for displaying the current time for a specific time zone.
     */
    //Display implements runnable
    //constructor
    static class TimeDisplayer implements Runnable {
        private int index;
        private String timeZone;

        /**
         * Constructor for the TimeDisplayer class.
         * @param index the index of the time label to update
         * @param timeZone the time zone for which to display the time
         */
        public TimeDisplayer(int index, String timeZone) {
            this.index = index;
            this.timeZone = timeZone;
        }

        /**
         * The run method displays the current time every second.
         */
        //runmethod set to deisplay every second (1000)
        @Override
        public void run() {
            while (true) {
                // Current time
                Date currentTime = TimeUpdater.getCurrentTime();
                if (currentTime != null && running) {
                    // Format and display the current time and date for the specific time zone
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                    formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
                    String formattedTime = formatter.format(currentTime);
                    SwingUtilities.invokeLater(() -> timeLabels[index].setText(formattedTime));
                }
                try {
                    // Sleep for 1 second before displaying again
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } 
    }
}

