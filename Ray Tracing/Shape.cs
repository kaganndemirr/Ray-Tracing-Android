using Android.Graphics;

namespace Ray_Tracing
{
    class Shape
    {
        public Color shapeColor;

        public virtual double Intersect(Vertex ro, Vertex rd) {return 0.0; }
    }
}