package org.fitness.classes;

import org.springframework.data.annotation.Id;

public  class PlanetFitnessMembership {
    @Id
    private final String membershipType;
    String[] features;
    private final String price;
    private String gymName;

    public PlanetFitnessMembership(String membershipType, String gymName,String[] features,String price) {
        this.membershipType = membershipType;
        this.features = features;
        this.price =price;
        this.gymName =gymName;
    }
}
