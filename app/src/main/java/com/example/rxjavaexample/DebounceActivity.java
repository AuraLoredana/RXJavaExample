package com.example.rxjavaexample;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DebounceActivity extends AppCompatActivity {
    private static final String TAG = "DebounceActivity";
    SearchView sv;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    long timeSinceLastRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debounce);

        sv = findViewById(R.id.searchView);
        timeSinceLastRequest = System.currentTimeMillis();

        Observable<String> textObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(newText);
                        }
                        return false;
                    }
                });
            }
        }).debounce(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap(s -> {
            final int delay = new Random().nextInt(2);
            return Observable.just(s).delay(delay, TimeUnit.SECONDS);
        });


        textObservable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: the time since last request " + (System.currentTimeMillis() - timeSinceLastRequest));
                Log.d(TAG, "onNext: search query " + s);
                sendFakeRequests(s);
                timeSinceLastRequest = System.currentTimeMillis();
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: is complete");
            }
        });
    }

    private void sendFakeRequests(String query) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
