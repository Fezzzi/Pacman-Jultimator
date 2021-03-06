package pacman_ultimater.project_base.gui_swing.ui.controller;

import pacman_ultimater.project_base.ai.*;
import pacman_ultimater.project_base.core.*;
import pacman_ultimater.project_base.custom_utils.IntPair;
import pacman_ultimater.project_base.custom_utils.Quintet;
import pacman_ultimater.project_base.custom_utils.TimersListeners;
import pacman_ultimater.project_base.gui_swing.model.GameConsts;
import pacman_ultimater.project_base.gui_swing.model.GameModel;
import pacman_ultimater.project_base.gui_swing.model.MainFrameModel;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static pacman_ultimater.project_base.gui_swing.model.GameConsts.*;

/**
 * Controlls game loading.
 * Destinguishes between first load, restart level load and next level load.
 */
class GameLoadController
{
    private static final int HEARTHSIZEINPX = 32;

    private final MainFrameModel model;
    private final GameModel vars;
    private JLabel loading, levelLabel, ready, highScoreText;
    private final int[] animLabelsIds = new int[3];
    private int newEntitySize = ENTITIESSIZEINPXS;

    GameLoadController(MainFrameModel model, GameModel vars)
            throws LineUnavailableException
    {
        this.model = model;
        this.vars = vars;
        vars.musicPlayer = AudioSystem.getClip();
        vars.topGhostInTiles = new IntPair(vars.map.item3.item1 - 1, vars.map.item3.item2 - 1);

        initLabels();
    }

    //<editor-fold desc="- HELPER FUNCTIONS Block -">

    /**
     * Initializes loading, level and ready labels.
     */
    private void initLabels()
    {
        final float minMult = Math.min(vars.vMult * 1.05f, vars.hMult);
        final int newTileSize = (int) (LoadMap.TILESIZEINPXS * minMult);

        ready = new JLabel();
        ready.setVisible(false);
        placeLabel(ready, "READY!", Color.yellow,
                new Point((int)(((LoadMap.DEFAULTWIDTH - 150) * vars.hMult) / 2) - 8,
                        (int)(((vars.topGhostInTiles.item2 + 6) * newTileSize) + 46 + ((32 * vars.vMult) - 30))),
                ClasspathFileReader.getFONT().deriveFont(Font.BOLD, 22 * minMult));
        ready.setHorizontalAlignment(SwingConstants.CENTER);
        vars.addedComponents.add(ready);

        loading = new JLabel();
        loading.setVisible(false);
        placeLabel(loading, "...Loading...", Color.yellow,
                new Point((int)(((LoadMap.DEFAULTWIDTH - 400) * vars.hMult) / 2), (int)(60 * vars.vMult)),
                ClasspathFileReader.getFONT().deriveFont(Font.BOLD, 48 * minMult));
        loading.setHorizontalAlignment(SwingConstants.CENTER);
        vars.addedComponents.add(loading);

        levelLabel = new JLabel();
        levelLabel.setVisible(false);
        placeLabel(levelLabel, "", Color.red,
                new Point((int)(((LoadMap.DEFAULTWIDTH - 300) * vars.hMult) / 2), (int)(140 * vars.vMult)),
                ClasspathFileReader.getFONT().deriveFont(Font.BOLD, 30 * minMult));
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vars.addedComponents.add(levelLabel);

        vars.pacLives = new JLabel[MAXLIVES];
        for (int i = 0; i < MAXLIVES; i++) {
            vars.pacLives[i] = new JLabel();
            vars.addedComponents.add(vars.pacLives[i]);
        }

        highScoreText = new JLabel();
    }

    /**
     * Function that handles loading, setting and placing of all the map tiles in game control.
     *
     * @param tiles game map in 2D tile array
     * @param color tiles color
     */
    void renderMap(Tile[][] tiles, Color color)
    {
        if (tiles == null){
            return;
        }

        int newWidth = (int) (vars.defSize.width * vars.hMult);
        int newHeight = (int) (vars.defSize.height * vars.vMult);
        int newTileSize = (int) (LoadMap.TILESIZEINPXS * Math.min(vars.vMult * 1.05, vars.hMult));
        Tile.tileSize = newTileSize;
        Tile.dotSize = newTileSize / 4;
        Tile.penSize = (int)(Tile.DEFPENSIZE * Math.min(vars.vMult, vars.hMult));

        Image bufferImage = model.mainPanel.createImage(newWidth, newHeight);
        vars.bufferGraphics = bufferImage.getGraphics();
        Graphics2D bg2D = (Graphics2D) vars.bufferGraphics;
        bg2D.setStroke(new BasicStroke(2 * Math.min(vars.vMult, vars.hMult)));
        vars.bufferGraphics.setColor(Color.BLACK);
        vars.bufferGraphics.fillRect(0, 0, newWidth, newHeight);
        vars.gameMap.setSize(newWidth, newHeight);

        if (color == LoadMap.TRANSPARENT) {
            color = LoadMap.chooseRandomColor();
        }
        if (color != Color.white) {
            vars.mapColor = color;
        }
        for (int i = 0; i < LoadMap.MAPHEIGHTINTILES; i++) {
            for (int j = 0; j < LoadMap.MAPWIDTHINTILES; j++) {
                tiles[i][j].DrawTile(vars.bufferGraphics, new Point(j * newTileSize, (i + 3) * newTileSize), color);
            }
        }
        vars.gameMap.setIcon(new ImageIcon(bufferImage));
    }

    /**
     * Creates deep copy of map array so the game can modify actual map but does not lose information about original map.
     *
     * @param source Original Map.
     * @return Tile[][]
     */
    private Tile[][] deepCopy(Tile[][] source)
    {
        Tile[][] destination = new Tile[source.length][];
        for (int i = 0; i < source.length; i++)
        {
            destination[i] = new Tile[source[i].length];
            for (int j = 0; j < source[i].length; j++)
                destination[i][j] = new Tile(source[i][j].tile);
        }
        return destination;
    }

    /**
     * Physically places picture's control in the form.
     *
     * @param pic Picture's control.
     * @param image Image to be assigned to picture's control.
     * @param point Picture's location.
     * @param size Picture's image size.
     */
    private void placePicture(JLabel pic, ImageIcon image, Point point, Dimension size)
    {
        pic.setIcon(image);
        pic.setLocation(point);
        pic.setSize(size);
        model.mainPanel.add(pic);
    }

    /**
     * Physically places label's control in the form.
     *
     * @param label Label's control.
     * @param text Text to be assigned to label's control.
     * @param color Assigned text's color.
     * @param point Label's location.
     * @param font Text's font.
     */
    private void placeLabel(JLabel label, String text, Color color, Point point, Font font)
    {
        label.setText(text);
        label.setForeground(color);
        label.setLocation(point);
        label.setFont(font);
        label.setFocusable(false);
        model.mainPanel.add(label);
    }

    /**
     * Resets entities to their original positions and states.
     */
    private void resetEntities()
    {
        final float minMult = Math.min(vars.vMult * 1.05f, vars.hMult);
        final int newTileSize = (int) (LoadMap.TILESIZEINPXS * minMult);
        final int newXPadding = (int)(((vars.defSize.width * vars.hMult) - (LoadMap.MAPWIDTHINTILES * newTileSize)) /2);

        vars.entities.get(0).item1 = LoadMap.PACMANINITIALX;
        vars.entities.get(0).item2 = LoadMap.PACMANINITIALY;
        vars.entities.get(0).item3.setIcon(new ImageIcon(Textures.drawEntity(
                model.mainPanel, minMult, "Entity1", "DIRECTION", 0
        )));
        vars.entities.get(1).item1 = vars.topGhostInTiles.item1;
        vars.entities.get(1).item2 = vars.topGhostInTiles.item2;
        vars.entities.get(1).item3.setIcon(new ImageIcon(Textures.drawEntity(
                model.mainPanel, minMult, "Entity2", "LEFT",0
        )));
        vars.entities.get(2).item1 = vars.topGhostInTiles.item1 - 2;
        vars.entities.get(2).item2 = vars.topGhostInTiles.item2 + 3;
        vars.entities.get(3).item1 = vars.topGhostInTiles.item1;
        vars.entities.get(3).item2 = vars.topGhostInTiles.item2 + 3;
        vars.entities.get(4).item1 = vars.topGhostInTiles.item1 + 2;
        vars.entities.get(4).item2 = vars.topGhostInTiles.item2 + 3;

        for(int i = 0; i < GameConsts.ENTITYCOUNT; ++i) {
            vars.entities.get(i).item3.setLocation(
                    new Point(vars.entities.get(i).item1 * newTileSize + newXPadding - 10,
                            vars.entities.get(i).item2 * newTileSize + (int)(40 * minMult)));

            if(i < 2)
                vars.entities.get(i).item4 = Direction.directionType.LEFT;
            else {
                vars.entities.get(i).item4 = Direction.directionType.DIRECTION;
                vars.entities.get(i).item3.setIcon(new ImageIcon(Textures.drawEntity(
                        model.mainPanel, minMult, "Entity" + Integer.toString(i + 1), i % 2 == 0 ? "UP" : "DOWN",0)
                ));
            }
        }
    }

    /**
     * Function that loads all the game entities and presets all their default settings such as position, direction, etc...
     */
    private void loadEntities()
    {
        //Entity's Data structure consists of:
        //  - Two numbers - x and y position on the map in Tiles.
        //  - Picture box containing entity's image and its physical location.
        //  - Direction used later for entity's movement and selecting the right image.
        //  - Type of entity such as NoAI, or all the other kinds of enemy's AI.
        DefaultAI[] defaultAIs = new DefaultAI[]
        {
            new BinkyAI(DefaultAI.nType.HOSTILEATTACK, 1, vars.map.item6),
            new PinkyAI(DefaultAI.nType.HOSTILEATTACK, 2, vars.map.item6),
            new InkyAI(DefaultAI.nType.HOSTILEATTACK, 3, vars.map.item6),
            new ClydeAI(DefaultAI.nType.HOSTILEATTACK, 4, vars.map.item6)
        };

        final float minMult = Math.min(vars.vMult * 1.05f, vars.hMult);
        final int newTileSize = (int) (LoadMap.TILESIZEINPXS * minMult);
        final int newXPadding = (int)(((vars.defSize.width * vars.hMult) - (LoadMap.MAPWIDTHINTILES * newTileSize)) /2);
        newEntitySize = (int)(ENTITIESSIZEINPXS * minMult);
        final boolean attack = vars.chaseMode > 0;

        for (DefaultAI ai : defaultAIs) {
            if (attack) {
                ai.state = DefaultAI.nType.HOSTILEATTACK;
            } else {
                ai.state = DefaultAI.nType.HOSTILERETREAT;
            }
        }

        vars.entities = new ArrayList<>();
        vars.entities.add(new Quintet<>(LoadMap.PACMANINITIALX, LoadMap.PACMANINITIALY,
                new JLabel(), Direction.directionType.LEFT, new DefaultAI(DefaultAI.nType.NOAI, 1, null)));
        vars.entities.add(new Quintet<>(vars.topGhostInTiles.item1, vars.topGhostInTiles.item2,
                new JLabel(), Direction.directionType.LEFT,
                vars.player2 ? new DefaultAI(DefaultAI.nType.NOAI, 2, null) : defaultAIs[0]));
        vars.entities.add(new Quintet<>(vars.topGhostInTiles.item1 - 2, vars.topGhostInTiles.item2 + 3,
                new JLabel(),Direction.directionType.DIRECTION,
                vars.player3 ? new DefaultAI(DefaultAI.nType.NOAI, 3, null) : defaultAIs[1]));
        vars.entities.add(new Quintet<>(vars.topGhostInTiles.item1, vars.topGhostInTiles.item2 + 3,
                new JLabel(),Direction.directionType.DIRECTION,
                vars.player4 ? new DefaultAI(DefaultAI.nType.NOAI, 3, null) : defaultAIs[2]));
        vars.entities.add(new Quintet<>(vars.topGhostInTiles.item1 + 2, vars.topGhostInTiles.item2 + 3,
                new JLabel(),Direction.directionType.DIRECTION, defaultAIs[3]));

        //Setting entities names for easy later manipulation and automatic image selection
        for (int i = 1; i <= ENTITYCOUNT; i++)
            vars.entities.get(i - 1).item3.setName("Entity" + Integer.toString(i));

        // All those magic numbers are X and Y axes correction for entities' pictures to be correctly placed.
        placePicture(vars.entities.get(0).item3,
            new ImageIcon(Textures.drawEntity(model.mainPanel, minMult, "Entity1", "DIRECTION", 0)),
            new Point(vars.entities.get(0).item1 * newTileSize + newXPadding - 8,
                    vars.entities.get(0).item2 * newTileSize + (int)(42 * minMult)),
            new Dimension(newEntitySize, newEntitySize));

        placePicture(vars.entities.get(1).item3,
            new ImageIcon(Textures.drawEntity(model.mainPanel, minMult, "Entity2", "LEFT", 0)),
            new Point(vars.entities.get(1).item1 * newTileSize + newXPadding - 8,
                    vars.entities.get(1).item2 * newTileSize + (int)(42 * minMult)),
            new Dimension(newEntitySize, newEntitySize));

        for (int i = 2; i < ENTITYCOUNT; i++) {
            placePicture(vars.entities.get(i).item3,
                new ImageIcon(Textures.drawEntity(model.mainPanel, minMult, "Entity" + Integer.toString(i + 1),
                        i % 2 == 0 ? "UP" : "DOWN", 0)),
                new Point(vars.entities.get(i).item1 * newTileSize + 3 + newXPadding - 8,
                        vars.entities.get(i).item2 * newTileSize + (int)(42 * minMult)),
                new Dimension(newEntitySize, newEntitySize));
        }
    }

    /**
     * Function that loads score labels and pacman lives.
     */
    private void loadHud()
    {
        final float minMult = Math.min(vars.vMult, vars.hMult);
        final Font hudTextFont = new Font("Arial", Font.BOLD, (int)(18 * minMult));
        final int hudLabelHeight = (int)(30 * vars.vMult);
        final int hudLabelWidth = (int)(200 * vars.hMult);
        final int newTileSize = (int)(LoadMap.TILESIZEINPXS * Math.min(vars.vMult  * 1.05, vars.hMult));
        final int newXPadding = (int)(((vars.defSize.width * vars.hMult) - (LoadMap.MAPWIDTHINTILES * newTileSize)) /2);
        int lives = 0;

        vars.up1.setSize(hudLabelWidth, hudLabelHeight);
        placeLabel(vars.up1, "1UP", Color.white,
                new Point(newXPadding + 3 * newTileSize, 0), hudTextFont);
        vars.up1.setVisible(true);

        vars.scoreBox.setSize(hudLabelWidth, hudLabelHeight);
        placeLabel(vars.scoreBox, vars.score > 0 ? Integer.toString(vars.score) : "00", Color.white,
                new Point(newXPadding + 4 * newTileSize, (int)(20 * minMult)), hudTextFont);
        vars.scoreBox.setVisible(true);

        //Selects labels depending on game mode
        if (!(vars.player2 || vars.player3 || vars.player4))
        {
            vars.highScoreBox.setSize(hudLabelWidth, hudLabelHeight);
            placeLabel(vars.highScoreBox, vars.highScore > 0 ? Integer.toString(vars.highScore) : "00", Color.white,
                    new Point(newXPadding + 21 * newTileSize, (int)(20 * minMult)), hudTextFont);
            vars.highScoreBox.setVisible(true);

            highScoreText.setSize(hudLabelWidth ,hudLabelHeight);
            placeLabel(highScoreText, "HIGH SCORE", Color.white,
                    new Point(newXPadding + 17 * newTileSize, 0), hudTextFont);
            highScoreText.setVisible(true);
        }
        else
        {
            vars.up2.setSize(hudLabelWidth, hudLabelHeight);
            placeLabel(vars.up2, "2UP", Color.white,
                    new Point(newXPadding + 21 * newTileSize, 0), hudTextFont);
            vars.up2.setVisible(true);

            vars.score2Box.setSize(hudLabelWidth, hudLabelHeight);
            placeLabel(vars.score2Box, vars.score2 > 0 ? Integer.toString(vars.score2) : "00", Color.white,
                    new Point(newXPadding + 22 * newTileSize, (int)(20 * minMult)), hudTextFont);
            vars.score2Box.setVisible(true);
        }

        // Places all lives on their supposed place.
        int newHeartSize = (int)(HEARTHSIZEINPX * minMult);
        for (JLabel item : vars.pacLives)
        {
            item.setSize(50,50);
            placePicture(item, new ImageIcon(Textures.drawEntity(
                    model.mainPanel, minMult - 0.1f, "Entity1", "LEFT", 0)),
                    new Point(newXPadding + lives * HEARTHSIZEINPX + newTileSize,
                            ((LoadMap.MAPHEIGHTINTILES + 3) * newTileSize) + 4),
                    new Dimension(newHeartSize, newHeartSize));
            lives++;
        }

        vars.fruitLabel.setSize((int)(26 * minMult),(int)(26 * minMult));
        vars.fruitLabel.setLocation(
                (int)(newXPadding + (LoadMap.MAPWIDTHINTILES * newTileSize)/2 - (15 * minMult)),
                (int)(((vars.topGhostInTiles.item2 + 6) * newTileSize) + 40 + ((34 * vars.vMult) - 30)));

        vars.fruitHud.setLocation(
                (int)(newXPadding + (newTileSize * LoadMap.MAPWIDTHINTILES) - (7 * 30 * minMult + 56)),
                ((LoadMap.MAPHEIGHTINTILES + 3) * newTileSize) + 4);
        vars.fruitHud.setSize((int)(7 * 28 * minMult + 56),(int)(35 * minMult));
        vars.fruitHud.setIcon(new ImageIcon(Textures.getFruits(model.mainPanel, minMult, vars.collectedFruits)));
        model.mainPanel.add(vars.fruitHud);
    }

    /**
     * Initializes pacman's and ghosts' timers.
     *
     * @param timers Structure holding listeners for each initialized timer.
     */
    private void initTimers(TimersListeners timers)
    {
        model.pacUpdater = new Timer(PACTIMER, timers.getPacman_timer());
        model.pacUpdater.setRepeats(true);

        model.ghostUpdater = new Timer(
                !(vars.player2 || vars.player3 || vars.player4)
                        ? (GameConsts.PACTIMER + 40 - (vars.level > 13 ? 65 : vars.level * 5))
                        : model.pacUpdater.getDelay() + (vars.player2 ? 10 : vars.player3 ? 20 : 30),
                timers.getGhost_timer());
        model.ghostUpdater.setRepeats(true);

        model.pacSmoothTimer = new Timer(
                model.pacUpdater.getDelay() / ((LoadMap.TILESIZEINPXS / 2) + 1),
                timers.getPacman_smooth_timer());
        model.pacSmoothTimer.setRepeats(true);

        model.ghostSmoothTimer = new Timer(
                model.ghostUpdater.getDelay() / ((LoadMap.TILESIZEINPXS / 2) + 1),
                timers.getGhost_smooth_timer());
        model.ghostSmoothTimer.setRepeats(true);
    }

    /**
     * Procedure serving for initialization of variables at the game load up.
     *
     * @throws IOException Propagation from highScore loading.
     * @throws LineUnavailableException Propagation from sound players creation.
     */
    private void varsInit()
        throws IOException, LineUnavailableException, UnsupportedAudioFileException
    {
        int newWidth = (int) (vars.defSize.width * vars.hMult);
        int newHeight = (int) (vars.defSize.height * vars.vMult);
        int newTileSize = (int) (LoadMap.TILESIZEINPXS * Math.min(vars.vMult * 1.05, vars.hMult));

        vars.gameMap = new JLabel();
        vars.gameMap.setSize(newWidth, newHeight);
        vars.gameMap.setLocation(((newWidth - (LoadMap.MAPWIDTHINTILES * newTileSize)) /2) - 8, vars.gameMap.getY());
        vars.extraLifeGiven = false;
        vars.score = 0;
        vars.score2 = 0;
        vars.initSoundPlayers();
        vars.lives = 3;
        vars.collectedFruits = new ArrayList<>();

        if (vars.highScore == -1)
            vars.highScore = HighScoreClass.loadHighScore();
    }

    /**
     * Procedure serving for variables resetting at the map load up.
     *
     * @param nextLevel boolean
     */
    private void varsReset(boolean nextLevel){
        vars.soundTick = 0;
        vars.keyPressed1 = vars.keyPressed2 = vars.keyPressed3 = vars.keyPressed4 = false;
        vars.ghostsEaten = 0;
        vars.keyCountdown1 = vars.keyCountdown2 = vars.keyCountdown3 = vars.keyCountdown4 = 0;
        vars.killed = false;
        vars.ticks = 0;
        vars.freeGhosts = 1;
        vars.eatEmTimer = 0;
        vars.ghostRelease = vars.player2 ? (GameConsts.BASEGHOSTRELEASETIMER / 5)
                : vars.player3 ? (GameConsts.BASEGHOSTRELEASETIMER / 7)
                : vars.player4 ? (GameConsts.BASEGHOSTRELEASETIMER / 10)
                : (GameConsts.BASEGHOSTRELEASETIMER - vars.level) / 3;

        if (nextLevel){
            vars.collectedDots = 0;
            vars.modeSwitchCounter = 0;
            vars.chaseMode = 0;
            vars.scatterMode = 70;
            vars.ghostRelease = vars.player2 ? (GameConsts.BASEGHOSTRELEASETIMER / 5)
                    : vars.player3 ? (GameConsts.BASEGHOSTRELEASETIMER / 7)
                    : vars.player4 ? (GameConsts.BASEGHOSTRELEASETIMER / 10)
                    : (GameConsts.BASEGHOSTRELEASETIMER - vars.level) / 3;
        }
    }

    /**
     * Corrects start positions of pacman and first ghost as they are located between the tiles at first.
     */
    private void correctStartPositions()
    {
        vars.entities.get(0).item3.setLocation(new Point(vars.entities.get(0).item3.getLocation().x - 6,
                vars.entities.get(0).item3.getLocation().y));
        vars.entities.get(1).item3.setLocation(new Point(vars.entities.get(1).item3.getLocation().x - 6,
                vars.entities.get(1).item3.getLocation().y));
    }

    /**
     * Sets visibility for components based on given intro/game boolean.
     *
     * @param gameComponents Indicates whether to set visible components for the intro or for the game.
     */
    private void toggleComponentsVisibility (boolean gameComponents)
    {
        vars.gameMap.setVisible(gameComponents);
        ready.setVisible(gameComponents);
        for(int i = 0; i < vars.entities.size(); ++i)
            vars.entities.get(i).item3.setVisible(gameComponents);

        levelLabel.setVisible(!gameComponents);
        loading.setVisible(!gameComponents);
    }

    /**
     * Resizes and repositions ready, level and loading labels
     */
    private void resizeLabels()
    {
        final float minMult = Math.min(vars.vMult, vars.hMult);
        final int newTileSize = (int) (LoadMap.TILESIZEINPXS * Math.min(vars.vMult * 1.05, vars.hMult));

        levelLabel.setSize((int)(300 * vars.hMult), (int)(60 * vars.hMult));
        loading.setSize((int)(400 * vars.hMult), (int)(60 * vars.vMult));
        ready.setSize((int)(150 * vars.hMult), (int)(30 * vars.vMult));

        levelLabel.setLocation((int)(((LoadMap.DEFAULTWIDTH - 300) * vars.hMult) / 2), (int)(140 * vars.vMult));
        loading.setLocation((int)(((LoadMap.DEFAULTWIDTH - 400) * vars.hMult) / 2), (int)(60 * vars.vMult));
        ready.setLocation(
                (int)(((LoadMap.DEFAULTWIDTH - 150) * vars.hMult) / 2) - 8,
                (int)(((vars.topGhostInTiles.item2 + 6) * newTileSize) + 46 + ((32 * vars.vMult) - 30)));

        levelLabel.setFont(ClasspathFileReader.getFONT().deriveFont(Font.BOLD, 30 * minMult));
        loading.setFont(ClasspathFileReader.getFONT().deriveFont(Font.BOLD, 48 * minMult));
        ready.setFont(ClasspathFileReader.getFONT().deriveFont(Font.BOLD, 22 * minMult));
    }

    //</editor-fold>

    //<editor-fold desc="- PUBLIC API Block -">

    /**
     * Provides next level loading in already loaded game.
     */
    void loadNextLevel()
    {
        LoadNextLevelTask lngTask = new LoadNextLevelTask();
        lngTask.execute();
    }

    /**
     * Provides already loaded level reloading.
     */
    void reloadLevel()
    {
        RestartLevelTask rgTask = new RestartLevelTask();
        rgTask.execute();
    }

    /**
     * Provides already loaded level rerendering
     */
    void resizeLevel()
    {
        final float minMult = Math.min(vars.vMult, vars.hMult);
        final Font hudTextFont = new Font("Arial", Font.BOLD, (int)(18 * minMult));
        final int hudLabelHeight = (int)(30 * vars.vMult);
        final int hudLabelWidth = (int)(200 * vars.hMult);
        final int newTileSize = (int)(LoadMap.TILESIZEINPXS * Math.min(vars.vMult  * 1.05f, vars.hMult));
        final int newXPadding = (int)(((vars.defSize.width * vars.hMult) - (LoadMap.MAPWIDTHINTILES * newTileSize)) /2) - 8;
        newEntitySize = (int)(ENTITIESSIZEINPXS * minMult);

        resizeLabels();
        renderMap(vars.mapFresh, vars.map.item4);

        for (JLabel label: new JLabel[]{vars.up1, vars.scoreBox, highScoreText, vars.highScoreBox, vars.up2, vars.score2Box}){
            label.setSize(hudLabelWidth, hudLabelHeight);
            label.setFont(hudTextFont);
        }

        vars.up1.setLocation(3 * newTileSize + newXPadding, 0);
        vars.scoreBox.setLocation(4 * newTileSize + newXPadding, (int)(20 * minMult));
        highScoreText.setLocation(17 * newTileSize + newXPadding, 0);
        vars.highScoreBox.setLocation(21 * newTileSize + newXPadding, (int)(20 * minMult));
        vars.up2.setLocation(21 * newTileSize + newXPadding, 0);
        vars.score2Box.setLocation(22 * newTileSize + newXPadding, (int)(20 * minMult));

        int i = 0;
        for (JLabel item : vars.pacLives)
        {
            item.setSize((int)(50 * minMult),(int)(50 * minMult));
            item.setLocation(
                    (int)(newXPadding + i * 28 * (minMult - 0.1f) + newTileSize),
                    (LoadMap.MAPHEIGHTINTILES * newTileSize) + (3 * LoadMap.TILESIZEINPXS));
            item.setIcon(new ImageIcon(Textures.drawEntity(
                    model.mainPanel, minMult - 0.1f, "Entity1", "LEFT", 0
            )));
            ++i;
        }

        vars.fruitLabel.setSize((int)(28 * minMult),(int)(28 * minMult));
        vars.fruitLabel.setLocation(
                (int)(newXPadding + (LoadMap.MAPWIDTHINTILES * newTileSize)/2 - (15 * minMult)),
                (int)(((vars.topGhostInTiles.item2 + 6) * newTileSize) + 40 + ((34 * vars.vMult) - 30)));
        if (vars.fruitLife > 0 && vars.fruitLife < GameConsts.FRUITLIFE){
            vars.fruitLabel.setIcon(new ImageIcon(
                    Textures.drawFruit(model.mainPanel, minMult, vars.level + 1)
            ));
        }

        vars.fruitHud.setLocation(
                (int)(newXPadding + (newTileSize * LoadMap.MAPWIDTHINTILES) - (7 * 30 * minMult + 56)),
                ((LoadMap.MAPHEIGHTINTILES + 2) * newTileSize) + 20);
        vars.fruitHud.setSize((int)(7 * 28 * minMult + 56),(int)(35 * minMult));
        vars.fruitHud.setIcon(new ImageIcon(Textures.getFruits(model.mainPanel, minMult, vars.collectedFruits)));

        if (vars.entities != null) {
            Quintet<Integer, Integer, JLabel, Direction.directionType, DefaultAI> entity;
            for (int j = 0; j <= vars.freeGhosts; j++) {
                entity = vars.entities.get(j);
                entity.item3.setSize(newEntitySize, newEntitySize);
                entity.item3.setLocation(entity.item1 * newTileSize + newXPadding,
                        entity.item2 * newTileSize + (int)(40 * Math.min(vars.vMult * 1.05, vars.hMult)));
                entity.item3.setIcon(new ImageIcon(Textures.drawEntity(
                        model.mainPanel, minMult, entity.item3.getName(), entity.item4.name(), 0
                )));
            }

            for (int j = vars.freeGhosts + 1; j < ENTITYCOUNT; j++) {
                entity = vars.entities.get(j);
                entity.item3.setSize(newEntitySize, newEntitySize);
                entity.item3.setLocation(entity.item1 * newTileSize + newXPadding,
                        (int) (entity.item2 * newTileSize + (42 * Math.min(vars.vMult * 1.05, vars.hMult))));
                entity.item3.setIcon(new ImageIcon(Textures.drawEntity(
                        model.mainPanel, minMult, entity.item3.getName(), entity.item4.name(), 0
                )));
            }
        }
        if (!model.pacUpdater.isRunning() && !model.ghostUpdater.isRunning()) {
            vars.gameMap.setLocation(newXPadding, vars.gameMap.getY());
        }
    }

    /**
     * Provides loading and general preparing of the game at the level start-up.
     *
     * @param timers Class implementing timers' handling.
     */
    void loadGame(TimersListeners timers)
    {
        PlayGameTask pgTask = new PlayGameTask(timers);
        pgTask.execute();
    }

    //</editor-fold>

    //<editor-fold desc="- LOADING TASKS Block -">

    private enum playGamePhase {PHASE1, PHASE2, PHASE3, INTERRUPTEDEXCEPTION, EXECUTIONEXCEPTION}

    class LoadNextLevelTask extends GameLoadSwingWorker
    {
        LoadNextLevelTask()
        {
            super(true);
        }

        @Override
        public Void doInBackground()
        {
            try {
                publish(playGamePhase.PHASE1);
                AnimationTask animTask = new AnimationTask(vars.level);
                animTask.execute();
                animTask.get();

                if (vars.level != 2 && vars.level != 5 && vars.level != 9 && vars.level != 12 && vars.level != 15){
                    Thread.sleep(1500);
                }
                vars.musicPlayer.stop();

                publish(playGamePhase.PHASE2);
                Thread.sleep(OPENINGTHEMELENGTH);

                publish(playGamePhase.PHASE3);
            }
            catch (InterruptedException exception){
                publish(playGamePhase.INTERRUPTEDEXCEPTION);
            }
            catch (ExecutionException exception){
                publish(playGamePhase.EXECUTIONEXCEPTION);
            }
            return null;
        }

        @Override
        protected void process(List<playGamePhase> phases)
        {
            final float minMult = Math.min(vars.vMult * 1.05f, vars.hMult);
            final int newTileSize = (int) (LoadMap.TILESIZEINPXS * minMult);

            try {
                for (playGamePhase phase : phases) {
                    if (vars.gameOn) {
                        switch (phase) {
                            case PHASE1:
                                varsReset(true);
                                levelLabel.setText("- Level " + Integer.toString(vars.level) + " -");
                                resizeLabels();
                                toggleComponentsVisibility(false);
                                if (vars.music)
                                    vars.playWithMusicPLayer(ClasspathFileReader.getPACMAN_INTERMISSION(),
                                            true, 0, 58000);
                                break;
                            case PHASE2:
                                if (vars.music)
                                    vars.musicPlayer.stop();

                                for (int i = MAXLIVES - 1; i > vars.lives - 2 && i >= 0; i--)
                                    vars.pacLives[i].setVisible(false);

                                resetEntities();
                                vars.fruitLife = 0;
                                vars.fruitLabel.setVisible(false);
                                vars.mapFresh = deepCopy(vars.map.item1);
                                model.ghostUpdater.setDelay(!(vars.player2 || vars.player3 || vars.player4) ?
                                        (GameConsts.PACTIMER + 40 - (vars.level > 13 ? 65 : vars.level * 5))
                                        : model.pacUpdater.getDelay() + (vars.player2 ? 10 : vars.player3 ? 20 : 30));
                                model.ghostSmoothTimer.setDelay(model.ghostUpdater.getDelay() / ((newTileSize / 2) + 1));

                                renderMap(vars.mapFresh, vars.map.item4);
                                toggleComponentsVisibility(true);

                                if (vars.music)
                                    vars.playWithMusicPLayer(ClasspathFileReader.getPACMAN_BEGINNING(),
                                            false, 0, 0);
                                break;
                            case PHASE3:
                                if (vars.music)
                                    vars.musicPlayer.close();

                                ready.setVisible(false);
                                model.mainPanel.revalidate();
                                correctStartPositions();
                                if (vars.music)
                                    vars.playWithMusicPLayer(ClasspathFileReader.getPACMAN_SIREN(),
                                            true, 0, 9100);

                                model.pacUpdater.start();
                                model.ghostUpdater.start();
                                break;
                            case INTERRUPTEDEXCEPTION:
                                if (vars.score > vars.highScore) {
                                    HighScoreClass.tryToSaveScore(vars.player2 || vars.player3 || vars.player4, vars.score);
                                }
                                MainFrameController.handleExceptions("java.lang.InterruptedException", model);
                                break;
                        }
                    }
                }
            }
            catch (LineUnavailableException | IOException | UnsupportedAudioFileException exception) {
                if (vars.score > vars.highScore) {
                    try {
                        HighScoreClass.tryToSaveScore(vars.player2 || vars.player3 || vars.player4, vars.score);
                    } catch (IOException ignore) {
                        MainFrameController.fatalErrorMessage("score was not saved!");
                    }
                }
                MainFrameController.handleExceptions(exception.toString(), model);
            }
        }
    }

    class RestartLevelTask extends GameLoadSwingWorker
    {
        RestartLevelTask()
        {
            super(false);
        }

        @Override
        protected void process(List<playGamePhase> phases)
        {
            try {
                for (playGamePhase phase : phases) {
                    if (vars.gameOn) {
                        switch (phase) {
                            case PHASE2:
                                if (vars.music) {
                                    vars.musicPlayer.stop();
                                    vars.playWithMusicPLayer(ClasspathFileReader.getPACMAN_BEGINNING(),
                                            false, 0, 0);
                                }
                                varsReset(false);
                                for (int i = MAXLIVES - 1; i > vars.lives - 2 && i >= 0; i--)
                                    vars.pacLives[i].setVisible(false);

                                if (vars.player2 || vars.player3 || vars.player4) {
                                    vars.score2Box.setText(Integer.toString(vars.score2));
                                }
                                resetEntities();
                                ready.setVisible(true);
                                renderMap(vars.mapFresh, vars.map.item4);
                                break;
                            case PHASE3:
                                if (vars.music)
                                    vars.musicPlayer.close();

                                ready.setVisible(false);
                                correctStartPositions();
                                if (vars.music)
                                    vars.playWithMusicPLayer(ClasspathFileReader.getPACMAN_SIREN(),
                                            true, 0, 9100);

                                model.pacUpdater.start();
                                model.ghostUpdater.start();
                                model.mainPanel.revalidate();
                                break;
                            case INTERRUPTEDEXCEPTION:
                                if (vars.score > vars.highScore) {
                                    HighScoreClass.tryToSaveScore(vars.player2 || vars.player3 || vars.player4, vars.score);
                                }
                                MainFrameController.handleExceptions("java.lang.InterruptedException", model);
                                break;
                        }
                    }
                }
            }
            catch (LineUnavailableException | IOException | UnsupportedAudioFileException exception) {
                if (vars.score > vars.highScore) {
                    try {
                        HighScoreClass.tryToSaveScore(vars.player2 || vars.player3 || vars.player4, vars.score);
                    } catch (IOException ignore) {
                        MainFrameController.fatalErrorMessage("score was not saved!");
                    }
                }
                MainFrameController.handleExceptions(exception.toString(), model);
            }
        }
    }

    class PlayGameTask extends GameLoadSwingWorker
    {
        final TimersListeners timers;

        PlayGameTask(TimersListeners timers)
        {
            super(true);
            this.timers = timers;
        }

        @Override
        protected void process(List<playGamePhase> phases)
        {
            try {
                for (playGamePhase phase : phases) {
                    if (vars.gameOn) {
                        switch (phase) {
                            case PHASE1:
                                varsInit();
                                varsReset(true);
                                initTimers(timers);
                                levelLabel.setText("- Level " + Integer.toString(vars.level) + " -");
                                resizeLabels();
                                loading.setVisible(true);
                                levelLabel.setVisible(true);

                                if (vars.music)
                                    vars.playWithMusicPLayer(ClasspathFileReader.getPACMAN_INTERMISSION(),
                                            true, 0, 58000);
                                break;

                            case PHASE2:
                                if (vars.music)
                                    vars.musicPlayer.close();

                                loadHud();
                                for (int i = MAXLIVES - 1; i > vars.lives - 2 && i >= 0; i--)
                                    vars.pacLives[i].setVisible(false);

                                loadEntities();
                                model.mainPanel.add(vars.fruitLabel);
                                vars.fruitLife = 0;
                                vars.fruitLabel.setVisible(false);
                                if (vars.level <= 13 && vars.level > 1)
                                    model.ghostUpdater.setDelay(model.ghostUpdater.getDelay() - 5);

                                vars.mapFresh = deepCopy(vars.map.item1);
                                model.mainPanel.add(vars.gameMap);
                                toggleComponentsVisibility(true);
                                renderMap(vars.mapFresh, vars.map.item4);

                                if (vars.music)
                                    vars.playWithMusicPLayer(ClasspathFileReader.getPACMAN_BEGINNING(),
                                            false, 0 , 0);
                                break;
                            case PHASE3:
                                if (vars.music)
                                    vars.musicPlayer.close();

                                ready.setVisible(false);
                                correctStartPositions();
                                if (vars.music)
                                    vars.playWithMusicPLayer(ClasspathFileReader.getPACMAN_SIREN(),
                                            true, 0, 9100);

                                model.pacUpdater.start();
                                model.ghostUpdater.start();
                                model.mainPanel.revalidate();
                                break;
                            case EXECUTIONEXCEPTION:
                                if (vars.score > vars.highScore) {
                                    HighScoreClass.tryToSaveScore(vars.player2 || vars.player3 || vars.player4, vars.score);
                                }
                                MainFrameController.handleExceptions("java.util.concurrent.ExecutionException", model);
                                break;
                            case INTERRUPTEDEXCEPTION:
                                if (vars.score > vars.highScore) {
                                    HighScoreClass.tryToSaveScore(vars.player2 || vars.player3 || vars.player4, vars.score);
                                }
                                MainFrameController.handleExceptions("java.lang.InterruptedException", model);
                                break;
                        }
                    }
                }
            }
            catch (LineUnavailableException | IOException | UnsupportedAudioFileException exception) {
                if (vars.score > vars.highScore) {
                    try {
                        HighScoreClass.tryToSaveScore(vars.player2 || vars.player3 || vars.player4, vars.score);
                    } catch (IOException ignore) {
                        MainFrameController.fatalErrorMessage("score was not saved!");
                    }
                }
                MainFrameController.handleExceptions(exception.toString(), model);
            }
        }
    }

    abstract class GameLoadSwingWorker extends SwingWorker<Void, playGamePhase>
    {
        final boolean withAnimation;

        GameLoadSwingWorker(boolean withAnimation){
            this.withAnimation = withAnimation;
        }

        @Override
        public Void doInBackground()
        {
            try {
                if (withAnimation) {
                    publish(playGamePhase.PHASE1);
                    AnimationTask animTask = new AnimationTask(vars.level);
                    animTask.execute();
                    animTask.get();

                    if (vars.level != 2 && vars.level != 5 && vars.level != 9 && vars.level != 12 && vars.level != 15){
                        Thread.sleep(1500);
                    }
                }

                publish(playGamePhase.PHASE2);
                Thread.sleep(OPENINGTHEMELENGTH);

                publish(playGamePhase.PHASE3);
            }
            catch(InterruptedException exception){
                publish(playGamePhase.INTERRUPTEDEXCEPTION);
            }
            catch(ExecutionException exception){
                publish(playGamePhase.EXECUTIONEXCEPTION);
            }
            return null;
        }
    }

    //</editor-fold>

    //<editor-fold desc="- ANIMATION TASKS Block -">

    /**
     * Represents single frame of loading animation
     */
    class AnimationFrame
    {
        final boolean initialisation;
        final boolean finished;
        final ImageIcon pacmanImage;
        final ImageIcon ghostImage;
        final JLabel[] entities;
        final Point[] locations;

        AnimationFrame(
                boolean initialisation, boolean finished,
                ImageIcon pacmanImage, ImageIcon ghostImage,
                JLabel[] entities, Point[] locations)
        {
            this.initialisation = initialisation;
            this.finished = finished;
            this.pacmanImage = pacmanImage;
            this.ghostImage = ghostImage;
            this.entities = entities;
            this.locations = locations;
        }
    }

    /**
     * Background worker handling loading animation playing. Works on publish/process base.
     */
    class AnimationTask extends SwingWorker<Void, AnimationFrame>
    {
        final int level;
        final float minMult;

        AnimationTask(int level){
            this.level = level;
            minMult = Math.min(vars.vMult * 1.05f, vars.hMult);
        }

        @Override
        public Void doInBackground()
                throws InterruptedException
        {
            if (level == 2) {
                playFirstAnimation();
            } else if (level == 5) {
                playSecondAnimation();
            } else if (level == 9 || level == 12 || level == 15) {
                playThirdAnimation();
            }

            return null;
        }

        /**
         * First animation, from left to right and from right to left, LEVEL 1 -> 2
         *
         * @throws InterruptedException To be handled by caller
         */
        private void playFirstAnimation()
                throws InterruptedException
        {
            int startX;
            int delta;
            int step = 0;
            JLabel[] elements = new JLabel[]{new JLabel(), new JLabel()};
            Point[] locations = new Point[2];
            ImageIcon pacmanImage = null;
            ImageIcon ghostImage = null;

            // INITIALIZATION PART: ------------------------------------------------------------------------------
            for (int i = 0; i <= 1 && vars.gameOn; i++) {
                elements[i].setSize(new Dimension(newEntitySize * 4, newEntitySize * 4));
            }
            publish(new AnimationFrame(true, false, null, null, elements, null));
            Thread.sleep(24);

            // FIRST PART: From right to left --------------------------------------------------------------------
            animRightToLeft(300, false, elements, locations);
            Thread.sleep(250);

            // SECOND PART: From left to right -------------------------------------------------------------------
            for (int j = 300; j >= 0 && vars.gameOn; --j)
            {
                startX = (int)(LoadMap.DEFAULTWIDTH * vars.hMult + 100);
                delta = startX/270;
                for (int i = 0; i < 2; i++) {
                    locations[i] = new Point(startX - (j*delta) + (int)((i-1) * j * 1.75 * minMult),
                            (int)((LoadMap.MAPHEIGHTINTILES / 2f + 6) * LoadMap.TILESIZEINPXS * minMult));
                }
                if (step < 4) {
                    pacmanImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult * 2, "Entity1", "DIRECTION", 0));
                    ghostImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult, "CanBeEaten", "RIGHT", 1));
                } else {
                    pacmanImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult * 2,"Entity1","RIGHT", 0));
                    ghostImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult, "CanBeEaten", "RIGHT", 0));
                }

                publish(new AnimationFrame(false, false, pacmanImage, ghostImage, elements, locations));
                step = (step + 1) % 8;
                Thread.sleep(18);
            }

            publish(new AnimationFrame(false, true, pacmanImage, ghostImage, elements, locations));
        }

        /**
         * Second animation, from left to right, Binky steps on a nail, LEVEL 4 -> 5
         *
         * @throws InterruptedException To be handled by caller
         */
        private void playSecondAnimation()
                throws InterruptedException
        {
            int startX;
            int delta;
            int step = 0;
            JLabel[] elements = new JLabel[]{new JLabel(), new JLabel(), new JLabel()};
            Point[] locations = new Point[3];
            ImageIcon pacmanImage = null;
            ImageIcon ghostImage;

            // INITIALIZATION PART: ------------------------------------------------------------------------------
            for (int i = 0; i <= 1 && vars.gameOn; i++) {
                elements[i].setSize(new Dimension(newEntitySize * 4, newEntitySize * 4));
            }
            elements[2].setSize(new Dimension(30, 10));
            elements[2].setIcon(new ImageIcon(Textures.drawNail(model.mainPanel, false)));

            publish(new AnimationFrame(true, false, null, null, elements, null));
            Thread.sleep(24);

            // FIRST PART: From right to left to half ------------------------------------------------------------
            animRightToLeft(120, false, elements, locations);

            // SECOND PART: From right to left to end ------------------------------------------------------------
            for (int j = 120; j < 300 && vars.gameOn; ++j)
            {
                startX = (int)(LoadMap.DEFAULTWIDTH * vars.hMult + 100);
                delta = (startX + 270)/270;
                for (int i = 0; i < 2; i++) {
                    if (i == 0)
                        locations[i] = new Point(startX - (j*delta),
                                (int)((LoadMap.MAPHEIGHTINTILES / 2f + 6) * LoadMap.TILESIZEINPXS * minMult));
                    else
                        locations[i] = new Point(startX - (120*delta) + (int)(40 + 19*minMult) - (j-120)/6,
                                (int)((LoadMap.MAPHEIGHTINTILES / 2f + 6) * LoadMap.TILESIZEINPXS * minMult));
                }
                locations[2] = new Point(startX - (120*delta) + (int)(30 + newEntitySize + 19*minMult),
                        (int)((LoadMap.MAPHEIGHTINTILES / 2f + 6) * LoadMap.TILESIZEINPXS * minMult) + 58);

                if (step < 4) {
                    pacmanImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult, "Entity1", "DIRECTION", 0));
                    ghostImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult, "Entity2_Stretch", "LEFT", (j-120)/6));
                } else {
                    pacmanImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult,"Entity1","LEFT", 0));
                    ghostImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult, "Entity2_Stretch", "LEFT", (j-120)/6));
                }

                publish(new AnimationFrame(false, false, pacmanImage, ghostImage, elements, locations));
                step = (step + 1) % 8;
                Thread.sleep(18);
            }
            Thread.sleep(800);

            // THIRD PART: Binky is hurt and embarassed ----------------------------------------------------------
            startX = (int)(LoadMap.DEFAULTWIDTH * vars.hMult + 100);
            delta = (startX + 270)/270;
            ghostImage = new ImageIcon(Textures.drawEntity(
                    model.mainPanel, minMult, "Entity2_Teared", "UP", 0));
            locations[1] = new Point(startX - (120*delta) + (int)(19*minMult),
                    (int)((LoadMap.MAPHEIGHTINTILES / 2f + 6) * LoadMap.TILESIZEINPXS * minMult));
            locations[2] = new Point(startX - (120*delta) + (int)(30 + newEntitySize + 19*minMult),
                    (int)((LoadMap.MAPHEIGHTINTILES / 2f + 6) * LoadMap.TILESIZEINPXS * minMult) + 58);
            elements[2].setIcon(new ImageIcon(Textures.drawNail(model.mainPanel, true)));

            publish(new AnimationFrame(false, false, null, ghostImage, elements, locations));
            Thread.sleep(500);
            ghostImage = new ImageIcon(Textures.drawEntity(
                    model.mainPanel, minMult, "Entity2_Teared", "DOWN", 0));
            locations[1] = new Point(startX - (120*delta) + (int)(19*minMult),
                    (int)((LoadMap.MAPHEIGHTINTILES / 2f + 6) * LoadMap.TILESIZEINPXS * minMult));
            locations[2] = new Point(startX - (120*delta) + (int)(30 + newEntitySize + 19*minMult),
                    (int)((LoadMap.MAPHEIGHTINTILES / 2f + 6) * LoadMap.TILESIZEINPXS * minMult) + 58);

            publish(new AnimationFrame(false, false, null, ghostImage, elements, locations));
            Thread.sleep(1500);

            publish(new AnimationFrame(false, true, pacmanImage, ghostImage, elements, locations));
        }

        /**
         * Third animation, from right to left, Binky is hurt, only naked binky back, LEVELS 8 -> 9, 11 -> 12, 14 -> 15
         *
         * @throws InterruptedException To be handled by caller
         */
        private void playThirdAnimation()
                throws InterruptedException
        {
            int startX;
            int delta;
            int step = 0;
            JLabel[] elements = new JLabel[]{new JLabel(), new JLabel()};
            Point[] locations = new Point[2];
            ImageIcon ghostImage = null;

            // INITIALIZATION PART: ------------------------------------------------------------------------------
            for (int i = 0; i <= 1 && vars.gameOn; i++) {
                elements[i].setSize(new Dimension(newEntitySize * 4, newEntitySize * 4));
            }
            publish(new AnimationFrame(true, false, null, null, elements, null));
            Thread.sleep(24);

            // FIRST PART: From right to left --------------------------------------------------------------------
            animRightToLeft(300, true, elements, locations);
            Thread.sleep(250);

            // SECOND PART: From left to right -------------------------------------------------------------------
            for (int j = 300; j >= 0 && vars.gameOn; --j)
            {
                startX = (int)(LoadMap.DEFAULTWIDTH * vars.hMult + 100);
                delta = startX/270;
                locations[1] = new Point(startX - (j*delta),
                            (int)((LoadMap.MAPHEIGHTINTILES / 2f + 6) * LoadMap.TILESIZEINPXS * minMult));

                if (step < 4) {
                    ghostImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult, "Entity2_Naked", "RIGHT", 1));
                } else {
                    ghostImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult, "Entity2_Naked", "RIGHT", 0));
                }

                publish(new AnimationFrame(false, false, null, ghostImage, elements, locations));
                step = (step + 1) % 8;
                Thread.sleep(18);
            }

            publish(new AnimationFrame(false, true, null, ghostImage, elements, locations));
        }

        /**
         * Core animation from right yo left
         *
         * @param len int
         * @param hurtGhost boolean
         * @param elements JLabel[]
         * @param locations Point[]
         * @throws InterruptedException To be handled by caller
         */
        private void animRightToLeft(int len, boolean hurtGhost, JLabel[] elements, Point[] locations)
                throws InterruptedException
        {
            int startX;
            int delta;
            int step = 0;
            ImageIcon pacmanImage;
            ImageIcon ghostImage;
            for (int j = 1; j < len && vars.gameOn; ++j)
            {
                startX = (int)(LoadMap.DEFAULTWIDTH * vars.hMult + 100);
                delta = (startX + 270)/270;
                for (int i = 0; i < 2; i++) {
                    locations[i] = new Point(startX - (j*delta) + (i*40) + (int)(i * (310 - j)/10 * minMult),
                            (int)((LoadMap.MAPHEIGHTINTILES / 2f + 6) * LoadMap.TILESIZEINPXS * minMult));
                }
                if (locations.length == 3) {
                    locations[2] = new Point(startX - (120*delta) + (int)(30 + newEntitySize + 19*minMult),
                            (int)((LoadMap.MAPHEIGHTINTILES / 2f + 6) * LoadMap.TILESIZEINPXS * minMult) + 58);
                }
                if (step < 4) {
                    pacmanImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult, "Entity1", "DIRECTION", 0));
                    ghostImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult, "Entity2" + (hurtGhost ? "_Hurt" : ""), "LEFT", 1));
                } else {
                    pacmanImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult, "Entity1","LEFT", 0));
                    ghostImage = new ImageIcon(Textures.drawEntity(
                            model.mainPanel, minMult, "Entity2" + (hurtGhost ? "_Hurt" : ""), "LEFT", 0));
                }

                publish(new AnimationFrame(false, false, pacmanImage, ghostImage, elements, locations));
                step = (step + 1) % 8;
                Thread.sleep(18);
            }
        }

        @Override
        protected void process(List<AnimationFrame> frames)
        {
            for (AnimationFrame frame : frames) {
                if(vars.gameOn) {
                    if (frame.initialisation) {
                        for (int i = 0; i < frame.entities.length; ++i) {
                            animLabelsIds[i] = model.mainPanel.getComponentCount();
                            model.mainPanel.add(frame.entities[i]);
                            model.mainPanel.getComponent(animLabelsIds[i]).setVisible(true);
                        }
                    } else if (frame.finished) {
                        for (int i = frame.entities.length - 1; i >= 0; --i) {
                            model.mainPanel.remove(animLabelsIds[i]);
                        }
                    } else {
                        for (int i = 0; i < frame.entities.length; ++i) {
                            frame.entities[i].setLocation(frame.locations[i].x, frame.locations[i].y);
                        }
                        if (frame.pacmanImage != null) {
                            frame.entities[0].setIcon(frame.pacmanImage);
                        }
                        if (frame.ghostImage != null) {
                            frame.entities[1].setIcon(frame.ghostImage);
                        }
                    }
                    model.mainPanel.revalidate();
                    model.mainPanel.repaint();
                }
            }
        }
    }

    //</editor-fold>
}
