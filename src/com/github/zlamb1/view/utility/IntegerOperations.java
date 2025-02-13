package com.github.zlamb1.view.utility;

public final class IntegerOperations implements INumericalOperations<Integer> {
    private static final IntegerOperations instance = new IntegerOperations();

    private IntegerOperations()
    {
    }

    public static IntegerOperations getInstance() {
        return instance;
    }

    @Override
    public Integer increment(Integer a) {
        return a + 1;
    }

    @Override
    public Integer decrement(Integer a) {
        return a - 1;
    }

    @Override
    public Integer parse(String text) throws NumberFormatException {
        return Integer.parseInt(text);
    }
}
