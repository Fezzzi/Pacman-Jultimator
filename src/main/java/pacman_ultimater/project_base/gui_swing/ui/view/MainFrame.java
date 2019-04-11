package pacman_ultimater.project_base.gui_swing.ui.view;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Application's view.
 * Creates application's window and fills it with labels used for menu orientation.
 */
public class MainFrame extends JFrame{

    //<editor-fold desc="- FRAME COMPONENTS Block -">

    private String resourcesPath = null;
    private JPanel mainPanel;
    private JFileChooser openFileDialog1;

    private Timer pacUpdater;
    private Timer ghostUpdater;
    private Timer pacSmoothTimer;
    private Timer ghostSmoothTimer;

    private JLabel pacmanLbl;
    private JLabel ultimateLbl;
    private JLabel copyrightLbl;
    private JLabel pressEnterLbl;
    private JLabel selectMapLbl;
    private JLabel orgGameLbl;
    private JLabel settingsLbl;
    private JLabel escLabelLbl;
    private JLabel highScrLbl;
    private JLabel vsLbl;
    private JLabel highScoreLabelLbl;
    private JLabel scoreLabelLbl;
    private JLabel highScoreNumLbl;
    private JLabel scoreNumLbl;
    private JLabel musicButtonLbl;
    private JLabel soundsButtonLbl;
    private JLabel musicBtnSelectorLbl;
    private JLabel soundsBtnSelectorLbl;
    private JLabel gameOverLabelLbl;
    private JLabel errorLdMapLbl;
    private JLabel errorInfoLbl;
    private JLabel tryAgainButLbl;
    private JLabel advancedLdButLbl;
    private JLabel typeSymbolsLbl;
    private JLabel typedSymbolsLbl;
    private JLabel typeHintLbl;

    //</editor-fold>

    //<editor-fold desc="- INITIALIZATION Block -">

    public MainFrame()
    {
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(resourcesPath + "\\PacManJUltimater.png").getImage());
        setMinimumSize(new Dimension(464, 529));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2 - this.getSize().width/2, dim.height/2 - this.getSize().height/2);
        setName("PacManJUltimater");
        setTitle("Pac-Man J-Ultimater");
        setResizable(false);

        initComponents();
        addComponents();

        setContentPane(mainPanel);
        mainPanel.revalidate();
    }

    private void initComponents()
    {
        instantiateComponents();
        //
        // Main Frame
        //
        this.setFocusable(false);
        //
        // Main Panel
        //
        mainPanel.setLayout(null);
        mainPanel.setBackground(SystemColor.BLACK);
        mainPanel.setFocusable(true);
        //
        // Open File Dialog
        //
        openFileDialog1.setDialogTitle("Choose Map File");
        openFileDialog1.setFileFilter(new FileFilter() {
            public boolean accept(File dir) {
                if(dir.isFile())
                    return dir.getName().toLowerCase().endsWith(".txt");
                return true;
            }
            public String getDescription(){
                return "";
            }
        });
        openFileDialog1.setFocusable(false);
        //
        // Pacman
        //
        pacmanLbl.setFont(new Font("Ravie", Font.BOLD, 54));
        pacmanLbl.setForeground(Color.YELLOW);
        pacmanLbl.setBounds(55, 50,380,75);
        pacmanLbl.setName("pacman");
        pacmanLbl.setText("PAC-MAN");
        pacmanLbl.setVisible(false);
        pacmanLbl.setFocusable(false);
        //
        // J-Ultimater
        //
        ultimateLbl.setFont(new Font("Ravie", Font.BOLD, 32));
        ultimateLbl.setForeground(Color.red);
        ultimateLbl.setBounds(43, 115,380,50);
        ultimateLbl.setName("jultimater");
        ultimateLbl.setText("- J-ULTIMATER -");
        ultimateLbl.setVisible(false);
        ultimateLbl.setFocusable(false);
        //
        // copyright
        //
        copyrightLbl.setForeground(Color.yellow);
        copyrightLbl.setBounds(270, 460, 179,17);
        copyrightLbl.setName("copyright");
        copyrightLbl.setText("© Copyright Filip Horký 2019");
        copyrightLbl.setVisible(false);
        copyrightLbl.setFocusable(false);
        //
        // PressEnter
        //
        pressEnterLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 34));
        pressEnterLbl.setForeground(Color.white);
        pressEnterLbl.setBounds(95, 180,400,200);
        pressEnterLbl.setName("pressEnter");
        pressEnterLbl.setText("<html>&nbsp;&nbsp;INSERT COIN<br/><br/>- press any key -</html>");
        pressEnterLbl.setVisible(false);
        pressEnterLbl.setFocusable(false);
        //
        // OrgGame
        //
        orgGameLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 34));
        orgGameLbl.setForeground(Color.white);
        orgGameLbl.setBounds(100,51,300,45);
        orgGameLbl.setName("OrgGame");
        orgGameLbl.setText("Original Game");
        orgGameLbl.setVisible(false);
        orgGameLbl.setFocusable(false);
        //
        // selectMap
        //
        selectMapLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 34));
        selectMapLbl.setForeground(Color.white);
        selectMapLbl.setBounds(128,118,250,45);
        selectMapLbl.setName("SelectMap");
        selectMapLbl.setText("Select Map");
        selectMapLbl.setVisible(false);
        selectMapLbl.setFocusable(false);
        //
        // Settings
        //
        settingsLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 34));
        settingsLbl.setForeground(Color.white);
        settingsLbl.setBounds(151,186,180,45);
        settingsLbl.setName("Settings");
        settingsLbl.setText("Settings");
        settingsLbl.setVisible(false);
        settingsLbl.setFocusable(false);
        //
        // VS
        //
        vsLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 34));
        vsLbl.setForeground(Color.white);
        vsLbl.setBounds(194,255,60,45);
        vsLbl.setName("Vs");
        vsLbl.setText("VS");
        vsLbl.setVisible(false);
        vsLbl.setFocusable(false);
        //
        // HighScr
        //
        highScrLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 34));
        highScrLbl.setForeground(Color.white);
        highScrLbl.setBounds(113,328,260,45);
        highScrLbl.setName("HighScr");
        highScrLbl.setText("Highest Score");
        highScrLbl.setVisible(false);
        highScrLbl.setFocusable(false);
        //
        // EscLabel
        //
        escLabelLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 20));
        escLabelLbl.setBounds(138, 427, 185, 22);
        escLabelLbl.setForeground(Color.yellow);
        escLabelLbl.setName("EscLabel");
        escLabelLbl.setText("Press ESC to return");
        escLabelLbl.setVisible(false);
        escLabelLbl.setFocusable(false);
        //
        // MusicButton
        //
        musicButtonLbl.setBorder(BorderFactory.createLineBorder(Color.white));
        musicButtonLbl.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 34));
        musicButtonLbl.setForeground(Color.white);
        musicButtonLbl.setBounds(158,120,140,44);
        musicButtonLbl.setName("MusicButton");
        musicButtonLbl.setText("<html><div style='padding-left: 8px; background-color: black; width: 140px;'>MUSIC</div></html>");
        musicButtonLbl.setVisible(false);
        musicButtonLbl.setFocusable(false);
        //
        // MusicBtnSelector
        //
        musicBtnSelectorLbl.setBackground(Color.yellow);
        musicBtnSelectorLbl.setBounds(150,120,24,44);
        musicBtnSelectorLbl.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 39));
        musicBtnSelectorLbl.setName("MusicBtnSelector");
        musicBtnSelectorLbl.setText("<html><div style='background-color: yellow; width: 8px;'>&nbsp;</div><html>");
        musicBtnSelectorLbl.setVisible(false);
        musicBtnSelectorLbl.setFocusable(false);
        //
        // SoundsButton
        //
        soundsButtonLbl.setBorder(BorderFactory.createLineBorder(Color.white));
        soundsButtonLbl.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 34));
        soundsButtonLbl.setForeground(Color.white);
        soundsButtonLbl.setBounds(154,233,150,44);
        soundsButtonLbl.setName("SoundsButton");
        soundsButtonLbl.setText("<html><div style='padding-left: 8px; background-color: black; width: 150px;'>SOUND</div></html>");
        soundsButtonLbl.setVisible(false);
        soundsButtonLbl.setFocusable(false);
        //
        // SoundsBtnSelector
        //
        soundsBtnSelectorLbl.setBackground(Color.yellow);
        soundsBtnSelectorLbl.setBounds(146,233,24,44);
        soundsBtnSelectorLbl.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 39));
        soundsBtnSelectorLbl.setName("SoundsBtnSelector");
        soundsBtnSelectorLbl.setText("<html><div style='background-color: yellow; width: 8px;'>&nbsp;</div><html>");
        soundsBtnSelectorLbl.setVisible(false);
        soundsBtnSelectorLbl.setFocusable(false);
        //
        // HighScoreLabel
        //
        highScoreLabelLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 34));
        highScoreLabelLbl.setForeground(Color.yellow);
        highScoreLabelLbl.setBounds(71,113,231,39);
        highScoreLabelLbl.setName("HighScoreLabel");
        highScoreLabelLbl.setText("Highest Score");
        highScoreLabelLbl.setVisible(false);
        highScoreLabelLbl.setFocusable(false);
        //
        // ScoreLabel
        //
        scoreLabelLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 28));
        scoreLabelLbl.setForeground(Color.white);
        scoreLabelLbl.setBounds(72,273,165,36);
        scoreLabelLbl.setName("ScoreLabel");
        scoreLabelLbl.setText("Your Score");
        scoreLabelLbl.setVisible(false);
        scoreLabelLbl.setFocusable(false);
        //
        // HighScoreNum
        //
        highScoreNumLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 38));
        highScoreNumLbl.setForeground(Color.yellow);
        highScoreNumLbl.setBounds(178,171,200,58);
        highScoreNumLbl.setName("HighScoreNum");
        highScoreNumLbl.setVisible(false);
        highScoreNumLbl.setFocusable(false);
        //
        // ScoreNum
        //
        scoreNumLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 33));
        scoreNumLbl.setForeground(Color.white);
        scoreNumLbl.setBounds(181,310,200,54);
        scoreNumLbl.setName("ScoreNum");
        scoreNumLbl.setVisible(false);
        scoreNumLbl.setFocusable(false);
        //
        // GameOverLabel
        //
        gameOverLabelLbl.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 48));
        gameOverLabelLbl.setForeground(Color.red);
        gameOverLabelLbl.setName("GameOverLabel");
        gameOverLabelLbl.setBounds(85,33,400,52);
        gameOverLabelLbl.setText("GAME OVER");
        gameOverLabelLbl.setVisible(false);
        gameOverLabelLbl.setFocusable(false);
        //
        // ErrorLdMap
        //
        errorLdMapLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 34));
        errorLdMapLbl.setForeground(Color.white);
        errorLdMapLbl.setBounds(78,42,302,39);
        errorLdMapLbl.setName("ErrorLdMap");
        errorLdMapLbl.setText("Error Loading Map");
        errorLdMapLbl.setVisible(false);
        errorLdMapLbl.setFocusable(false);
        //
        // ErrorInfo
        //
        errorInfoLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 34));
        errorInfoLbl.setForeground(Color.white);
        errorInfoLbl.setBounds(0,50, 450, 350);
        errorInfoLbl.setName("ErrorInfo");
        errorInfoLbl.setText("");
        errorInfoLbl.setVisible(false);
        errorInfoLbl.setFocusable(false);
        //
        // TryAgainBut
        //
        tryAgainButLbl.setBackground(Color.white);
        tryAgainButLbl.setBorder(BorderFactory.createLineBorder(Color.white));
        tryAgainButLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 25));
        tryAgainButLbl.setForeground(Color.black);
        tryAgainButLbl.setBounds(27,370,148,31);
        tryAgainButLbl.setName("TryAgainBut");
        tryAgainButLbl.setText("<html><div style='background-color: white; width: 148px; padding-left: 5px'>TRY AGAIN</div><html>");
        tryAgainButLbl.setVisible(false);
        tryAgainButLbl.setFocusable(false);
        //
        // AdvancedLdBut
        //
        advancedLdButLbl.setBackground(Color.white);
        advancedLdButLbl.setBorder(BorderFactory.createLineBorder(Color.white));
        advancedLdButLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 25));
        advancedLdButLbl.setForeground(Color.black);
        advancedLdButLbl.setBounds(201,370,230,31);
        advancedLdButLbl.setName("AdvancedLdBut");
        advancedLdButLbl.setText("<html><div style='background-color: white; width: 230px; padding-left: 5px'>ADVANCED LOAD</div></html>");
        advancedLdButLbl.setVisible(false);
        advancedLdButLbl.setFocusable(false);
        //
        // TypeSymbols
        //
        typeSymbolsLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 34));
        typeSymbolsLbl.setForeground(Color.white);
        typeSymbolsLbl.setBounds(98,163,379,39);
        typeSymbolsLbl.setName("TypeSymbols");
        typeSymbolsLbl.setText("Type 5 symbols:");
        typeSymbolsLbl.setVisible(false);
        typeSymbolsLbl.setFocusable(false);
        //
        // TypedSymbols
        //
        typedSymbolsLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 36));
        typedSymbolsLbl.setForeground(Color.yellow);
        typedSymbolsLbl.setBounds(98,260,350,48);
        typedSymbolsLbl.setName("TypedSymbols");
        typedSymbolsLbl.setText("Q ; F ; X ; A ; W");
        typedSymbolsLbl.setVisible(false);
        typedSymbolsLbl.setFocusable(true);
        //
        // TypeHint
        //
        typeHintLbl.setBorder(BorderFactory.createLineBorder(Color.white));
        typeHintLbl.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 22));
        typeHintLbl.setForeground(Color.white);
        typeHintLbl.setBounds(35,141,386,180);
        typeHintLbl.setName("TypeHint");
        typeHintLbl.setText("<html><div style='padding-left: 10px; padding-top: 10px;'>[Free ; Pellet ; P. pellet ; Wall ; Gate]</div></html>");
        typeHintLbl.setVisible(false);
        typeHintLbl.setFocusable(false);
    }

    private void instantiateComponents(){
        mainPanel = new JPanel();
        openFileDialog1 = new JFileChooser(resourcesPath);

        // Timers will be set up properly lately
        pacUpdater = null;
        pacSmoothTimer = null;
        ghostUpdater = null;
        ghostSmoothTimer = null;

        pacmanLbl = new JLabel();
        ultimateLbl = new JLabel();
        copyrightLbl = new JLabel();
        pressEnterLbl = new JLabel();
        selectMapLbl = new JLabel();
        orgGameLbl = new JLabel();
        settingsLbl = new JLabel();
        escLabelLbl = new JLabel();
        highScrLbl = new JLabel();
        vsLbl = new JLabel();
        highScoreLabelLbl = new JLabel();
        scoreLabelLbl = new JLabel();
        highScoreNumLbl = new JLabel();
        scoreNumLbl = new JLabel();
        musicButtonLbl = new JLabel();
        musicBtnSelectorLbl = new JLabel();
        soundsButtonLbl = new JLabel();
        soundsBtnSelectorLbl = new JLabel();
        gameOverLabelLbl = new JLabel();
        errorLdMapLbl = new JLabel();
        errorInfoLbl = new JLabel();
        tryAgainButLbl = new JLabel();
        advancedLdButLbl = new JLabel();
        typeSymbolsLbl = new JLabel();
        typedSymbolsLbl = new JLabel();
        typeHintLbl = new JLabel();
    }

    private void addComponents()
    {
        mainPanel.add(typeSymbolsLbl);
        mainPanel.add(typedSymbolsLbl);
        mainPanel.add(typeHintLbl);
        mainPanel.add(advancedLdButLbl);
        mainPanel.add(tryAgainButLbl);
        mainPanel.add(errorLdMapLbl);
        mainPanel.add(gameOverLabelLbl);
        mainPanel.add(soundsButtonLbl);
        mainPanel.add(soundsBtnSelectorLbl);
        mainPanel.add(musicButtonLbl);
        mainPanel.add(musicBtnSelectorLbl);
        mainPanel.add(scoreNumLbl);
        mainPanel.add(highScoreNumLbl);
        mainPanel.add(scoreLabelLbl);
        mainPanel.add(highScoreLabelLbl);
        mainPanel.add(vsLbl);
        mainPanel.add(highScrLbl);
        mainPanel.add(escLabelLbl);
        mainPanel.add(settingsLbl);
        mainPanel.add(orgGameLbl);
        mainPanel.add(selectMapLbl);
        mainPanel.add(pressEnterLbl);
        mainPanel.add(copyrightLbl);
        mainPanel.add(ultimateLbl);
        mainPanel.add(pacmanLbl);
        mainPanel.add(errorInfoLbl);
        mainPanel.add(openFileDialog1);
    }

    //</editor-fold>>

    //<editor-fold desc="- GETTERS Block -"

    public JPanel getMainPanel() {
        return mainPanel;
    }
    public JLabel getPacmanLbl(){
        return pacmanLbl;
    }
    public JLabel getUltimateLbl() {
        return ultimateLbl;
    }
    public JLabel getCopyrightLbl() {
        return copyrightLbl;
    }
    public JLabel getPressEnterLbl() {
        return pressEnterLbl;
    }
    public JLabel getSelectMapLbl() {
        return selectMapLbl;
    }
    public JLabel getOrgGameLbl() {
        return orgGameLbl;
    }
    public JLabel getSettingsLbl() {
        return settingsLbl;
    }
    public JLabel getEscLabelLbl() {
        return escLabelLbl;
    }
    public JLabel getHighScrLbl() {
        return highScrLbl;
    }
    public JLabel getVsLbl() {
        return vsLbl;
    }
    public JLabel getMusicButtonLbl() {
        return musicButtonLbl;
    }
    public JLabel getSoundsButtonLbl() {
        return soundsButtonLbl;
    }
    public JLabel getMusicBtnSelectorLbl() {
        return musicBtnSelectorLbl;
    }
    public JLabel getSoundsBtnSelectorLbl() {
        return soundsBtnSelectorLbl;
    }
    public JLabel getHighScoreLabelLbl() {
        return highScoreLabelLbl;
    }
    public JLabel getScoreLabelLbl() {
        return scoreLabelLbl;
    }
    public JLabel getHighScoreNumLbl() {
        return highScoreNumLbl;
    }
    public JLabel getScoreNumLbl() {
        return scoreNumLbl;
    }
    public JLabel getGameOverLabelLbl(){
        return gameOverLabelLbl;
    }
    public JLabel getErrorLdMapLbl() {
        return errorLdMapLbl;
    }
    public JLabel getTryAgainButLbl() {
        return tryAgainButLbl;
    }
    public JLabel getAdvancedLdButLbl() {
        return advancedLdButLbl;
    }
    public JLabel getTypeSymbolsLbl() {
        return typeSymbolsLbl;
    }
    public JLabel getTypedSymbolsLbl() {
        return typedSymbolsLbl;
    }
    public JLabel getTypeHintLbl() {
        return typeHintLbl;
    }
    public JLabel getErrorInfoLbl() {
        return errorInfoLbl;
    }

    public Timer getPacUpdater() {
        return pacUpdater;
    }
    public Timer getGhostUpdater() {
        return ghostUpdater;
    }
    public Timer getPacSmoothTimer() {
        return pacSmoothTimer;
    }
    public Timer getGhostSmoothTimer() {
        return ghostSmoothTimer;
    }

    public JFileChooser getOpenFileDialog1() {
        return openFileDialog1;
    }

    public String getResourcesPath() {
        if(resourcesPath == null) {
            resourcesPath = getResourcePath();
            this.setIconImage(new ImageIcon(resourcesPath + "\\PacManJUltimater.png").getImage());
        }

        return resourcesPath;
    }

    /**
     * All resource files the application uses
     */
    private static final String[] resourceFiles = new String[]{
            "\\config.bin","\\OriginalMap.txt",
            "\\sounds\\pacman_beginning.wav","\\sounds\\pacman_chomp.wav","\\sounds\\pacman_death.wav",
            "\\sounds\\pacman_eatensiren.wav","\\sounds\\pacman_eatghost.wav","\\sounds\\pacman_extrapac.wav",
            "\\sounds\\pacman_intermission.wav","\\sounds\\pacman_powersiren.wav","\\sounds\\pacman_siren.wav",
            "\\textures\\CanBeEaten.png","\\textures\\Entity1RightBig.png","\\textures\\Life.png","\\textures\\PacExplode.png",
            "\\textures\\PacStart.png","\\textures\\PacStartBig.png",
            "\\textures\\Entity1Down.png","\\textures\\Entity1Left.png","\\textures\\Entity1Right.png","\\textures\\Entity1Up.png",
            "\\textures\\Entity2Down.png","\\textures\\Entity2Left.png","\\textures\\Entity2Right.png","\\textures\\Entity2Up.png",
            "\\textures\\Entity3Down.png","\\textures\\Entity3Left.png","\\textures\\Entity3Right.png","\\textures\\Entity3Up.png",
            "\\textures\\Entity4Down.png","\\textures\\Entity4Left.png","\\textures\\Entity4Right.png","\\textures\\Entity4Up.png",
            "\\textures\\Entity5Down.png","\\textures\\Entity5Left.png","\\textures\\Entity5Right.png","\\textures\\Entity5Up.png",
            "\\textures\\EyesDown.png","\\textures\\EyesLeft.png","\\textures\\EyesRight.png","\\textures\\EyesUp.png",
    };

    /**
     * Checks existance of resources directory on preset places.
     * If not found, asks user to specify path to this directory.
     * @return String representing absolute path to resource directory.
     */
    private String getResourcePath(){
        String resources = new File("src\\main\\resources").getAbsolutePath();
        String[] errors = checkResources(resources);
        if(errors.length != 0){
            resources = new File("resources").getAbsolutePath();
            errors = checkResources(resources);
            if(errors.length != 0){
                JOptionPane.showMessageDialog(mainPanel, "NO RESOURCES DIRECTORY FOUND, PLEASE PROVIDE IT.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                JFileChooser locateResources = new JFileChooser();
                locateResources.setDialogTitle("Locate Resources");
                locateResources.setAcceptAllFileFilterUsed(false);
                locateResources.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                mainPanel.add(locateResources);
                locateResources.setVisible(true);
                boolean valid = false;
                while (!valid) {
                    if (locateResources.showOpenDialog(mainPanel) == JFileChooser.APPROVE_OPTION){
                        resources = locateResources.getSelectedFile().getAbsolutePath();
                        errors = checkResources(resources);
                        if(errors.length == 0)
                            valid = true;
                        else{
                            StringBuilder message = new StringBuilder("");
                            int counter = 10;
                            for(String error: errors){
                                if(counter > 0) {
                                    message.append(error + '\n');
                                    --counter;
                                }
                            }
                            if(errors.length > 10)
                                message.append("AND " + Integer.toString(errors.length - 10) + " MORE...");
                            JOptionPane.showMessageDialog(mainPanel, message.toString(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else{
                        valid = true;
                        return null;
                    }
                }
                mainPanel.remove(locateResources);
            }
            return resources;
        }
        return resources;
    }

    /**
     * Checks whether given resource directory contains all requested files.
     * @param resources Absolute path to resources directory.
     * @return String array of errors - missing files.
     */
    private String[] checkResources(String resources){
        if(new File(resources).exists()){
            ArrayList<String> errors = new ArrayList<>();
            for(String resourceFile: resourceFiles){
                if(!(new File(resources + resourceFile).exists()))
                    errors.add("resources" + resourceFile + " not found");
            }
            return errors.toArray(new String[0]);
        }
        return new String[]{"resources directory not found"};
    }

    //</editor-fold>
}