string stringTest(void){
	return "string test success!";
}

void main(void){
	string s;
	s = "string test";
	write(s);
	writeln();
	write("test");
	writeln();
	write(stringTest());
}
