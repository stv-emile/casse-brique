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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.winter.gamemd.brickbreaker;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.ui.UI;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BrickBreakerApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {

        System.out.println("setting init --> setTitle : Brick, setVersion : beta.1.0 ");
        settings.setTitle("BrickBreaker");
        settings.setVersion("1.0");
    }

    private PaddleComponent playerPaddle;
    private LinkedList<Entity> brickEntities;

    @Override
    protected void initInput() {
        System.out.println("init Input()");

        System.out.println("to Input Create UserAction for ---> up ");
        getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                System.out.println("Action Up hit -----> call playerBat.down()");
                playerPaddle.up();
            }

            @Override
            protected void onActionEnd() {
                System.out.println("Action Up End -----> call playerBat.stop()");
                playerPaddle.stop();
            }
        }, KeyCode.LEFT);

        System.out.println("to Input Create UserAction for ---> Down ");
        getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                System.out.println("Action Down hit -----> call playerBat.down()");
                playerPaddle.down();
            }

            @Override
            protected void onActionEnd() {
                System.out.println("Action Down End -----> call playerBat.stop()");
                playerPaddle.stop();
            }
        }, KeyCode.RIGHT);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        System.out.println("initGameVars()");
        System.out.println("create 2 variable for the Game : player1score and player2score");
        vars.put("player1score", 2);
        //vars.put("player2score", 0);
    }

    @Override
    protected void initGame() {
        System.out.println("Game init");
        System.out.println("add event listener to \"player1score\" property");
        getWorldProperties().<Integer>addListener("player1score", (old, newScore) -> {
            if (newScore == 0) {
                showGameOver("Player 1");
                System.out.println(" Player 1 : newScore == 0 for player1score -> Game over");
            }
        });

      /*  System.out.println("add event listener to \"player2score\" property");
        getWorldProperties().<Integer>addListener("player2score", (old, newScore) -> {
            if (newScore == 11) {
                showGameOver("Player 2");
                System.out.println(" Player 2 : newScore == 11 for player2score-> Game over");
            }
        });*/

        System.out.println("add BrickBreakerFactory ======");
        getGameWorld().addEntityFactory(new BrickBreakerFactory());

        System.out.println("Set Background Color =======");
        getGameScene().setBackgroundColor(Color.rgb(0, 0, 5));

        System.out.println("init Screen Bounds =======");
        initScreenBounds();

        System.out.println("init Game Objects =======");
        initGameObjects();
    }

    @Override
    protected void initPhysics() {
        System.out.println("initPhysics().");

        System.out.println("to Physics world --> put gravity at 0, 0");
        getPhysicsWorld().setGravity(0, 0);

        System.out.println("create a collision Handler for Ball, and Wall Entity");
        System.out.println("to Physics world --> add collision Ball-Wall Handler ");
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BALL, EntityType.WALL) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                if (boxB.getName().equals("BOT")) {
                    System.out.println("Ball collide Left side Wall --> increment player2score");
                    inc("player1score", -1);
                }
                System.out.println("hitboxname --> "+ boxB.getName());
                System.out.println("play hit_wall Sound and shake");
                play("hit_wall.wav");
                getGameScene().getViewport().shakeTranslational(5);
            }
        });

        System.out.println("create Collision handler --> Ball-Paddle Handler ");
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BALL, EntityType.PADDLE) {
            @Override
            protected void onCollisionBegin(Entity a, Entity bat) {
                System.out.println("Ball collide with Player bat --> play hit_wall Sound");
                play("hit_bat.wav");
                System.out.println("playHitAnimation for bat");
                playHitAnimation(bat);
            }
        });

        System.out.println("to Physics world --> add collision Ball-Brick handler");
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BALL, EntityType.BRICK) {
            @Override
            protected void onCollisionBegin(Entity a, Entity bat) {
                System.out.println("Ball collide with Player bat --> play hit_wall Sound");
                play("hit_bat.wav");
                System.out.println("playHitAnimation for bat");
                playHitAnimation(bat);
            }
        });

        //getPhysicsWorld().addCollisionHandler();


        //System.out.println("to Physics world --> copy this collision Handler for Ball-Enemy_Bat handler");
        //getPhysicsWorld().addCollisionHandler(ballBatHandler.copyFor(EntityType.BALL, EntityType.ENEMY_BAT));


    }

    @Override
    protected void initUI() {
        System.out.println("initUI() --> ");

        System.out.println("instantiate the MainUiController : controller");
        MainUIController controller = new MainUIController();

        System.out.println("from main.fxml file Create a root and add the controller to it --> root. ");
        UI ui = getAssetLoader().loadUI("main.fxml", controller);

        System.out.println("add game data to controller : player1score");
        controller.getLabelScorePlayer().textProperty().bind(getip("player1score").asString());
        //controller.getLabelScoreEnemy().textProperty().bind(getip("player2score").asString());

        System.out.println("add the root ui to the GameScene ");
        getGameScene().addUI(ui);
    }

    private void initScreenBounds() {
        System.out.println("init ScreenBounds()--> from EntityType.Wall Create an Entity and add it to GameWorld");

        Entity walls = entityBuilder()
                .type(EntityType.WALL)
                .collidable()
                .buildScreenBounds(150);

        getGameWorld().addEntity(walls);
    }

    private void initGameObjects() {
        System.out.println("initGameObjects --> Creating objects for the game :");

        Entity ball = spawn("ball", getAppWidth() / 2 - 5, getAppHeight() / 2 - 5);
        System.out.println("Create Ball.");

        Entity paddle = spawn("paddle", new SpawnData(getAppWidth() / 2, getAppHeight() - 30).put("isPlayer", true));
        System.out.println("Create Paddle --> data : isPlayer = true");

     /*   Entity brick = spawn("brick", new SpawnData(3 * getAppWidth() / 4 - 20, getAppHeight() / 2 - 30).put("isBreakable", true));
        System.out.println("Create Brick --> data : isPlayer = false");*/

        for (int i=100; i < 700; i+=40){
            for(int j=50; j< 150; j+=20){
                System.out.println("Create Brick --> data : isBreakable, health, Treasure, ..");
                //brickEntities.add(
                        spawn("brick", new SpawnData( i, j).put("isBreakable", true));
                //);
            }
        }

        System.out.println("set playerPaddle field of the game with : BrickComponent class of paddle.");
        playerPaddle = paddle.getComponent(PaddleComponent.class);
    }

    private void playHitAnimation(Entity bat) {
        System.out.println("playHitAnimation --> animation on hit");
        animationBuilder()
                .autoReverse(true)
                .duration(Duration.seconds(0.5))
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .rotate(bat)
                .from(FXGLMath.random(-25, 25))
                .to(0)
                .buildAndPlay();
    }

    private void showGameOver(String winner) {
        System.out.println("GameOver("+winner+") --> show dialogBox");
        getDialogService().showMessageBox(winner + " Thanks for playing", getGameController()::exit);
        getDialogService().showMessageBox("Game Over \nThanks for playing ", getGameController()::exit);
    }

    public static void main(String[] args) {
        System.out.println("from main -->> Game Starting...");
        launch(args);
        System.out.println(getAppHeight());
        System.out.println(getAppWidth());
    }
}
