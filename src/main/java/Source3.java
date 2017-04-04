import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

/**
 * Created by UberV on 4/4/2017.
 */
public class Source3 {

    public static void main(String[] args) {
        Observable cold = Observable.interval(1, TimeUnit.SECONDS);

        // Convert Cold observable to Hot
        ConnectableObservable hot= cold.publish();

        System.out.println("Subscriber 1 connected...");
        Disposable s1 = hot.subscribe(tick->System.out.println("Subscriber 1: "+tick));

        System.out.println("Starting emission...");
        // begin emitting items
        hot.connect();

        delay(5000);
        // subscribe another consumer
        System.out.println("Subscriber 2 connected...");
        hot.subscribe(tick->System.out.println("Subscriber 2: "+tick));

        delay(5000);

        // remove subscriber to add it later
        System.out.println("Disconnecting Subscriber 1...");
        s1.dispose();

        delay(5000);

        System.out.println("Reconnecting Subscriber 1...");
        s1 = hot.subscribe(tick->System.out.println("Subscriber 1: "+tick));

        delay(100500);
    }

    public static void delay(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
