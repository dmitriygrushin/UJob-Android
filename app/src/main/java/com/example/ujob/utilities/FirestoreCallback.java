package com.example.ujob.utilities;

/*  Helps handle async Firebase calls. Code will be cleaner when using async methods. */
public interface FirestoreCallback <E> {
    // void onCallBack(User user);
    void onCallBack(E e);
}
