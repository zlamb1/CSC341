package com.github.zlamb1.view.listener;

public interface INumericListener<T extends Number> extends IValueListener<T> {
    void onValueChange(T oldValue, T newValue);
}
