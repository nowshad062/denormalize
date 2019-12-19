package com.pvamu.den.service;
/*
 * Reference: https://markhneedham.com/blog/2013/08/05/javajblas-calculating-eigenvector-centrality-of-an-adjacency-matrix/
 * 
 */
import java.util.ArrayList;
import java.util.List;

import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pvamu.den.dal.UserRelationDALImpl;

@Service
public class EgenVectorC {

	@Autowired
	private  UserRelationDALImpl userRelationDALImpl;
	
public   List<Double> getNormalisedEigenVectors(List<Long> nodes)  {
	DoubleMatrix matrix = new DoubleMatrix(getMaxtrix(nodes));
		
		ComplexDoubleMatrix eigenvalues = Eigen.eigenvalues(matrix);
//		for (ComplexDouble eigenvalue : eigenvalues.toArray()) {
//		    System.out.println(String.format("%.2f ", eigenvalue.abs()));
//		}
		List<Double> principalEigenvector = getPrincipalEigenvector(matrix);
		//System.out.println("principalEigenvector = " + principalEigenvector);
		System.out.println("normalisedPrincipalEigenvector = " + normalised(principalEigenvector));
		return normalised(principalEigenvector);
	}
	
	
	private  List<Double> getPrincipalEigenvector(DoubleMatrix matrix) {
	    int maxIndex = getMaxIndex(matrix);
	    ComplexDoubleMatrix eigenVectors = Eigen.eigenvectors(matrix)[0];
	    return getEigenVector(eigenVectors, maxIndex);
	}

	private  int getMaxIndex(DoubleMatrix matrix) {
	    ComplexDouble[] doubleMatrix = Eigen.eigenvalues(matrix).toArray();
	    int maxIndex = 0;
	    for (int i = 0; i < doubleMatrix.length; i++){
	        double newnumber = doubleMatrix[i].abs();
	        if ((newnumber > doubleMatrix[maxIndex].abs())){
	            maxIndex = i;
	        }
	    }
	    return maxIndex;
	}

	private  List<Double> getEigenVector(ComplexDoubleMatrix eigenvector, int columnId) {
	    ComplexDoubleMatrix column = eigenvector.getColumn(columnId);

	    List<Double> values = new ArrayList<Double>();
	    for (ComplexDouble value : column.toArray()) {
	        values.add(value.abs()  );
	    }
	    return values;
	}
	
	
	private  List<Double> normalised(List<Double> principalEigenvector) {
	    double total = sum(principalEigenvector);
	    List<Double> normalisedValues = new ArrayList<Double>();
	    for (Double aDouble : principalEigenvector) {
	        normalisedValues.add(aDouble / total);
	    }
	    return normalisedValues;
	}

	private  double sum(List<Double> principalEigenvector) {
	    double total = 0;
	    for (Double aDouble : principalEigenvector) {
	        total += aDouble;
	    }
	    return total;
	}
	
	private double[][]  getMaxtrix(List<Long> nodes) {
		 double[][] targetMatrix =  new double[nodes.size()][nodes.size()] ;
			for( int x = 0 ; x < nodes.size() ; x++) {	
				for(int y = 0 ; y < nodes.size() ; y++) {	
					if( nodes.get(x).equals(nodes.get(y))) {
						targetMatrix[x][y] = 1.0;	
					}else {
					targetMatrix[x][y] = userRelationDALImpl.isRelatedAnyDirection(nodes.get(x), nodes.get(y));
					}
				}
			}
			//Test whether the matrix is symmetric
			int flag = 0;
			for( int x = 0 ; x < nodes.size() ; x++) {	
				for(int y = 0 ; y <= x ; y++) {	
					if (targetMatrix[x][y] != targetMatrix[y][x])
					{
						flag = 1;
						System.out.println("Error: the adjacent matrix is not symmetric.");
						break;
					}
				}
				if(flag == 1)
					break;
			}
			return targetMatrix;
	}
}
