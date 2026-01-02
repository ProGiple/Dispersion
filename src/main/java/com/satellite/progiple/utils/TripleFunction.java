package com.satellite.progiple.utils;

@FunctionalInterface
public interface TripleFunction<A, B, C, O> {
    O apply(A a, B b, C c);
}
