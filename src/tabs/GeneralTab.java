package tabs;

public class GeneralTab extends AbstractTab
{
    //private String TITLES;
    private String[] TITLES = {
        "Folder Size",//0
        "MP3 Bitrate",//1
        "Background",//2 (Has BG + background size)
        "Custom Hitsounds",//3 File size > 44
    };
    
    GeneralTab()
    {
        TITLES.clone();
    }
}
