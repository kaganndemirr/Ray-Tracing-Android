using Android.Graphics;
using Java.Lang;

namespace Ray_Tracing
{
    class Triangle : Shape
    {
        private Vertex v0;
        private Vertex v1;
        private Vertex v2;

        public Triangle(Vertex v0, Vertex v1, Vertex v2, Color shapeColor)
        {
            this.v0 = v0;
            this.v1 = v1;
            this.v2 = v2;
            this.shapeColor = shapeColor;
        }

        public override double Intersect(Vertex ro, Vertex rd)
        {
            Vertex normal = (v1.Subs(v0)).CrossProduct(v2.Subs(v0));
            Vertex r;
            double s, s1, s2, s3;

            double d = -(normal.Mul(v0));
            double t = -(normal.Mul(ro.Add(d))) / (normal.Mul(rd));

            if (t > 0)
            {
                r = ro.Add(rd.Mul(t));

                s = (v1.Subs(v0)).CrossProduct(v2.Subs(v0)).Length();
                s1 = (r.Subs(v0)).CrossProduct(v2.Subs(v0)).Length();
                s2 = (v1.Subs(v0)).CrossProduct(r.Subs(v0)).Length();
                s3 = (v1.Subs(r)).CrossProduct(v2.Subs(r)).Length();

                double difference = Math.Abs(s - (s1 + s2 + s3));
                double epsilon = 0.005;

                if (difference <= epsilon)
                {
                    return t;
                }

                else
                    return 0.0;
            }

            else
                return 0.0;
        }
    }
}