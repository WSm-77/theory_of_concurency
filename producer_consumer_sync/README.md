# Producer-Consumer Problem with Synchronized Buffer

## Overview
Implementation of the classic Producer-Consumer synchronization problem using Java's `synchronized` keyword and condition variables (`wait()`/`notifyAll()`).

## Key Concepts
- **Synchronization**: Using `synchronized` blocks for mutual exclusion
- **Condition variables**: `wait()` and `notifyAll()` for coordinating threads
- **Buffer**: Fixed-size bounded buffer for thread-safe data exchange

## Files
- `CondWaitEx.java`: Main## How to run
 implementation with Producer, Consumer, UniqId and Buffer classes

## How to run
```bash
javac src/main/java/app/CondWaitEx.java
java -cp src/main/java app.CondWaitEx
```

## Learning outcomes
- Understanding producer-consumer synchronization patterns
- Using `synchronized` for critical sections
- Proper use of `wait()` and `notifyAll()` for thread coordination
