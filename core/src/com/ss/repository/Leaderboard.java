package com.ss.repository;

//the best, as much as gdx as it can :)
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;

//ok any current project have this one
import com.ss.GMain;
import com.ss.core.exSprite.GShapeSprite;
import com.ss.core.util.GAssetsManager;
import com.ss.core.util.GLayer;
import com.ss.core.util.GScreen;
import com.ss.core.util.GSound;
import com.ss.core.util.GStage;
import com.ss.core.util.GUI;

/////// depend upon abstraction, not implementation
//import com.ss.GMain;
//import com.ss.gameLogic.data.Level;
//import com.ss.gameLogic.model.BoardModel;
//import com.ss.gameLogic.model.GUITools;
//import com.ss.gameLogic.objects.BoardConfig;

@SuppressWarnings("unused")
public class Leaderboard extends GScreen {

  public interface GUIService {
    Label getWhiteText(String text, Color color, float scale);
    BitmapFont whiteFont();
  }

  public interface LocalizeService {
    String getLocalizeText(String key);
    String formatLocalizeText(String key, Object args);
  }

  public interface FirebaseService {
    void crashLog(String message);
    void trackScreen(String message);
  }

  public interface ScreenService {
    GScreen backScreen();
  }

  public interface RankingService {
    int getNRank();
    long getCurrentScore(int rank);
  }

  private                   TextureAtlas                      lbAtlas;
  private                   TextureAtlas                      commonAtlas;
  private                   Group                             loadingGroup;
  private                   Group                             childGroup;
  private                   Group                             registerGroup;
  private                   GUIService                        guiService;
  private                   LocalizeService                   localizeService;
  private                   FirebaseService                   firebaseService;
  private                   ScreenService                     screenService;
  private                   RankingService                    rankingService;
  public  static            int                               mode                  = 0;
  private static  final     String                            apiUrl                = "http://picachu.bonanhem.com:9001/";

  private                   Table                             table;
  private                   ScrollPane                        scroll;
  private                   Group                             meGroup;
  private                   String []                         topTypeStr;
  private                   Button[]                          btn_tags              = new Button[3];
  private                   int                               tagSelectedID         = 0;

  //localize keys
  private String      TOP0            = "top0";                       // TOP TUẦN
  private String      TOP1            = "top1";                       // TOP THÁNG
  private String      TOP2            = "top2";                       // TOP MỌI LÚC
  private String      RANK_PREFIX     = "rank";                       // TOP
  private String[]    RANK_POSTFIX    = {"rank0", "rank1", "rank2"};  // ĂN CHƠI, CÔNG TỬ, ĐẠI GIA...
  private String      LBB_NOTICE      = "ldbnotice0";                 // Để xem bảng xếp hạng bạn cần phải đăng ký tên \nngười chơi
  private String      PLAYER_NAME     = "playername";                 // Tên người chơi:
  private String      TEXT_INPUT      = "textinput";
  private String      PLAYER          = "player";
  private String      LDB_NOTICE1     = "ldbnotice1";                 // Đang đăng ký tên người chơi
  private String      LDB_NOTICE2     = "ldbnotice2";                 // Đăng ký thất bại, xin hãy thử lại sau
  private String      LDB_NOTICE3     = "ldbnotice3";                 // Đăng ký thành công!\nĐang đồng bộ điểm...
  private String      LDB_NOTICE4     = "ldbnotice4";                 // Đang tải dữ liệu BXH
  private String      LDB_NOTICE5     = "ldbnotice5";                 // Dữ liệu BXH Failed
  private String      ACCEPT          = "accept";                     // OK
  private String      YOUR_NAME       = "yourname";                   // Bạn ({0})
  private String      SSCORE          = "sscore";                     // Điểm

  public Leaderboard(GUIService guiService,
                     LocalizeService localizeService,
                     FirebaseService firebaseSerivce,
                     ScreenService sreenService,
                     RankingService rankingService) {

    this.guiService = guiService;
    this.localizeService = localizeService;
    this.firebaseService = firebaseSerivce;
    this.screenService = sreenService;
    this.rankingService = rankingService;

    this.topTypeStr = new String[] {
        localizeService.getLocalizeText(TOP0),
        localizeService.getLocalizeText(TOP1),
        localizeService.getLocalizeText(TOP2),
    };
  }

  @Override
  public void dispose() {

  }

  @Override
  public void init() {
    //Level.loadData();
    //BoardModel.loadPlayData();
//    GSound.initSound("match.mp3");
//    GSound.initSound("click.mp3");
    Group parentGroup = new Group();
    GStage.addToLayer(GLayer.ui, parentGroup);

//    lbAtlas = GAssetsManager.getTextureAtlas("leaderboard.atlas");
//    commonAtlas = GAssetsManager.getTextureAtlas("common.atlas");
//    GAssetsManager.getTextureAtlas("play.atlas");

//    Image bg = GUI.createImage(commonAtlas, "bg");
    Image bg = GUI.createImage(GMain.leaderBoard, "bg");
    assert bg != null;
    bg.setScale(1.2f);
    parentGroup.addActor(bg);
    bg.setOrigin(Align.center);
    bg.setPosition(GStage.getWorldWidth()/2,GStage.getWorldHeight()/2,Align.center);

    Gdx.app.log("bg size", bg.getWidth() + " " + bg.getHeight());
   // bg.setSize(GStage.getWorldWidth(), GStage.getWorldHeight());

    childGroup = new Group();
    parentGroup.addActor(childGroup);

    //childGroup.setScale(0);
    childGroup.setOrigin(Align.center);
    childGroup.setPosition(GStage.getWorldWidth()/2+110, GStage.getWorldHeight()/2+50-1000);
    childGroup.addAction(Actions.moveTo(GStage.getWorldWidth()/2+110, GStage.getWorldHeight()/2+50,0.8f, Interpolation.swingOut));

    initTag();

    Image img = GUI.createImage(GMain.leaderBoard, "panel");
    assert img != null;
    childGroup.addActor(img);
    img.setPosition(0, 0, Align.center);

//    Image ribbon = GUI.createImage(lbAtlas, "ribbon");
    Image ribbon = GUI.createImage(GMain.leaderBoard, "ribbon");
    assert ribbon != null;
    childGroup.addActor(ribbon);
    ribbon.setPosition(0, -300, Align.center);

//    Image title = GUI.createImageLocalize(lbAtlas, "topscore");
    Image title = GUI.createImageLocalize(GMain.leaderBoard, "topscore");
    assert title != null;
    childGroup.addActor(title);
    title.setPosition(0, -360, Align.center);

//    Image rank_overlay = GUI.createImage(lbAtlas, "rank_overlay");
    Image rank_overlay = GUI.createImage(GMain.leaderBoard, "rank_overlay");
    assert rank_overlay != null;
    childGroup.addActor(rank_overlay);
    rank_overlay.setPosition(0, -300, Align.center);
    rank_overlay.setScaleY(0.8f);

    Label rank_txt = guiService.getWhiteText(localizeService.formatLocalizeText(RANK_PREFIX, RANK_POSTFIX[mode]) , Color.WHITE, 0.75f);
    rank_txt.setAlignment(Align.center);
    childGroup.addActor(rank_txt);
    rank_txt.setPosition(0, -305, Align.center);


    Button btn_exit = GUI.createButtonLocalize(GMain.liengAtlas, "btn_x");
    assert btn_exit != null;
    parentGroup.addActor(btn_exit);
    btn_exit.setPosition(GStage.getWorldWidth()-50, 50, Align.center);
    btn_exit.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
//        GSound.playSound("click.mp3");
        setScreen(screenService.backScreen());
      }
    });

    initScroll();
    initData();

    if(PlayerData.isRegister){
      OnLeaderboardTagChange(tagSelectedID);
    }
    else{
      initRegister();
    }

    firebaseService.crashLog("leaderboard");
    firebaseService.trackScreen("Leaderboard");
  }



  private void initRegister(){
    if(registerGroup!=null){
      registerGroup.remove();
    }
    registerGroup = new Group();
    GStage.addToLayer(GLayer.top, registerGroup);


    GShapeSprite overlay = new GShapeSprite();
    overlay.createRectangle(true, 0,0,GStage.getWorldWidth(), GStage.getWorldHeight());
    overlay.setColor(0,0,0,0.95f);
    registerGroup.addActor(overlay);

    final Group childGroup2 = new Group();
    registerGroup.addActor(childGroup2);

    childGroup2.setScale(0);
    childGroup2.setOrigin(Align.center);
    childGroup2.setPosition(GStage.getWorldWidth()/2, GStage.getWorldHeight()/2);
    childGroup2.addAction(Actions.scaleTo(1,1,0.6f, Interpolation.swingOut));

    Image img = GUI.createImage(GMain.leaderBoard, "panel_register");
    assert img != null;
    childGroup2.addActor(img);
    img.setOrigin(Align.center);
    img.setScale(1.3f);
    img.setPosition(0, 0, Align.center);

    Image title = GUI.createImageLocalize(GMain.leaderBoard, "tittle_notice");
    assert title != null;
    childGroup2.addActor(title);
    title.setPosition(0, -220, Align.center);

    Label label = guiService.getWhiteText(localizeService.getLocalizeText(LBB_NOTICE), Color.WHITE, 0.8f);
    label.setAlignment(Align.center);
    childGroup2.addActor(label);
    label.setPosition(0, -140, Align.center);


    Label nickname = guiService.getWhiteText(localizeService.getLocalizeText(PLAYER_NAME), Color.WHITE, 0.9f);
    nickname.setAlignment(Align.center);
    childGroup2.addActor(nickname);
    nickname.setPosition(-150, 30, Align.center);

    TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
    tfs.font = guiService.whiteFont();
    tfs.fontColor = Color.WHITE;
    tfs.background = new TextureRegionDrawable(GMain.leaderBoard.findRegion(TEXT_INPUT));
    final TextField userNameTf = new TextField(localizeService.getLocalizeText(PLAYER) + MathUtils.random(1000000, 9999999), tfs);
    childGroup2.addActor(userNameTf);
    userNameTf.setWidth(250);
    userNameTf.setPosition(100, 30, Align.center);

    Button btn_ok = GUI.createTextButton(GMain.leaderBoard.findRegion("btn_yellow"), guiService.whiteFont(), "OK");//GUI.createButtonLocalize(commonAtlas, "btn_resume");
    childGroup2.addActor(btn_ok);
    btn_ok.setPosition(0, 140, Align.center);
    btn_ok.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
//        GSound.playSound("click.mp3");
        registerGroup.remove();
        registerGroup = null;

        showLoadingGroup(localizeService.getLocalizeText(LDB_NOTICE1), false);
        PlayerData.name = userNameTf.getText();
        PlayerData.getMe(() -> showLoadingGroup(localizeService.getLocalizeText(LDB_NOTICE2), true), () -> {
          //removeLoadingGroup();
          showLoadingGroup(localizeService.getLocalizeText(LDB_NOTICE3), false);
          postAllScore();
        });
      }
    });


    TextField.OnscreenKeyboard keyboard = visible -> {
      //Gdx.input.setOnscreenKeyboardVisible(visible);
      //childGroup2.addAction(Actions.moveTo(GStage.getWorldWidth()/2, GStage.getWorldHeight()/2-300, 0.3f));

      Gdx.input.getTextInput(new Input.TextInputListener() {
        @Override
        public void input(String text) {
          userNameTf.setText(text);
        }

        @Override
        public void canceled() {
          //childGroup2.addAction(Actions.moveTo(GStage.getWorldWidth()/2, GStage.getWorldHeight()/2, 0.3f));

        }
      }, localizeService.getLocalizeText(PLAYER_NAME), userNameTf.getText(), "");

    };
    userNameTf.setOnscreenKeyboard(keyboard);
    //btn_resume.setPosition(GStage.getWorldWidth()/2, GStage.getWorldHeight()/2, Align.center);
  }

  private void postAllScore(){
    for(int i=0; i<= rankingService.getNRank(); i++) {
//            if (i > 4)
//                break;
      final int rank = i;

      long score = rankingService.getCurrentScore(i);
      PlayerData.postScore(rank, score,
      () -> { //success
        if(rank == rankingService.getNRank()){
          //removeLoadingGroup();
          OnLeaderboardTagChange(tagSelectedID);
        }
      },
      () -> { //fail
        if(rank == rankingService.getNRank()){
          //removeLoadingGroup();
          OnLeaderboardTagChange(tagSelectedID);
        }
      });
    }
  }

  private void initTag(){

    for(int i=0;i<3;i++) {
      Button btn_tag = GUI.createTextButtonEx(GMain.leaderBoard.findRegion("tag"), guiService.whiteFont(), topTypeStr[i], 0, -10);
      childGroup.addActor(btn_tag);
      btn_tag.setPosition((i==tagSelectedID)?-720:-670, i*100 - 200);

      btn_tags[i] = btn_tag;
      final int type = i;
      btn_tag.addListener(new ClickListener(){
        @Override
        public void clicked(InputEvent event, float x, float y) {
//          GSound.playSound("click.mp3");

          tagSelectedID = type;
          for(int j=0;j<3;j++){
            btn_tags[j].setPosition((type==j)?-720:-670, j*100 - 200);
          }

          OnLeaderboardTagChange(type);

        }
      });
    }
    //OnLeaderboardTagChange(0);
  }


  private void OnLeaderboardTagChange(int type){
    showLoadingGroup(localizeService.getLocalizeText(LDB_NOTICE4), false);


    PlayerData.getLeaderboard(type, mode, () -> showLoadingGroup(localizeService.getLocalizeText(LDB_NOTICE5), true), () -> {
      removeLoadingGroup();
      populateData(PlayerData.tmpRet);
      getMeScore(type);
    });

  }

  private void getMeScore(int type){
    PlayerData.getPosition(type, mode, () -> {

    }, () -> populateMeData(PlayerData.tmpRet));
  }

  private void removeLoadingGroup(){
    if(loadingGroup!=null)
    {
      loadingGroup.remove();
      loadingGroup = null;
    }
  }

  private void showLoadingGroup(String text, boolean okButton){
    if(loadingGroup!=null)
    {
      loadingGroup.remove();
      loadingGroup = null;
    }

    GShapeSprite overlay = new GShapeSprite();
    overlay.createRectangle(true, 0,0,GStage.getWorldWidth(), GStage.getWorldHeight());
    overlay.setColor(0,0,0,0.75f);


    loadingGroup = new Group();
    loadingGroup.addActor(overlay);

    Label rank_txt = guiService.getWhiteText(text, Color.WHITE, 0.75f);
    rank_txt.setAlignment(Align.center);
    loadingGroup.addActor(rank_txt);
    rank_txt.setPosition(GStage.getWorldWidth() / 2, GStage.getWorldHeight() / 2, Align.center);

    if(okButton){
      Button btn_ok = GUI.createTextButton(GMain.leaderBoard.findRegion("btn_yellow"), guiService.whiteFont(), localizeService.getLocalizeText(ACCEPT));//GUI.createButtonLocalize(commonAtlas, "btn_resume");
      loadingGroup.addActor(btn_ok);
      btn_ok.setPosition(GStage.getWorldWidth() / 2, GStage.getWorldHeight() / 2 + 100, Align.center);
      btn_ok.addListener(new ClickListener(){
        @Override
        public void clicked(InputEvent event, float x, float y) {
//          GSound.playSound("click.mp3");
          loadingGroup.remove();
          loadingGroup = null;
          setScreen(screenService.backScreen());
        }
      });
    }
    else {
      Image loading = GUI.createImage(GMain.leaderBoard, "loading");
      assert loading != null;
      loading.setOrigin(Align.center);
      loadingGroup.addActor(loading);
      loading.addAction(Actions.forever(Actions.rotateBy(90, 1f)));
      loading.setPosition(GStage.getWorldWidth() / 2, GStage.getWorldHeight() / 2 - 150,Align.center);
    }
    GStage.addToLayer(GLayer.top, loadingGroup);
  }

  private void initScroll(){

    table = new Table();

    int panelW = 1000;
    int panelH = 400;
    scroll = new ScrollPane(table);

    scroll.setSize(panelW, panelH);
    scroll.setPosition(0, -220, Align.bottom);
    childGroup.addActor(scroll);
    //scroll.setDebug(true);
    //table.setDebug(true);
    //populateData();
    //populateMeData();
  }

  private void populateMeData(JsonValue me){
    if(meGroup != null){
      meGroup.remove();

    }
    meGroup = new Group();
    Image panel = GUI.createImage(GMain.leaderBoard, "row_overlay");
    assert panel != null;
    meGroup.addActor(panel);
    meGroup.setSize(panel.getWidth(), panel.getHeight());
    meGroup.setHeight(panel.getHeight()+10);
    panel.setPosition(0,0,Align.bottomLeft);


    Label rankId = guiService.getWhiteText( me.getLong("position") +".", Color.GOLD, 0.8f);
    meGroup.addActor(rankId);
    rankId.setAlignment(Align.right);
    rankId.setPosition(100, 30, Align.right);


    Label upgradeName = guiService.getWhiteText(localizeService.formatLocalizeText(YOUR_NAME, PlayerData.name), Color.GOLD, 0.8f);
    meGroup.addActor(upgradeName);
    upgradeName.setAlignment(Align.left);
    upgradeName.setPosition(125, 30, Align.left);


    Label valuetxt = guiService.getWhiteText(me.getInt("score") + localizeService.getLocalizeText(SSCORE), Color.GOLD, 0.8f);
    meGroup.addActor(valuetxt);
    valuetxt.setAlignment(Align.right);
    valuetxt.setPosition(870, 30, Align.right);

    childGroup.addActor(meGroup);
    meGroup.setPosition(-467, 237, Align.left);
  }

  private void populateData(JsonValue list){
    table.clearChildren();
    table.pad(10).defaults().expandX().space(4);

    for(int i=list.size-1;i>=0; i--) {
      JsonValue it = list.get(i);
      Group item = new Group();
      Image panel = GUI.createImage(GMain.leaderBoard, "row_overlay");
      assert panel != null;
      item.addActor(panel);
      item.setSize(panel.getWidth(), panel.getHeight());
      item.setHeight(panel.getHeight()+2);
      panel.setPosition(0,0,Align.bottomLeft);

      if(i<3){
        Image rankIcon = GUI.createImage(GMain.leaderBoard, "rank"+i);
        assert rankIcon != null;
        item.addActor(rankIcon);
        rankIcon.setPosition(45,30,Align.left);
      }else {
        Label rankId = guiService.getWhiteText((i+1)+".", Color.WHITE, 0.9f);
        item.addActor(rankId);
        rankId.setAlignment(Align.right);
        rankId.setPosition(100, 30, Align.right);
      }


      Label upgradeName = guiService.getWhiteText(it.getString("name"), Color.WHITE, 0.8f);
      item.addActor(upgradeName);
      upgradeName.setAlignment(Align.left);
      upgradeName.setPosition(125, 30, Align.left);


      Label valueTxt = guiService.getWhiteText(it.getLong("score")+ localizeService.getLocalizeText(SSCORE), Color.WHITE, 0.8f);
      item.addActor(valueTxt);
      valueTxt.setAlignment(Align.right);
      valueTxt.setPosition(870, 30, Align.right);

      table.row();
      table.add(item).expandX().fillX();

    }
    scroll.layout();
    scroll.setScrollPercentY(1f);
    scroll.updateVisualScroll();
  }

  private void initData(){

  }

  @Override
  public void run() {

  }

  public static class PlayerData {
    public static String token;
    public static String name;
    public static String id;
    static int avatar;
    static boolean isRegister;

    static JsonValue tmpRet = null;
    static String []names= {"week", "month", "life"};

    static boolean isInited = false;

    public static void init(){
      if(isInited)
        return;
      Preferences pref = Gdx.app.getPreferences("PlayerData");
//        pref.clear();
//        pref.flush();
      isRegister = pref.getBoolean("isRegister", false);
      if(isRegister){
        id = pref.getString("id", "");
        name = pref.getString("name", "Player");
        token = pref.getString("token", "");
        avatar = pref.getInteger("avatar", 0);
      }else{
        id = RandomString.MD5_Hash(RandomString.getAlphaNumericString(32));
        token = RandomString.MD5_Hash(RandomString.getAlphaNumericString(32));
        name = "Player";
        avatar = 0;
      }
      isInited = true;
    }

    public static void save(){
      Preferences pref = Gdx.app.getPreferences("PlayerData");
      pref.putString("id", id);
      pref.putString("name", name);
      pref.putString("token", token);
      pref.putInteger("avatar", avatar);
      pref.putBoolean("isRegister", isRegister);
      pref.flush();

    }
    static void getMe(Runnable failed, Runnable successed) {
      new Thread(() -> {
        if (!isInited)
          init();
        Runnable finalRun;
        try {
          JsonValue r = HttpRequest.SendGetRequestJson(apiUrl+"me?id=" + id + "&token=" + token + "&avatar=" + avatar + "&name=" + RandomString.urlEncode(name));

          assert r != null;
          int error = r.getInt("error");
          if (error == 0) {
            int register = r.getInt("register");
            if (register == 1) {
              isRegister = true;
              save();
            }
            finalRun = successed;
          } else {
            String message = r.has("message") ? r.getString("message") : "";
            finalRun = failed;
            Gdx.app.log("", message);
          }
        } catch (Exception e) {
          e.printStackTrace();

          finalRun = failed;
        }
        if(finalRun!=null)
          Gdx.app.postRunnable(finalRun);
      }).start();
    }

    static void postScore(int rank, long score, Runnable failed, Runnable successed){
      new Thread(() -> {
        if (!isInited)
          init();
        Runnable finalRun;
        try {
          JsonValue r = HttpRequest.SendGetRequestJson(apiUrl+"score?id=" + id + "&token=" + token + "&rank=" + rank + "&score=" + score);

          assert r != null;
          int error = r.getInt("error");
          if (error == 0) {
            finalRun = successed;
          } else {
            String message = r.has("message") ? r.getString("message") : "";
            finalRun = failed;
            Gdx.app.log("", message);
          }
        } catch (Exception e) {
          e.printStackTrace();
          finalRun = failed;
        }
        if(finalRun!=null)
          Gdx.app.postRunnable(finalRun);
      }).start();
    }


    static void getLeaderboard(int type, int rank, Runnable failed, Runnable successed){
      tmpRet = null;
      new Thread(() -> {
        if (!isInited)
          init();
        Runnable finalRun;
        try {
          String name = names[type];
          JsonValue r = HttpRequest.SendGetRequestJson(apiUrl+name+"?rank=" + rank);

          assert r != null;
          int error = r.getInt("error");
          if (error == 0) {
            tmpRet = r.get("list");
            finalRun = successed;
          } else {
            String message = r.has("message") ? r.getString("message") : "";
            finalRun = failed;
            Gdx.app.log("", message);
          }
        } catch (Exception e) {
          e.printStackTrace();
          finalRun = failed;
        }
        if(finalRun!=null)
          Gdx.app.postRunnable(finalRun);
      }).start();
    }

    public static void getPosition(int type, int rank, Runnable failed, Runnable successed){
      tmpRet = null;
      new Thread(() -> {
        if (!isInited)
          init();
        Runnable finalRun;
        try {
          String name = names[type] +"_"+rank;
          JsonValue r = HttpRequest.SendGetRequestJson(apiUrl+"position?type=" + name+"&id="+id);

          assert r != null;
          int error = r.getInt("error");
          if (error == 0) {
            tmpRet = r.get("pos");
            finalRun = successed;
          } else {
            String message = r.has("message") ? r.getString("message") : "";
            finalRun = failed;
            Gdx.app.log("", message);
          }
        } catch (Exception e) {
          e.printStackTrace();
          finalRun = failed;
        }
        if(finalRun!=null)
          Gdx.app.postRunnable(finalRun);

      }).start();
    }
  }
}

