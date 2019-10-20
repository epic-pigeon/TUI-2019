package Problem_4;

import com.sun.org.apache.xalan.internal.xslt.Process;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TLogParser {
    public static class TLogEntry {
        private String name;
        private double value;

        @Override
        public String toString() {
            return name + ", " + value;
        }
    }

    public static class TLogPoint {
        private long unixTime;
        private double latitude, longitude, yaw, altitude;

        public long getUnixTime() {
            return unixTime;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getYaw() {
            return yaw;
        }

        public double getAltitude() {
            return altitude;
        }

        @Override
        public String toString() {
            return "TLogPoint{" +
                    "unixTime=" + unixTime +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", yaw=" + yaw +
                    ", altitude=" + altitude +
                    '}';
        }
    }

    public static class TLogFile {
        private List<TLogEntry> file;
        private transient List<TLogPoint> points;

        public TLogFile(List<TLogEntry> file) {
            this.file = file;
        }

        public static TLogFile from(File file) throws IOException {
            return new TLogFile(parse(file));
        }

        public List<TLogPoint> getPoints() {
            if (points == null) calculatePoints();
            return points;
        }

        private void calculatePoints() {
            points = new TLogPointsCalculator(file).calculate();
        }
    }

    private static class TLogPointsCalculator {
        private static String MAVLINK_UNIX_TIME = "time_unix_usec_._mavlink_system_time_t";
        private static String MAVLINK_LATITUDE = "lat_._mavlink_global_position_int_t";
        private static String MAVLINK_LONGITUDE = "lon_._mavlink_global_position_int_t";
        private static String MAVLINK_ALTITUDE = "alt_._mavlink_global_position_int_t";
        private static String MAVLINK_YAW = "yaw_._mavlink_attitude_t";

        private int i;
        private List<TLogEntry> entries;

        private TLogPointsCalculator(List<TLogEntry> entries) {
            this.entries = entries;
            i = 0;
        }

        private TLogEntry getNext() {
            return i < entries.size() ? entries.get(i++) : null;
        }

        private boolean hasNext() {
            return i < entries.size();
        }

        private TLogEntry peekNext() {
            return i < entries.size() ? entries.get(i) : null;
        }

        public List<TLogPoint> calculate() {
            List<TLogPoint> points = new ArrayList<>();
            TLogEntry entry;
            while ((entry = getNext()) != null) {
                if (entry.name.equals(MAVLINK_UNIX_TIME)) {
                    TLogPoint point = new TLogPoint();
                    point.unixTime = (int) entry.value;
                    while (hasNext() && !peekNext().name.equals(MAVLINK_UNIX_TIME)) {
                        entry = getNext();
                        if (entry.name.equals(MAVLINK_ALTITUDE)) {
                            point.altitude = entry.value / 1_000;
                        } else if (entry.name.equals(MAVLINK_LATITUDE)) {
                            point.latitude = entry.value / 10_000_000;
                        } else if (entry.name.equals(MAVLINK_LONGITUDE)) {
                            point.longitude = entry.value / 10_000_000;
                        } else if (entry.name.equals(MAVLINK_YAW)) {
                            point.yaw = entry.value;
                        }
                    }
                    if (point.unixTime != 0 && point.altitude != 0 && point.latitude != 0 && point.longitude != 0 && point.yaw != 0) {
                        points.add(point);
                    }
                }
            }
            return points;
        }
    }

    private static List<TLogEntry> parse(File file) throws IOException {
        List<TLogEntry> result = new ArrayList<>();
        try (BufferedReader fstream = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = fstream.readLine()) != null) {
                //System.out.println(line);
                TLogEntry entry = new TLogEntry();
                entry.name = line.substring(0, line.indexOf(','));
                entry.value = Double.parseDouble(line.substring(line.indexOf(',') + 2));
                result.add(entry);
            }
        }
        return result;
    }

    private static List<TLogPoint> parseFile(File file) throws IOException {
        return TLogFile.from(file).getPoints();
    }

    private static File generateTxt(File tlog, String filename) throws IOException, InterruptedException {
        File result = new File("./" + filename);

        new ProcessBuilder("./lib/TLogReaderV5.exe", tlog.getAbsolutePath(), result.getAbsolutePath()).inheritIO().start().waitFor();

        return result;
    }

    public static List<TLogPoint> parseTLog(File file) throws IOException, InterruptedException {
        return parseFile(generateTxt(file, "_" + new Date().getTime()));
    }
}
