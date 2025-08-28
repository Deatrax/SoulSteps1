package com.DMA173.soulsteps.Charecters;

/**
 * NPC class for characters like Lena, town residents, Veridia employees
 * Now supports hair and clothing system for different NPC types.
 * Demonstrates inheritance - NPCs share Character functionality but have different behavior
 */
public class NPC extends Character {
    
    private String dialogue;
    private boolean hasBeenTalkedTo;
    private String npcType; // "resident", "veridia_employee", "ally", "police", "firefighter"
    
    public NPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType) {
        // NPCs with hardcoded hair and clothing based on type
        super(assets, characterType, startX, startY, 100f, getAppearanceForNPCType(npcType));
        this.name = name;
        this.npcType = npcType;
        this.isInteractable = true;
        this.hasBeenTalkedTo = false;
        this.dialogue = "Hello there."; // Default dialogue
    }
    
    public NPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType, ClothesContainer clothes) {
        super(assets, characterType, startX, startY, 100f, clothes);
        this.name = name;
        this.npcType = npcType;
        this.isInteractable = true;
        this.hasBeenTalkedTo = false;
        this.dialogue = "Hello there."; // Default dialogue
    }
    
    /**
     * Returns appropriate hair and clothing configuration based on NPC type
     */
    private static ClothesContainer getAppearanceForNPCType(String npcType) {
        switch (npcType.toLowerCase()) {
            case "veridia_employee":
            case "office_worker":
                return new ClothesContainer(2, 103, 0, 0); // Professional hair + Office suit
            case "police":
            case "officer":
                return new ClothesContainer(1, 101, 0, 0); // Short hair + Police suit
            case "firefighter":
                return new ClothesContainer(1, 102, 0, 0); // Short hair + Firefighter suit
            case "delivery":
            case "delivery_person":
                return new ClothesContainer(3, 104, 0, 0); // Casual hair + Delivery suit
            case "resident_formal":
                return new ClothesContainer(2, 5, 0, 0); // Professional hair + Formal shirt and pants
            case "resident_casual":
                return new ClothesContainer(4, 6, 0, 0); // Casual hair + Casual shirt and pants
            case "resident_woman":
                return new ClothesContainer(5, 1, 0, 0); // Long hair + Women's dress
            case "cold_weather":
                return new ClothesContainer(3, 2, 0, 0); // Medium hair + Coat/jacket 1
            case "winter_resident":
                return new ClothesContainer(6, 4, 0, 0); // Styled hair + Coat/jacket 2
            case "ally":
            case "resident":
            default:
                return new ClothesContainer(3, 5, 0, 0); // Default: medium hair + casual clothing
        }
    }
    
    /**
     * NPCs have simple AI behavior - mostly stationary
     */
    @Override
    public void update(float delta) {
        updateStateTime(delta);
        // NPCs can have simple patrol patterns or remain stationary
        this.setMoving(false);
    }
    
    public void interact(Player player) {
        if (!hasBeenTalkedTo) {
            System.out.println(name + ": " + dialogue);
            hasBeenTalkedTo = true;
            
            // Different NPC types can affect kindness differently
            if (npcType.equals("ally")) {
                player.adjustKindness(5);
            } else if (npcType.equals("veridia_employee")) {
                // Talking to Veridia employees might decrease kindness slightly
                player.adjustKindness(-2);
            } else if (npcType.equals("police") && player.getEvidenceCount() > 0) {
                // Police might be helpful if player has evidence
                player.adjustKindness(3);
                System.out.println("The officer seems interested in your evidence.");
            }
        } else {
            System.out.println(name + ": We've already talked.");
        }
    }
    
    /**
     * Special method for Veridia employees
     */
    public void setAsVeridiaEmployee(String department) {
        this.npcType = "veridia_employee";
        this.setClothes(new ClothesContainer(2, 103, 0, 0)); // Professional hair + Office suit
        this.dialogue = "Welcome to Veridia Corporation, " + department + " department.";
    }
    
    /**
     * Special method for creating police NPCs
     */
    public void setAsPoliceOfficer(String rank) {
        this.npcType = "police";
        this.setClothes(new ClothesContainer(1, 101, 0, 0)); // Short hair + Police suit
        this.dialogue = "I'm " + rank + " " + name + ". How can I help you today?";
    }
    
    /**
     * Special method for creating firefighter NPCs
     */
    public void setAsFirefighter() {
        this.npcType = "firefighter";
        this.setClothes(new ClothesContainer(1, 102, 0, 0)); // Short hair + Firefighter suit
        this.dialogue = "Stay safe out there! Fire safety is everyone's responsibility.";
    }
    
    /**
     * Special method for creating female residents
     */
    public void setAsFemaleResident(String personalityType) {
        this.npcType = "resident_woman";
        switch (personalityType.toLowerCase()) {
            case "elegant":
                this.setClothes(new ClothesContainer(5, 1, 0, 0)); // Long hair + dress
                break;
            case "casual":
                this.setClothes(new ClothesContainer(4, 6, 0, 0)); // Medium hair + casual
                break;
            case "professional":
                this.setClothes(new ClothesContainer(2, 5, 0, 0)); // Professional hair + formal
                break;
            default:
                this.setClothes(new ClothesContainer(5, 1, 0, 0)); // Default long hair + dress
        }
        this.dialogue = "It's a lovely day in the neighborhood, isn't it?";
    }
    
    /**
     * Change NPC's complete appearance dynamically (for story events)
     */
    public void changeAppearanceForEvent(int hairType, int outfitType, String newDialogue) {
        this.changeHair(hairType);
        this.changeOutfit(outfitType);
        this.dialogue = newDialogue;
        this.hasBeenTalkedTo = false; // Allow new conversation
    }
    
    /**
     * Change NPC's clothing dynamically (for story events) - keeps existing hair
     */
    public void changeClothingForEvent(int newOutfitType, String newDialogue) {
        this.changeOutfit(newOutfitType);
        this.dialogue = newDialogue;
        this.hasBeenTalkedTo = false; // Allow new conversation
    }
    
    /**
     * Change NPC's hair dynamically (for story events) - keeps existing clothing
     */
    public void changeHairstyleForEvent(int newHairType, String newDialogue) {
        this.changeHair(newHairType);
        this.dialogue = newDialogue;
        this.hasBeenTalkedTo = false; // Allow new conversation
    }
    
    /**
     * Make NPC go undercover by changing their complete appearance
     */
    public void goUndercover(String targetType) {
        switch (targetType.toLowerCase()) {
            case "veridia":
                changeAppearanceForEvent(2, 103, "Just another day at the office...");
                break;
            case "police":
                changeAppearanceForEvent(1, 101, "Move along, citizen.");
                break;
            case "civilian":
                changeAppearanceForEvent(3, 5, "Beautiful weather we're having.");
                break;
            default:
                changeAppearanceForEvent(3, 5, "Hello there.");
        }
        this.npcType = "undercover_" + targetType;
    }
    
    // Getters and setters
    public String getDialogue() { return dialogue; }
    public void setDialogue(String dialogue) { this.dialogue = dialogue; }
    public String getNpcType() { return npcType; }
    public void setNpcType(String npcType) { 
        this.npcType = npcType; 
        this.setClothes(getAppearanceForNPCType(npcType)); // Update appearance when type changes
    }
    public boolean hasBeenTalkedTo() { return hasBeenTalkedTo; }
    public void resetConversation() { this.hasBeenTalkedTo = false; }
}