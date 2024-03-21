package org.fitness.classes;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document(collection = "plans")
public class FitnessWorldMembership implements Serializable {
    @Id
    private final String membershipType;
    private final String gymName;
    private final String price;
    private final List<String> features;

    public FitnessWorldMembership(String membershipType, String gymName, String price, List<String> features) {
        this.membershipType = membershipType;
        this.gymName = gymName;
        this.price= price;
        this.features = features;
    }


}
