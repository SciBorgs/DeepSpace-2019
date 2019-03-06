package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Starts the <code>chezy_lidar</code> C++ program, parses its
 * output, and feeds the LIDAR points to the {@link LidarProcessor}.
 * <p>
 * Once started, a separate thread reads the stdout of the
 * <code>chezy_lidar</code> process and parses the (angle, distance)
 * values in each line. Each resulting {@link LidarPoint} is passed
 * to {@link LidarProcessor.addPoint(...)}.
 */
public class LidarServer {
    private static LidarServer mInstance = null;
    private static BufferedReader mBufferedReader;
    private boolean mRunning = false;
    private Thread mThread;
    private Process mProcess;
    private boolean mEnding = false;
    public Hashtable<Double,Double> lidarScan = new Hashtable<Double,Double>();
    private Hashtable<Double,Integer> lidarZeros = new Hashtable<Double,Integer>();
    private String lidarPath = "/home/root/ultra_simple";
    private double minimumZeros = 5;

    public static LidarServer getInstance() {
        if (mInstance == null) {
            mInstance = new LidarServer();
        }
        return mInstance;
    }

    public boolean isLidarConnected() {
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec("/bin/ls /dev/serial/by-id/");
            InputStreamReader reader = new InputStreamReader(p.getInputStream());
            BufferedReader response = new BufferedReader(reader);
            String s;
            while ((s = response.readLine()) != null) {
                if (s.equals("usb-Silicon_Labs_CP2102_USB_to_UART_Bridge_Controller_0001-if00-port0"))
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean start() {
        if (!isLidarConnected()) {
            System.err.println("Cannot start LidarServer: not connected");
            return false;
        }
        synchronized (this) {
            if (mRunning) {
                System.err.println("Cannot start LidarServer: already running");
                return false;
            }
            if (mEnding) {
                System.err.println("Cannot start LidarServer: thread ending");
                return false;
            }
            mRunning = true;
        }

        System.out.println("Starting lidar");
        try {
            mProcess = new ProcessBuilder().command(lidarPath).start();
            mThread = new Thread(new ReaderThread());
            mThread.start();
            InputStreamReader reader = new InputStreamReader(mProcess.getInputStream());
            mBufferedReader = new BufferedReader(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean stop() {
        synchronized (this) {
            if (!mRunning) {
                System.err.println("Cannot stop LidarServer: not running");
                return false;
            }
            mRunning = false;
            mEnding = true;
        }

        System.out.println("Stopping Lidar...");

        try {
            mProcess.destroyForcibly();
            mProcess.waitFor();
            mThread.join();
        } catch (InterruptedException e) {
            System.err.println("Error: Interrupted while stopping lidar");
            e.printStackTrace();
            synchronized (this) {
                mEnding = false;
            }
            return false;
        }
        System.out.println("Lidar Stopped");
        synchronized (this) {
            mEnding = false;
        }
        return true;
    }

    public synchronized boolean isRunning() {return mRunning;}
    public synchronized boolean isEnding()  {return mEnding;}


    private void handleLine(String line) {
        boolean isNewScan = line.substring(line.length() - 1).equals("s");
        if (isNewScan) {
            line = line.substring(0, line.length() - 1);
        }

        long curSystemTime = System.currentTimeMillis();
        double curFPGATime = Timer.getFPGATimestamp();

        String[] parts = line.split(",");
        if (parts.length == 3) {
            try {
                long ts = Long.parseLong(parts[0]);
                long ms_ago = curSystemTime - ts;
                double angle = ((int) Double.parseDouble(parts[1])) % 360;
                double distance = Double.parseDouble(parts[2]);

                if (!(lidarZeros.containsKey(angle))){lidarZeros.put(angle,0);}

                if (distance == 0 && !(lidarZeros.get(angle) >= minimumZeros))
                   lidarZeros.put(angle,lidarZeros.get(angle) + 1);
                else
                    lidarScan.put(angle,distance);
                if (distance != 0)
                    lidarZeros.put(angle,0);
            } catch (java.lang.NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (isRunning()) {
                try {
                    if (mBufferedReader.ready()) {
                        String line = mBufferedReader.readLine();
                        if (line == null) { // EOF
                            throw new EOFException("End of chezy-lidar process InputStream");
                        }
                        handleLine(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
