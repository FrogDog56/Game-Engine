package me.frogdog.engine.core.input.mouse;

import me.frogdog.engine.game.Main;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class Mouse {

    private final Vector2d previousPos, currentPos;
    private final Vector2f displVec;

    public boolean erase = true;
    private boolean inWindow = false, leftButtonPress = false, rightButtonPress = false, leftButtonUp = false, rightButtonUp = false, scrollUp = false, scrollDown = false;

    public Mouse() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void init() {
        GLFW.glfwSetCursorPosCallback(Main.getWindow().getWindow(), ((window, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        }));

        GLFW.glfwSetCursorEnterCallback(Main.getWindow().getWindow(), ((window, entered) -> {
            inWindow = entered;
        }));

        GLFW.glfwSetMouseButtonCallback(Main.getWindow().getWindow(), ((window, button, action, mods) -> {
            leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
            leftButtonUp = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE;
            rightButtonUp = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_RELEASE;
        }));

        GLFW.glfwSetScrollCallback(Main.getWindow().getWindow(), ((window, xoffset, yoffset) -> {
            if (yoffset > 0) {
                scrollUp = true;
            } else {
                scrollUp = false;
            }

            if (yoffset < 0) {
                scrollDown = true;
            } else {
                scrollDown = false;
            }
        }));
    }

    public void input() {
        displVec.x = 0;
        displVec.y = 0;
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double x = currentPos.x - previousPos.x;
            double y = currentPos.y - previousPos.y;
            boolean rotateX = x != 0;
            boolean rotateY = y != 0;
            if (rotateX) {
                displVec.y = (float) x;
            }

            if (rotateY) {
                displVec.x = (float) y;
            }
        }

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public void clear() {
        leftButtonUp = false;
        rightButtonUp = false;
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public Vector2f getCurrentPos() {
        return new Vector2f((float) currentPos.x, (float) currentPos.y * 1);
    }

    public Vector2f getHudPos() {
        return new Vector2f((((float) currentPos.x - Main.getWindow().getWidth() / 2) / 100) / 8, ((((float) currentPos.y - Main.getWindow().getHeight() / 2) / 100) / 4.5f ) * -1);
    }

    public boolean isLeftButtonPress() {
        return leftButtonPress;
    }

    public boolean isRightButtonPress() {
        return rightButtonPress;
    }

    public boolean isLeftButtonUp() {
        return leftButtonUp;
    }

    public boolean isRightButtonUp() {
        return rightButtonUp;
    }

    public boolean isScrollUp() {
        return scrollUp;
    }

    public boolean isScrollDown() {
        return scrollDown;
    }
}
