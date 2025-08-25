package com.DMA173.soulsteps.Charecters;

/**
 * NPC class for characters like Lena, town residents, Veridia employees
 * Demonstrates inheritance - NPCs share Character functionality but have different behavior
 */
public class NPC extends Character {
    
    private String dialogue;
    private boolean hasBeenTalkedTo;
    private String npcType; // "resident", "veridia_employee", "ally"
    
    public NPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType) {
        super(assets, characterType, startX, startY, 100f); // NPCs move slower
        this.name = name;
        this.npcType = npcType;
        this.isInteractable = true;
        this.hasBeenTalkedTo = false;
        this.dialogue = "Hello there."; // Default dialogue
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
            }
        }
    }
    
    // Getters and setters
    public String getDialogue() { return dialogue; }
    public void setDialogue(String dialogue) { this.dialogue = dialogue; }
    public String getNpcType() { return npcType; }
    public boolean hasBeenTalkedTo() { return hasBeenTalkedTo; }
}
