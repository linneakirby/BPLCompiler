int ifTest(int i){
	if(i == 10){
		return 1;
	}
	return 0;
}

void main(void){
	write(ifTest(10));
	writeln();
	write(ifTest(4));
	writeln();
}
