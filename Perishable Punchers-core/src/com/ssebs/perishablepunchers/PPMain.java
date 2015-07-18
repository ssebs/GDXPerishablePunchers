package com.ssebs.perishablepunchers;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Perishable Punchers Game developed by ssebs
 * 
 * @author ssebs
 */

public class PPMain extends ApplicationAdapter
{
	final int WIDTH = 1280;
	final int HEIGHT = 720;

	Preferences preferences;

	SpriteBatch batch;
	Texture begin, background, menu, controls, maps, mapButton, shieldButton, backButton, attackButton, jumpButton, leftButton, rightButton, changeCharButton, charSelect;
	Texture enemyAttack, fighter, enemy, fighterKunch, explosion, shield, fireBallButton, mute, sound;
	OrthographicCamera camera;
	Rectangle player, npc;

	int x = 256 + 64, y = 64, ex = WIDTH - (256 + 64), wy = 64;
	int playerHeath = 100, enemyHealth = 100;
	int mapID;
	int fbX, fbY;// fireball coords
	int hitCounter = 0;
	int shieldCount = 0;
	int fireBallCount = 0;

	private static final int FRAME_COLS = 2;
	private static final int FRAME_ROWS = 2;

	Animation enemyWalkAnimation, enemyWalkAnimation2, p1WalkAnimationR, p1WalkAnimationL, p2WalkAnimationR, p2WalkAnimationL, p3WalkAnimationR, p3WalkAnimationL, p4WalkAnimationR, p4WalkAnimationL;
	Texture enemyWalkSheet, enemyWalkSheet2, p1WalkRSheet, p1WalkLSheet, p2WalkRSheet, p2WalkLSheet, p3WalkRSheet, p3WalkLSheet, p4WalkRSheet, p4WalkLSheet;
	TextureRegion[] enemyWalkFrames, enemyWalkFrames2, p1WalkRFrames, p1WalkLFrames, p2WalkRFrames, p2WalkLFrames, p3WalkRFrames, p3WalkLFrames, p4WalkRFrames, p4WalkLFrames;
	TextureRegion enemyCurrentFrame, enemyCurrentFrame2, p1WalkRFrame, p1WalkLFrame, p2WalkRFrame, p2WalkLFrame, p3WalkRFrame, p3WalkLFrame, p4WalkRFrame, p4WalkLFrame;

	float enemyStateTime, enemyStateTime2, stateTimeP1L, stateTimeP1R, stateTimeP2L, stateTimeP2R, stateTimeP3L, stateTimeP3R, stateTimeP4L, stateTimeP4R;

	boolean isEnemyMovingLeft = false;
	boolean isEnemyMovingRight = false;
	boolean isPlayerMovingRight = false;
	boolean isPlayerMovingLeft = false;
	boolean kunching = false;
	boolean firstTimePlaying = true;
	boolean enemyAttacking = false;
	boolean debugging = false;
	boolean renderFireBall = false;
	boolean isUsingShield = false;
	boolean isRenderingFinishIt = false;
	boolean isRenderingItDied = false;
	boolean isRenderingBegin = true;
	boolean canPlayerMove = true;

	boolean isSoundOn;

	GestureDetector gestureDetector;
	MyGestureListener myGestureListener;

	Sound jumpSound, hitSound, fireBallSound, whooshSound, teaBagSound;

	BitmapFont arial;

	GameState gameState = GameState.MENU;
	CharacterColor characterColor = CharacterColor.PINK;

	Texture finishIt, itDied;

	enum GameState
	{
		MENU, PLAY, CONTROLS, CHARACTER_SELECTION, MAP_SELECTION;
	}

	enum CharacterColor
	{
		PINK, RED, GREEN, BLUE;
	}

	@Override
	public void create()
	{
		arial = new BitmapFont(Gdx.files.internal("data/BloodFont.fnt"));
		arial.setColor((float) 88 / 255, 0, 0, 1);
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("data/Sounds/Jump.wav"));
		hitSound = Gdx.audio.newSound(Gdx.files.internal("data/Sounds/Punch.wav"));
		fireBallSound = Gdx.audio.newSound(Gdx.files.internal("data/Sounds/Haduken.wav"));
		whooshSound = Gdx.audio.newSound(Gdx.files.internal("data/Sounds/Whoosh.wav"));
		teaBagSound = Gdx.audio.newSound(Gdx.files.internal("data/Sounds/TeaBag.wav"));
		// ^sounds
		{
			enemyWalkSheet = new Texture(Gdx.files.internal("data/Dargon/AnimR.png"));
			TextureRegion[][] tmp = TextureRegion.split(enemyWalkSheet, enemyWalkSheet.getWidth() / FRAME_COLS, enemyWalkSheet.getHeight() / FRAME_ROWS);
			enemyWalkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					enemyWalkFrames[index++] = tmp[i][j];
				}
			}
			enemyWalkAnimation = new Animation(0.25f, enemyWalkFrames);
			enemyStateTime = 0f;
			// dragon right
		}
		{
			enemyWalkSheet2 = new Texture(Gdx.files.internal("data/Dargon/AnimL.png"));
			TextureRegion[][] tmp2 = TextureRegion.split(enemyWalkSheet2, enemyWalkSheet2.getWidth() / FRAME_COLS, enemyWalkSheet2.getHeight() / FRAME_ROWS);
			enemyWalkFrames2 = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index2 = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					enemyWalkFrames2[index2++] = tmp2[i][j];
				}
			}
			enemyWalkAnimation2 = new Animation(0.25f, enemyWalkFrames2);
			enemyStateTime2 = 0f;
			// dragon left

		}
		// Player 1 \/
		{
			p1WalkLSheet = new Texture(Gdx.files.internal("data/Player1/AnimL.png")); // #9
			TextureRegion[][] tmp = TextureRegion.split(p1WalkLSheet, p1WalkLSheet.getWidth() / FRAME_COLS, p1WalkLSheet.getHeight() / FRAME_ROWS); // #10
			p1WalkLFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					p1WalkLFrames[index++] = tmp[i][j];
				}
			}
			p1WalkAnimationL = new Animation(0.15f, p1WalkLFrames); // #11
			stateTimeP1L = 0f;
			// player1Left
		}
		{
			p1WalkRSheet = new Texture(Gdx.files.internal("data/Player1/AnimR.png")); // #9
			TextureRegion[][] tmp = TextureRegion.split(p1WalkRSheet, p1WalkRSheet.getWidth() / FRAME_COLS, p1WalkRSheet.getHeight() / FRAME_ROWS); // #10
			p1WalkRFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					p1WalkRFrames[index++] = tmp[i][j];
				}
			}
			p1WalkAnimationR = new Animation(0.15f, p1WalkRFrames); // #11
			stateTimeP1R = 0f;
			// player1Right
		}
		// Player 2 \/
		{
			p2WalkLSheet = new Texture(Gdx.files.internal("data/Player2/AnimL.png")); // #9
			TextureRegion[][] tmp = TextureRegion.split(p2WalkLSheet, p2WalkLSheet.getWidth() / FRAME_COLS, p2WalkLSheet.getHeight() / FRAME_ROWS); // #10
			p2WalkLFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					p2WalkLFrames[index++] = tmp[i][j];
				}
			}
			p2WalkAnimationL = new Animation(0.15f, p2WalkLFrames); // #11
			stateTimeP2L = 0f;
			// player2Left
		}
		{
			p2WalkRSheet = new Texture(Gdx.files.internal("data/Player2/AnimR.png")); // #9
			TextureRegion[][] tmp = TextureRegion.split(p2WalkRSheet, p2WalkRSheet.getWidth() / FRAME_COLS, p2WalkRSheet.getHeight() / FRAME_ROWS); // #20
			p2WalkRFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					p2WalkRFrames[index++] = tmp[i][j];
				}
			}
			p2WalkAnimationR = new Animation(0.15f, p2WalkRFrames); // #22
			stateTimeP2R = 0f;
			// player2Right
		}
		// Player 3 \/
		{
			p3WalkLSheet = new Texture(Gdx.files.internal("data/Player3/AnimL.png")); // #9
			TextureRegion[][] tmp = TextureRegion.split(p3WalkLSheet, p3WalkLSheet.getWidth() / FRAME_COLS, p3WalkLSheet.getHeight() / FRAME_ROWS); // #10
			p3WalkLFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					p3WalkLFrames[index++] = tmp[i][j];
				}
			}
			p3WalkAnimationL = new Animation(0.15f, p3WalkLFrames); // #11
			stateTimeP3L = 0f;
			// player3Left
		}
		{
			p3WalkRSheet = new Texture(Gdx.files.internal("data/Player3/AnimR.png")); // #9
			TextureRegion[][] tmp = TextureRegion.split(p3WalkRSheet, p3WalkRSheet.getWidth() / FRAME_COLS, p3WalkRSheet.getHeight() / FRAME_ROWS); // #30
			p3WalkRFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					p3WalkRFrames[index++] = tmp[i][j];
				}
			}
			p3WalkAnimationR = new Animation(0.15f, p3WalkRFrames); // #33
			stateTimeP3R = 0f;
			// player3Right
		}
		// Player 4 \/
		{
			p4WalkLSheet = new Texture(Gdx.files.internal("data/Player4/AnimL.png")); // #9
			TextureRegion[][] tmp = TextureRegion.split(p4WalkLSheet, p4WalkLSheet.getWidth() / FRAME_COLS, p4WalkLSheet.getHeight() / FRAME_ROWS); // #10
			p4WalkLFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					p4WalkLFrames[index++] = tmp[i][j];
				}
			}
			p4WalkAnimationL = new Animation(0.15f, p4WalkLFrames); // #11
			stateTimeP4L = 0f;
			// player4Left
		}
		{
			p4WalkRSheet = new Texture(Gdx.files.internal("data/Player4/AnimR.png")); // #9
			TextureRegion[][] tmp = TextureRegion.split(p4WalkRSheet, p4WalkRSheet.getWidth() / FRAME_COLS, p4WalkRSheet.getHeight() / FRAME_ROWS); // #40
			p4WalkRFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					p4WalkRFrames[index++] = tmp[i][j];
				}
			}
			p4WalkAnimationR = new Animation(0.15f, p4WalkRFrames); // #44
			stateTimeP4R = 0f;
			// player4Right
		}

		/*
		* 
		* 
		* 
		* 
		* 
		* 
		* 
		* 
		* 
		* 
		* 
		* */
		menu = new Texture(Gdx.files.internal("data/Screens/Menu.png"));
		if (Gdx.app.getType() == ApplicationType.Android)
		{
			controls = new Texture(Gdx.files.internal("data/Screens/ControlsAndroid.png"));
		} else
		{
			controls = new Texture(Gdx.files.internal("data/Screens/ControlsDesktop.png"));
		}
		charSelect = new Texture(Gdx.files.internal("data/Screens/CharacterSelection.png"));
		maps = new Texture(Gdx.files.internal("data/Screens/Maps.png"));

		mapButton = new Texture(Gdx.files.internal("data/Buttons/ChangeMap.png"));
		backButton = new Texture(Gdx.files.internal("data/Buttons/Back.png"));
		attackButton = new Texture(Gdx.files.internal("data/Buttons/Attack.png"));
		jumpButton = new Texture(Gdx.files.internal("data/Buttons/Jump.png"));
		leftButton = new Texture(Gdx.files.internal("data/Buttons/Left.png"));
		rightButton = new Texture(Gdx.files.internal("data/Buttons/Right.png"));
		fireBallButton = new Texture(Gdx.files.internal("data/Buttons/FireBallButton.png"));
		changeCharButton = new Texture(Gdx.files.internal("data/Buttons/ChangeChar.png"));
		shieldButton = new Texture(Gdx.files.internal("data/Buttons/Shield.png"));
		mute = new Texture(Gdx.files.internal("data/Buttons/Mute.png"));
		sound = new Texture(Gdx.files.internal("data/Buttons/Sound.png"));

		enemy = new Texture(Gdx.files.internal("data/Dargon/Stand.png"));
		enemyAttack = new Texture(Gdx.files.internal("data/Dargon/Kunch.png"));
		explosion = new Texture(Gdx.files.internal("data/ExplosionHD75Opacity.png"));
		shield = new Texture(Gdx.files.internal("data/Shield.png"));
		finishIt = new Texture(Gdx.files.internal("data/FinishIt.png"));
		itDied = new Texture(Gdx.files.internal("data/ItDied.png"));
		begin = new Texture(Gdx.files.internal("data/Begin.png"));

		player = new Rectangle(x, y, 256, 256);
		npc = new Rectangle(ex, wy, 256, 256);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		batch = new SpriteBatch();

		myGestureListener = new MyGestureListener();
		gestureDetector = new GestureDetector(myGestureListener);

		Gdx.input.setInputProcessor(gestureDetector);
		Gdx.input.setCatchBackKey(true);

		preferences = Gdx.app.getPreferences("Perishable Punchers Settings.prefs");
		isSoundOn = preferences.getBoolean("isSoundOn");
	}

	@Override
	public void render()
	{
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		Gdx.graphics.setTitle("Perishable Punchers");
		switch (gameState)
		{
		case MENU:
			renderMenu();
			break;
		case PLAY:
			renderGame();
			break;
		case CONTROLS:
			renderControls();
			break;
		case CHARACTER_SELECTION:
			renderCharSelect();
			break;
		case MAP_SELECTION:
			renderMapSelection();
			break;
		default:
			break;
		}
	}

	private void renderMenu()
	{
		// from here
		Vector3 touchPos;
		touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		if (debugging)
		{
			System.out.println("X" + (int) touchPos.x);
			System.out.println("Y" + (int) touchPos.y);
			// to here is so the display coords work
		}
		if (Gdx.input.isKeyJustPressed(Keys.ENTER))
		{
			if (firstTimePlaying)
			{
				characterColor = CharacterColor.PINK;
				mapID = 1;
				loadCharTexture(characterColor);
				loadMapTexture(mapID);
				gameState = GameState.PLAY;
				firstTimePlaying = false;
			} else
			{
				gameState = GameState.PLAY;
			}
		}

		if (Gdx.input.isButtonPressed(0))
		{
			if (touchPos.x > 400 && touchPos.x < 875)
			{
				if (touchPos.y > 390 && touchPos.y < 545)
				{
					if (!firstTimePlaying)
					{
						// in play button

						gameState = GameState.PLAY;
					} else
					{
						gameState = GameState.CHARACTER_SELECTION;

					}
				}
			}
			if (touchPos.x > 475 && touchPos.x < 800)
			{
				if (touchPos.y > 225 && touchPos.y < 330)
				{
					// in controls button
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					gameState = GameState.CONTROLS;
				}
			}
			if (touchPos.x > 475 && touchPos.x < 800)
			{
				if (touchPos.y > 60 && touchPos.y < 170)
				{
					// in quit button
					dispose();
					System.exit(0);
				}
			}
			if (touchPos.x > 16 && touchPos.x < 64 + 16)
			{
				if (touchPos.y > 16 && touchPos.y < 64 + 16)
				{
					// in mute button
					isSoundOn = !isSoundOn;
					preferences.putBoolean("isSoundOn", isSoundOn);
					preferences.flush();
					try
					{
						Thread.sleep(100);
					} catch (Exception e)
					{
					}
				}
			}

		}
		batch.begin();
		batch.draw(menu, 0, 0);
		if (isSoundOn)
		{
			batch.draw(sound, 16, 16);
		} else
		{
			batch.draw(mute, 16, 16);
		}
		batch.end();

	}

	private void renderCharSelect()
	{
		batch.begin();
		batch.draw(charSelect, 0, 0);
		batch.end();
		// from here
		Vector3 touchPos;
		touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		if (debugging)
		{
			System.out.println("X" + (int) touchPos.x);
			System.out.println("Y" + (int) touchPos.y);
		}
		// to here is so the display coords work
		if (Gdx.input.isButtonPressed(0))
		{
			if (touchPos.x > 210 && touchPos.x < 435)
			{
				if (touchPos.y > 360 && touchPos.y < 630)
				{
					if (debugging)
					{
						// top left
						System.out.println("TOP LEFT");
					}
					characterColor = CharacterColor.PINK;
					loadCharTexture(characterColor);
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					if (firstTimePlaying)
					{
						gameState = GameState.MAP_SELECTION;
					} else
					{
						gameState = GameState.PLAY;
					}
					firstTimePlaying = false;
				}
			}
			if (touchPos.x > 850 && touchPos.x < 1075)
			{
				if (touchPos.y > 360 && touchPos.y < 630)
				{
					if (debugging)
					{
						// top right
						System.out.println("TOP RIGHT");
					}
					characterColor = CharacterColor.RED;
					loadCharTexture(characterColor);
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					if (firstTimePlaying)
					{
						gameState = GameState.MAP_SELECTION;
					} else
					{
						gameState = GameState.PLAY;
					}
					firstTimePlaying = false;
				}
			}
			if (touchPos.x > 210 && touchPos.x < 435)
			{
				if (touchPos.y > 64 && touchPos.y < 330)
				{
					if (debugging)
					{
						// bott left
						System.out.println("BOTTOM LEFT");
					}
					characterColor = CharacterColor.GREEN;
					loadCharTexture(characterColor);
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					if (firstTimePlaying)
					{
						gameState = GameState.MAP_SELECTION;
					} else
					{
						gameState = GameState.PLAY;
					}
					firstTimePlaying = false;
				}
			}
			if (touchPos.x > 850 && touchPos.x < 1075)
			{
				if (touchPos.y > 64 && touchPos.y < 330)
				{
					if (debugging)
					{
						// bott right
						System.out.println("BOTTOM RIGHT");
					}
					characterColor = CharacterColor.BLUE;
					loadCharTexture(characterColor);
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					if (firstTimePlaying)
					{
						gameState = GameState.MAP_SELECTION;
					} else
					{
						gameState = GameState.PLAY;
					}
					firstTimePlaying = false;
				}
			}
		}

		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			gameState = GameState.MENU;
		}
		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			gameState = GameState.MENU;
		}

	}

	private void renderMapSelection()
	{
		batch.begin();
		batch.draw(maps, 0, 0);
		batch.end();

		// from here
		Vector3 touchPos;
		touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		if (debugging)
		{
			System.out.println("X" + (int) touchPos.x);
			System.out.println("Y" + (int) touchPos.y);
		}
		// to here is so the display coords work
		if (Gdx.input.isButtonPressed(0))
		{
			if (touchPos.x > 85 && touchPos.x < 565)
			{
				if (touchPos.y > 355 && touchPos.y < 630)
				{
					if (debugging)
					{
						// top left
						System.out.println("TOP LEFT");
					}
					loadMapTexture(1);
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					gameState = GameState.PLAY;
				}
			}
			if (touchPos.x > 705 && touchPos.x < 1190)
			{
				if (touchPos.y > 355 && touchPos.y < 630)
				{
					if (debugging)
					{
						// top left
						System.out.println("TOP RIGHT");
					}
					loadMapTexture(2);
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					gameState = GameState.PLAY;
				}
			}
			if (touchPos.x > 85 && touchPos.x < 565)
			{
				if (touchPos.y > 65 && touchPos.y < 340)
				{
					if (debugging)
					{
						// top left
						System.out.println("BOTT LEFT");
					}
					loadMapTexture(3);
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					gameState = GameState.PLAY;
				}
			}
			if (touchPos.x > 705 && touchPos.x < 1190)
			{
				if (touchPos.y > 65 && touchPos.y < 340)
				{
					if (debugging)
					{
						// top left
						System.out.println("BOTT RIGHT");
					}
					loadMapTexture(4);
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					gameState = GameState.PLAY;
				}
			}

		}

		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			gameState = GameState.CHARACTER_SELECTION;
		}
		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			gameState = GameState.CHARACTER_SELECTION;
		}

	}

	private void renderGame()
	{
		enemyStateTime += Gdx.graphics.getDeltaTime();
		enemyStateTime2 += Gdx.graphics.getDeltaTime();
		stateTimeP1L += Gdx.graphics.getDeltaTime();
		stateTimeP1R += Gdx.graphics.getDeltaTime();
		stateTimeP2L += Gdx.graphics.getDeltaTime();
		stateTimeP2R += Gdx.graphics.getDeltaTime();
		stateTimeP3L += Gdx.graphics.getDeltaTime();
		stateTimeP3R += Gdx.graphics.getDeltaTime();
		stateTimeP4L += Gdx.graphics.getDeltaTime();
		stateTimeP4R += Gdx.graphics.getDeltaTime();
		// ^ Statetimes
		enemyCurrentFrame = enemyWalkAnimation.getKeyFrame(enemyStateTime, true);
		enemyCurrentFrame2 = enemyWalkAnimation2.getKeyFrame(enemyStateTime2, true);
		p1WalkLFrame = p1WalkAnimationL.getKeyFrame(stateTimeP1L, true);
		p1WalkRFrame = p1WalkAnimationR.getKeyFrame(stateTimeP1R, true);
		p2WalkLFrame = p2WalkAnimationL.getKeyFrame(stateTimeP2L, true);
		p2WalkRFrame = p2WalkAnimationR.getKeyFrame(stateTimeP2R, true);
		p3WalkLFrame = p3WalkAnimationL.getKeyFrame(stateTimeP3L, true);
		p3WalkRFrame = p3WalkAnimationR.getKeyFrame(stateTimeP3R, true);
		p4WalkLFrame = p4WalkAnimationL.getKeyFrame(stateTimeP4L, true);
		p4WalkRFrame = p4WalkAnimationR.getKeyFrame(stateTimeP4R, true);
		// ^set frames

		// from here
		Vector3 touchPos;
		touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		if (debugging)
		{
			System.out.println("X" + (int) touchPos.x);
			System.out.println("Y" + (int) touchPos.y);
		}
		if (canPlayerMove)
		{
			if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W))
			{
				if (y < 65)
				{
					jump();
				}
			}
			if (Gdx.input.isKeyPressed(Keys.LEFT))
			{
				if (x > 16)
				{
					x -= 800 * Gdx.graphics.getDeltaTime();
					isPlayerMovingLeft = true;
					isPlayerMovingRight = false;
				}
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				if (x < WIDTH - (256 + 16))
				{
					x += 800 * Gdx.graphics.getDeltaTime();
					isPlayerMovingLeft = false;
					isPlayerMovingRight = true;
				}
			} else if (Gdx.input.isKeyPressed(Keys.A))
			{
				if (x > 16)
				{
					x -= 800 * Gdx.graphics.getDeltaTime();
					isPlayerMovingLeft = true;
					isPlayerMovingRight = false;
				}
			} else if (Gdx.input.isKeyPressed(Keys.D))
			{
				if (x < WIDTH - (256 + 16))
				{
					x += 800 * Gdx.graphics.getDeltaTime();
					isPlayerMovingLeft = false;
					isPlayerMovingRight = true;
				}
			} else
			{
				isPlayerMovingLeft = false;
				isPlayerMovingRight = false;
			}

			if (Gdx.input.isKeyJustPressed(Keys.S) || Gdx.input.isKeyJustPressed(Keys.DOWN))
			{
				attack();
			}
			if (Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Keys.SHIFT_RIGHT))
			{
				shootFireBall();
			}
			if (Gdx.input.isKeyJustPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyJustPressed(Keys.CONTROL_RIGHT))
			{
				useShield();
			}
		}
		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			gameState = GameState.MENU;
		}

		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			gameState = GameState.MENU;
		}

		logic(touchPos.x, touchPos.y);

		batch.begin();
		batch.draw(background, 0, 0);
		if (isEnemyMovingRight)
		{
			batch.draw(enemyCurrentFrame, ex, wy);
		} else if (isEnemyMovingLeft)
		{
			batch.draw(enemyCurrentFrame2, ex, wy);
		} else
		{
			if (enemyAttacking)
			{
				batch.draw(enemyAttack, ex, wy);
			} else
			{
				batch.draw(enemy, ex, wy);
			}
		}
		if (!kunching)
		{
			// batch.draw(fighter, x, y);
			if (isPlayerMovingRight)
			{
				if (characterColor == CharacterColor.PINK)
				{
					batch.draw(p1WalkRFrame, x, y);
				} else if (characterColor == CharacterColor.GREEN)
				{
					batch.draw(p2WalkRFrame, x, y);
				} else if (characterColor == CharacterColor.RED)
				{
					batch.draw(p3WalkRFrame, x, y);
				} else if (characterColor == CharacterColor.BLUE)
				{
					batch.draw(p4WalkRFrame, x, y);
				}
			} else if (isPlayerMovingLeft)
			{
				if (characterColor == CharacterColor.PINK)
				{
					batch.draw(p1WalkLFrame, x, y);
				} else if (characterColor == CharacterColor.GREEN)
				{
					batch.draw(p2WalkLFrame, x, y);
				} else if (characterColor == CharacterColor.RED)
				{
					batch.draw(p3WalkLFrame, x, y);
				} else if (characterColor == CharacterColor.BLUE)
				{
					batch.draw(p4WalkLFrame, x, y);
				}
			} else
			{
				batch.draw(fighter, x, y);
			}

		} else
		{
			batch.draw(fighterKunch, x, y);
		}

		if (renderFireBall)
		{
			batch.draw(explosion, fbX, fbY);
		}

		if (isUsingShield)
		{
			batch.draw(shield, x, y);
		}

		if (Gdx.app.getType() == ApplicationType.Android)
		{
			batch.draw(leftButton, 4, 4);
			batch.draw(jumpButton, 12 + 256, 4);
			batch.draw(rightButton, 8 + 128, 4);
			batch.draw(attackButton, WIDTH - 128 - 4, 4);
			batch.draw(fireBallButton, WIDTH - 256 - 8, 4);
			batch.draw(shieldButton, WIDTH - 256 - 128 - 12, 4);
			batch.draw(changeCharButton, (WIDTH / 2) - 128, 4);
			batch.draw(mapButton, (WIDTH / 2) - 128, 8 + 64);
		} else
		{
			batch.draw(changeCharButton, 4, 4);
			batch.draw(mapButton, 4, 8 + 64);
			batch.draw(backButton, 4, HEIGHT - (64 + 8));
		}
		arial.draw(batch, "You: " + playerHeath, 256, (HEIGHT / 2) + 256);
		arial.draw(batch, "Enemy: " + enemyHealth, WIDTH - 512, (HEIGHT / 2) + 256);

		if (isRenderingFinishIt)
		{
			batch.draw(finishIt, (WIDTH / 2) - 256, HEIGHT / 2);
		}
		if (isRenderingItDied)
		{
			batch.draw(itDied, (WIDTH / 2) - 256, HEIGHT / 2);
		}
		if (isRenderingBegin && playerHeath == 100 && enemyHealth == 100)
		{
			batch.draw(begin, (WIDTH / 2) - 256, HEIGHT / 2);
			Thread thread = new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					try
					{
						Thread.sleep(2000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					isRenderingBegin = false;
				}
			});
			thread.start();
		}
		batch.end();

	}

	private void logic(float mX, float mY)
	{
		player.x = x;
		player.y = 64;
		npc.x = ex;

		if (enemyHealth < 1)
		{
			// render it dide
			isRenderingItDied = true;
			isRenderingFinishIt = false;
			enemyHealth = 0;
			Thread thread = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						Thread.sleep(500);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					isRenderingBegin = true;
					resetGame();
				}
			});
			thread.start();
		} else if (enemyHealth <= 10)
		{
			// render finish it
			isRenderingFinishIt = true;
			isRenderingItDied = false;
		} else if (playerHeath < 1)
		{
			// render it dide
			isRenderingItDied = true;
			isRenderingFinishIt = false;
			canPlayerMove = false;
			playerHeath = 0;
			Thread thread = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						Thread.sleep(2000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					resetGame();
				}
			});
			thread.start();
		} else if (playerHeath <= 10)
		{
			// render finish it
			isRenderingFinishIt = true;
			isRenderingItDied = false;
			canPlayerMove = false;
		} else
		{
			isRenderingFinishIt = false;
			isRenderingItDied = false;
			canPlayerMove = true;
		}

		if (Gdx.app.getType() == ApplicationType.Android)
		{
			if (Gdx.input.isButtonPressed(0))
			{
				if (mX > 4 && mX < 4 + 128)
				{
					if (mY > 4 && mY < 4 + 128)
					{
						if (x > 16)
						{
							if (canPlayerMove)
							{
								x -= 800 * Gdx.graphics.getDeltaTime();
								isPlayerMovingLeft = true;
								isPlayerMovingRight = false;
							}
						}
					}
				}
				if (mX > 8 + 128 && mX < 8 + 128 + 128)
				{
					if (mY > 4 && mY < 4 + 128)
					{
						if (x < WIDTH - (256 + 32))
						{
							if (canPlayerMove)
							{
								x += 800 * Gdx.graphics.getDeltaTime();
								isPlayerMovingLeft = false;
								isPlayerMovingRight = true;
							}
						}
					}
				}
			}
		}
		if (Gdx.input.justTouched())
		{
			if (Gdx.app.getType() == ApplicationType.Desktop)
			{
				if (mX > 4 && mX < 4 + 256 && mY > (8 + 64) && mY < (8 + 64) + 64)
				{
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					gameState = GameState.MAP_SELECTION;
				}

				if (mX > 4 && mX < 4 + 256 && mY > (8) && mY < (8) + 64)
				{
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					gameState = GameState.CHARACTER_SELECTION;
				}

				if (mX > 4 && mX < 4 + 128 && mY > HEIGHT - (64 + 8))
				{
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					gameState = GameState.MENU;
				}
			}
			if (Gdx.app.getType() == ApplicationType.Android)
			{
				if (mX > (WIDTH / 2) - 128 && mX < (WIDTH / 2) - 128 + 256 && mY > 8 + 64 && mY < 8 + 64 + 64)
				{
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					gameState = GameState.MAP_SELECTION;
				}
				if (mX > (WIDTH / 2) - 128 && mX < (WIDTH / 2) - 128 + 256 && mY > 4 && mY < 4 + 64)
				{
					try
					{
						Thread.sleep(250);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					gameState = GameState.CHARACTER_SELECTION;

				}
				if (mX > WIDTH - 256 - 128 - 12 && mX < WIDTH - 256 - 128 - 12 + 128)
				{
					if (mY > 4 && mY < 4 + 128)
					{
						if (canPlayerMove)
						{
							useShield();
						}
					}
				}
				if (mX > WIDTH - 128 - 4 && mX < WIDTH - 128 - 4 + 128)
				{
					if (mY > 4 && mY < 4 + 128)
					{
						if (canPlayerMove)
						{
							// attack button
							Gdx.input.vibrate(50);
							attack();
						}
					}
				}
				if (mX > 12 + 256 && mX < 12 + 256 + 128)
				{
					if (mY > 4 && mY < 4 + 128)
					{
						if (y < 70)
						{
							if (canPlayerMove)
							{
								jump();
							}
						}
					}
				}

				if (mX > WIDTH - 256 - 8 && mX < (WIDTH - 256 - 8) + 128)
				{
					if (mY > 4 && mY < 4 + 128)
					{
						if (!renderFireBall)
						{
							if (canPlayerMove)
							{
								// fireball button
								Gdx.input.vibrate(50);
								shootFireBall();
							}
						}
					}
				}
			}

		}

		dargonAI();

	}

	private void useShield()
	{
		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				isUsingShield = true;
				shieldCount++;
				try
				{
					Thread.sleep(2500);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				isUsingShield = false;
			}
		});
		if (shieldCount < 2)
		{
			thread.start();
		}
	}

	Thread thread4;

	private void jump()
	{
		if (isSoundOn)
		{
			jumpSound.play();
		}

		thread4 = new Thread(new Runnable()
		{
			public void run()
			{

				while (y < 400)
				{
					y += 600 * Gdx.graphics.getDeltaTime();
					try
					{
						Thread.sleep(5);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				try
				{
					Thread.sleep(50);
				} catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
				while (y > 63)
				{
					y -= 800 * Gdx.graphics.getDeltaTime();
					try
					{
						Thread.sleep(5);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

			}

		});
		thread4.start();
	}

	Thread thread3;

	private void attack()
	{

		kunching = true;
		if (npc.overlaps(player))
		{
			thread3 = new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					if (isSoundOn && y > 64)
					{
						teaBagSound.play();
						if (enemyHealth > 0)
						{
							enemyHealth -= 10;
						}
					} else if (isSoundOn)
					{
						hitSound.play();
						if (enemyHealth > 0)
						{
							enemyHealth -= 5;
						}
					}

				}
			});
			thread3.start();
		} else
		{
			thread3 = new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					if (isSoundOn)
					{
						whooshSound.play();
					}
				}
			});
			thread3.start();
		}
		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					Thread.sleep(100);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				kunching = false;
			}
		});
		thread.start();
	}

	Thread thread2;

	private void shootFireBall()
	{

		// System.out.println("fireball method called");
		thread2 = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				if (isSoundOn)
				{
					fireBallSound.play();
				}
			}
		});

		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				renderFireBall = true;
				fbX = x;
				if (fbX < ex - 64)
				{
					thread2.start();
				} else if (fbX > ex + 64)
				{
					thread2.start();
				} else
				{

				}
				do
				{
					if (fbX < ex - 64)
					{
						fbX += 1400 * Gdx.graphics.getDeltaTime();
					} else if (fbX > ex + 64)
					{
						fbX -= 1400 * Gdx.graphics.getDeltaTime();
					} else
					// touching
					{
						renderFireBall = false;
						fbX = x;
						enemyHealth -= 20;
					}
					try
					{
						Thread.sleep(15);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				} while (renderFireBall);
			}
		});
		if (fireBallCount < 2)
		{
			if (fbX < ex - 64)
			{
				fireBallCount++;
				thread.start();
			} else if (fbX > ex + 64)
			{
				fireBallCount++;
				thread.start();
			}
		}

	}

	private void dargonAI()
	{
		if (enemyHealth > 10)
		{
			if (enemyHealth > 1)
			{
				if (ex < x - 64)
				{
					isEnemyMovingRight = true;
					ex += 200 * Gdx.graphics.getDeltaTime();
					enemyAttacking = false;
				} else if (ex > x + 64)
				{
					isEnemyMovingLeft = true;
					ex -= 200 * Gdx.graphics.getDeltaTime();
					enemyAttacking = false;
				} else
				// touching
				{
					if (!isUsingShield)
					{

						isEnemyMovingLeft = false;
						isEnemyMovingRight = false;
						int rand = (int) (Math.random() * 600) + 1;
						// System.out.println(rand);
						if (rand > 570)
						{
							enemyAttacking = true;

							// System.out.println("ENEMY HIT");
							if (isSoundOn)
							{
								hitSound.play();
							}
							if (playerHeath > 0)
							{
								playerHeath -= 5;
							}

							Thread thread = new Thread(new Runnable()
							{

								@Override
								public void run()
								{
									try
									{
										Thread.sleep(150);
									} catch (InterruptedException e)
									{
										e.printStackTrace();
									}
									enemyAttacking = false;
								}
							});
							thread.start();
						} else
						{

						}

					}
				}
			}
		}
	}

	private void resetGame()
	{
		x = 64;
		ex = WIDTH - 256 + 8;
		enemyHealth = 100;
		playerHeath = 100;
		isRenderingBegin = true;
		shieldCount = 0;
		fireBallCount = 0;
	}

	private void renderControls()
	{
		batch.begin();
		batch.draw(controls, 0, 0);
		batch.end();
		if (Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isButtonPressed(0))
		{
			try
			{
				Thread.sleep(250);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			gameState = GameState.MENU;
		}
		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			gameState = GameState.MENU;
		}
	}

	private void loadCharTexture(CharacterColor color)
	{
		if (color == CharacterColor.PINK)
		{
			fighter = new Texture(Gdx.files.internal("data/Player1/Stand.png"));
			fighterKunch = new Texture(Gdx.files.internal("data/Player1/Kunch.png"));
		} else if (color == CharacterColor.RED)
		{
			fighter = new Texture(Gdx.files.internal("data/Player3/Stand.png"));
			fighterKunch = new Texture(Gdx.files.internal("data/Player3/Kunch.png"));
		} else if (color == CharacterColor.GREEN)
		{
			fighter = new Texture(Gdx.files.internal("data/Player2/Stand.png"));
			fighterKunch = new Texture(Gdx.files.internal("data/Player2/Kunch.png"));
		} else if (color == CharacterColor.BLUE)
		{
			fighter = new Texture(Gdx.files.internal("data/Player4/Stand.png"));
			fighterKunch = new Texture(Gdx.files.internal("data/Player4/Kunch.png"));
		}
	}

	private void loadMapTexture(int id)
	{
		switch (id)
		{
		case 1:
			background = new Texture(Gdx.files.internal("data/Maps/Background1.png"));
			break;
		case 2:
			background = new Texture(Gdx.files.internal("data/Maps/Background2.png"));
			break;
		case 3:
			background = new Texture(Gdx.files.internal("data/Maps/Background3.png"));
			break;
		case 4:
			background = new Texture(Gdx.files.internal("data/Maps/Background4.png"));
			break;
		default:
			break;
		}
	}

	@Override
	public void dispose()
	{
		super.dispose();

		jumpSound.dispose();
		hitSound.dispose();
		fireBallSound.dispose();
		whooshSound.dispose();
		menu.dispose();
		controls.dispose();
		maps.dispose();
		backButton.dispose();
		attackButton.dispose();
		jumpButton.dispose();
		leftButton.dispose();
		rightButton.dispose();
		changeCharButton.dispose();
		charSelect.dispose();
		enemyAttack.dispose();
		enemy.dispose();
		explosion.dispose();
		shield.dispose();
		fireBallButton.dispose();
		mute.dispose();
		sound.dispose();
		enemyWalkSheet.dispose();
		enemyWalkSheet2.dispose();
		p1WalkRSheet.dispose();
		p1WalkLSheet.dispose();
		p2WalkRSheet.dispose();
		p2WalkLSheet.dispose();
		p3WalkRSheet.dispose();
		p3WalkLSheet.dispose();
		p4WalkRSheet.dispose();
		p4WalkLSheet.dispose();
		finishIt.dispose();
		itDied.dispose();
		shieldButton.dispose();
		if (!firstTimePlaying)
		{
			fighter.dispose();
			background.dispose();
		}
		firstTimePlaying = false;
		System.exit(0);

	}

}
