package com.example.rxjavaexample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.rxbinding3.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BufferActivity extends AppCompatActivity {
    private static final String TAG = "BufferActivity";
    Button button;
    CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer);

        button = findViewById(R.id.button);
        RxView.clicks(button)
                .map(unit -> 1)
                .buffer(4, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(List<Integer> integers) {
                Log.d(TAG, "onNext:" + "you clicked:" + integers.size() + " times ");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "isComplete");
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
