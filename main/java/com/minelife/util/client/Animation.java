package com.minelife.util.client;

import com.google.common.collect.Lists;
import com.sun.javafx.geom.Vec3f;

import java.util.List;

public class Animation {

    private List<Object> actions;
    private float rotX, rotY, rotZ, posX, posY, posZ, scalar;
    private float startX, startY, startZ;
    private int stage = 0;

    public Animation(float startX, float startY, float startZ) {
        actions = Lists.newArrayList();
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.posX = startX;
        this.posY = startY;
        this.posZ = startZ;
    }

    public List<Object> getActions() {
        return actions;
    }

    public Animation rotate(float rotX, float rotY, float rotZ) {
        actions.add(new SimpleRotation(rotX, rotY, rotZ));
        return this;
    }

    public Animation rotateTo(EnumRotation axis, float degree, float increment) {
        actions.add(new ComplexRotation(axis, degree, increment));
        return this;
    }

    public Animation translate(float x, float y, float z) {
        actions.add(new SimpleTranslation(x, y, z));
        return this;
    }

    public Animation translateTo(float x, float y, float z, float increment) {
        actions.add(new ComplexTranslation(x, y, z, increment));
        return this;
    }

    public Animation scale(float scalar) {
        actions.add(new SimpleScalar(scalar));
        return this;
    }

    public Animation scaleTo(float scalar, float increment) {
        actions.add(new ComplexScalar(scalar, increment));
        return this;
    }

    public Animation wait(int milliseconds) {
        actions.add(new Wait(milliseconds));
        return this;
    }

    public void animate() {
        if (actions.isEmpty()) {
            return;
        }

        Object currentAnimation = actions.get(0);

        if (currentAnimation instanceof SimpleRotation) {
            SimpleRotation simpleRotation = (SimpleRotation) currentAnimation;
            simpleRotation.execute();
            actions.remove(0);
            stage++;
        } else if (currentAnimation instanceof ComplexRotation) {
            ComplexRotation complexRotation = (ComplexRotation) currentAnimation;
            complexRotation.execute();

            if (complexRotation.done) {
                actions.remove(0);
                stage++;
            }
        } else if (currentAnimation instanceof SimpleTranslation) {
            SimpleTranslation simpleTranslation = (SimpleTranslation) currentAnimation;
            simpleTranslation.execute();
            actions.remove(0);
            stage++;
        } else if (currentAnimation instanceof ComplexTranslation) {
            ComplexTranslation complexTranslation = (ComplexTranslation) currentAnimation;
            complexTranslation.execute();

            if (complexTranslation.done) {
                actions.remove(0);
                stage++;
            }
        } else if (currentAnimation instanceof SimpleScalar) {
            SimpleScalar simpleScalar = (SimpleScalar) currentAnimation;
            simpleScalar.execute();
            actions.remove(0);
            stage++;
        } else if (currentAnimation instanceof ComplexScalar) {
            ComplexScalar complexScalar = (ComplexScalar) currentAnimation;
            complexScalar.execute();

            if (complexScalar.done) {
                actions.remove(0);
                stage++;
            }
        } else if (currentAnimation instanceof Wait) {
            Wait wait = (Wait) currentAnimation;
            wait.start();
            if (wait.done) {
                actions.remove(0);
                stage++;
            }
        }

    }

    public float rotX() {
        return rotX;
    }

    public float rotY() {
        return rotY;
    }

    public float rotZ() {
        return rotZ;
    }

    public float posX() {
        return posX;
    }

    public float posY() {
        return posY;
    }

    public float posZ() {
        return posZ;
    }

    public float scalar() {
        return scalar;
    }

    private void setRotX(final float rotX) {
        this.rotX = rotX;
    }

    private void setRotY(final float rotY) {
        this.rotY = rotY;
    }

    private void setRotZ(final float rotZ) {
        this.rotZ = rotZ;
    }

    private void setPosX(final float posX) {
        this.posX = posX;
    }

    private void setPosY(final float posY) {
        this.posY = posY;
    }

    private void setPosZ(final float posZ) {
        this.posZ = posZ;
    }

    private void setScalar(final float scalar) {
        this.scalar = scalar;
    }

    public float startX() {
        return startX;
    }

    public float startY() {
        return startY;
    }

    public float startZ() {
        return startZ;
    }

    public int stage() {
        return stage;
    }

    private class SimpleRotation {
        private float rotX, rotY, rotZ;

        public SimpleRotation(float rotX, float rotY, float rotZ) {
            this.rotX = rotX;
            this.rotY = rotY;
            this.rotZ = rotZ;
        }

        public void execute() {
            setRotX(rotX);
            setRotY(rotY);
            setRotZ(rotZ);
        }

    }

    private class ComplexRotation {
        private EnumRotation axis;
        private float degree, increment;
        private boolean done = false;
        private boolean isLess = false;
        private boolean started = false;

        public ComplexRotation(EnumRotation axis, float degree, float increment) {
            this.axis = axis;
            this.degree = degree;
            this.increment = increment;
        }

        public void execute() {
            float rotation = axis == EnumRotation.x ? rotX() : axis == EnumRotation.y ? rotY() : rotZ();
            if (!started) {
                isLess = rotation < degree;
                started = true;
            }

            if (isLess && rotation < degree) {
                if (axis == EnumRotation.x) {
                    setRotX(rotation + increment > degree ? degree : rotation + increment);
                } else if (axis == EnumRotation.y) {
                    setRotY(rotation + increment > degree ? degree : rotation + increment);
                } else {
                    setRotZ(rotation + increment > degree ? degree : rotation + increment);
                }

            }

            if (!isLess && rotation > degree) {
                if (axis == EnumRotation.x) {
                    setRotX(rotation - increment < degree ? degree : rotation - increment);
                } else if (axis == EnumRotation.y) {
                    setRotY(rotation - increment < degree ? degree : rotation - increment);
                } else {
                    setRotZ(rotation - increment < degree ? degree : rotation - increment);
                }
            }

            if (rotation == degree) {
                done = true;
            }
        }
    }

    private class SimpleTranslation {
        private float posX, posY, posZ;

        public SimpleTranslation(float posX, float posY, float posZ) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
        }

        public void execute() {
            setPosX(posX);
            setPosY(posY);
            setPosZ(posZ);
        }
    }

    private class ComplexTranslation {
        private float x, y, z, increment;
        private boolean done = false;
        private Vec3f v1, v2, v1v2;

        private boolean isLessX = false, isLessY = false, isLessZ = false;
        private boolean started = false;

        public ComplexTranslation(float x, float y, float z, float increment) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.increment = increment;
        }

        public void execute() {

            if (!started) {
                started = true;

                v1 = new Vec3f(posX(), posX(), posZ());
                v2 = new Vec3f(x, y, z);

                v1v2 = new Vec3f(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);

                if (x < posX()) {
                    isLessX = true;
                }

                if (y < posY()) {
                    isLessY = true;
                }

                if (z < posZ()) {
                    isLessZ = true;
                }
            }

            if (isLessX) {
                float x = posX - Math.abs(increment * v1v2.x);
                setPosX(x < this.x ? this.x : x);
            } else {
                float x = posX + Math.abs(increment * v1v2.x);
                setPosX(x > this.x ? this.x : x);
            }

            if (isLessY) {
                float y = posY - Math.abs(increment * v1v2.y);
                setPosY(y < this.y ? this.y : y);
            } else {
                float y = posY + Math.abs(increment * v1v2.y);
                setPosY(y > this.y ? this.y : y);
            }

            if (isLessZ) {
                float z = posZ - Math.abs(increment * v1v2.z);
                setPosZ(z < this.z ? this.z : z);
            } else {
                float z = posZ + Math.abs(increment * v1v2.z);
                setPosZ(z > this.z ? this.z : z);
            }

            if (posX == x && posY == y && posZ == z) {
                done = true;
            }
        }
    }

    private class SimpleScalar {
        private float scalar;

        public SimpleScalar(float scalar) {
            this.scalar = scalar;
        }

        public void execute() {
            setScalar(scalar);
        }
    }

    private class ComplexScalar {
        private float scalar;
        private float increment;
        private boolean done = false;
        private boolean isLess = false;
        private boolean started = false;

        public ComplexScalar(float scalar, float increment) {
            this.scalar = scalar;
            this.increment = increment;
        }

        public void execute() {

            if (!started) {
                isLess = scalar() < scalar;
                started = true;
            }

            if (isLess && scalar() < scalar) {
                setScalar(scalar() + increment > scalar ? scalar : scalar() + increment);
            }

            if (!isLess && scalar() > scalar) {
                setScalar(scalar() - increment < scalar ? scalar : scalar() - increment);
            }

            if (scalar() == scalar) {
                done = true;
            }
        }
    }

    private class Wait {
        private long duration;
        private boolean started = false;
        private long startTime;
        private boolean done = false;

        public Wait(long duration) {
            this.duration = duration;
        }

        public void start() {
            if (!started) {
                started = true;
                startTime = System.currentTimeMillis();
            } else {
                done = System.currentTimeMillis() - startTime > duration;
            }
        }

    }

    public enum EnumRotation {
        x, y, z;
    }
}