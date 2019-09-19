package hopfiel;

import Jama.Matrix;

/**
 * Created by cosijopii on 21/01/17.
 */
public class M {




    public static void main(String argv[]){


        double p[][]={{1,-1,-1 ,1},
                      {-1,1,1,-1}};



        Matrix matrix=new Matrix(p);
        Matrix w=new Matrix(4,4);
        Matrix I=Matrix.identity(4,4);
        Matrix wi;

        matrix.getMatrix(0,0,0,3).transpose().times(matrix.getMatrix(0,0,0,3)).print(2,0);

        for (int i = 0; i < 2; i++) {

         wi=matrix.getMatrix(i,i,0,3).transpose().times(matrix.getMatrix(i,i,0,3)).minus(I);
         w=wi.plus(w);
            w.print(2,0);
        }




    }



}
