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
		Matrix lambdaI = Matrix.identity(XTransX.getRowDimension(), XTransX.getColumnDimension());
		
		//Construct vector of weights 
		Matrix w = (((XTransX.plus(lambdaI)).inverse()).times(XTrans)).times(y);
		
		return w;
		
	}
	
	public static Matrix tenFoldCV(Matrix y, Matrix X, int numTrials){
		
		Matrix[] foldsX = new Matrix[10];
		Matrix[] foldsY = new Matrix[10];
		
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
				rand1 = ((int)Math.random() * (X.getRowDimension()-1))+1;
				rand2 = ((int)Math.random() * (X.getRowDimension()-1))+1;
				tempX = X.getMatrix(rand1, rand1, 1, numCols);
				tempY = y.getMatrix(rand1, rand1, 1, numCols);
				
			}
			for(k = 0; k < 9; k++){
				//Construct X and y folds
				foldsX[k] = X.getMatrix((k*foldNumRows)+1,((k+1)*foldNumRows),1,numCols);
				foldsY[k] = y.getMatrix((k*foldNumRows)+1,((k+1)*foldNumRows),1,numCols);
			}
			
			foldsX[9] = X.getMatrix((9 * foldNumRows)+1, X.getRowDimension(), 1, numCols);
			foldsY[9] = y.getMatrix((9 * foldNumRows)+1, y.getRowDimension(), 1, numCols);
			
			double avgSoS = 0;
			for(j = 0; j < 10; j++){
				Matrix w;
				double lambdaHat = Math.random(); //No, not Ayn
				w = ridgeReg(foldsY[j],foldsX[j],lambdaHat);
				avgSoS += sumOfSquares(w.transpose().times(foldsX[j]),foldsY[j]);
			}
			avgSoS = avgSoS/10d;
		}
		
		return null;
	}
	
	
	/*
	 * Computes sum of squared residuals. yhat and y are assumed to both be single-column
	 * multi-row matrices of the same dimensions
	 */
	public static double sumOfSquares(Matrix yhat, Matrix y){
		double out = 0d;
		for(int i = 0; i < yhat.getRowDimension(); i++){
			out += Math.pow((yhat.get(i,1)-y.get(i,1)),2);
		}
		return out;
	}
}
