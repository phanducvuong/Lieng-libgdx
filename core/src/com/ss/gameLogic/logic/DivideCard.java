package com.ss.gameLogic.logic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.ss.core.action.exAction.GSimpleAction;
import com.ss.core.util.GStage;
import com.ss.gameLogic.Game;
import com.ss.gameLogic.effects.Effect;
import com.ss.gameLogic.objects.Bot;
import com.ss.gameLogic.objects.Card;

import java.util.Collections;
import java.util.Random;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class DivideCard {

  private Game game;
  private Logic logic = Logic.getInstance();
  private Rule rule = Rule.getInstance();
  private Effect effect;

  private int turn = 0, turnCardDown = -1, countTurn = -1; //reset when new game
  private Bot botPresent;

  public DivideCard(Game game) {

    this.game = game;
    effect = Effect.getInstance(game);

  }

  private void startDivide(Card cardDown) {

    countTurn++; // count number of cardDown in each player (maximum is 3)
    if (countTurn < game.numOfPlayer*3) {
      botPresent.lsCardDown.add(cardDown);

      cardDown.setActive(true);
      Card cardUp = game.lsCardUp.get(turnCardDown);
      botPresent.lsCardUp.add(cardUp);

      cardDown.getCard().addAction(GSimpleAction.simpleAction(this::divide));
    }
    else {

      moveCardResidual();
      game.gCard.addAction(sequence(
              delay(.25f),
              run(() -> {
                effect.formatCardDown(game.lsBotActive);
                logic.findIdRuleOfLsBot(game.lsBotActive);
              }),
              delay(1f),
              run(() -> game.startBet())
      ));

    }

  }

  private boolean divide(float dt, Actor a) {

    turn += 1;
    Image card = (Image) a;
    card.setZIndex(1000);
    Vector2 v = logic.getPosByIdBot(botPresent.id);

    effect.divide(card, v.x, v.y);
    game.gCard.addAction(sequence(
            delay(.1f),
            run(this::nextTurn)
    ));

    return true;
  }

  public void nextTurn() {

//    turn++;
    turnCardDown++;
    if (turn >= game.numOfPlayer)
      turn = 0;
    if (game.lsBotActive.size() > 0) {
      botPresent = game.lsBotActive.get(turn);
      startDivide(game.lsCardDown.get(turnCardDown));
    }

  }

  private void resetDesk() {

    for (int i = game.lsCardDown.size()-1; i>=0; i--) {
      Card cardDown = game.lsCardDown.get(i);
      int offset = (game.lsCardDown.size() - i)/2;
      cardDown.setPosition(GStage.getWorldWidth()/2 - cardDown.getWidth()/2 - offset,
                            GStage.getWorldHeight()/2 - cardDown.getHeight()/2 + 20 - offset);
      cardDown.addCardToScene(game.gCard);
    }

  }

  private void moveCardResidual() {

    for (int i=game.lsCardDown.size()-1; i>=0; i--) {
      Card card = game.lsCardDown.get(i);
      if (!card.isActive())
        effect.moveCardResidual(card, i);
    }

  }

  private void shuffleLsCardUp() {
    Collections.shuffle(game.lsCardUp, new Random());
  }

  public void reset() {

    turn = 0;
    turnCardDown = -1;
    countTurn = -1;
    botPresent = null;

    for (int i=0; i<game.lsCardDown.size(); i++) {
      game.lsCardDown.get(i).reset();
      game.lsCardUp.get(i).reset();
    }

    resetDesk();
    shuffleLsCardUp();

  }

  public void setTurn(int turn) {
    this.turn = turn;
  }

}
