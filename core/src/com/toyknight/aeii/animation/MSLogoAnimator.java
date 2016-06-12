package com.toyknight.aeii.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.toyknight.aeii.ResourceManager;
import com.toyknight.aeii.utils.FileProvider;

/**
 * @author toyknight 4/2/2015.
 */
public class MSLogoAnimator extends Animator {

    private final int WIDTH = 178;
    private final int HEIGHT = 178;

    private final Animation animation;
    private final ShapeRenderer shape_renderer;

    private float time_waited = 0f;

    public MSLogoAnimator() {
        Texture texture_ms_logo = new Texture(FileProvider.getAssetsFile("images/ms_logo.png"));
        animation = ResourceManager.createAnimation(texture_ms_logo, 8, 5, 0.065f);
        shape_renderer = new ShapeRenderer();
        shape_renderer.setAutoShapeType(true);
    }

    @Override
    public void render(Batch batch) {
        shape_renderer.begin();
        shape_renderer.set(ShapeRenderer.ShapeType.Filled);
        shape_renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        shape_renderer.rect(0, 0, Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight());
        shape_renderer.end();

        batch.begin();
        TextureRegion current_frame = animation.getKeyFrame(getStateTime(), true);
        int draw_x = (Gdx.app.getGraphics().getWidth() - WIDTH * ts / 48) / 2;
        int draw_y = (Gdx.app.getGraphics().getHeight() - HEIGHT * ts / 48) / 2;
        batch.draw(current_frame, draw_x, draw_y, WIDTH * ts / 48, HEIGHT * ts / 48);
        batch.end();
    }

    @Override
    public void update(float delta_time) {
        if (time_waited < 0.5f) {
            time_waited += delta_time;
        } else {
            addStateTime(delta_time);
        }
    }

    @Override
    public boolean isAnimationFinished() {
        return animation.isAnimationFinished(getStateTime());
    }

}
