#include <iostream>
#include <omp.h>

int main() {
    int sum = 0;

    #pragma omp parallel
    {
        int thread_id = omp_get_thread_num();
        int num_threads = omp_get_num_threads();
        std::cout << "Hello from thread " << thread_id << " out of " << num_threads << " threads." << std::endl;

        int sum = 0;
        #pragma omp paralell for reduction(+:sum)
        for (int i = 0; i < 2; i++) {
            sum++;
            std::cout << "Thread " << thread_id << " processing iteration " << i << ", current sum: " << sum << std::endl;
        }

        std::cout << "Thread " << thread_id << " finished with local sum: " << sum << std::endl;

        // #pragma omp atomic
        // sum += sum;
        // #pragma omp sections
        // {
        //     #pragma omp section
        //     {
        //         std::cout << "Hello from thread " << thread_id << " out of " << num_threads << " threads." << std::endl;
        //     }
        // }

        // #pragma omp single
        // {
        //     std::cout << "This is executed by a single thread with id: " << omp_get_thread_num() << std::endl;
        // }

        // #pragmap sections
        // {
        //     #pragma omp section
        //     {
        //         std::cout << "Hello from thread " << thread_id << " out of " << num_threads << " threads." << std::endl;
        //     }
        // }

        // #pragma omp single
        // {
        //     std::cout << "This is executed by a single thread with id: " << omp_get_thread_num() << std::endl;
        // }
    }

    std::cout << "Final sum: " << sum << std::endl;

    return 0;
}
