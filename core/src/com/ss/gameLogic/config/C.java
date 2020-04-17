package com.ss.gameLogic.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;
import java.util.MissingResourceException;

public class C {

    public static class remote {
        public static int adsTime = 50;
        public static long moneyAds = 500000;
        public static long minusMoney = 2;
        static void initRemoteConfig() {

        }
    }

    public static class lang {

      private static String idCountry = "";
      private static I18NBundle locale;
      public static String title = "";
      public static String adsTimeLbl = "";
      public static String raise = "";
      public static String call = "";
      public static String fold = "";
      public static String allIn = "";
      public static String winner = "";
      public static String receive = "";
      public static String minBet = "";
      public static String adsOutOfMoney = "";
      public static String startScene = "";
      public static String rank = "";
      public static String otherGame = "";
      public static String divideCard = "";
      public static String newRound = "";
      public static String tutorial = "";
      public static String players = "";
      public static String moneyPlayer = "";
      public static String bet = "";
      public static String titleSetting = "";
      public static String music = "";
      public static String sound = "";
      public static String startPanelBet = "";
      public static String notifyExitGame = "";
      public static String yes = "";
      public static String no = "";

      static void initLocalize() {
        FileHandle specFilehandle = Gdx.files.internal("i18n/lang_" + "id");
        FileHandle baseFileHandle = Gdx.files.internal("i18n/lang");

        try {
          locale = I18NBundle.createBundle(specFilehandle, new Locale(""));
          idCountry = locale.get("id_country");
        }
        catch (MissingResourceException e) {
          locale = I18NBundle.createBundle(baseFileHandle, new Locale(""));
          idCountry = locale.get("id_country");
        }

        title = locale.get("title");
        adsTimeLbl = locale.format("adsTime", remote.adsTime);

        raise = locale.get("raise");
        call = locale.get("call");
        fold = locale.get("fold");
        allIn = locale.get("all_in");
        winner = locale.get("win");
        receive = locale.get("receive");
        minBet = locale.get("min_bet");
        startScene = locale.get("start_scene");
        rank = locale.get("rank");
        otherGame = locale.get("other_game");
        divideCard = locale.get("divide_card");
        newRound = locale.get("new_round");
        players = locale.get("players");
        moneyPlayer = locale.get("money_player");
        bet = locale.get("bet");
        music = locale.get("music");
        sound = locale.get("sound");
        titleSetting = locale.get("title_setting");
        startPanelBet = locale.get("start_panel_bet");
        yes = locale.get("yes");
        no = locale.get("no");

        if (idCountry.equals("VN"))
          tutorial = Strings.tutorialVN;
        else
          tutorial = Strings.tutorialEN;

        adsOutOfMoney = locale.format("out_of_money", remote.moneyAds);
        notifyExitGame = locale.format("notify_exit_game", remote.minusMoney);
      }
    }

    public static void init() {
        remote.initRemoteConfig();
        lang.initLocalize();
    }
}
