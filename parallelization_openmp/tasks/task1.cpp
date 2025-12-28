// Napisz program wykonujący mnożenie:
// •skalar razy wektor (α×v)
// •wektor razy wektor (wynik skalar) (wT×v)

#include <iostream>
#include <omp.h>

// #define SIZE 100000
#define SIZE 5

void multiply_scalar_vector(double scalar, double* vector, double *result, int size) {
    #pragma omp parallel for
    for (int i = 0; i < size; i++) {
        result[i] = scalar * vector[i];
        printf("Thread %d processing element %d, partial result = %f\n",
               omp_get_thread_num(), i, scalar * vector[i]);
    }
}

double multiply_vector_vector(double* vec1, double* vec2, int size) {
    double result = 0.0;
    #pragma omp parallel for reduction(+:result)
    for (int i = 0; i < size; i++)  {
        result += vec1[i] * vec2[i];
        printf("Thread %d processing element %d, partial result = %f\n",
               omp_get_thread_num(), i, vec1[i] * vec2[i]);
    }

    return result;
}

int main() {
    double scalar = 2.0;
    double vector[SIZE];
    double vector2[SIZE];
    for (int i = 0; i < SIZE; i++) {
        vector[i] = i + 1;
        vector2[i] = i + 1;
    }
    double result_scalar_vector[SIZE];

    multiply_scalar_vector(scalar, vector, result_scalar_vector, SIZE);

    std::cout << "Result of scalar-vector multiplication:" << std::endl;
    for (int i = 0; i < SIZE; i++) {
        std::cout << result_scalar_vector[i] << " ";
    }
    std::cout << std::endl;

    double res = multiply_vector_vector(vector, vector2, SIZE);
    printf("Result of vector-vector multiplication: %f\n", res);

    return 0;
}
