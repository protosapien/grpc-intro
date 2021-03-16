package com.datasciencegroup.grpc.two.server.rpctypes;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccountDatabase {

    /*

    This the DB
    1 => 10
    2 => 20
    3 => 30
    ...
    10 => 100

     */

    private static final Map<Integer, Integer> MAP = IntStream
            .range(1, 11) // this is simple for-loop
            .boxed()
            .collect(Collectors.toMap(
                    Function.identity(), v -> 100)
            );

    public static int getBalance(int accountId) {
        return MAP.get(accountId);
    }

    public static Integer addBalance(int accountId, int amount) {
        return MAP.computeIfPresent(accountId, (k, v) -> v + amount);
    }

    public static Integer deductBalance(int accountId, int amount) {
        return MAP.computeIfPresent(accountId, (k, v) -> v - amount);
    }

    public static void printAccountDetails() {
        // print out the details of the database
        System.out.println(
                "\n\t\t" + MAP
        );
    }
}
