package com.github.zlamb1.view.utility;

public interface INumericalOperations<T extends Number> {
    T increment(T a);
    T decrement(T a);
    T parse(String text) throws NumberFormatException;
}
