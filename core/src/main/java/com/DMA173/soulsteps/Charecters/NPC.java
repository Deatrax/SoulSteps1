package com.DMA173.soulsteps.Charecters;

/**
 * NPC class for characters like Lena, town residents, Veridia employees
 * Now supports clothing system for different NPC types.
 * Demonstrates inheritance - NPCs share Character functionality but have different behavior
 */
public class NPC extends Character {
    
    private String dialogue;
    private boolean hasBeenTalkedTo;
    private String npcType; // "resident", "veridia_employee", "ally", "police", "firefighter"
    
    public NPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType) {
        // NPCs with hardcoded clothing based on type
        super(assets, characterType, startX, startY, 100f, getClothingForNPCType(npcType));
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
     * Returns appropriate clothing configuration based on NPC type
     */
    private static ClothesContainer getClothingForNPCType(String npcType) {
        switch (npcType.toLowerCase()) {
            case "veridia_employee":
            case "office_worker":
                return new ClothesContainer(0, 103, 0, 0); // Office suit
            case "police":
            case "officer":
                return new ClothesContainer(0, 101, 0, 0); // Police suit
            case "firefighter":
                return new ClothesContainer(0, 102, 0, 0); // Firefighter suit
            case "delivery":
            case "delivery_person":
                return new ClothesContainer(0, 104, 0, 0); // Delivery suit
            case "resident_formal":
                return new ClothesContainer(0, 5, 0, 0); // Formal shirt and pants
            case "resident_casual":
                return new ClothesContainer(0, 6, 0, 0); // Casual shirt and pants
            case "resident_woman":
                return new ClothesContainer(0, 1, 0, 0); // Women's dress
            case "cold_weather":
                return new ClothesContainer(0, 2, 0, 0); // Coat/jacket 1
            case "winter_resident":
                return new ClothesContainer(0, 4, 0, 0); // Coat/jacket 2
            case "ally":
            case "resident":
            default:
                return new ClothesContainer(0, 5, 0, 0); // Default to casual clothing
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
        this.setClothes(new ClothesContainer(0, 103, 0, 0)); // Office suit
        this.dialogue = "Welcome to Veridia Corporation, " + department + " department.";
    }
    
    /**
     * Special method for creating police NPCs
     */
    public void setAsPoliceOfficer(String rank) {
        this.npcType = "police";
        this.setClothes(new ClothesContainer(0, 101, 0, 0)); // Police suit
        this.dialogue = "I'm " + rank + " " + name + ". How can I help you today?";
    }
    
    /**
     * Special method for creating firefighter NPCs
     */
    public void setAsFirefighter() {
        this.npcType = "firefighter";
        this.setClothes(new ClothesContainer(0, 102, 0, 0)); // Firefighter suit
        this.dialogue = "Stay safe out there! Fire safety is everyone's responsibility.";
    }
    
    /**
     * Change NPC's clothing dynamically (for story events)
     */
    public void changeClothingForEvent(int newOutfitType, String newDialogue) {
        this.changeOutfit(newOutfitType);
        this.dialogue = newDialogue;
        this.hasBeenTalkedTo = false; // Allow new conversation
    }
    
    // Getters and setters
    public String getDialogue() { return dialogue; }
    public void setDialogue(String dialogue) { this.dialogue = dialogue; }
    public String getNpcType() { return npcType; }
    public void setNpcType(String npcType) { 
        this.npcType = npcType; 
        this.setClothes(getClothingForNPCType(npcType)); // Update clothing when type changes
    }
    public boolean hasBeenTalkedTo() { return hasBeenTalkedTo; }
    public void resetConversation() { this.hasBeenTalkedTo = false; }
}