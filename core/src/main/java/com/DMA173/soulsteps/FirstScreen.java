package com.DMA173.soulsteps;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class FirstScreen extends ScreenAdapter {

    OrthographicCamera camera;
    TiledMap map;
    OrthogonalTiledMapRenderer renderer;

    Texture playerTex;
    Vector2 playerPos;
    float speed = 100;

    ArrayList<Rectangle> collisionRects = new ArrayList<>();
    SpriteBatch batch;

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 1200);

        map = new TmxMapLoader().load("mymap.tmx"); // Put your map in assets folder
        renderer = new OrthogonalTiledMapRenderer(map);

        playerTex = new Texture("player.png");
        playerPos = new Vector2(100, 100);

        MapLayer collisionLayer = map.getLayers().get("Collisions");
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    collisionRects.add(rect);
                }
            }
        }
    }

    @Override
    public void render(float delta) {
                ScreenUtils.clear(Color.BLACK);
        Vector2 oldPos = new Vector2(playerPos);
        
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  playerPos.x -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) playerPos.x += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    playerPos.y += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  playerPos.y -= speed * delta;

        // Rectangle playerRect = new Rectangle(playerPos.x, playerPos.y, playerTex.getWidth(), playerTex.getHeight());
        // for (Rectangle rect : collisionRects) {
        //     if (playerRect.overlaps(rect)) {
        //         playerPos.set(oldPos);
        //         break;
        //     }
        // }

        // camera.position.set(playerPos.x + playerTex.getWidth() / 2f, playerPos.y + playerTex.getHeight() / 2f, 0);
        camera.update();

        renderer.setView(camera);
        renderer.render();
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float scale = 50f; // Scale down player to 30%
        batch.draw(playerTex, playerPos.x, playerPos.y,
                playerTex.getWidth() * scale, playerTex.getHeight() * scale);

        batch.end();
        renderer.getBatch().begin();
        renderer.getBatch().draw(playerTex, playerPos.x, playerPos.y);
        renderer.getBatch().end();
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        playerTex.dispose();
    }
}