int x;
int y;

int func(int i){
	write("in func");
	i = i+5;
	return i;
}

void test(int i){
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
