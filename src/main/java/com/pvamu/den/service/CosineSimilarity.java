package com.pvamu.den.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.pvamu.den.model.AuxliaryUser;
import com.pvamu.den.model.TargetedUsers;

/**
 * Measures the Cosine similarity of two vectors of an inner product space and
 * compares the angle between them.
 *
 * <p>
 * For further explanation about the Cosine Similarity, refer to
 * http://en.wikipedia.org/wiki/Cosine_similarity.
 * </p>
 *
 * @since 1.0
 */
@Service
public class CosineSimilarity {

    /**
     * Calculates the cosine similarity for two given vectors.
     *
     * @param leftVector left vector
     * @param rightVector right vector
     * @return cosine similarity between the two vectors
     */
    private Double cosineSimilarity(final Map<CharSequence, Double> leftVector,
                                   final Map<CharSequence, Double> rightVector) {
        if (leftVector == null || rightVector == null) {
            throw new IllegalArgumentException("Vectors must not be null");
        }

        final Set<CharSequence> intersection = getIntersection(leftVector, rightVector);

        final double dotProduct = dot(leftVector, rightVector, intersection);
        double d1 = 0.0d;
        for (final Double value : leftVector.values()) {
            d1 += Math.pow(value, 2);
        }
        double d2 = 0.0d;
        for (final Double value : rightVector.values()) {
            d2 += Math.pow(value, 2);
        }
        double cosineSimilarity;
        if (d1 <= 0.0 || d2 <= 0.0) {
            cosineSimilarity = 0.0;
        } else {
            cosineSimilarity = dotProduct / (Math.sqrt(d1) * Math.sqrt(d2));
        }
        return cosineSimilarity;
    }

    /**
     * Returns a set with strings common to the two given maps.
     *
     * @param leftVector left vector map
     * @param rightVector right vector map
     * @return common strings
     */
    private Set<CharSequence> getIntersection(final Map<CharSequence, Double> leftVector,
            final Map<CharSequence, Double> rightVector) {
        final Set<CharSequence> intersection = new HashSet<>(leftVector.keySet());
        intersection.retainAll(rightVector.keySet());
        return intersection;
    }

    /**
     * Computes the dot product of two vectors. It ignores remaining elements. It means
     * that if a vector is longer than other, then a smaller part of it will be used to compute
     * the dot product.
     *
     * @param leftVector left vector
     * @param rightVector right vector
     * @param intersection common elements
     * @return the dot product
     */
    private double dot(final Map<CharSequence, Double> leftVector, final Map<CharSequence, Double> rightVector,
            final Set<CharSequence> intersection) {
        long dotProduct = 0;
        for (final CharSequence key : intersection) {
            dotProduct += leftVector.get(key) * rightVector.get(key);
        }
        return dotProduct;
    }
    
    
    public double findcosineSimilarity(TargetedUsers tarUser , AuxliaryUser auxUser , int config) {
		
		Map<CharSequence, Double> leftVector = new HashMap<>();
		Map<CharSequence, Double> rightVector = new HashMap<>();
	
		if ( config == 0) {
			leftVector.put("db",(double) tarUser.getDob());
			leftVector.put("gn", (double) tarUser.getGender());
			
			rightVector.put("db",(double)  auxUser.getDob());
			rightVector.put("gn",(double)  auxUser.getGender());
		}else if (config == 1){
			//System.out.println("Degree and Centrality are picked.");
			leftVector.put("de",(double)  tarUser.getxAxis());
			leftVector.put("ce", (double) tarUser.getCentrality());
			
			rightVector.put("de",(double)  auxUser.getxAxis());
			rightVector.put("ce", (double) auxUser.getCentrality());
		}else {
			leftVector.put("db", (double) tarUser.getDob());
			leftVector.put("gn",(double)  tarUser.getGender());
			
			rightVector.put("db", (double) auxUser.getDob());
			rightVector.put("gn", (double) auxUser.getGender());
			
			leftVector.put("de", (double) tarUser.getxAxis());
			leftVector.put("ce", (double) tarUser.getCentrality());
			
			rightVector.put("de",(double)  auxUser.getxAxis());
			rightVector.put("ce", (double) auxUser.getCentrality());
		}
		

		return cosineSimilarity(leftVector, rightVector);
		
		
	}

}

