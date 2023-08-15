package com.sollace.iaf;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class DistinctStack<T> {

    private final Deque<T> stack = new ArrayDeque<>();
    private final Set<T> set = new HashSet<>();

    public void push(@Nullable T t) {
        if (t != null && set.add(t)) {
            stack.add(t);
        }
    }

    @Nullable
    public T poll() {
        return stack.poll();
    }

    public boolean hasSeen(T t) {
        return set.contains(t);
    }
}
