/**
 *  Java program to use Java 21 Foreign Function.
 */

package com.myforeignfunction;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.util.NoSuchElementException;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

/**
 *  Main class.
 */
public class Main {

    // JVM entry point.
    public static void main(String[] args) throws Throwable {

        try(Arena arena = Arena.ofConfined()) {

            // Linker to use native methods.
            final var linker = Linker.nativeLinker();

            // Setting linker to default lookup.
            final var symbolLookup = linker.defaultLookup();

            // Memory segment we are going to use.
            final var memorySegment = symbolLookup.find("atoi").orElseThrow();

            // Variable with method we are looking for with return type long..
            final var functionDescriptor = FunctionDescriptor.of(JAVA_LONG, ADDRESS);

            // Calling method to memorySegment address.
            final var methodHandler = linker.downcallHandle(memorySegment, functionDescriptor);

            // String "25" is allocated outside the HEAP memory.
            final var segmentAllocator = arena.allocateFrom("25");

            // Getting the result.
            final var result = (long) methodHandler.invokeExact(segmentAllocator);

            // Printing the result and its type.
            System.out.println("Object value " + result); // Output: Object value 25
            System.out.println("Object type " + ((Object)result).getClass().getName());
                                                                    // Output: Object type java.lang.Long

        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
        }

    }
}