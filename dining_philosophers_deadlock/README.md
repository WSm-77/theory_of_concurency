# Dining Philosophers Problem - Deadlock Prevention Strategies

## Overview
Implementations of 6 different solutions to the classic Dining Philosophers problem, demonstrating various deadlock prevention and avoidance strategies.

## Solutions Implemented

| Version | Approach | Synchronization | Status |
|---------|----------|-----------------|--------|
| 1 | Sequential fork acquisition | Fork objects | ❌ Deadlock-prone |
| 2 | Random fork order | ReentrantLock | ✓ Deadlock-free |
| 3 | Asymmetric solution | Semaphore | ✓ Deadlock-free |
| 4 | Try-lock with backoff | ReentrantLock.tryLock() | ✓ Deadlock-free |
| 5 | Mediator pattern | Lock + Semaphore | ✓ Deadlock-free |
| 6 | Canteen with overflow | Lock + Semaphore | ✓ Deadlock-free |

## Key Files
- `AbstractPhilosopher.java`: Base class implementing eating/thinking cycles and measurement
- `Philosopher1-6.java`: Individual solution variants
- `Main.java`: Benchmark harness measuring mean fork acquisition time

## How to run
```bash
./gradlew run
```

## Learning outcomes
- Deadlock conditions and prevention strategies
- Lock ordering and asymmetric solutions
- Semaphore-based resource allocation
- Performance comparison of synchronization primitives
