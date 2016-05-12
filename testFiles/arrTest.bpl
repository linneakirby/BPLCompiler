int test(int y[]){
	int i;
	i = 0;
	while(i < 10){
		write("i is: ");
		write(i);
		writeln();
		y[i] = i+5;
		write(y[i]);
		writeln();
		i = i+1;
	}
	return y[9];
}

void main(void){
	int a;
	int x[10];
	a = test(x);
	write("RETURNED: ");
	write(a);
	writeln();
}
