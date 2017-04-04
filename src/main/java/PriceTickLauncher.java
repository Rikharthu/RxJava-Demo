import io.reactivex.Flowable;

/**
 * Created by UberV on 4/4/2017.
 */
public class PriceTickLauncher {
    /*
    public static void main(String[] args) {
        SomeFeed<PriceTick> feed = new SomeFeed<>();
        Flowable<PriceTick> flowable = Flowable.create(emitter ->
        {
            SomeListener listener = new SomeListener() {
                @Override
                public void priceTick(PriceTick event) {
                    emitter.onNext(event);
                    if (event.isLast()) {
                        emitter.onComplete();
                    }
                }

                @Override
                public void error(Throwable e) {
                    emitter.onError(e);
                }
            };
            feed.register(listener);
        }, BackpressureStrategy.BUFFER);

        ConnectableFlowable<PriceTick> hotObservable = flowable.publish();
        hotObservable.connect();

        hotObservable.take(10).subscribe((priceTick) ->
                System.out.printf("1 %s %4s %6.2f%n", priceTick.getDate(),
                        priceTick.getInstrument(), priceTick.getPrice()));

        sleep(1_000);

        hotObservable.take(10).subscribe((priceTick) ->
                System.out.printf("2 %s %4s %6.2f%n", priceTick.getDate(),
                        priceTick.getInstrument(), priceTick.getPrice()));
    }
    */
}