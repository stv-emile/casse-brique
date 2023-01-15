/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.winter.gamemd.brickbreaker;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.beans.binding.Bindings;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.getip;


public class BrickBreakerFactory implements EntityFactory {

    @Spawns("ball")
    public Entity newBall(SpawnData data) {
        System.out.println("Creating Ball...");
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().density(0.3f).restitution(1.0f));
        physics.setOnPhysicsInitialized(() -> physics.setLinearVelocity(5 * 60, -5 * 60));

        System.out.println("set endGame = true : When player1score == 10 or player2score == 10");
        //var endGame = getip("player1score").isEqualTo(10).or(getip("player2score").isEqualTo(10));
        var endGame = getip("player1score").isEqualTo(1);


        System.out.println("Create Emitter Particle");
        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();

        System.out.println("set emitter particle  Start-color to : LIGHT-YELLOW (if endGame true) LIGHT-YELLOW (otherwise)");
        emitter.startColorProperty().bind(
                Bindings.when(endGame)
                        .then(Color.BLUEVIOLET)
                        .otherwise(Color.LIGHTYELLOW)
        );

        System.out.println("set emitter particle  end-color to : RED (if endGame true) LIGHTBLUE (otherwise)");
        emitter.endColorProperty().bind(
                Bindings.when(endGame)
                        .then(Color.ORANGE)
                        .otherwise(Color.LIGHTBLUE)
        );

        System.out.println("apply a BlendMode, Size and Emission Rate");
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSize(3, 10);
        emitter.setEmissionRate(1);

        System.out.println("Create en return entity builded : EntiyType=Ball, ComponentType=Ball BoundingShape=circle, physics, collidable, paricle=emile");
        return entityBuilder(data)
                .type(EntityType.BALL)
                .bbox(new HitBox(BoundingShape.circle(3)))
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new ParticleComponent(emitter))
                .with(new BallComponent())
                .build();
    }

    @Spawns("paddle")
    public Entity newBat(SpawnData data) {

        System.out.println("to Create Entity based on EntityType, call entityBuilder: Bat");

        System.out.println("get if it a player or not : by isPlayer from data ");
        boolean isPlayer = data.get("isPlayer");

        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);
        System.out.println("Create en return entity builded : EntiyType=PaddleComponentType= Bat or EnemyBat " +
                "Rectangle, physics, collidable");
        return entityBuilder()
                .from(data)
                .type(EntityType.PADDLE)
                .viewWithBBox(new Rectangle(80, 15, Color.LIGHTGRAY))
                .with(new CollidableComponent(true))
                .with(physics)
                .with(new PaddleComponent())
                .build();
    }

    @Spawns("brick")
    public Entity newBrick(SpawnData data) {

        System.out.println("to Create Entity based on EntityType, call entityBuilder: brick");

        boolean isBreakable = data.get("isBreakable");

        PhysicsComponent physics = new PhysicsComponent();

        physics.setBodyType(BodyType.STATIC);
        System.out.println("Create en return entity builded : EntiyType=Brick Rectangle, physics, collidable");
        return entityBuilder()
                .from(data)
                .type(EntityType.PADDLE)
                .viewWithBBox(new Rectangle(20, 10, Color.BISQUE))
                .with(new CollidableComponent(true))
                .with(physics)
                .with(new PaddleComponent())
                .build();
    }
}
