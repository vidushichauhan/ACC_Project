package org.fitness.classes;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document(collection = "plans")
public class GoodLifeMembership implements Serializable {
    @Id
    private String membershipType;
    private String price;
    private List<String> features;
    private String gymName;

    // Default constructor
    public GoodLifeMembership() {
    }

    // Parameterized constructor
    public GoodLifeMembership(String membershipType, String price, List<String> features, String gymName) {
        this.membershipType = membershipType;
        this.price = price;
        this.features = features;
        this.gymName = gymName;
    }

    // Getters and setters
    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }
}
