package org.eclipse.nebula.widgets.xviewer.util;

/**
 * @author Donald G. Dunne
 */
public class Pair<X, Y> {
   private X first;
   private Y second;

   public Pair(X first, Y second) {
      this.first = first;
      this.second = second;
   }

   public Y getSecond() {
      return second;
   }

   public void setSecond(Y second) {
      this.second = second;
   }

   public X getFirst() {
      return first;
   }

   public void setFirst(X first) {
      this.first = first;
   }

}