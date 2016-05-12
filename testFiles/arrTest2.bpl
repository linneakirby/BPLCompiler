string s(string y[]){
	y[0] = "potato";
	y[1] = "cat";
	y[2] = "hedgehog";
	y[3] = "banana";
}

void main(void){
	string arr[4];
	s(arr);
	write(arr[2]);
	writeln();
	write(arr[0]);
	writeln();
}
