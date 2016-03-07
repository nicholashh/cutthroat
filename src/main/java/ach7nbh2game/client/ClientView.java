package ach7nbh2game.client;

import ach7nbh2game.client.Window.Component;
import ach7nbh2game.client.adapters.IViewToModel;
import ach7nbh2game.main.Constants.Action;
import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.main.Constants.Tool;
import ach7nbh2game.network.packets.ClientAction;
import ach7nbh2game.network.packets.GameState;
import ach7nbh2game.util.Logger;
import ach7nbh2game.util.lambda.LambdaZeroVoid;
import com.googlecode.blacken.terminal.BlackenKeys;

import java.util.ArrayList;

public class ClientView {

    private IViewToModel model;

    private Window window = new Window();

    private final int moveUp = BlackenKeys.KEY_UP;
    private final int moveLeft = BlackenKeys.KEY_LEFT;
    private final int moveDown = BlackenKeys.KEY_DOWN;
    private final int moveRight = BlackenKeys.KEY_RIGHT;

    private final int actionUp = 'w';
    private final int actionLeft = 'a';
    private final int actionDown = 's';
    private final int actionRight = 'd';

    private final int selectGun = '1';
    private final int selectPickaxe = '2';

    // states
    private enum State {TEXT_INPUT_MODE, IN_GAME_MODE}
    private final String welcomeMessage = "Welcome to Cutthroat!\n\n\n";
    private final String usernamePrompt = welcomeMessage + "Username: ";
    private final String serverPrompt = welcomeMessage + "Server Hostname: ";
    private boolean userInputDone = false;
    private String userInput = "";

    private State state = null;

    // tool to use for actions
    private Tool tool = Tool.GUN;

    public ClientView (IViewToModel modelIn) {

        System.out.println("making new ClientView...");

        model = modelIn;
    }

    public void start () {

        System.out.println("starting the ClientView!");

        window.start();
        beginAcceptingCharacterInput();

    }

    public void showMap (ArrayList<ArrayList<Integer>> map) {

    	state = State.IN_GAME_MODE;
    	
        window.fill(Component.CenterPanel, map);
        window.repaint();

    }

	public String askForUsername () {
		return askForThing(usernamePrompt, "");
	}
	
	public String askForServerIP () {
		return askForThing(serverPrompt, ""); // localhost // cutthroat.pwnz.org
	}
	
	private LambdaZeroVoid updatePrompt;
	
	public String askForThing (String label, String value) {
		return askForThing(label, value, false);
	}

	public void updateThing (String newLabel) {
		askForThing(newLabel, "", true);
	}
	
	public String askForThing (String label, String value, boolean updateOnly) {

		Logger.Singleton.log(this, 0, "askForThing(" +
				"updateOnly = " + updateOnly + ", " +
				"value = \"" + value.replace('\n', ' ') + "\", " +
				"label = \"" + label.replace('\n', ' ') + "\")");
			
		if (!updateOnly) {

			state = State.TEXT_INPUT_MODE;
			userInputDone = false;
			userInput = value;
			
		}

		updatePrompt = () -> showPrompt(label + userInput);
		updatePrompt.run();
		
		if (!updateOnly) {

			while (true) {
				if (!userInputDone) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					break;
				}
			}

			return userInput;

		} else {
			return null;
		}
		
	}
	
	private void showPrompt (final String prompt) {
		showPrompt(prompt, Component.CenterPanel);
	}

	private void showPrompt (final String prompt, final Component component) {

		//Logger.Singleton.log(this, 0, "showPrompt(" +
		//		"component = \"" + component + "\", " +
		//		"prompt = \"" + prompt.replace('\n', ' ') + "\")");

    	final ArrayList<ArrayList<Integer>> toPrint = new ArrayList<>();

        final String[] separate = prompt.split("\n");
        
        final int numTextRows = separate.length;
        final int numRowsAvailable = window.height(component);
        final int numRowsPaddingTop = calcHalfDifferenceOrZero(numRowsAvailable, numTextRows);
        final int numRowsPaddingBottom = numRowsAvailable - (numRowsPaddingTop + numTextRows);
        
        final int numColsAvailable = window.width(component);
        
        toPrint.addAll(makeBlankRows(numRowsPaddingTop, numColsAvailable));
        
        for (int i = 0; i < numTextRows; i++) {
        	
        	final char[] line = separate[i].toCharArray();
        	final int numTextCols = line.length;
            final int numColsPaddingLeft = calcHalfDifferenceOrZero(numColsAvailable, numTextCols);
            final int numColsPaddingRight = numColsAvailable - (numColsPaddingLeft + numTextCols); 

            final ArrayList<Integer> toAdd = new ArrayList<>();
            toAdd.addAll(makeBlankCols(numColsPaddingLeft));
            for (int j = 0; j < numTextCols; j++) toAdd.add((int)line[j]);
            toAdd.addAll(makeBlankCols(numColsPaddingRight));
            toPrint.add(toAdd);
            
        }
        
        toPrint.addAll(makeBlankRows(numRowsPaddingBottom, numColsAvailable));

        window.fill(component, toPrint);
        
        window.repaint();
        
    }
	
	private int calcHalfDifferenceOrZero (final int biggerNumber, final int smallerNumber) {
		return Math.max(0, (biggerNumber - smallerNumber) / 2);
	}
	
	private ArrayList<Integer> makeBlankCols (final int numCols) {
		final ArrayList<Integer> toReturn = new ArrayList<>();
		for (int i = 0; i < numCols; i++)
			toReturn.add((int)' ');
		return toReturn;
	}
	
	private ArrayList<ArrayList<Integer>> makeBlankRows (final int numRows, final int rowLength) {
		final ArrayList<ArrayList<Integer>> toReturn = new ArrayList<>();
		for (int i = 0; i < numRows; i++) {
			final ArrayList<Integer> toAdd = new ArrayList<>();
			for (int j = 0; j < rowLength; j++)
				toAdd.add((int)' ');
			toReturn.add(toAdd);
		}
		return toReturn;
	}

    public void showScores (GameState gameState) {
    	// TODO
    }

    private void beginAcceptingCharacterInput () {

        (new Thread () { public void run () {
        	
            while (true) {
            	
            	int input = window.waitForUserInput();

        		// Logger.Singleton.log(this, 0, "read a \'" + input + "\'");

        		switch (state) {

        		case TEXT_INPUT_MODE:
                	
            		// Logger.Singleton.log(this, 0, "state = " + state);

                	if (!userInputDone) {
                		
                		// Logger.Singleton.log(this, 0, "usernInputDone = " + userInputDone);

                		if ((input >= 65 && input <= 90) ||      // uppercase letters
                				(input >= 97 && input <= 122) || // lowercase letters
                				(input >= 48 && input <= 57) ||  // numbers
								(input == 46))					 // period
                			userInput += (char)input;
                		else switch (input) {
                		case BlackenKeys.KEY_BACKSPACE:
                			// Logger.Singleton.log(this, 0, "BACKSPACE");
                			int length = userInput.length();
                			if (length > 0)
                				userInput = userInput.substring(0, length - 1);
                			break;
                		case BlackenKeys.KEY_ENTER:
                			// Logger.Singleton.log(this, 0, "ENTER");
                			userInputDone = true;
                			break;
                		}

                		updatePrompt.run();

                	}
                	
                	break;

        		case IN_GAME_MODE:
                	
                	ClientAction action = new ClientAction();

                	switch (input) {

                	// moving

                	case moveUp:
                		action.setAction(Action.MOVE);
                		action.setDirection(Direction.UP);
                		model.performAction(action);
                		break;
                	case moveLeft:
                		action.setAction(Action.MOVE);
                		action.setDirection(Direction.LEFT);
                		model.performAction(action);
                		break;
                	case moveDown:
                		action.setAction(Action.MOVE);
                		action.setDirection(Direction.DOWN);
                		model.performAction(action);
                		break;
                	case moveRight:
                		action.setAction(Action.MOVE);
                		action.setDirection(Direction.RIGHT);
                		model.performAction(action);
                		break;

                	// using a selected tool

                	case actionUp:
                		switch (tool) {
                		case GUN:
                			action.setAction(Action.SHOOT);
                			break;
                		case PICKAXE:
                			action.setAction(Action.DIG);
                		}
                		action.setDirection(Direction.UP);
                		model.performAction(action);
                		break;
                	case actionLeft:
                		switch (tool) {
                		case GUN:
                			action.setAction(Action.SHOOT);
                			break;
                		case PICKAXE:
                			action.setAction(Action.DIG);
                		}
                		action.setDirection(Direction.LEFT);
                		model.performAction(action);
                		break;
                	case actionDown:
                		switch (tool) {
                		case GUN:
                			action.setAction(Action.SHOOT);
                			break;
                		case PICKAXE:
                			action.setAction(Action.DIG);
                		}
                		action.setDirection(Direction.DOWN);
                		model.performAction(action);
                		break;
                	case actionRight:
                		switch (tool) {
                		case GUN:
                			action.setAction(Action.SHOOT);
                			break;
                		case PICKAXE:
                			action.setAction(Action.DIG);
                		}
                		action.setDirection(Direction.RIGHT);
                		model.performAction(action);
                		break;

                	// selecting a tool

                	case selectGun:
                		tool = Tool.GUN;
                		break;
                	case selectPickaxe:
                		tool = Tool.PICKAXE;
                		break;

                	// miscellaneous

                	case BlackenKeys.RESIZE_EVENT:
                		window.handleResize();
                		break;

                	}
                	
                	break;

                }

            }

        } } ).start();

    }

}
