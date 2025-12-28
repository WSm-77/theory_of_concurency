#include <stdio.h>
#include <omp.h>

int main() {
    const int N = 100;
    int a[N];

    for (int i = 0; i < N; i++)
        a[i] = 1;

    long total_sum = 0;

    #pragma omp parallel for reduction(+:total_sum)
    for (int i = 0; i < N; i++) {
        total_sum += a[i];
        printf("Thread %d processing index %d, current total_sum = %ld\n",
               omp_get_thread_num(), i, total_sum);
    }

    printf("Final sum = %ld\n", total_sum);
    return 0;
}
