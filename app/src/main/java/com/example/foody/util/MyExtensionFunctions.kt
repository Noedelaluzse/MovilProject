package com.example.foody.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}



fun <T> LiveData<T>.observeOnceAfterInit(owner: LifecycleOwner, observer: (T) -> Unit) {
    var firstObservation = true

    observe(owner, object: Observer<T>
    {
        override fun onChanged(value: T) {
            if(firstObservation)
            {
                firstObservation = false
            }
            else
            {
                removeObserver(this)
                observer(value)
            }
        }
    })
}
