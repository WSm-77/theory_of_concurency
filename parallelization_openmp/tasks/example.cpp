#include<stdio.h>
#include<stdlib.h>
#include<omp.h>

int main(){
        printf("\n\nSTART");
        int suma=1000;
        int *sum = (int*)malloc( sizeof(int ));
        #pragma omp parallel shared(suma)
        {
                printf("po paralell %d\n",omp_get_thread_num());
                #pragma omp sections
                {
                        #pragma omp section
                        {
                                printf("sekcja 1 %d\n",omp_get_thread_num());
                        }
                        #pragma omp section
                        {
                                printf("sekcja 2 %d\n",omp_get_thread_num());
                        }
                }

                #pragma omp for
                for(int i=0;i<1000;i++){
                        suma++;
                }

printf("Watek %d suma=%d\n",omp_get_thread_num(),suma);
        }
        printf("END");
        return 0;
}
