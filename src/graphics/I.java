package graphics;

public interface I {
    public interface Area{
        public boolean hit(int x, int y);
        public void dn(int x, int y);
        public void drag(int x, int y);
        public void up(int x, int y);
    }
}

