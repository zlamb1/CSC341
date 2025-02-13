package com.github.zlamb1.view.listener;

public interface IValueListener<T> {
    void onValueChange(T oldValue, T newValue);
}
