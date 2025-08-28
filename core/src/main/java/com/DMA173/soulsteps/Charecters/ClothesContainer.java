package com.DMA173.soulsteps.Charecters;

/**
 * Simple data container for character clothing configuration.
 * Acts as a struct to hold clothing information using integer encoding.
 */
public class ClothesContainer {
    
    public int hairType;
    public int outfitType;
    public int modelType;
    public int accessoryType;
    
    /**
     * Default constructor - all values set to 0 (no clothing/default)
     */
    public ClothesContainer() {
        this.hairType = 0;
        this.outfitType = 0;
        this.modelType = 0;
        this.accessoryType = 0;
    }
    
    /**
     * Constructor with all clothing parameters
     * @param modelType is deprecated
     */
    public ClothesContainer(int hairType, int outfitType, int modelType, int accessoryType) {
        this.hairType = hairType;
        this.outfitType = outfitType;
        this.modelType = modelType;
        this.accessoryType = accessoryType;
    }
    
    /**
     * Constructor with main parameters (accessory defaults to 0)
     */
    public ClothesContainer(int hairType, int outfitType, int modelType) {
        this.hairType = hairType;
        this.outfitType = outfitType;
        this.modelType = modelType;
        this.accessoryType = 0;
    }
    
    /**
     * Constructor with hair and outfit only
     */
    public ClothesContainer(int hairType, int outfitType) {
        this.hairType = hairType;
        this.outfitType = outfitType;
        this.modelType = 0;
        this.accessoryType = 0;
    }
    
    /**
     * Copy constructor
     */
    public ClothesContainer(ClothesContainer other) {
        this.hairType = other.hairType;
        this.outfitType = other.outfitType;
        this.modelType = other.modelType;
        this.accessoryType = other.accessoryType;
    }
    
    @Override
    public String toString() {
        return "ClothesContainer{" +
                "hairType=" + hairType +
                ", outfitType=" + outfitType +
                ", modelType=" + modelType +
                ", accessoryType=" + accessoryType +
                '}';
    }
}