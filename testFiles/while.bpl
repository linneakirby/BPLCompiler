int whileTest(void){
	int i;
	int l;
	i = 0;
	while(i != 5){
		i = i+1;
		write("i is now: ");
		write(i);
		writeln();
		l = 0;
		while(l < 3){
			l = l+1;
			write(l);
			writeln();
		}
	}

	return i;
}

void main(void){
	write(whileTest());
	writeln();
}
