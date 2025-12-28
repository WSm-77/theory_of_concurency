# Theory of Concurrency - Lab Assignments

This repository contains solutions for concurrency and parallel programming assignments covering synchronization, deadlock prevention, concurrent data structures, and parallelization techniques.

## Projects

### Java Concurrency Labs

- **[producer_consumer_sync/](producer_consumer_sync/)** - Producer-Consumer problem with condition variables
  - Demonstrates synchronized access with `wait()`/`notifyAll()`

- **[dining_philosophers_deadlock/](dining_philosophers_deadlock/)** - Dining Philosophers problem with 6 solutions
  - Compares deadlock prevention strategies: asymmetry, try-locks, semaphores, mediator pattern

- **[concurrent_collections/](concurrent_collections/)** - Thread-safe list implementation
  - Explores synchronization patterns for concurrent data structures

### Python Projects

- **[diekert_graph/](diekert_graph/)** - Diekert graphs and Foata Normal Form
  - Graph analysis, partial order visualization

- **[gauss_elimination/](gauss_elimination/)** - Parallel Gaussian elimination with dependency analysis
  - Concurrency analysis for numerical algorithms

### Utilities

- **[Matrices/](Matrices/)** - Matrix checker for Gaussian elimination verification

- **[parallelization_openmp/](parallelization_openmp/)** - OpenMP parallelization exercises

## Quick Start

### Java Projects
```bash
cd dining_philosophers_deadlock/lab4
./gradlew run
```

### Python Projects
```bash
cd diekert_graph
uv sync
uv run python main.py examples/example1.json
```

## Project Structure

Each Java lab project follows Maven conventions:
```
project/
├── README.md
├── build.gradle (or pom.xml)
├── src/
│   └── main/java/app/
└── build/
```

Python projects use `pyproject.toml` with `uv` package manager.

## Documentation

See individual project README.md files for detailed information about each assignment, learning outcomes, and usage instructions.
