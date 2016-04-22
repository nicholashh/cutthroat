package ach7nbh2game.client;

import ach7nbh2game.client.Window.Component;
import ach7nbh2game.client.adapters.IViewToModel;
import ach7nbh2game.main.Constants;
import ach7nbh2game.main.Constants.Action;
import ach7nbh2game.main.Constants.Direction;
import ach7nbh2game.main.Constants.Tool;
import ach7nbh2game.network.packets.*;
import ach7nbh2game.util.Logger;
import ach7nbh2game.util.lambda.LambdaZeroVoid;
import com.googlecode.blacken.terminal.BlackenKeys;

import java.util.ArrayList;
import java.util.Map;

public class ClientView {

    private IViewToModel model;

    private Window window = new Window();

    private final int moveUp = BlackenKeys.KEY_UP;
    private final int moveLeft = BlackenKeys.KEY_LEFT;
    private final int moveDown = BlackenKeys.KEY_DOWN;
    private final int moveRight = BlackenKeys.KEY_RIGHT;

	private final int actionUp1 = 'w';
	private final int actionUp2 = 'W';
	private final int actionLeft1 = 'a';
	private final int actionLeft2 = 'A';
	private final int actionDown1 = 's';
	private final int actionDown2 = 'S';
	private final int actionRight1 = 'd';
	private final int actionRight2 = 'D';

	private final int selectPickaxe = '1';
    private final int selectGun = '2';
    private final int selectRocket = '3';

    private PlayerState myState = null;

    private enum State {TEXT_INPUT_MODE, IN_GAME_MODE}

	public enum VerticalAlignment {TOP, CENTER}
	public enum HorizontalAlignment {LEFT, CENTER}
    private final String welcomeMessage =
			"_________         __    __  .__                        __   \n" +
			"\\_   ___ \\ __ ___/  |__/  |_|  |_________  _________ _/  |__\n" +
			"/    \\  \\/|  |  \\   __\\   __\\  |  \\_  __ \\/  _ \\__  \\\\   ___\\\n" +
			"\\     \\___|  |  /|  |  |  | |   Y  \\  | \\(  <_> ) __ \\|  |  \n" +
			" \\______  /____/ |__|  |__| |___|  /__|   \\____(____  /__|  \n" +
			"        \\/                       \\/                 \\/      \n\nVersion 0.7\n\n";
    private final String usernamePrompt = welcomeMessage + "Username: ";
    private final String serverPrompt = welcomeMessage + "Server Hostname: ";
	private final String serverPromptFailed = welcomeMessage + "Couldn't find the specified server.\n\nServer " +
			"Hostname: ";
	private boolean userInputDone = false;
    private String userInput = "";

    private State state = null;

    // tool to use for actions
    private Tool tool = Tool.PICKAXE;

    public ClientView (IViewToModel modelIn) {

        System.out.println("making new ClientView...");

        model = modelIn;
    }

    public void start () {

        System.out.println("starting the ClientView!");

        window.start();
		SoundEffect.init();
		SoundEffect.volume = SoundEffect.Volume.LOW;
		SoundEffect.MENU_BGM.loop();
        beginAcceptingCharacterInput();

    }

	public void updateState (GameState gameState) {
		
		if (state != State.IN_GAME_MODE) {
			SoundEffect.MENU_BGM.stop();
			SoundEffect.GAME_START.play();
			state = State.IN_GAME_MODE;
			userInput = "";
		}

		window.fill(Component.CenterPanel, gameState.getFrame());
		populateMenus(gameState);
		window.repaint();
		for (Constants.ServerToClientSound gs : gameState.getSounds()) {
			switch(gs) {
				case GUN_FIRE:
					SoundEffect.GUN_FIRE.play();
					break;
				case GUN_WHIFF:
					SoundEffect.GUN_WHIFF.play();
					break;
				case PICKAXE_HIT_WALL:
					SoundEffect.PICK_WALL.play();
					break;
				case PICKAXE_HIT_PLAYER:
					SoundEffect.PICK_PLAYER.play();
					break;
				case PICKAXE_WHIFF:
					SoundEffect.PICK_WHIFF.play();
					break;
				case PICKUP_ITEM:
					SoundEffect.PICKUP.play();
					break;
				case PLAYER_DIES:
					SoundEffect.PLAYER_DEATH.play();
					break;
				case PLAYER_SPAWNS:
					SoundEffect.PLAYER_SPAWN.play();
					break;
				case BULLET_HIT_PLAYER:
					SoundEffect.BULLET_PLAYER.play();
					break;
				case BULLET_HIT_WALL:
					SoundEffect.BULLET_WALL.play();
					break;
				case BULLET_HIT_BULLET:
					SoundEffect.BULLET_BULLET.play();
                    break;
                case ROCKET_LAUNCH:
                    SoundEffect.ROCKET_LAUNCH.play();
                    break;
                case ROCKET_EXPLODE:
                    SoundEffect.ROCKET_EXPLODE.play();
                    break;
				case TR_APPEAR:
					SoundEffect.TR_APPEAR.play();
					break;
			}
		}
	}

	private void populateMenus (GameState gameState) {

		String rightPrompt = "";

		Map<String, PlayerObservableState> otherPlayerStates = gameState.getOtherPlayerStates();

		rightPrompt += " \u25B6HEALTH";
		for (String player : otherPlayerStates.keySet())
			rightPrompt += String.format("\n  %3d: %s", otherPlayerStates.get(player).getHealth(), player);

		rightPrompt += "\n\n \u25B6SCORE";
		for (String player : otherPlayerStates.keySet())
			rightPrompt += String.format("\n  %05.2f: %s", otherPlayerStates.get(player).getScore(), player);

		myState = gameState.getPlayerState();

		//rightPrompt += "\n";
		//rightPrompt += "\n \u25B6My Items";
		//rightPrompt += String.format("\n  Pickaxe Damage:  %2d", myState.getPickaxeDmg());
		//rightPrompt += String.format("\n  Gun     Damage:  %2d", myState.getGunDmg());
		//rightPrompt += String.format("\n  Bullet  Damage:  %2d", myState.getBulletDmg());

		showPrompt(rightPrompt, Component.RightPanel, VerticalAlignment.TOP, HorizontalAlignment.LEFT);

		String leftPrompt = "";

		leftPrompt += "\u2665";
		double percentHealth = myState.getHealth()*1.0/Constants.clientHealth;
		int wholebar = window.height(Component.LeftPanel)-1;
		double filledbar = wholebar*percentHealth;
		double emptybar = wholebar-filledbar;

		for (int i = 0; i < emptybar; i++) {
			leftPrompt += "\n ";
		}
		for (int i = 0; i < filledbar; i++) {
			leftPrompt += "\n"+"\u2588";
		}

		showPrompt(leftPrompt, Component.LeftPanel, VerticalAlignment.TOP, HorizontalAlignment.LEFT);

		String bottomPrompt = "";

		// pickaxe
		bottomPrompt += selectedPick();
		switch(myState.getPickaxeDmg()) {
			case Constants.pickaxe1:
				bottomPrompt += "1)";
				break;
			case Constants.pickaxe2:
				bottomPrompt += "2)";
				break;
			case Constants.pickaxe3:
				bottomPrompt += "3)";
				break; }
		bottomPrompt += ":\u221E ";

		// gun
		bottomPrompt += selectedGun();
		switch(myState.getGunDmg()) {
			case Constants.gun1:
				bottomPrompt += "1)";
				break;
			case Constants.gun2:
				bottomPrompt += "2)"; }
        bottomPrompt += ":"+myState.getAmmo()+" ";

		// rocket
        bottomPrompt += selectedRocket();
        bottomPrompt += myState.getRocketAmmo();

		showPrompt(bottomPrompt, Component.BottomPanel, VerticalAlignment.TOP, HorizontalAlignment.CENTER);

		String topPrompt = "First to " + Constants.killsToWin + " points wins\n";
		topPrompt += "(1 kill = 1 point, " + (int)(100 * Constants.bountyPercent) + "% kill bounty)";
		showPrompt(topPrompt, Component.TopPanel, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);

	}

    private String selectedGun() {
        if (tool == Tool.GUN) {
            return "*Gun(";
        } else {
            return " Gun(";
        }
    }

    private String selectedPick() {
        if (tool == Tool.PICKAXE) {
            return "*Pick(";
        } else {
            return " Pick(";
        }
    }

    private String selectedRocket() {
        if (tool == Tool.ROCEKT) {
            return "*Rocket(1):";
        } else {
            return " Rocket(1):";
        }
    }

    public void clearMenus () {
		showPrompt("", Component.LeftPanel);
		showPrompt("", Component.RightPanel);
		showPrompt("", Component.TopPanel);
		showPrompt("", Component.BottomPanel);
	}

	public void endGame(PlayerInfo client) {
		Logger.Singleton.log(this, 0, "ending game");
		showPrompt(client.getUsername()+" won!", Component.TopPanel);
		SoundEffect.GAME_END.play();
	}

	public String askForUsername () {
		String instructions = "Enter: Submit username";
		showPrompt(instructions, Component.BottomPanel, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
		return askForThing(usernamePrompt, "");
	}
	
	public String askForServerIP () {
		String instructions = "Enter: Submit server hostname";
		showPrompt(instructions, Component.BottomPanel, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
		return askForThing(serverPrompt, Constants.defaultHostname);
	}

	public String askForServerIPFailed() {
		String instructions = "Enter: Submit server hostname";
		showPrompt(instructions, Component.BottomPanel, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
		return askForThing(serverPromptFailed, Constants.defaultHostname);
	}
	
	private LambdaZeroVoid updatePrompt;
	
	public String askForThing (String label, String value) {
		return askForThing(label, value, false, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
	}

	public String askForThing(String label, String value, VerticalAlignment valign, HorizontalAlignment halign) {
		return askForThing(label, value, false, valign, halign);
	}

	public void updateThing (String newLabel) {
		askForThing(newLabel, "", true, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
	}

	public void updateThing(String newLabel, VerticalAlignment valign, HorizontalAlignment halign) {
		askForThing(newLabel, "", true, valign, halign);
	}

	private String askForThing (String label, String value, boolean updateOnly, VerticalAlignment valign,
								HorizontalAlignment halign) {

		Logger.Singleton.log(this, 0, "askForThing:\n" +
				"\tupdateOnly = " + updateOnly + "\n" +
				"\tvalue = \"" + value.replace('\n', ' ') + "\"\n" +
				"\tlabel = \"" + label.replace('\n', ' ') + "\")");

		if (!updateOnly) {
			userInputDone = false;
			userInput = value;
			state = State.TEXT_INPUT_MODE;
		}

		updatePrompt = () -> showPrompt(label + userInput, Component.CenterPanel, valign, halign);
		updatePrompt.run();

		window.repaint();
		
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
		showPrompt(prompt, component, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
	}

	public void showPrompt (final String prompt, final Component component,
			final VerticalAlignment verticalAlignment,
			final HorizontalAlignment horizontalAlignment) {

		//Logger.Singleton.log(this, 0, "showPrompt(" +
		//		"component = \"" + component + "\", " +
		//		"prompt = \"" + prompt.replace('\n', ' ') + "\")");

    	final ArrayList<ArrayList<Integer>> toPrint = new ArrayList<>();

        final String[] separate = prompt.split("\n");
        
        final int numTextRows = separate.length;
		final int numColsAvailable = window.width(component);
		final int numRowsAvailable = window.height(component);

		final int numRowsPaddingTop = calcHalfDifferenceOrZero(numRowsAvailable, numTextRows);
		final int numRowsPaddingBottom = numRowsAvailable - (numRowsPaddingTop + numTextRows);

		switch (verticalAlignment) {
			case CENTER:
				toPrint.addAll(makeBlankRows(numRowsPaddingTop, numColsAvailable));
				break;
		}

		for (int i = 0; i < numTextRows; i++) {

			final char[] line = separate[i].toCharArray();
			final int numTextCols = line.length;
			final int numColsPaddingLeft = calcHalfDifferenceOrZero(numColsAvailable, numTextCols);
			final int numColsPaddingRight = numColsAvailable - (numColsPaddingLeft + numTextCols);

			final ArrayList<Integer> toAdd = new ArrayList<>();

			switch (horizontalAlignment) {
				case CENTER:
					toAdd.addAll(makeBlankCols(numColsPaddingLeft));
					break;
			}

			for (int j = 0; j < numTextCols; j++) toAdd.add((int)line[j]);

			switch (horizontalAlignment) {
				case CENTER:
					toAdd.addAll(makeBlankCols(numColsPaddingRight));
					break;
				case LEFT:
					toAdd.addAll(makeBlankCols(numColsPaddingLeft + numColsPaddingRight));
					break;
			}

			toPrint.add(toAdd);

		}

		switch (verticalAlignment) {
			case CENTER:
				toPrint.addAll(makeBlankRows(numRowsPaddingBottom, numColsAvailable));
				break;
			case TOP:
				toPrint.addAll(makeBlankRows(numRowsPaddingTop + numRowsPaddingBottom, numColsAvailable));
				break;
		}

		window.fill(component, toPrint);
        
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
                                (input == 46))                   // period
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
							case moveUp:
								model.selectUp();
								window.repaint();
								break;
							case moveDown:
								model.selectDown();
								window.repaint();
								break;
//							case moveLeft:
//								model.selectLeft();
//								window.repaint();
//								break;
//							case moveRight:
//								model.selectRight();
//								window.repaint();
//								break;
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

					case actionUp1:
					case actionUp2:
                		switch (tool) {
                		case GUN:
                			action.setAction(Action.SHOOT_GUN);
                			break;
                		case PICKAXE:
                			action.setAction(Action.DIG);
                            break;
                        case ROCEKT:
                            action.setAction(Action.SHOOT_ROCKET);
                            break;
                		}
                		action.setDirection(Direction.UP);
                		model.performAction(action);
                		break;
					case actionLeft1:
					case actionLeft2:
                		switch (tool) {
                		case GUN:
                			action.setAction(Action.SHOOT_GUN);
                			break;
                		case PICKAXE:
                			action.setAction(Action.DIG);
                            break;
                        case ROCEKT:
                            action.setAction(Action.SHOOT_ROCKET);
                            break;
                        }
                		action.setDirection(Direction.LEFT);
                		model.performAction(action);
                		break;
					case actionDown1:
					case actionDown2:
                		switch (tool) {
                		case GUN:
                			action.setAction(Action.SHOOT_GUN);
                			break;
                		case PICKAXE:
                			action.setAction(Action.DIG);
                            break;
                        case ROCEKT:
                            action.setAction(Action.SHOOT_ROCKET);
                            break;
                        }
                		action.setDirection(Direction.DOWN);
                		model.performAction(action);
                		break;
					case actionRight1:
					case actionRight2:
                		switch (tool) {
                		case GUN:
                			action.setAction(Action.SHOOT_GUN);
                			break;
                		case PICKAXE:
                			action.setAction(Action.DIG);
                            break;
                        case ROCEKT:
                            action.setAction(Action.SHOOT_ROCKET);
                            break;
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
                    case selectRocket:
                        try {
                            if (myState.hasRocket()) {
                                tool = Tool.ROCEKT;
                            }
                        } catch (NullPointerException e) {
                            // nothing
                        }
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
