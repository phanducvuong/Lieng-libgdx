package com.ss.gameLogic;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.ss.core.util.GLayer;
import com.ss.core.util.GStage;
import com.ss.gameLogic.card.Number;
import com.ss.gameLogic.card.Type;
import com.ss.gameLogic.logic.Bet;
import com.ss.gameLogic.logic.Logic;
import com.ss.gameLogic.logic.Rule;
import com.ss.gameLogic.objects.Bot;
import com.ss.gameLogic.objects.Card;
import com.ss.gameLogic.ui.GamePlayUI;

import java.util.ArrayList;
import java.util.List;

public class Game {

  private Logic logic = Logic.getInstance();

  public Group gBackground, gCard, gBtn, gBot;

  public List<Bot> lsBot, lsBotActive; //reset lsBotActive when change numOfPlayer
  public List<Card> lsCardDown, lsCardUp;
  public Bot winner; //set null when player go out start screen
  public int numOfPlayer = 6;
  public long moneyBet = 10000;

  public GamePlayUI gamePlayUI;
  public Bet bet;

  public Game() {

    this.lsBotActive = new ArrayList<>();

    initLayer();
    initBotAndCard();

    getLsBotActive();
    gamePlayUI = new GamePlayUI(this);
    bet = new Bet(this);

  }

  private void initBotAndCard() {

    lsBot = new ArrayList<>();
    for (int i=0; i<6; i++)
      lsBot.add(new Bot(i));

    lsCardDown = new ArrayList<>();
    for (int i=0; i<52; i++)
      lsCardDown.add(new Card(null, null, true));

    lsCardUp = new ArrayList<>();
    for (Number number : Number.values())
      for (Type type : Type.values())
        lsCardUp.add(new Card(type, number, false));

  }

  private void initLayer() {

    gBackground = new Group();
    gCard = new Group();
    gBot = new Group();
    gBtn = new Group();

    GStage.addToLayer(GLayer.ui, gBackground);
    GStage.addToLayer(GLayer.ui, gCard);
    GStage.addToLayer(GLayer.ui, gBot);
    GStage.addToLayer(GLayer.ui, gBtn);

  }

  public void setNumOfPlayer(int num) {
    this.numOfPlayer = num;
  }

  public void getLsBotActive() {

    lsBotActive.clear();
    switch (numOfPlayer) {

      case 2:
        lsBotActive.add(lsBot.get(0));
        lsBotActive.add(lsBot.get(3));
        break;
      case 3:
        lsBotActive.add(lsBot.get(0));
        lsBotActive.add(lsBot.get(2));
        lsBotActive.add(lsBot.get(4));
        break;
      case 4:
        lsBotActive.add(lsBot.get(0));
        lsBotActive.add(lsBot.get(2));
        lsBotActive.add(lsBot.get(3));
        lsBotActive.add(lsBot.get(4));
        break;
      case 5:
        lsBotActive.add(lsBot.get(0));
        lsBotActive.add(lsBot.get(1));
        lsBotActive.add(lsBot.get(2));
        lsBotActive.add(lsBot.get(4));
        lsBotActive.add(lsBot.get(5));
        break;
      case 6:
        lsBotActive.add(lsBot.get(0));
        lsBotActive.add(lsBot.get(1));
        lsBotActive.add(lsBot.get(2));
        lsBotActive.add(lsBot.get(3));
        lsBotActive.add(lsBot.get(4));
        lsBotActive.add(lsBot.get(5));

    }

    for (Bot bot : lsBotActive) {
      bot.setAlive(true);
      bot.setActive(true);
      //todo: replace tempMoney to moneyPlayer in share preference
//      bot.setTotalMoney(logic.initMoneyBot(tempMoney));
      bot.setTotalMoney(1000000);
      bot.setTotalMoneyBet(10000);
    }

  }

  public void resetGame() {

    for (int i=0; i<lsCardDown.size(); i++) {
      lsCardDown.get(i).reset();
      lsCardUp.get(i).reset();
    }

    for (Bot bot : lsBotActive)
      bot.setTotalMoneyBet(moneyBet);

    bet.setTotalMoneyBet(moneyBet);

  }

  public void newRound() {

    bet.totalMoney = moneyBet * lsBotActive.size();
    gamePlayUI.eftLbTotalMoney(0);

    for (Bot bot : lsBotActive) {
      bot.setTotalMoney(bot.getTotalMoney() - moneyBet);
      bot.convertMoneyToString();
    }

  }

  public void startBet() {

    int indexBet = logic.getIdBotToStartBet(winner);
    lsBotActive.get(indexBet).isStartBet = true;
    bet.startBet(lsBotActive.get(indexBet));

  }

  public Bot findWinner() {

    List<Bot> tempLsBot = new ArrayList<>();
    for (Bot bot : lsBotActive)
      if (bot.isAlive())
        tempLsBot.add(bot);

    //label: update idBot of card in lsCardUp buy index lsBot alive
    for (Bot bot : tempLsBot)
      for (Card card : bot.lsCardUp)
        card.setIdBot(tempLsBot.indexOf(bot));

    winner = Rule.getInstance().getBotWinner(tempLsBot);
    logMoneyBot();
    return winner;

  }

  public void logMoneyBot() {

    for (Bot bot : lsBotActive)
      System.out.println(bot.id + "   TOTAL MONEY  " + bot.getTotalMoney() + "  MONEY BET  " + bot.getTotalMoneyBet());

  }

  public Bot getWinner() {

    for (Bot bot : lsBotActive)
      if (bot.isAlive())
        winner = bot;
    return winner;

  }

}
