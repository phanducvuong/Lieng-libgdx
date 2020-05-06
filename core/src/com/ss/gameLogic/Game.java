package com.ss.gameLogic;

import com.badlogic.gdx.scenes.scene2d.Group;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.ss.GMain;
import com.ss.core.util.GLayer;
import com.ss.core.util.GStage;
import com.ss.gameLogic.card.Number;
import com.ss.gameLogic.card.Type;
import com.ss.gameLogic.config.C;
import com.ss.gameLogic.config.Config;
import com.ss.gameLogic.effects.Effect;
import com.ss.gameLogic.logic.Bet;
import com.ss.gameLogic.logic.DivideCard;
import com.ss.gameLogic.logic.Logic;
import com.ss.gameLogic.logic.Rule;
import com.ss.gameLogic.objects.Bot;
import com.ss.gameLogic.objects.Card;
import com.ss.gameLogic.ui.GamePlayUI;
import com.ss.gameLogic.ui.StartScene;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Game {

  private Logic logic;
  private Effect effect;

  public Group gBackground,
          gCard,
          gBtn,
          gAlert,
          gBot,
          gEffect,
          gChip,
          gStartScene,
          gTest;

  public List<Bot> lsBot, lsBotActive; //reset lsBotActive when change numOfPlayer
  public List<Card> lsCardDown, lsCardUp;
  public Bot winner; //set null when player go out startScene screen
  public int numOfPlayer = 2, countShowFullscreen = 0, countPlayWinInGame = 0;
  public long moneyBet = 10000;
  private long tempMoneyPlayer = 20000;
  public boolean isInGame = false;

  public DivideCard divideCard;
  public GamePlayUI gamePlayUI;
  public StartScene startScene;
  public Bet bet;

  public Game() {

    gBackground = new Group();
    gBot = new Group();
    gBtn = new Group();
    gChip = new Group();
    gCard = new Group();
    gEffect = new Group();
    gAlert = new Group();
    gStartScene = new Group();

    this.lsBotActive = new ArrayList<>();
    this.logic = Logic.getInstance();
    this.logic.setGame(this);
    effect = Effect.getInstance(this);

    initLayer();
    initBotAndCard();

    startScene = new StartScene(this, gStartScene);
    startScene.addToScene();
    divideCard = new DivideCard(this);
    gamePlayUI = new GamePlayUI(this);
    bet = new Bet(this);

  }

  private void initBotAndCard() {

    lsBot = new ArrayList<>();
    for (int i=0; i<6; i++)
      lsBot.add(new Bot(gBot, gEffect, i));

    lsCardDown = new ArrayList<>();
    for (int i=0; i<52; i++)
      lsCardDown.add(new Card(null, null, true));

    lsCardUp = new ArrayList<>();
    for (Number number : Number.values())
      for (Type type : Type.values())
        lsCardUp.add(new Card(type, number, false));

  }

  public void initLayer() {

    GStage.addToLayer(GLayer.ui, gBackground);
    GStage.addToLayer(GLayer.ui, gBot);
    GStage.addToLayer(GLayer.ui, gChip);
    GStage.addToLayer(GLayer.ui, gCard);
    GStage.addToLayer(GLayer.ui, gBtn);
    GStage.addToLayer(GLayer.ui, gStartScene);
    GStage.addToLayer(GLayer.ui, gEffect);
    GStage.addToLayer(GLayer.ui, gAlert);

    gTest = new Group();
    GStage.addToLayer(GLayer.ui, gTest);

  }

  public void setData(int numOfPlayer, long moneyBet) {
    this.numOfPlayer = numOfPlayer;
    this.moneyBet = moneyBet;

    for (Bot bot : lsBot) {
      bot.reset();
      bot.removeActor();
    }

    countShowFullscreen = 0;

    gamePlayUI.lbMoneyBetInGame.setText(C.lang.bet + ": " + logic.convertMoneyBet(moneyBet));
    gamePlayUI.showBtnNewRound();
    getLsBotActive();
  }

  private void getLsBotActive() {

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
      if (lsBotActive.indexOf(bot) == 0)
        bot.setTotalMoney(GMain.pref.getLong("money"));
      else
        bot.setTotalMoney(logic.initMoneyBot(lsBotActive.get(0).getTotalMoney(), moneyBet));
      bot.setName();
      bot.setAlive(true);
      bot.setActive(true);
      bot.addToScene();
      bot.convertTotalMoneyToString();
    }

  }

  private void resetGame() {

    gamePlayUI.reset();
    bet.reset();
    divideCard.reset();
    if (winner == null)
      divideCard.setTurn(0);
    else
      divideCard.setTurn(lsBotActive.indexOf(winner));

    for (Bot bot : lsBotActive) {
      bot.reset();
      bot.setTotalMoneyBet(moneyBet);
    }

    bet.setTotalMoneyBet(moneyBet);

  }

  public void newRound() {

    countShowFullscreen += 1;
    if (countShowFullscreen == Config.SHOW_FULL_SCREEN) {
      gamePlayUI.showAdsFullscreen();
      countShowFullscreen = 0;
    }

    if (lsBotActive.get(0).getTotalMoney() < moneyBet)
      gamePlayUI.showAlertAds();
    else {
      resetGame();

//      System.out.println(moneyBet);

      bet.totalMoney = moneyBet * lsBotActive.size();
      gamePlayUI.eftLbTotalMoney(0);

      for (Bot bot : lsBotActive) {
        bot.setTotalMoney(bot.getTotalMoney() - moneyBet);
        bot.convertTotalMoneyToString();
        bot.chipOutNewRound(this, moneyBet);
      }

      logic.saveMoney(lsBotActive.get(0).getTotalMoney());

      logMoneyBot();

      //nếu player thắng trên số ván quy định => can thiệp vào bài của player => thua
      divideCard.chkTimePlayerWinInGame(lsCardUp);
      divideCard.nextTurn();

      isInGame = true;
    }

  }

  public void startBet() {

    if (lsBotActive.size() > 0) {
      if (winner != null && winner.id == 0)
        gamePlayUI.showBtnBet();
      else if (winner == null)
        gamePlayUI.showBtnBet();

      int indexBet = logic.getIdBotToStartBet(lsBotActive, winner);
      lsBotActive.get(indexBet).isStartBet = true;
      bet.startBet(lsBotActive.get(indexBet));
    }

  }

  public void findWinner() {

    List<Bot> tempLsBot = new ArrayList<>();
    for (Bot bot : lsBotActive)
      if (bot.isAlive()) {
        tempLsBot.add(bot);
        bot.hideConditionBet();
      }

    //label: update idBot of card in lsCardUp buy index lsBot alive
    for (Bot bot : tempLsBot)
      for (Card card : bot.lsCardUp)
        card.setIdBot(tempLsBot.indexOf(bot));

    winner = Rule.getInstance().getBotWinner(tempLsBot);
    tempLsBot.remove(winner);

    //if money bot is < money bet or == 0 => new bot
    gBot.addAction(
            sequence(
                    delay(1f),
                    run(() -> {
                      gamePlayUI.showAllWhenFindWinner(tempLsBot, winner);
                      gamePlayUI.showBannerWin(winner);
                    })
            )
    );

    logMoneyBot();
  }

  public void logMoneyBot() {

    System.out.println("-----------------------------------");
    for (Bot bot : lsBotActive)
      System.out.println(bot.id + "   TOTAL MONEY  " + bot.getTotalMoney() + "  MONEY BET  " + bot.getTotalMoneyBet());

  }

  public void getWinner() {

    for (Bot bot : lsBotActive)
      if (bot.isAlive())
        winner = bot;

    if (winner != null) {
      winner.hideConditionBet();
      gamePlayUI.showCardWinner(winner);
      gamePlayUI.showBannerWin(winner);
    }

    //todo: show bot win

  }

  public void resetData() {

    lsBotActive.clear();
    gamePlayUI.reset();
    bet.reset();
    divideCard.reset();
    winner = null;
    isInGame = false;

  }

}
