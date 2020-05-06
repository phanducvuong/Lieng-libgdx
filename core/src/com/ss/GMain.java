package com.ss;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.platform.IPlatform;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.ss.core.effect.SoundEffects;
import com.ss.core.exSprite.particle.GParticleSystem;
import com.ss.core.util.GAssetsManager;
import com.ss.core.util.GDirectedGame;
import com.ss.core.util.GScreen;
import com.ss.core.util.GStage;
import com.ss.core.util.GStage.StageBorder;
import com.ss.gameLogic.config.C;
import com.ss.gameLogic.config.Config;
import com.ss.gameLogic.logic.Logic;
import com.ss.scenes.GameScene;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GMain extends GDirectedGame {

  public static boolean isDebug = false;
  public static final boolean isTest = false;
  public static int screenHeight = 0;
  public static int screenWidth = 0;
  public static final int testType = 2;
  public static TextureAtlas liengAtlas, cardAtlas, startSceneAtlas, wheelAtlas, particleAtlas, leaderBoard;
  public static float ratioX, ratioY;
  public static Preferences pref;
  public static GameScene gameScene;

  public static IPlatform platform;
  public static GMain inst;

  public GMain(IPlatform plat) {
    platform = plat;
    inst = this;
  }

  private void init() {
    screenWidth = 1280;
    screenHeight = 720;
    GStage.init(screenWidth, screenHeight, 0, 0, new StageBorder() {
      @Override
      public void drawHorizontalBorder(Batch spriteBatch, float paramFloat1, float paramFloat2) {

      }

      @Override
      public void drawVerticalBorder(Batch spriteBatch, float paramFloat1, float paramFloat2) {

      }
    });
  }

  private static GScreen menuScreen() {
    return gameScene = new GameScene();
  }

  public void create() {

    pref = Gdx.app.getPreferences("lieng");
    if (!pref.getBoolean("isNewbie")) {

      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
      String day = formatter.format(Calendar.getInstance().getTime());

      pref.putBoolean("isNewbie", true);
      pref.putLong("money", Config.MONEY_NEWBIE);
      pref.putString("day", day);
      pref.putInteger("spin", Config.SPIN_TIME);
      pref.flush();

    }

    initLocalNotification();

    liengAtlas = GAssetsManager.getTextureAtlas("lieng.atlas");
    cardAtlas = GAssetsManager.getTextureAtlas("card.atlas");
    startSceneAtlas = GAssetsManager.getTextureAtlas("start_scene.atlas");
    wheelAtlas = GAssetsManager.getTextureAtlas("wheel.atlas");
    particleAtlas = GAssetsManager.getTextureAtlas("particle.atlas");
    leaderBoard = GAssetsManager.getTextureAtlas("leaderboard.atlas");

    SoundEffects.initSound();

    this.init();
    initPosConfig();

    SoundEffects.initSound();
    C.init();
    this.setScreen(menuScreen());

  }
  
  private void initPosConfig() {

//    Config.POS_BOT_0 = new Vector2(GStage.getWorldWidth()/2 - 170, GStage.getWorldHeight() - 200);
    Config.POS_BOT_0.x = GStage.getWorldWidth()/2 - 170;
    Config.POS_BOT_0.y = GStage.getWorldHeight() - 200;

//    Config.POS_BOT_1 = new Vector2(GStage.getWorldWidth() - 270, GStage.getWorldHeight()/2 + 10);
    Config.POS_BOT_1.x = GStage.getWorldWidth() - 270;
    Config.POS_BOT_1.y = GStage.getWorldHeight()/2 + 10;

//    Config.POS_BOT_2 = new Vector2(GStage.getWorldWidth() - 310, GStage.getWorldHeight()/2 - 200);
    Config.POS_BOT_2.x = GStage.getWorldWidth() - 310;
    Config.POS_BOT_2.y = GStage.getWorldHeight()/2 - 200;

//    Config.POS_BOT_3 = new Vector2(GStage.getWorldWidth()/2 - 30, 40);
    Config.POS_BOT_3.x = GStage.getWorldWidth()/2 - 30;
    Config.POS_BOT_3.y = 40;

//    Config.POS_BOT_4 = new Vector2(180, Config.POS_BOT_2.y);
    Config.POS_BOT_4.x = 180;
    Config.POS_BOT_4.y = Config.POS_BOT_2.y;

//    Config.POS_BOT_5 = new Vector2(Config.POS_BOT_4.x - 45, Config.POS_BOT_1.y);
    Config.POS_BOT_5.x = Config.POS_BOT_4.x - 45;
    Config.POS_BOT_5.y = Config.POS_BOT_1.y;

  }

  private void initLocalNotification(){

    platform.SetDailyNotification(2, "Liêng 2020", "Chơi ngay để nhận miễn phí $" + Config.MONEY_NOTIFY, 2, 19);
    platform.SetDailyNotification(7, "Lieng 2020", "Chơi ngay để nhận miễn phí $" + Config.MONEY_NOTIFY, 7, 19);

    int noId = platform.GetNotifyId();
    if(noId==-1) {
      //binhthuong
    } else if(noId == 1) {
      long money = pref.getLong("money") + Config.MONEY_NOTIFY;
      Logic.getInstance().saveMoney(money);
    }

  }

  public void dispose() {
    GMain.platform.log("############## gmain dispose");
    GParticleSystem.saveAllFreeMin();
    super.dispose();
  }
}
