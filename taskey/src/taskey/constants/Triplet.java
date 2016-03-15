package taskey.constants;

/**
 * This class is used to implement triplet generic
 * http://stackoverflow.com/questions/6010843/java-how-to-store-data-triple-in-a-list
 */

public class Triplet<T, U, V>
{
   private T a;
   private U b;
   private V c;

   public Triplet(T a, U b, V c)
   {
    this.a = a;
    this.b = b;
    this.c = c;
   }

   public T getA(){ return a;}
   public U getB(){ return b;}
   public V getC(){ return c;}
}