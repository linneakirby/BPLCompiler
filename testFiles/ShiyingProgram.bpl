int x;
int y;

int func(int i){
	int p;
	p = 9;
	write(p);
	writeln();
	write("in func");
	writeln();
	i = i+5;
	return i;
}

void test(int i){
	int k;
	int l;
	int m;
	m = 5;
	l = m+5;
	write(l);
	writeln();
	write(i);
	writeln();
	y = func(2);
	write(y);
	writeln();
}

int q;

void main(void){
	test(1);
}
