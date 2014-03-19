package com.eventshop.eventshoplinux.test;

import java.util.ArrayList;
import java.util.List;

public class PerformanceTest {
  private static final long MEGABYTE = 1024L * 1024L;

  public static long bytesToMegabytes(long bytes) {
    return bytes / MEGABYTE;
  }

  public static void main(String[] args) {
    // I assume you will know how to create a object Person yourself...
    List<Person> list = new ArrayList<Person>();
    for (int i = 0; i <= 100000; i++) {
      list.add(new Person("Jim", "Knopf"));
    }
    // Get the Java runtime
    Runtime runtime = Runtime.getRuntime();
    // Run the garbage collector
    runtime.gc();
    // Calculate the used memory
    long memory = runtime.totalMemory() - runtime.freeMemory();
    System.out.println("Used memory is bytes: " + memory);
    System.out.println("Used memory is megabytes: "
        + bytesToMegabytes(memory));
    
    long startTime = System.currentTimeMillis();

    long total = 0;
    for (int i = 0; i < 10000000; i++) {
      total += i;
    }

    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    System.out.println(elapsedTime);
  }
  
  private static class Person{
	  String fname;
	  String lname;
	  public Person(String fname, String lname){
		  
		  this.fname = fname;
		  this.lname = lname;
	  }
}
}

