package skin;

public class SkinSpecs
{
    //@formatter:off
    public final static Spec[] Hitcircle =
    {
        new Spec( "hitcircle.png", 0, 128, 128 ),
        new Spec( "hitcircleoverlay.png", 0, 128, 128 ),
        new Spec( "approachcircle.png", 0, 128, 128 ),
        new Spec( "reversearrow.png", 0, 128, 128 ),
    };
    

    public final static Spec[] Spinner =
    {
        new Spec( "spinner-background.png" ),
        new Spec( "spinner-circle.png", 0, 666, 666 ),
        new Spec( "spinner-approachcircle.png" ),
        new Spec( "spinner-metre.png" ),
        new Spec( "spinner-clear.png" ),
        new Spec( "spinner-spin.png" ),
        new Spec( "spinner-osu.png" ),
    };

    public final static Spec[] Countdown =
    {
        new Spec( "ready.png" ),
        new Spec( "count3.png" ),
        new Spec( "count2.png" ),
        new Spec( "count1.png" ),
        new Spec( "go.png" ),
    };

    public final static Spec[] Cursor =
    {
        new Spec( "cursor.png" ),
        new Spec( "cursortrail.png" ),
    };

    public final static Spec[] Scorebar =
    {
        new Spec( "scorebar-bg.png" ),
        new Spec( "scorebar-colour.png", true ),
    };

    public final static Spec[] Hitbursts =
    {
        new Spec( "hit0.png", 0, 256, 256 ),
        new Spec( "hit50.png", 0, 256, 256 ),
        new Spec( "hit100.png", 0, 256, 256 ),
        new Spec( "hit100k.png", 0, 256, 256 ),
        new Spec( "hit300.png", 0, 256, 256 ),
        new Spec( "hit300k.png", 0, 256, 256 ),
        new Spec( "hit300g.png", 0, 256, 256 ),
    };

    public final static Spec[] ScorebarKi =
    {
        new Spec( "scorebar-ki.png" ),
        new Spec( "scorebar-kidanger.png" ),
        new Spec( "scorebar-kidanger2.png" ),
    };

    public final static Spec[] SliderScores =
    {
        new Spec( "sliderpoint10.png" ),
        new Spec( "sliderpoint30.png" ),
    };

    public final static Spec[] HitcircleNumbers =
    {
        new Spec( "default-0.png" ),
        new Spec( "default-1.png" ),
        new Spec( "default-2.png" ),
        new Spec( "default-3.png" ),
        new Spec( "default-4.png" ),
        new Spec( "default-5.png" ),
        new Spec( "default-6.png" ),
        new Spec( "default-7.png" ),
        new Spec( "default-8.png" ),
        new Spec( "default-9.png" ),
    };

    public final static Spec[] ScoreNumbers =
    {
        new Spec( "score-0.png" ),
        new Spec( "score-1.png" ),
        new Spec( "score-2.png" ),
        new Spec( "score-3.png" ),
        new Spec( "score-4.png" ),
        new Spec( "score-5.png" ),
        new Spec( "score-6.png" ),
        new Spec( "score-7.png" ),
        new Spec( "score-8.png" ),
        new Spec( "score-9.png" ),
        new Spec( "score-comma.png" ),
        new Spec( "score-dot.png" ),
        new Spec( "score-percent.png" ),
        new Spec( "score-x.png" ),
    };

    public final static Spec[] SectionMarkers =
    {
        new Spec( "section-pass.png" ),
        new Spec( "section-fail.png" ),
    };

    public final static Spec[] Fruits =
    {
        new Spec( "fruit-apple.png" ),
        new Spec( "fruit-apple-overlay.png" ),
        new Spec( "fruit-grapes.png" ),
        new Spec( "fruit-grapes-overlay.png" ),
        new Spec( "fruit-orange.png" ),
        new Spec( "fruit-orange-overlay.png" ),
        new Spec( "fruit-pear.png" ),
        new Spec( "fruit-pear-overlay.png" ),
        new Spec( "fruit-drop.png" ),
        //new Spec( "fruit-ryuuta.png", true ),
    };
    
    //For other things such as comboburst
    public final static Spec[] Other = 
    {
        new Spec( "button-left.png" ),
        new Spec( "button-middle.png" ),
        new Spec( "button-right.png" ),
        new Spec( "comboburst.png", 1, 500, 767, true ),
        new Spec( "fail-background.png", 0, 1024, 768 ),
        new Spec( "followpoint.png", true ),
        new Spec( "hitcircleselect.png" ),
        new Spec( "lighting.png" ),
        new Spec( "menu-back.png", true ),
        new Spec( "menu-button-background.png" ),
        new Spec( "pause-back.png" ),
        new Spec( "pause-continue.png" ),
        new Spec( "pause-overlay.png", 0, 1024, 768 ),
        new Spec( "pause-retry.png" ),
        new Spec( "play-skip.png", true ),
        new Spec( "play-warningarrow.png" ),
        new Spec( "playfield.png", 0, 1024, 768 ),
        new Spec( "ranking-a-small.png" ),
        new Spec( "ranking-a.png" ),
        new Spec( "ranking-b-small.png" ),
        new Spec( "ranking-b.png" ),
        new Spec( "ranking-c-small.png" ),
        new Spec( "ranking-c.png" ),
        new Spec( "ranking-d-small.png" ),
        new Spec( "ranking-d.png" ),
        new Spec( "ranking-s-small.png" ),
        new Spec( "ranking-s.png" ),
        new Spec( "ranking-sh-small.png" ),
        new Spec( "ranking-sh.png" ),
        new Spec( "ranking-x-small.png" ),
        new Spec( "ranking-x.png" ),
        new Spec( "ranking-xh-small.png" ),
        new Spec( "ranking-xh.png" ),
        new Spec( "ranking-accuracy.png" ),
        new Spec( "ranking-back.png" ),
        new Spec( "ranking-graph.png" ),
        new Spec( "ranking-maxcombo.png" ),
        new Spec( "ranking-panel.png" ),
        new Spec( "ranking-perfect.png" ),
        new Spec( "ranking-replay.png" ),
        new Spec( "ranking-retry.png" ),
        new Spec( "ranking-title.png" ),
        new Spec( "reversearrow.png" ),
        new Spec( "selection-mod-autoplay.png" ),
        new Spec( "selection-mod-doubletime.png" ),
        new Spec( "selection-mod-easy.png" ),
        new Spec( "selection-mod-flashlight.png" ),
        new Spec( "selection-mod-halftime.png" ),
        new Spec( "selection-mod-hardrock.png" ),
        new Spec( "selection-mod-hidden.png" ),
        new Spec( "selection-mod-nofail.png" ),
        new Spec( "selection-mod-novideo.png" ),
        new Spec( "selection-mod-relax.png" ),
        new Spec( "selection-mod-relax2.png" ),
        new Spec( "selection-mod-spunout.png" ),
        new Spec( "selection-mod-suddendeath.png" ),
        new Spec( "selection-mods-over.png" ),
        new Spec( "selection-mods.png" ),
        new Spec( "selection-options-over.png" ),
        new Spec( "selection-options.png" ),
        new Spec( "selection-random-over.png" ),
        new Spec( "selection-random.png" ),
        new Spec( "selection-selectoptions-over.png" ),
        new Spec( "selection-selectoptions.png" ),
        new Spec( "selection-tab.png" ),
        new Spec( "sliderb.png", true),
        new Spec( "sliderfollowcircle.png", true ),
        new Spec( "sliderscorepoint.png" ),
        new Spec( "star.png" ),
        new Spec( "star2.png" ),

    };
    
    public final static String[] SET_NAMES =
    {
        "Hitcircle", //0
        "Spinner", //1
        "Spinner Hints", //2
        "Countdown", //3
        "Cursor", //4
        "Scorebar", //5
        "Hitbursts", //6
        "Scorebar Ki", //7
        "Slider Scores", //8
        "Hitcircle Numbers", //9
        "Score Numbers", //10
        "Section Markers", //11
        "Fruits", //12
    };
    
    public final static Spec[][] ALL_SPECS = 
    {
        Hitcircle,
        Spinner,
        Countdown,
        Cursor,
        Scorebar,
        Hitbursts,
        ScorebarKi,
        SliderScores,
        HitcircleNumbers,
        ScoreNumbers,
        SectionMarkers,
        Fruits,
    };
    
    //@formatter:on

    // public static void main (String[] args)
    // {
    // System.out.println(Countdown.length);
    // }
}
