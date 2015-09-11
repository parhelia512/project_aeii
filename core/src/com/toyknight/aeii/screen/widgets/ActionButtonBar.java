package com.toyknight.aeii.screen.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.SnapshotArray;
import com.toyknight.aeii.entity.GameCore;
import com.toyknight.aeii.manager.GameHost;
import com.toyknight.aeii.manager.GameManager;
import com.toyknight.aeii.ResourceManager;
import com.toyknight.aeii.entity.Ability;
import com.toyknight.aeii.entity.Unit;
import com.toyknight.aeii.screen.GameScreen;
import com.toyknight.aeii.utils.Platform;

import java.util.HashMap;

/**
 * Created by toyknight on 4/26/2015.
 */
public class ActionButtonBar extends HorizontalGroup {

    private final int ts;
    private final GameScreen screen;
    private final int PADDING_LEFT;
    private final int BUTTON_WIDTH;
    private final int BUTTON_HEIGHT;

    private final HashMap<String, CircleButton> buttons;

    public ActionButtonBar(GameScreen screen) {
        this.screen = screen;
        this.ts = screen.getContext().getTileSize();
        this.PADDING_LEFT = screen.getContext().getTileSize() / 4;
        this.BUTTON_WIDTH = getPlatform() == Platform.Desktop ? ts / 24 * 20 : ts / 24 * 40;
        this.BUTTON_HEIGHT = getPlatform() == Platform.Desktop ? ts / 24 * 21 : ts / 24 * 42;
        this.buttons = new HashMap<String, CircleButton>();
        initComponents();
    }

    private void initComponents() {
        CircleButton btn_buy = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(0), ts);
        btn_buy.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManager().setState(GameManager.STATE_SELECT);
                screen.showUnitStore();
            }
        });
        buttons.put("buy", btn_buy);
        CircleButton btn_occupy = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(1), ts);
        btn_occupy.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameHost.doOccupy();
                screen.onButtonUpdateRequested();
            }
        });
        buttons.put("occupy", btn_occupy);
        CircleButton btn_repair = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(1), ts);
        btn_repair.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameHost.doRepair();
                screen.onButtonUpdateRequested();
            }
        });
        buttons.put("repair", btn_repair);
        CircleButton btn_attack = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(2), ts);
        btn_attack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManager().beginAttackPhase();
                screen.onButtonUpdateRequested();
            }
        });
        buttons.put("attack", btn_attack);
        CircleButton btn_summon = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(3), ts);
        btn_summon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManager().beginSummonPhase();
                screen.onButtonUpdateRequested();
            }
        });
        buttons.put("summon", btn_summon);
        CircleButton btn_move = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(4), ts);
        btn_move.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManager().beginMovePhase();
                screen.onButtonUpdateRequested();
            }
        });
        buttons.put("move", btn_move);
        CircleButton btn_standby = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(5), ts);
        btn_standby.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameHost.doStandbyUnit();
                screen.onButtonUpdateRequested();
            }
        });
        buttons.put("standby", btn_standby);
        CircleButton btn_heal = new CircleButton(CircleButton.SMALL, ResourceManager.getActionIcon(7), ts);
        btn_heal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGameManager().beginHealPhase();
                screen.onButtonUpdateRequested();
            }
        });
        buttons.put("heal", btn_heal);
    }

    private GameManager getGameManager() {
        return screen.getGameManager();
    }

    private Platform getPlatform() {
        return screen.getContext().getPlatform();
    }

    private GameCore getGame() {
        return getGameManager().getGame();
    }

    public int getButtonHeight() {
        return BUTTON_HEIGHT;
    }

    public boolean isButtonAvailable(String name) {
        return buttons.get(name).isVisible();
    }

    @Override
    public void addActor(Actor actor) {
        actor.setVisible(true);
        super.addActor(actor);
    }

    @Override
    public void clear() {
        for (CircleButton button : buttons.values()) {
            button.setVisible(false);
        }
        super.clear();
    }

    public void updateButtons() {
        this.clear();
        if (screen.canOperate()) {
            Unit selected_unit = getGameManager().getSelectedUnit();
            switch (getGameManager().getState()) {
                case GameManager.STATE_ACTION:
                    getGameManager().createAttackablePositions(selected_unit);
                    if (getGameManager().canSelectedUnitAct()) {
                        if (getGameManager().hasEnemyWithinRange(selected_unit)) {
                            addActor(buttons.get("attack"));
                        }
                        if (selected_unit.hasAbility(Ability.NECROMANCER)
                                && getGameManager().hasTombWithinRange(selected_unit)) {
                            addActor(buttons.get("summon"));
                        }
                        if (selected_unit.hasAbility(Ability.HEALER)) {
                            addActor(buttons.get("heal"));
                        }
                        if (getGameManager().getGame().canOccupy(selected_unit, selected_unit.getX(), selected_unit.getY())) {
                            addActor(buttons.get("occupy"));
                        }
                        if (getGameManager().getGame().canRepair(selected_unit, selected_unit.getX(), selected_unit.getY())) {
                            addActor(buttons.get("repair"));
                        }
                    }
                    addActor(buttons.get("standby"));
                    break;
                case GameManager.STATE_BUY:
                    getGameManager().createAttackablePositions(selected_unit);
                    if (selected_unit.isCommander() && selected_unit.getTeam() == getGame().getCurrentTeam()
                            && getGame().isCastleAccessible(getGame().getMap().getTile(selected_unit.getX(), selected_unit.getY()))) {
                        addActor(buttons.get("buy"));
                        addActor(buttons.get("move"));
                        if (getGameManager().hasEnemyWithinRange(selected_unit)) {
                            addActor(buttons.get("attack"));
                        }
                    }
                    break;
                default:
                    //do nothing
            }
            this.layout();
        }
    }

    @Override
    public void layout() {
        SnapshotArray<Actor> children = getChildren();
        int btn_count = children.size;
        int margin_left = (screen.getViewportWidth() - btn_count * BUTTON_WIDTH - (btn_count + 1) * PADDING_LEFT) / 2;
        for (int i = 0; i < btn_count; i++) {
            children.get(i).setBounds(
                    margin_left + PADDING_LEFT + i * (BUTTON_WIDTH + PADDING_LEFT), 0, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
    }

    @Override
    public void draw(Batch batch, float parent_alpha) {
        super.draw(batch, parent_alpha);
    }

}