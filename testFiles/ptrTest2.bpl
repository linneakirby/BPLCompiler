int m;
int test(int *z){
	m = 10;
	z = &m;
	return *z;	
}

void main(void){
	int x;
	int *y;
	x = 1;
	y = &x;
	write(test(y));
	writeln();
}
