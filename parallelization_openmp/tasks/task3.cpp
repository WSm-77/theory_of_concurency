#include <iostream>
#include <omp.h>
#include <cmath>

// #define SIZE 100000
#define SIZE 5

double  func (double x){return  sin (x)*x*x ;}

double integral(double a, double b, int n) {
    double h = (b - a) / n;
    double sum = 0.0;

    #pragma omp parallel for reduction(+:sum)
    for (int i = 0; i < n; i++) {
        double x_i = a + i * h;
        double x_next = a + (i + 1) * h;
        sum += 0.5 * (func(x_i) + func(x_next)) * h;
        printf("Thread %d processing trapezoid %d, partial sum = %f\n",
                omp_get_thread_num(), i, sum);
    }

    return sum;

}

int main() {
    double a = 0.0;
    double b = 10.0;
    int n = 100;
    double result = integral(a, b, n);
    std::cout << "Approximate integral from " << a << " to " << b << " is " << result << std::endl;

    return 0;
}
