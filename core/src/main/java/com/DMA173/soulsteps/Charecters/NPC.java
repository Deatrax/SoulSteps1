package com.DMA173.soulsteps.Charecters;

public class NPC extends Character {
    
    private String dialogue;
    private String npcType;

    public NPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType) {
        super(assets, characterType, startX, startY, 0f, getAppearanceForNPCType(npcType)); // NPCs don't move
        this.name = name;
        this.npcType = npcType;
        this.isInteractable = true;
        this.dialogue = "Hello."; // Default dialogue
    }

    private static ClothesContainer getAppearanceForNPCType(String npcType) {
        switch (npcType.toLowerCase()) {
            case "resident_woman":
                return new ClothesContainer(5, 1); // Long hair + Women's dress
            case "resident_casual":
            default:
                return new ClothesContainer(3, 5); // Default: medium hair + casual clothing
        }
    }

    @Override
    public void update(float delta) {
        // NPCs are stationary for now
        updateStateTime(delta);
        this.setMoving(false);
    }

    // Getters and setters
    public String getDialogue() { return dialogue; }
    public void setDialogue(String dialogue) { this.dialogue = dialogue; }
}