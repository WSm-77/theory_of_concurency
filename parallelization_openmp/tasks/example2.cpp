#include <iostream>
#include <omp.h>

int main() {
    int sum = 0;

    #pragma omp parallel reduction(+:sum)
    {
        int thread_id   = omp_get_thread_num();
        int num_threads = omp_get_num_threads();
        std::cout << "Hello from thread " << thread_id
                  << " out of " << num_threads << " threads." << std::endl;

        int local_sum = 0;

        #pragma omp for
        for (int i = 0; i < 2; i++) {
            local_sum++;
            std::cout << "Thread " << thread_id
                      << " processing iteration " << i
                      << ", current local_sum: " << local_sum << std::endl;
        }

        std::cout << "Thread " << thread_id
                  << " finished with local sum: " << local_sum << std::endl;

        // accumulate local_sum into the parallel reduction variable
        sum += local_sum;
    }

    std::cout << "Final sum: " << sum << std::endl;
    return 0;
}
