int whileTest(void){
	int i;
	i = 0;
	while(i != 10){
		i = i+1;
		write("i is now: ");
		write(i);
		writeln();
	}
	return i;
}

void main(void){
	write(whileTest());
	writeln();
}
