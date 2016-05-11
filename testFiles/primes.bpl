int isPrime(int n) {
    int divisor;
    if (n < 2) {
        return 0;
    }
    if (n < 4) {
        return 1;
    }
    divisor = 2;
    while (divisor < n) {
        if (n % divisor == 0) {
            return 0;
        }
        divisor = divisor + 1;
    }
    return 1;
}

void main(void) {
    int n;
    int max;
    int cur;
    n = 0;
    cur = 0;
    max = 10;
    write("The first ");
    write(max);
    write("primes are ");
    while (cur < max) {
        if (isPrime(n)) {
            write(n);
            cur = cur + 1;
        }
        n = n + 1;
    }
}
