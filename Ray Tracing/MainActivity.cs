using Android.App;
using Android.OS;
using Android.Support.V7.App;
using Android.Runtime;
using Android.Widget;
using Android.Graphics;

using System.Collections.Generic;
using System.Linq;

namespace Ray_Tracing
{
    [Activity(Label = "@string/app_name", Theme = "@style/AppTheme", MainLauncher = true)]
    public class MainActivity : AppCompatActivity
    {

        private Color TraceRay(Vertex ro, Vertex rd, Shape[] shapes)
        {
            Intersection intersection = new Intersection();
            List<Intersection> intersections = new List<Intersection>();

            for(int i = 0; i < 3; i++)
            {
                double t = shapes[i].Intersect(ro, rd);

                if (t > 0.0)
                {
                    intersection.distance = t;
                    intersection.indices = i;

                    intersections.Add(intersection);
                }
            }

            if (intersections.Count() > 0)
            {
                double min_distance = double.MaxValue;
                int min_indices = -1;

                for (int i = 0; i < intersections.Count(); i++)
                {
                    if (intersections[i].distance < min_distance)
                    {
                        min_indices = intersections[i].indices;
                        min_distance = intersections[i].distance;
                    }
                }

                return shapes[min_indices].shapeColor;
            }

            return Color.Black;
        }

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.activity_main);

            Button renderButton = FindViewById<Button>(Resource.Id.renderButton);
            ImageView rttImageView = FindViewById<ImageView>(Resource.Id.rttImageView);

            renderButton.Click += delegate
            {
                Bitmap surface = Bitmap.CreateBitmap(800, 450, Bitmap.Config.Rgb565);
                rttImageView.SetImageBitmap(surface);

                Triangle T1 = new Triangle(new Vertex(0, 30, 40), new Vertex(40, -30, 120), new Vertex(-40, -30, 120), Color.Blue);
                Triangle T2 = new Triangle(new Vertex(-50, 30, 124), new Vertex(50, 30, 124), new Vertex(0, -30, 44), Color.Red);
                Triangle T3 = new Triangle(new Vertex(-30, 0, 37), new Vertex(30, 40, 117), new Vertex(30, -40, 117), Color.Green);

                Shape[] shapes = {T1, T2, T3};

                Vertex camera = new Vertex(0, 0, 0);

                for (int y = 0; y < 450; y++)
                {
                    for (int x = 0; x < 800; x++)
                    {
                        Vertex pixel = new Vertex(16 * x / 799.0 - 8, 4.5 - y * 9 / 449.0, 10);
                        Vertex Rd = (pixel.Subs(camera)).Normalize();
                        Color c = TraceRay(camera, Rd, shapes);
                        surface.SetPixel(x, y, c);
                    }
                    rttImageView.Invalidate();
                }
            };

        }
        public override void OnRequestPermissionsResult(int requestCode, string[] permissions, [GeneratedEnum] Android.Content.PM.Permission[] grantResults)
        {
            Xamarin.Essentials.Platform.OnRequestPermissionsResult(requestCode, permissions, grantResults);

            base.OnRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}