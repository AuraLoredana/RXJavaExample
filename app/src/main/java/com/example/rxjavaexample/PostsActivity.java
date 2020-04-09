package com.example.rxjavaexample;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rxjavaexample.models.Post;
import com.example.rxjavaexample.networking.RetrofitService;

import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PostsActivity extends AppCompatActivity {

    private static final String TAG = "PostsActivity";

    //ui
    private RecyclerView recyclerView;

    // vars
    private CompositeDisposable disposables = new CompositeDisposable();
    private RecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        recyclerView = findViewById(R.id.recycler_view);

        initRecyclerView();

        getPostsObservable()
                .subscribeOn(Schedulers.io())
                .concatMap((Function<Post, ObservableSource<Post>>) this::getCommentsObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Post>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(Post post) {
                updatePost(post);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError:" + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: isComplete");
            }
        });
    }

    private Observable<Post> getPostsObservable() {
        return RetrofitService.getRequestApi()
                .getPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<List<Post>, ObservableSource<Post>>)
                        posts -> Observable.fromIterable(posts)
                                .subscribeOn(Schedulers.io()));

    }

    private void updatePost(Post post) {
        adapter.updatePost(post);
    }

    private Observable<Post> getCommentsObservable(final Post post) {
        return RetrofitService.getRequestApi().getComments(post.getId()).map(comments -> {
            int delay = ((new Random()).nextInt(5) + 1) * 1000;
            Thread.sleep(delay);
            Log.d(TAG, "sleep" + Thread.currentThread().getName() + delay);
            post.setComments(comments);
            return post;
        }).subscribeOn(Schedulers.io());

    }

    private void initRecyclerView() {
        adapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}