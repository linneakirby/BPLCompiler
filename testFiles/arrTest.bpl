int x[10];
void main(void){
	int i;
	i = 0;
	while(i < 10){
		write("i is: ");
		write(i);
		writeln();
		x[i] = i+5;
		write(x[i]);
		writeln();
		i = i+1;
	}
}
