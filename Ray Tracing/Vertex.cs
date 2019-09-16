using Java.Lang;

namespace Ray_Tracing
{
    class Vertex
    {
        private double x;
        private double y;
        private double z;
    
        Vertex() { }

        public Vertex(double x, double y, double z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vertex Normalize()
        {
            double length = Math.Sqrt(x * x + y * y + z * z);
            x /= length;
            y /= length;
            z /= length;
            return this;
        }

        public double Length()
        {
            return Math.Sqrt(x * x + y * y + z * z);
        }

        public Vertex CrossProduct(Vertex v)
        {
            return new Vertex(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
        }

        public Vertex Add (Vertex v)
        {
            return new Vertex(x + v.x, y + v.y, z + v.z);
        }

        public Vertex Add (double d)
        {
            return new Vertex(x + d, y + d, z + d);
        }

        public Vertex Subs (Vertex v)
        {
            return new Vertex(x - v.x, y - v.y, z - v.z);
        }

        public double Mul (Vertex v)
        {
            return x * v.x + y * v.y + z * v.z;
        }

        public Vertex Mul (double d)
        {
            return new Vertex(x * d, y * d, z * d);
        }

        public Vertex Div (double d)
        {
            return new Vertex(x / d, y / d, z / d);
        }

        public Vertex Mul (double d, Vertex v)
        {
            return new Vertex(d * v.x, d * v.y, d * v.z);
        }

    }
}