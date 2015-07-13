package com.ssebs.perishablepunchers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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

// TODO: add change character option, redesign art, health of both characters,
// diff maps, shield 

public class PPMain extends ApplicationAdapter
{
	final int WIDTH = 1280;
	final int HEIGHT = 720;

	Preferences preferences;

	SpriteBatch batch;
	Texture background, menu, controls, backButton, attackButton, jumpButton, leftButton, rightButton, charSelect;
	Texture enemyAttack, fighter, enemy, fighterKunch, explosion, fireBallButton, mute, sound;
	OrthographicCamera camera;
	Rectangle player, npc;

	int x = 256 + 64, y = 64, ex = WIDTH - (256 + 64), wy = 64, fighterHealth = 100, enemyHealth = 100;
	int fbX, fbY;// fireball coords

	private static final int FRAME_COLS = 2;
	private static final int FRAME_ROWS = 2;

	Animation walkAnimation, walkAnimation2, p1WalkAnimationR, p1WalkAnimationL, p2WalkAnimationR, p2WalkAnimationL, p3WalkAnimationR, p3WalkAnimationL, p4WalkAnimationR, p4WalkAnimationL;
	Texture walkSheet, walkSheet2, p1WalkRSheet, p1WalkLSheet, p2WalkRSheet, p2WalkLSheet, p3WalkRSheet, p3WalkLSheet, p4WalkRSheet, p4WalkLSheet;
	TextureRegion[] walkFrames, walkFrames2, p1WalkRFrames, p1WalkLFrames, p2WalkRFrames, p2WalkLFrames, p3WalkRFrames, p3WalkLFrames, p4WalkRFrames, p4WalkLFrames;
	TextureRegion currentFrame, currentFrame2, p1WalkRFrame, p1WalkLFrame, p2WalkRFrame, p2WalkLFrame, p3WalkRFrame, p3WalkLFrame, p4WalkRFrame, p4WalkLFrame;
	float stateTime, stateTime2, stateTimeP1L, stateTimeP1R, stateTimeP2L, stateTimeP2R, stateTimeP3L, stateTimeP3R, stateTimeP4L, stateTimeP4R;

	boolean isMovingLeft = false;
	boolean isMovingRight = false;
	boolean kunch = false;
	boolean clicking = false;
	boolean overlapping = false;
	boolean isPressingButton = false;
	boolean isPressingButton2 = false;
	boolean ableToJump = true;
	boolean firstTimePlaying = true;
	boolean enemyAttacking = false;
	boolean debugging = false;
	boolean renderFireBall = false;
	boolean isPlayerMovingRight = false;
	boolean isPlayerMovingLeft = false;

	// boolean isSoundOn = true;// THIS SHOULD BE A SETTING
	boolean isSoundOn;

	GestureDetector gestureDetector;
	MyGestureListener myGestureListener;

	Sound jumpSound, hitSound, fireBallSound;

	GameState gameState = GameState.MENU;
	CharacterColor characterColor = CharacterColor.PINK;

	enum GameState
	{
		MENU, PLAY, CONTROLS, CHARACTER_SELECTION;
	}

	enum CharacterColor
	{
		PINK, RED, GREEN, BLUE;
	}

	@Override
	public void create()
	{

		jumpSound = Gdx.audio.newSound(Gdx.files.internal("data/Sounds/Jump.wav"));
		hitSound = Gdx.audio.newSound(Gdx.files.internal("data/Sounds/Punch.wav"));
		fireBallSound = Gdx.audio.newSound(Gdx.files.internal("data/Sounds/Haduken.wav"));
		// ^sounds
		{
			walkSheet = new Texture(Gdx.files.internal("data/Dargon/AnimR.png"));
			TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth() / FRAME_COLS, walkSheet.getHeight() / FRAME_ROWS);
			walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					walkFrames[index++] = tmp[i][j];
				}
			}
			walkAnimation = new Animation(0.25f, walkFrames);
			stateTime = 0f;
			// dragon right
		}
		{
			walkSheet2 = new Texture(Gdx.files.internal("data/Dargon/AnimL.png"));
			TextureRegion[][] tmp2 = TextureRegion.split(walkSheet2, walkSheet2.getWidth() / FRAME_COLS, walkSheet2.getHeight() / FRAME_ROWS);
			walkFrames2 = new TextureRegion[FRAME_COLS * FRAME_ROWS];
			int index2 = 0;
			for (int i = 0; i < FRAME_ROWS; i++)
			{
				for (int j = 0; j < FRAME_COLS; j++)
				{
					walkFrames2[index2++] = tmp2[i][j];
				}
			}
			walkAnimation2 = new Animation(0.25f, walkFrames2);
			stateTime2 = 0f;
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
		background = new Texture(Gdx.files.internal("data/Screens/Background.png"));
		menu = new Texture(Gdx.files.internal("data/Screens/Menu.png"));
		controls = new Texture(Gdx.files.internal("data/Screens/Controls.png"));
		charSelect = new Texture(Gdx.files.internal("data/Screens/CharacterSelection.png"));

		backButton = new Texture(Gdx.files.internal("data/Buttons/Back.png"));
		attackButton = new Texture(Gdx.files.internal("data/Buttons/Attack.png"));
		jumpButton = new Texture(Gdx.files.internal("data/Buttons/Jump.png"));
		leftButton = new Texture(Gdx.files.internal("data/Buttons/Left.png"));
		rightButton = new Texture(Gdx.files.internal("data/Buttons/Right.png"));
		fireBallButton = new Texture(Gdx.files.internal("data/Buttons/FireBallButton.png"));
		mute = new Texture(Gdx.files.internal("data/Buttons/Mute.png"));
		sound = new Texture(Gdx.files.internal("data/Buttons/Sound.png"));

		enemy = new Texture(Gdx.files.internal("data/Dargon/Stand.png"));
		enemyAttack = new Texture(Gdx.files.internal("data/Dargon/Kunch.png"));
		explosion = new Texture(Gdx.files.internal("data/ExplosionHD75Opacity.png"));

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
		Gdx.graphics.setTitle("Perishable Punchers|FPS:" + Gdx.graphics.getFramesPerSecond());
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
		default:
			break;
		}
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
					firstTimePlaying = false;
					gameState = GameState.PLAY;
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
					firstTimePlaying = false;
					gameState = GameState.PLAY;
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
					firstTimePlaying = false;
					gameState = GameState.PLAY;
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
					firstTimePlaying = false;
					gameState = GameState.PLAY;
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

	private void renderGame()
	{
		stateTime += Gdx.graphics.getDeltaTime();
		stateTime2 += Gdx.graphics.getDeltaTime();
		stateTimeP1L += Gdx.graphics.getDeltaTime();
		stateTimeP1R += Gdx.graphics.getDeltaTime();
		stateTimeP2L += Gdx.graphics.getDeltaTime();
		stateTimeP2R += Gdx.graphics.getDeltaTime();
		stateTimeP3L += Gdx.graphics.getDeltaTime();
		stateTimeP3R += Gdx.graphics.getDeltaTime();
		stateTimeP4L += Gdx.graphics.getDeltaTime();
		stateTimeP4R += Gdx.graphics.getDeltaTime();
		// ^ Statetimes
		currentFrame = walkAnimation.getKeyFrame(stateTime, true);
		currentFrame2 = walkAnimation2.getKeyFrame(stateTime2, true);
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
		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
		{
			if (y < 65)
			{
				jump();
			}
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
		{
			x -= 800 * Gdx.graphics.getDeltaTime();
			isPlayerMovingLeft = true;
			isPlayerMovingRight = false;
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
		{
			x += 800 * Gdx.graphics.getDeltaTime();
			isPlayerMovingLeft = false;
			isPlayerMovingRight = true;
		} else
		{
			isPlayerMovingLeft = false;
			isPlayerMovingRight = false;
		}
		if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN))
		{
			attack();
		}
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
		{
			shootFireBall();
		}

		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			gameState = GameState.MENU;
		}

		if (Gdx.input.isKeyPressed(Keys.BACK))
		{
			gameState = GameState.MENU;
		}

		// ////////make touch left move guy left & vice versa,;
		if (Gdx.input.isButtonPressed(0))
		{
			if (touchPos.x > 4 + 4 + 256 && touchPos.x < 4 + 4 + 256 + 256)
			{
				if (touchPos.y > 4 && touchPos.y < 4 + 128)
				{
					if (x > 16)
					{
						x -= 800 * Gdx.graphics.getDeltaTime();
						isPlayerMovingLeft = true;
						isPlayerMovingRight = false;
					}
				}
			} else if (touchPos.x > 765 && touchPos.x < 1015)
			{
				if (touchPos.y > 8 && touchPos.y < 8 + 128)
				{
					if (x < WIDTH - (256 + 16))
					{
						x += 800 * Gdx.graphics.getDeltaTime();
						isPlayerMovingLeft = false;
						isPlayerMovingRight = true;
					}
				}
			}
		}

		logic(touchPos.x, touchPos.y);

		batch.begin();
		batch.draw(background, 0, 0);
		if (!kunch)
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
			if (overlapping)
			{
				if (isSoundOn)
				{
					hitSound.play();
				}
			}

		}

		if (isMovingRight)
		{
			batch.draw(currentFrame, ex, wy);
		} else if (isMovingLeft)
		{
			batch.draw(currentFrame2, ex, wy);
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

		if (overlapping)
		{
			// batch.draw(explosion, x - 128, y - 128);
		}
		// batch.draw(backButton, 4, HEIGHT - (64 + 4));

		if (renderFireBall)
		{
			batch.draw(explosion, fbX, fbY);
		}

		batch.draw(attackButton, 4, 4);
		batch.draw(leftButton, (256 + 4), 4);
		batch.draw(fireBallButton, WIDTH / 2 - (128), 4);
		batch.draw(jumpButton, WIDTH - (256 + 4), 4);
		batch.draw(rightButton, WIDTH - (512 + 4), 4);

		batch.end();
	}

	private void logic(float mX, float mY)
	{
		player.x = x;
		player.y = 64;
		npc.x = ex;

		if (Gdx.input.isButtonPressed(0))
		{
			if (mX > 4 && mX < 4 + 256)
			{
				if (mY > 4 && mY < 4 + 128)
				{
					// attack button
					isPressingButton = true;
					attack();
				}
			} else if (mX > 1025 && mX < 1272)
			{
				if (mY > 8 && mY < 4 + 122)
				{
					// jump button
					isPressingButton2 = true;

					if (y < 70)
					{
						jump();
					}
				}
			} else if (mX > 516 && mX < 764)
			{
				if (mY > 8 && mY < 4 + 122)
				{
					if (!renderFireBall)
					{
						// fireball button
						shootFireBall();
					}
				}
			} else
			{
				isPressingButton2 = false;
				isPressingButton = false;
			}

			if (mX > 4 && mX < 260)
			{
				if (mY > 650 && mY < 716)
				{
					// gameState = GameState.MENU;
				}
			}

		} else
		{
			clicking = false;
		}
		if (Gdx.input.isButtonPressed(1))
		{
			attack();
		} else
		{
			clicking = false;
		}

		dargonAI();

	}

	private void jump()
	{
		if (isSoundOn)
		{
			jumpSound.play();
		}
		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				int speed = 8;
				while (y < 400)
				{

					y += 800 * Gdx.graphics.getDeltaTime();
					if (y < 256)
					{
						speed = 10;
					} else if (y > 300)
					{
						speed = 15;
					} else
					{
						speed = 15;
					}
					try
					{
						Thread.sleep(speed);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				while (y > 64)
				{
					y -= 600 * Gdx.graphics.getDeltaTime();

					if (y < 256)
					{
						speed = 10;
					} else if (y > 300)
					{
						speed = 15;
					} else
					{
						speed = 15;
					}
					try
					{
						Thread.sleep(speed);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					if (y > 64 && y < 16)
						y = 64;
				}

			}
		});
		thread.start();
	}

	private void attack()
	{

		if (!clicking)
		{
			kunch = true;
			if (npc.overlaps(player))
			{
				// deal damage

				Thread thread = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						overlapping = true;
						// hitSound.play();
						try
						{
							Thread.sleep(250);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}

						overlapping = false;
					}
				});
				thread.start();
			} else
			{
				overlapping = false;
			}
		}

		clicking = true;
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
				kunch = false;
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
				// touching
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
		thread.start();
	}

	private void dargonAI()
	{
		if (ex < x - 64)
		{
			isMovingRight = true;
			ex += 200 * Gdx.graphics.getDeltaTime();
			enemyAttacking = false;
		} else if (ex > x + 64)
		{
			isMovingLeft = true;
			ex -= 200 * Gdx.graphics.getDeltaTime();
			enemyAttacking = false;
		} else
		// touching
		{
			isMovingLeft = false;
			isMovingRight = false;
			int rand = (int) (Math.random() * 600);
			// System.out.println(rand);
			if (rand > 550)
			{
				enemyAttacking = true;
				if (isSoundOn)
				{
					// System.out.println("ENEMY HIT");
					hitSound.play();
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

	@Override
	public void dispose()
	{
		super.dispose();
		batch.dispose();
		background.dispose();
		if (!firstTimePlaying)
		{
			fighter.dispose();
		}
		firstTimePlaying = false;
	}

}
