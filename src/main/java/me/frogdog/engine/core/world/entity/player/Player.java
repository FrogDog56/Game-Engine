package me.frogdog.engine.core.world.entity.player;

import me.frogdog.engine.core.EngineManager;
import me.frogdog.engine.core.input.Keyboard;
import me.frogdog.engine.core.world.Model;
import me.frogdog.engine.core.world.entity.Entity;
import me.frogdog.engine.core.world.terrain.Terrain;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Player extends Entity {

    private static final float SPEED = 20.0f;
    private static final float TURN_SPEED = 100.0f;
    private static final float GRAVITY = -50.0f;
    private static final float JUMP_POWER = 30.0f;
    private static final float TERRAIN_HEIGHT = 0.0f;

    private float currentTurnSpeed = 0;
    private float currentSpeed = 0;
    private float currentUpSpeed = 0;

    private boolean isInAir = false;

    public Player(Model model, Vector3f pos, Vector3f rotation, float scale) {
        super(model, pos, rotation, scale);
    }

    public void update(Keyboard keyboard, Terrain terrain) {
        input(keyboard);
        super.incRotation(0, currentTurnSpeed * EngineManager.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * EngineManager.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotation().y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotation().y)));
        super.incPos(dx, 0, dz);
        this.currentUpSpeed += GRAVITY * EngineManager.getFrameTimeSeconds();
        float terrainHeight = terrain.getTerrainHeight(super.getRotation().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight) {
            this.currentUpSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }
        super.incPos(0, currentUpSpeed * EngineManager.getFrameTimeSeconds(), 0);
    }

    public void input(Keyboard keyboard) {
        if (keyboard.isKeyDown(GLFW.GLFW_KEY_W)) {
            this.currentSpeed = SPEED;
        } else if (keyboard.isKeyDown(GLFW.GLFW_KEY_S)) {
            this.currentSpeed = -SPEED;
        } else {
            this.currentSpeed = 0;
        }

        if (keyboard.isKeyDown(GLFW.GLFW_KEY_A)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else if (keyboard.isKeyDown(GLFW.GLFW_KEY_D)) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }

        if (keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
            jump();
        }
    }

    private void jump() {
        if (!isInAir) {
            this.currentUpSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

}
