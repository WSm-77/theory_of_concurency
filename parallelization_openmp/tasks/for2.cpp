#include <stdio.h>
#include <omp.h>

int main() {
    const int N = 10;
    int a[N];
    long total_sum = 0;

    // Initialize array
    for (int i = 0; i < N; i++)
        a[i] = 1;   // easy to verify: sum should be 100

    // Parallel region with reduction
    #pragma omp parallel
    {
        long partial_sum = 0;

        // Each thread computes a partial sum
        #pragma omp for
        for (int i = 0; i < N; i++) {
            partial_sum += a[i];
            printf("Thread %d processing element %d, partial_sum = %ld\n",
                   omp_get_thread_num(), i, partial_sum);
        }

        printf("Thread %d finished with partial sum = %ld\n",
               omp_get_thread_num(), partial_sum);

        // Combine partial sums using a critical section
        #pragma omp critical
        {
            total_sum += partial_sum;
        }
    }

    printf("Final sum = %ld\n", total_sum);
    return 0;
}
