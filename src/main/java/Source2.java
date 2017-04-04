import io.reactivex.Observable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by UberV on 4/4/2017.
 */
public class Source2 {
    /* In the next example we consider a feed that pumps every second during the week,
    but to save CPU only pumps every three seconds during the weekend.
    We can use that hybrid “metronome” to produce market data ticks at the desired rate. */

    private static long start = System.currentTimeMillis();


    public static void main(String[] args) {

        // create observables representing tick time
        // TODO then apply filtering to schedule and merge them.
        // will emit an event every second
        Observable<Long> fast = Observable.interval(1, TimeUnit.SECONDS);
        // will emit an event every 3 seconds
        Observable<Long> slow = Observable.interval(3, TimeUnit.SECONDS);

        // Merge 2 observables to create our clock
        Observable<Long> clock = Observable.merge(
                // will emit it's items if it is slow time now
                slow.filter(tick -> isSlowTickTimeDebug()),
                // will emit it's items if it is fast time now
                fast.filter(tick -> !isSlowTickTimeDebug())
        );

        // finally subscribe to print the time
        clock.subscribe(tick -> System.out.println(tick + ", " + new Date()));

        try {
            Thread.sleep(60_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //  ticks fast for 15 seconds and then slow for 15 seconds:
    public static Boolean isSlowTickTimeDebug() {
        return (System.currentTimeMillis() - start) % 30_000 >= 15_000;
    }

    private static boolean isSlowTickTime() {
        return LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY ||
                LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}
