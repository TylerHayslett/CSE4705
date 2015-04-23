import Jama.*;

public class MathUtils {
	
	public static Matrix ridgeReg(double[][] yVec, double[][] XMat, double lambda){
		Matrix y = new Matrix(yVec);
		Matrix X = new Matrix(XMat);
		
		return ridgeReg(y,X,lambda);
	}
	
	public static Matrix ridgeReg(Matrix y, Matrix X, double lambda){
		//Compute transpose of X
		Matrix XTrans = X.transpose();
		//Compute product of transpose X and X
		Matrix XTransX = XTrans.times(X);
		//Build matrix with lambdas along diagonal and zeros everywhere else
		Matrix lambdaI = Matrix.identity(XTransX.getRowDimension(), XTransX.getColumnDimension()).times(lambda);
		
		//Construct vector of weights
		Matrix w = (((XTransX.plus(lambdaI)).inverse()).times(XTrans)).times(y);
		
		return w;
		
	}
	
	//LOLOVERLOAD
	public static double tenFoldCV(double[][] yVec, double[][] XMat, int numTrials){
		return tenFoldCV(new Matrix(yVec),new Matrix(XMat),numTrials);
	}
	
	public static double tenFoldCV(Matrix y, Matrix X, int numTrials){
		
		Matrix[] foldsX = new Matrix[10];
		Matrix[] foldsY = new Matrix[10];
		
		double[] lambdas = new double[numTrials];
		double[] sumOfSquares = new double[numTrials];
				
		int foldNumRows = (int)Math.floor(X.getRowDimension()/10);
		int numCols = X.getColumnDimension();
		int i,j,k;
		
		
		//For numTrials, randomly scramble X and y (still keep row correspondence, however)
		//Then partition into 10 folds, randomly choose lambda, and find the average sum of squares
		for(i = 0; i < numTrials; i++){
			
			//Randomly swap rows in X and y. The number of rows divided by two is the number of
			//swaps which occur
			int rand1,rand2;
			Matrix tempX,tempY;
			
			
			for(k = 0; k < (int)Math.floor(X.getRowDimension()/2); k++){
				
				rand1 = (int)(Math.random() * (X.getRowDimension()-1));
				rand2 = (int)(Math.random() * (X.getRowDimension()-1));
				//Save temporary rows in X and y
				Matrix xRow1 = X.getMatrix(rand1, rand1, 0, numCols-1);
				Matrix yRow1 = y.getMatrix(rand1, rand1, 0,0);
				Matrix xRow2 = X.getMatrix(rand2, rand2, 0, numCols-1);
				Matrix yRow2 = y.getMatrix(rand2, rand2, 0,0);
				//Then swap
				X.setMatrix(rand1,rand1,0,numCols-1,xRow2);
				y.setMatrix(rand1,rand1,0,0,yRow2);
				X.setMatrix(rand2, rand2, 0, numCols-1, xRow1);
				y.setMatrix(rand2, rand2, 0,0, yRow1);
				
			}
			
			for(k = 0; k < 9; k++){
				//Construct X and y folds
				foldsX[k] = X.getMatrix((k*foldNumRows),((k+1)*foldNumRows)-1,0,numCols-1);
				foldsY[k] = y.getMatrix((k*foldNumRows),((k+1)*foldNumRows)-1,0,0);
			}
			
			foldsX[9] = X.getMatrix((9 * foldNumRows), X.getRowDimension()-1, 0, numCols-1);
			foldsY[9] = y.getMatrix((9 * foldNumRows), y.getRowDimension()-1, 0,0);
			
			double avgSoS = 0;
			double lambdaHat = Math.pow(10, -2*Math.random()-3); //No, not Ayn
			lambdas[i] = lambdaHat;
			for(j = 0; j < 10; j++){
				Matrix w;
				w = ridgeReg(foldsY[j],foldsX[j],lambdaHat);
				Matrix yFold = foldsX[j].times(w);
				avgSoS += sumOfSquares(yFold,foldsY[j]);
			}
			avgSoS = avgSoS/10d;
			sumOfSquares[i] = avgSoS;
			
		}
		double out = 0d;
		double minSoS = Double.MAX_VALUE;
		for(j = 0;j < numTrials;j++){
			System.out.println("Error of " + sumOfSquares[j] + " for lambda = " + lambdas[j]);
			if(sumOfSquares[j] < minSoS){
				minSoS = sumOfSquares[j];
				out = lambdas[j];
			}
		}
		return out;
	}
	
	
	/*
	 * Computes sum of squared residuals. yhat and y are assumed to both be single-column
	 * multi-row matrices of the same dimensions
	 */
	public static double sumOfSquares(Matrix yhat, Matrix y){
		double out = 0d;
		for(int i = 0; i < yhat.getRowDimension(); i++){
			out += Math.pow((yhat.get(i,0)-y.get(i,0)),2);
		}
		return out;
	}
}
