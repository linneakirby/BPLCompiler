int ifElse(int i){
	int j;
	if(i == 10){
		j = 1;
	}
	else{
		if(i == 3){
			j = 2;
		}
		else{
			j = 10;
		}
	}
	return j;
}

void main(void){
	write(ifElse(10));
	writeln();
	write(ifElse(3));
	writeln();
	write(ifElse(5));
	writeln();
}
