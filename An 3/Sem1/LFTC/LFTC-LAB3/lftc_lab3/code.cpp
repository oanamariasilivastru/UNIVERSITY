int main() {
    int a;
    int b;
    citeste(a);
    citeste(b);

    int r<-1;
    while (r != 0) {
        r<-a%b;
        a<-b;
        b<-r;
    }
}
