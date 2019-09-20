import java.util.Calendar;
import java.util.Date;

/**
 * コンストラクタでタイムスパン（時間の間隔）を指定します。
 * タイムスパンを ミリ秒、秒、分、時間、日数で取得することができます。
 * @author inapapapa
 */
public class TimeSpan {
    private long totalMilliseconds;
    private int milliseconds;
    
    private long totalSeconds;
    private int seconds;
    
    private long totalMinutes;
    private int minutes;

    private long totalHours;
    private int hours;
    
    private long totalDays;
    
    public TimeSpan(long totalMilliseconds) {
        
        //メモ Fri Jun 27 20:51:21 JST 2014 = 1403869881668
        // Integer.MAX_VALUE 2147483647
        
        this.totalMilliseconds = totalMilliseconds;
        this.totalSeconds = this.totalMilliseconds / 1000;
        this.totalMinutes = this.totalSeconds / 60;
        this.totalHours = this.totalMinutes / 60;
        this.totalDays = this.totalHours / 24;
        
        this.milliseconds = (int) (this.totalMilliseconds - (this.totalSeconds * 1000));
        this.seconds = (int) (this.totalSeconds - (this.totalMinutes * 60));
        this.minutes = (int) (this.totalMinutes - (this.totalHours * 60));
        this.hours = (int) (this.totalHours - (this.totalDays * 24));
    }
    public long getTotalMilliseconds() {
        return totalMilliseconds;
    }
    public int getMilliseconds() {
        return milliseconds;
    }
    public long getTotalSeconds() {
        return totalSeconds;
    }
    public int getSeconds() {
        return seconds;
    }
    public long getTotalMinutes() {
        return totalMinutes;
    }
    public int getMinutes() {
        return minutes;
    }
    public long getTotalHours() {
        return totalHours;
    }
    public int getHours() {
        return hours;
    }
    public long getTotalDays() {
        return totalDays;
    }
}
