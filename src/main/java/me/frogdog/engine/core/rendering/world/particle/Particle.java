package me.frogdog.engine.core.rendering.world.particle;

import me.frogdog.engine.core.EngineManager;
import me.frogdog.engine.utils.Consts;
import org.joml.Vector3f;

public class Particle {

    private ParticleTexture particleTexture;
    private Vector3f position;
    private Vector3f velocity;
    private float gravityEffect;
    private float lifeLength;
    private float rotation;
    private float scale;
    private float blend;

    private float elapsedTime = 0;

    public Particle(ParticleTexture particleTexture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale) {
        this.particleTexture = particleTexture;
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
    }

    public boolean update() {
        velocity.y += Consts.GRAVITY * gravityEffect * EngineManager.getFrameTimeSeconds();
        Vector3f change = new Vector3f(velocity);
        change.mul(EngineManager.getFrameTimeSeconds());
        position.add(change);
        elapsedTime += EngineManager.getFrameTimeSeconds();
        return elapsedTime < lifeLength;
    }

    private void updateStage() {
        float lifeFactor = elapsedTime / lifeLength;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public ParticleTexture getParticleTexture() {
        return particleTexture;
    }

    public float getBlend() {
        return blend;
    }
}
