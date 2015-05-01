import java.awt.Color;

import javax.swing.JFrame;

import Jama.*;
import org.math.*;
import org.math.plot.*;


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
	public static double tenFoldCV(double[][] yVec, double[][] XMat, int numTrials,boolean graphing){
		return tenFoldCV(new Matrix(yVec),new Matrix(XMat),numTrials,graphing);
	}
	
	public static double tenFoldCV(Matrix y, Matrix X, int numTrials,boolean graphing){
		
		Matrix[] foldsX = new Matrix[10];
		Matrix[] foldsY = new Matrix[10];
		
		double[] errS = new double[numTrials];
		
		double[] lambdas = new double[numTrials];
		double[] sumOfSquares = new double[numTrials];
				
		int foldNumRows = (int)Math.floor(X.getRowDimension()/10);
		int numCols = X.getColumnDimension();
		int i,j,k;
		
		//Randomly swap rows in X and y (keeping correspondence the same). 
		//The number of rows divided by two is the number of swaps which occur
		int rand1,rand2;		
	
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
		
		//For numTrials, partition X and y into 10 folds, randomly choose lambda, 
		//and find the average sum of squares
		for(i = 0; i < numTrials; i++){
			
			
			for(k = 0; k < 9; k++){
				//Construct X and y folds
				foldsX[k] = X.getMatrix((k*foldNumRows),((k+1)*foldNumRows)-1,0,numCols-1);
				foldsY[k] = y.getMatrix((k*foldNumRows),((k+1)*foldNumRows)-1,0,0);
			}
			
			foldsX[9] = X.getMatrix((9 * foldNumRows), X.getRowDimension()-1, 0, numCols-1);
			foldsY[9] = y.getMatrix((9 * foldNumRows), y.getRowDimension()-1, 0,0);
			
			double avgErr = 0;
			double lambdaHat = Math.pow(10,(-2*Math.random())*(Math.round(2*(Math.random()-.5))*3)); //No, not Ayn
			lambdas[i] = lambdaHat;
			for(j = 0; j < 10; j++){
				Matrix w;
				w = ridgeReg(foldsY[j],foldsX[j],lambdaHat);
				Matrix yFold = foldsX[j].times(w);
				avgErr += computeError(yFold,foldsY[j],foldsX[j],lambdaHat);
			}
			avgErr = avgErr/10d;
			errS[i] = avgErr;
			sumOfSquares[i] = avgErr;
			
		}
		
		double out = 0d;
		double minSoS = Double.MAX_VALUE;
		double maxSoS = Double.MIN_VALUE;
		
		for(j = 0;j < numTrials;j++){
			//System.out.println("Error of " + sumOfSquares[j] + " for lambda = " + lambdas[j]);
			if(sumOfSquares[j] < minSoS){
				minSoS = sumOfSquares[j];
				out = lambdas[j];
			}
			if(sumOfSquares[j] > maxSoS){
				maxSoS = sumOfSquares[j];
			}
		}
		if(graphing){
			//Plot logarithmic curve of lambda versus average error incured 
			Plot2DPanel plot = new Plot2DPanel();
			plot.addScatterPlot("",Color.cyan,lambdas, errS);
			plot.setAxisLabels("Lambda","Error");
			plot.setAxisScale(0, "LOG");
			plot.setSize(600, 600);
			JFrame frame = new JFrame("Lambda versus Error Computed for 10 Fold Cross Validation");
			frame.setContentPane(plot);
			frame.setVisible(true);			
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
	/*
	 * This method uses Wahba's method for determining the optimal lambda value using the RSS and squared number degrees of
	 * free. This is super computationally intensive so that's fun.
	 */
	public static double computeError(Matrix yhat, Matrix y,Matrix X, double lambda){
		double rss = sumOfSquares(yhat,y);
		//A = (lambda^2)I
		Matrix A = Matrix.identity(X.getColumnDimension(), X.getColumnDimension()).times(Math.pow(lambda,2));
		//B = (Xt * X + (lambda^2)I)
		Matrix B = (X.transpose().times(X)).plus(A);
		//C = (X * (B^-1) * Xt) 
		Matrix C = X.times(B.inverse()).times(X.transpose());
		//Trace(C)^2
		double degsOfFreedom = Math.pow(((Matrix.identity(C.getRowDimension(),C.getColumnDimension()).minus(C)).trace()),2);
		return rss/degsOfFreedom;
		
	}
	/*
	 * Computes r for a column vector of target values in the test, a matrix of x values in the test, and a vector of weights
	 * computed by linear regression 
	 */
	public static double computeR2(Matrix yTest, Matrix xTest, Matrix w, boolean graphing){
		double yBar = 0;
		for(int i = 0; i < yTest.getRowDimension(); i++){
			yBar += yTest.get(i,0);
		}
		yBar = yBar/yTest.getRowDimension();	//Compute average of target vals
		Matrix yHat = xTest.times(w);	//Now compute target vals
		
		//This is somewhat complicated but it constructs a scatter plot of data points 
		//marking predicted y versus target y for the test data. A line x=y is likewise plotted 
		//to visualize correlation
		if(graphing){
			double[] yHatArray = yHat.getColumnPackedCopy();
			double[] yTestArray = yTest.getColumnPackedCopy();
			double[] perfect = new double[1000];
			double minHat = Double.MAX_VALUE;
			double maxHat = Double.MIN_VALUE;
			double minTest = Double.MAX_VALUE;
			double maxTest = Double.MIN_VALUE;
			for(int i = 0;i < yHatArray.length; i ++){
				if(minHat > yHatArray[i]){
					minHat = yHatArray[i];
				}
				if(maxHat < yHatArray[i]){
					maxHat = yHatArray[i];
				}
				if(minTest > yTestArray[i]){
					minTest = yTestArray[i];
				}
				if(maxTest < yTestArray[i]){
					maxTest = yTestArray[i];
				}
			}
			double lowerBound = (minHat < minTest) ? minHat : minTest;
			double upperBound = (maxHat < maxTest) ? maxTest : maxHat;
			double rangeSteps = (Math.abs(upperBound-lowerBound))/perfect.length;
			for(int i = 0; i < perfect.length; i++){
				perfect[i] = lowerBound + rangeSteps*i;
			}
			Plot2DPanel plot = new Plot2DPanel();
			plot.addScatterPlot("", Color.black,perfect,perfect);
			plot.addScatterPlot("",Color.cyan,yHatArray,yTestArray);
			plot.setAxisLabels("Predicted Y Values for Test Data","Target Y Values for Test Data");
			plot.setSize(600, 600);
			JFrame frame = new JFrame("Correlation of YHat, Y");
			frame.setContentPane(plot);
			frame.setVisible(true);
			
		}
		double ssr,sst;
		ssr = sst = 0;
		
		for(int i = 0; i < yTest.getRowDimension();i++){
			ssr += Math.pow((yHat.get(i, 0) - yBar),2);	//Get sum of squared residuals in the target estimation and total sum of squared residuals
			sst += Math.pow((yTest.get(i, 0) - yBar),2);
		}
		return (ssr/sst);
	}
}
