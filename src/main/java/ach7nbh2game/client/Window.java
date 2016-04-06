package ach7nbh2game.client;

import ach7nbh2game.main.Constants;
import ach7nbh2game.util.Logger;
import ach7nbh2game.util.lambda.LambdaTwoReturn;
import ach7nbh2game.util.lambda.LambdaZeroReturn;

import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.CellWalls;
import com.googlecode.blacken.terminal.CursesLikeAPI;
import com.googlecode.blacken.terminal.TerminalInterface;
import com.googlecode.blacken.terminal.TerminalStyle;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 *     - - -   top
 *   | * * * | right
 *   | * * * |
 *     - - -   bottom
 */

public class Window {

    private CursesLikeAPI terminal;

    // the invariant components in the window
    public enum Component {LeftPanel, RightPanel, TopPanel, BottomPanel, CenterPanel,
        LeftWindowBorder, LeftMapBorder, RightMapBorder, RightWindowBorder,
        TopWindowBorder, TopMapBorder, BottomMapBorder, BottomWindowBorder}

    // the invariant component sizes
    private int centerPanelHeight = Constants.clientMapHeight;
    private int centerPanelWidth = Constants.clientMapWidth;

    // the variant component sizes
    private int leftPanelWidth = 1;
    private int rightPanelWidth = 21;
    private int topPanelHeight = 2;
    private int bottomPanelHeight = 1;
    private int mapBorder = 1;
    private int windowBorder = 1;

    // the dimensions of the window layout
    private int[][] dimensions () {
        return new int[][]{{
                windowBorder, leftPanelWidth, mapBorder,
                centerPanelWidth, mapBorder, rightPanelWidth, windowBorder},{
                windowBorder, topPanelHeight, mapBorder,
                centerPanelHeight, mapBorder, bottomPanelHeight, windowBorder}};
    }

    // the index of the left edge of each component
    private int left (Component component) {
        int position = 0;
        switch (component) {
            case RightWindowBorder:
                position += dimensions()[0][5];
            case RightPanel:
                position += dimensions()[0][4];
            case RightMapBorder:
                position += dimensions()[0][3];
            case CenterPanel:
            case TopPanel:
            case BottomPanel:
            case TopMapBorder:
            case BottomMapBorder:
                position += dimensions()[0][2];
            case LeftMapBorder:
                position += dimensions()[0][1];
            case LeftPanel:
            case TopWindowBorder:
            case BottomWindowBorder:
                position += dimensions()[0][0];
            case LeftWindowBorder: }
        return position;
    }

    // the width of each component
    public int width (Component component) {
        int width = 0;
        switch (component) {
            case LeftPanel:
                return leftPanelWidth;
            case RightPanel:
                return rightPanelWidth;
            case LeftMapBorder:
            case RightMapBorder:
                return mapBorder;
            case LeftWindowBorder:
            case RightWindowBorder:
                return windowBorder;
            case TopWindowBorder:
            case BottomWindowBorder:
                //width += dimensions()[0][0] + dimensions()[0][6];
                width += dimensions()[0][1] + dimensions()[0][5];
                width += dimensions()[0][2] + dimensions()[0][4];
            case CenterPanel:
            case TopPanel:
            case BottomPanel:
            case TopMapBorder:
            case BottomMapBorder:
                width += dimensions()[0][3]; }
        return width;
    }

    // the index of the right edge of each component
    private int right (Component component) {
        return left(component) + width(component);
    }

    // the index of the top edge of each component
    private int top (Component component) {
        int position = 0;
        switch (component) {
            case BottomWindowBorder:
                position += dimensions()[1][5];
            case BottomPanel:
                position += dimensions()[1][4];
            case BottomMapBorder:
                position += dimensions()[1][3];
            case CenterPanel:
            case LeftPanel:
            case RightPanel:
            case LeftMapBorder:
            case RightMapBorder:
                position += dimensions()[1][2];
            case TopMapBorder:
                position += dimensions()[1][1];
            case TopPanel:
            case LeftWindowBorder:
            case RightWindowBorder:
                position += dimensions()[1][0];
            case TopWindowBorder: }
        return position;
    }

    // the height of each component
    public int height (Component component) {
        int height = 0;
        switch (component) {
            case TopPanel:
                return topPanelHeight;
            case BottomPanel:
                return bottomPanelHeight;
            case TopMapBorder:
            case BottomMapBorder:
                return mapBorder;
            case TopWindowBorder:
            case BottomWindowBorder:
                return windowBorder;
            case LeftWindowBorder:
            case RightWindowBorder:
                //height += dimensions()[1][0] + dimensions()[1][6];
                height += dimensions()[1][1] + dimensions()[1][5];
                height += dimensions()[1][2] + dimensions()[1][4];
            case CenterPanel:
            case LeftPanel:
            case RightPanel:
            case LeftMapBorder:
            case RightMapBorder:
                height += dimensions()[1][3]; }
        return height;
    }

    // the index of the bottom edge of each component
    private int bottom (Component component) {
        return top(component) + height(component);
    }

    public void start () {

        System.out.println("starting the Window!");

        setUpTerminal();
        drawTerminalStructure();

    }

    private void setUpTerminal () {

        TerminalInterface newTerminal = new SwingTerminal();
        newTerminal.init("Cutthroat",
                bottom(Component.BottomWindowBorder) + 1,
                right(Component.RightWindowBorder) + 1);
        terminal = new CursesLikeAPI(newTerminal);

        ColorPalette palette = new ColorPalette();
        palette.addAll(ColorNames.XTERM_256_COLORS, false);
        palette.putMapping(ColorNames.SVG_COLORS);
        terminal.setPalette(palette);

        terminal.move(-1, -1);

    }

    private void drawTerminalStructure () {

        for (Component component : Component.values()) {
            switch (component) {
                case LeftMapBorder:
                case RightMapBorder:
                    fill(component, "\u2502".codePointAt(0)); break;
                case TopMapBorder:
                case BottomMapBorder:
                    fill(component, "\u2500".codePointAt(0)); break;
            }
        }

        //for (Component component : Component.values()) {
        //    switch (component) {
        //        case LeftPanel:
        //        case RightPanel:
        //        case TopPanel:
        //        case BottomPanel:
        //            fill(component, '='); break;
        //        case CenterPanel:
        //            fill(component, '*'); break;
        //        case LeftWindowBorder:
        //            fill(component, ' '); break;
        //        case LeftMapBorder:
        //        case RightMapBorder:
        //            fill(component, "\u2502".codePointAt(0)); break;
        //        case RightWindowBorder:
        //        case TopWindowBorder:
        //            fill(component, ' '); break;
        //        case TopMapBorder:
        //        case BottomMapBorder:
        //            fill(component, "\u2500".codePointAt(0)); break;
        //        case BottomWindowBorder:
        //            fill(component, ' '); break;
        //    }
        //}

        //for (Component component : Component.values()) {
        //    switch (component) {
        //        case LeftPanel: fill(component, 'L'); break;
        //        case RightPanel: fill(component, 'R'); break;
        //        case TopPanel: fill(component, 'T'); break;
        //        case BottomPanel: fill(component, 'B'); break;
        //        case CenterPanel: fill(component, 'C'); break;
        //        case LeftWindowBorder: fill(component, '<'); break;
        //        case LeftMapBorder: fill(component, '<'); break;
        //        case RightMapBorder: fill(component, '>'); break;
        //        case RightWindowBorder: fill(component, '>'); break;
        //        case TopWindowBorder: fill(component, '^'); break;
        //        case TopMapBorder: fill(component, '^'); break;
        //        case BottomMapBorder: fill(component, 'v'); break;
        //        case BottomWindowBorder: fill(component, 'v'); break;
        //    }
        //}

    }

    public void clear (Component component) {
        fill(component, ' ');
    }

    public void fill (Component component, int thing) {
        fill(component, () -> thing);
    }

    public void fill (Component component, ArrayList<ArrayList<Integer>> thing) {
    	
    	// Logger.Singleton.log(this, 0, "fill(...)");

        // see if the thing fits exactly into the component
        int rightNumRows = height(component);
        int rightNumCols = width(component);
        boolean hasRightNumRows = thing.size() == rightNumRows;
        boolean hasRightNumCols = false;
        if (hasRightNumRows) {
            hasRightNumCols = true;
            for (ArrayList<Integer> row : thing) {
                if (row.size() != rightNumCols) {
                    hasRightNumCols = false;
                    Logger.Singleton.log(this, 0, "fill(...): dimension mismatch");
                    Logger.Singleton.log(this, 1, component+" rows: "+height(component));
                    Logger.Singleton.log(this, 1, component+" cols: "+width(component));
                    Logger.Singleton.log(this, 1, "thing rows: "+thing.size());
                    Logger.Singleton.log(this, 1, "thing cols: "+row.size());
                    break;
                }
            }
        }

        // if so, fill it!
        if (hasRightNumRows && hasRightNumCols) {
    		// Logger.Singleton.log(this, 0, "fill(...): filling");
            fill(component, (x, y) -> {
                return thing.get(y).get(x);
            });
        } else {
    		// TODO


        }

    }

    private void fill (Component component, LambdaZeroReturn<Integer> lambda) {
        fill(component, (x, y) -> lambda.run());
    }

    private void fill (Component component, LambdaTwoReturn<Integer,Integer,Integer> lambda) {
        int xLocal = 0;
        for (int x = left(component); x < right(component); x++) {
            int yLocal = 0;
            for (int y = top(component); y < bottom(component); y++) {
                setTerminal(x, y, lambda.run(xLocal, yLocal));
                yLocal++;
            }
            xLocal++;
        }
    }

    private void setTerminal (int x, int y, int character) {

        try { if (y < terminal.getHeight() && x < terminal.getWidth()) {

            terminal.set(y, x, new String(Character.toChars(character)), 7, 0,
                    EnumSet.noneOf(TerminalStyle.class),
                    EnumSet.noneOf(CellWalls.class));

        } } catch (IndexOutOfBoundsException e) {
            // TODO this is not ideal, but it almost never happens, sooooo...
            System.err.println("error in setTerminal(): " + e.toString());
        }

    }

    public void handleResize () {
        terminal.clear();
        drawTerminalStructure();
    }

    public void repaint () {
        try {
            terminal.refresh();
        } catch (IndexOutOfBoundsException e) {
            // TODO omg we really should not have to be doing this...
            System.err.println("error in repaint(): " + e.toString());
        }
    }

    public int waitForUserInput () {
        return terminal.getch();
        //int[] returnValue = {0};
        //Thread thread = new Thread () {
        //    public void run () {
        //        returnValue[0] = terminal.getch();
        //    }
        //};
        //thread.start();
        //try {
        //    thread.join();
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
        //return returnValue[0];
    }

}
