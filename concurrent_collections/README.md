# Concurrent Collections

## Overview
Implementation of a thread-safe list (SyncList) demonstrating concurrent data structure design patterns.

## Implementation Details
- **Thread-safety**: Synchronized methods for add, contains, and remove operations
- **Concurrency**: Multiple threads performing simultaneous operations on shared data
- **Consistency**: Proper handling of race conditions through synchronization

## Files
- `SyncList.java`: Thread-safe list implementation with synchronized methods
- `Main.java`: Test harness with concurrent add/contains/remove operations

## How to run
```bash
./gradlew run
```

Or with direct compilation:
```bash
javac src/main/java/app/*.java
java -cp src/main/java app.Main
```

## Learning outcomes
- Designing thread-safe data structures
- Trade-offs between synchronization granularity and performance
- Testing concurrent correctness and data consistency
