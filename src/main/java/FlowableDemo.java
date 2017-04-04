import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.flowables.ConnectableFlowable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by UberV on 4/4/2017.
 */
public class FlowableDemo {


    public static void main(String[] args) {

        ContactsBook book = new ContactsBook();

        // Create a COLD flowable
        Flowable<Person> flowable = Flowable.create(emitter -> {
            MyListener<Person> personListener = new MyListener<Person>() {
                @Override
                public void onDataReady(Person data) {
                    System.out.println("Emitting: " + data);
                    emitter.onNext(data);
                }

                @Override
                public void onDataComplete() {
                    emitter.onComplete();
                }

                @Override
                public void onDataError(String errorMessage) {
                    emitter.onError(new Exception(errorMessage));
                }
            };

            book.register(personListener);
        }, BackpressureStrategy.DROP);
        // - BackpressureMode.MISSING to apply no backpressure. If the stream can’t keep up,
        //   may throw a MissingBackpressureException or IllegalStateException.
        // - BackpressureStrategy.ERROR emits a MissingBackpressureException if the downstream can't keep up.
        // - BackpressureStrategy.DROP Drops the incoming onNext value if the downstream can't keep up.
        // - BackpressureStrategy.LATEST Keeps the latest onNext value and overwrites it with newer
        //   ones until the downstream can consume it.

        book.addPerson(new Person("Vasja", "Pupkin", 17));
        book.addPerson(new Person("Vasja", "Pupkin", 18));
        flowable.subscribe(person -> {
            System.out.println(person.toString());
            // simulate some delay, e.g: user data processing
            delay(3000);
        });

//        book.addPerson(new Person("Vasja", "Pupkin", 19));
//        book.addPerson(new Person("Vasja", "Pupkin", 20));
//        book.addPerson(new Person("Vasja", "Pupkin", 21));
//        book.addPerson(new Person("Vasja", "Pupkin", 22));

        flowable.subscribe(person -> System.out.println(person.toString()));

        // Convert to HOT flowable
        ConnectableFlowable<Person> hotFlowable = flowable.publish();
        hotFlowable.connect();
        book.addPerson(new Person("Vasja", "Pupkin", 23));
        book.addPerson(new Person("Vasja", "Pupkin", 24));
        book.addPerson(new Person("Vasja", "Pupkin", 25));
        book.addPerson(new Person("Vasja", "Pupkin", 26));
        for (int i = 0; i < 500; i++) {
            book.addPerson(new Person("Vasja", "Pupkin", 30 + i));
        }
    }

    public static void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}


interface MyListener<T> {
    void onDataReady(T data);

    void onDataComplete();

    void onDataError(String errorMessage);
}

class Person {
    private String firstname;
    private String lastname;
    private int age;

    public Person() {
    }

    public Person(String firstname, String lastname, int age) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", age=" + age +
                '}';
    }
}

class ContactsBook {

    private List<Person> people = new ArrayList<>();
    private List<MyListener<Person>> listeners = new ArrayList<>();

    public void addPerson(Person p) {
        for (MyListener<Person> listener : listeners) {
            listener.onDataReady(p);
        }
        people.add(p);
    }

    public void register(MyListener<Person> listener) {
        listeners.add(listener);
    }

}
